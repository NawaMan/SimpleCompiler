/*----------------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2008-2021 Nawapunth Manusitthipol. Implements with and for Java 11 JDK.
 *----------------------------------------------------------------------------------------------------------------------
 * LICENSE:
 * 
 * This file is part of Nawa's SimpleCompiler.
 * 
 * The project is a free software; you can redistribute it and/or modify it under the SIMILAR terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * You are only required to inform me about your modification and redistribution as or as part of commercial software
 * package. You can inform me via nawa<at>nawaman<dot>net.
 * 
 * The project is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * ---------------------------------------------------------------------------------------------------------------------
 */

package net.nawaman.compiler;

import java.io.Serializable;
import java.util.Set;

import net.nawaman.task.Process;
import net.nawaman.task.TaskEntry;

/**
 * A compiler
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
public class Compiler extends Process implements Serializable {
	
    private static final long serialVersionUID = -3754986809952649257L;

    public Compiler(String pName, TaskEntry ... pTEs) {
		this.Name     = pName;
		this.TEntries = (pTEs == null)?null:pTEs.clone();
	}
	String      Name;
	TaskEntry[] TEntries;
	
	/** Returns the name of the compiler */
	final public String getName() {
		return this.Name;
	}
	final protected TaskEntry[] newTaskEntries() {
		return this.TEntries;
	}
	
	/** Returns the array of the task entries */
	final protected TaskEntry[] getTaskEntries() {
		if(this.Tasks == null) this.Tasks = this.newTaskEntries();
		return this.Tasks;
	}
	
	TaskEntry[] Tasks;
	final public int getTaskCount() {
		return (this.getTaskEntries() == null)?0:this.getTaskEntries().length;
	}
	final public TaskEntry getTaskEntry(int I) {
		if((I < 0) || (I >= ((this.getTaskEntries() == null)?0:this.getTaskEntries().length))) return null;
		return this.getTaskEntries()[I];
	}
	final public TaskForCompiler getTask(int I) {
		if((I < 0) || (I >= ((this.getTaskEntries() == null)?0:this.getTaskEntries().length))) return null;
		TaskEntry TE = this.getTaskEntries()[I];
		return (TE == null)?null:(TaskForCompiler)TE.getTask();
	}
	
	// Compilation -----------------------------------------------------------------------------------------------------
	
	protected CompileProduct newCompileProduct(CodeFeeders pCodeFeeders, CompilationOptions pOptions) {
		return new CompileProduct(pCodeFeeders);
	}

	/** Create a new CompileProduct to be used in the compilation */
	protected void setCompileProductDataFromOption(CompileProduct pCProduct, CompilationOptions pCCOptions) {
		if(pCProduct == null) return;
		// Only CompilationOptions.Simple is accept because it is only one we know how to handle
		if(!(pCCOptions instanceof CompilationOptions.Simple)) return;
		
		Set<String> Names = ((CompilationOptions.Simple)pCCOptions).getDataNames();
		if(Names == null) return;
		
		for(String DName : Names)
			pCProduct.setArbitraryData(DName, ((CompilationOptions.Simple)pCCOptions).getData(DName));
		
		return;
	}
	
	private TaskEntry TaskEntryInProgress  = null;
	
	/** Returns the index of task being run */
	final protected int getTaskNumberInProgress() {
		TaskEntry[] TEs = this.getTaskEntries();
		TaskEntry   TE  = this.getTaskEntryInProgress();
		for(int i = 0; i < TEs.length; i++) {
			if(TEs[i] == TE) return i;
		}
		return -1;
	}
	
	/** Returns the TaskEntry of task being run */
	final protected TaskEntry getTaskEntryInProgress() {
		return this.TaskEntryInProgress;
	}

	/** Compile from code feeders */
	final public CompileProduct compile(CodeFeeders pCodeFeeders) {
		return this.compile(pCodeFeeders, null);
	}
	/** Compile from code feeders */
	final public CompileProduct compile(CodeFeeders pCodeFeeders, CompilationOptions pOptions) {
		if(this.getTaskEntries() == null) return null;
		CompileProduct Context = this.newCompileProduct(pCodeFeeders, pOptions);
		this.setCompileProductDataFromOption(Context, pOptions);
		
		TaskEntry[] TEs = this.getTaskEntries();

		try {
			for(int i = 0; i < TEs.length; i++) {
				this.TaskEntryInProgress = TEs[i];
				if(this.TaskEntryInProgress == null) continue;	// TODOLATER - This should have throw an internal error
				TaskForCompiler CT = (TaskForCompiler)this.TaskEntryInProgress.getTask();
				if(CT == null) continue;						// TODOLATER - This should have throw an internal error
				
				try { // Perform task base on its kind
					if(CT.isCompilerTask()) {
						boolean IsTaskSuccess = this.doCompilerTask(Context, this.TaskEntryInProgress, pOptions);
						if(IsTaskSuccess && !Context.hasFatalErrMessage()) continue;
					} else if(CT.isCodeTask()) {
						boolean IsTaskSuccess = this.doCodeTask(Context, this.TaskEntryInProgress, pOptions);
						if(IsTaskSuccess && !Context.hasFatalErrMessage()) continue;
					} else if(CT.isFeederTask()) {
						boolean IsTaskSuccess = this.doFeederTask(Context, this.TaskEntryInProgress, pOptions);
						if(IsTaskSuccess && !Context.hasFatalErrMessage()) continue;
					}
				} catch(Exception E) {
					Context.reportError(String.format("There is an exception thrown while executing Task #%d.", i), E);
					break;
				}
				break;
			}
		} finally {
			this.TaskEntryInProgress = null;
		}
		return Context;
	}
	
	/** Do task for the compile task */
	final protected boolean doCompilerTask(CompileProduct pContext, TaskEntry pTE, CompilationOptions pOptions) {
		return this.doTask(pContext, pContext, pTE, pOptions);
	}
	/** Do task for the compile task */
	final protected boolean doCodeTask(CompileProduct pContext, TaskEntry pTE, CompilationOptions pOptions) {
		if(!pContext.startCode()) {
			// TODELETE No report any more since there is nothing wrong with having no code to compile  
			// Report error
			// pContext.reportFatalError("Unknown Error: The compile product is unable to start a task for Code", null);
			return false;
		}
		do {
			// Perform the task
			if(!this.doTask(pContext, pContext, pTE, pOptions)) return false;
		} while(pContext.nextCode());
		return true;
	}
	/** Do task for the compile task */
	final protected boolean doFeederTask(CompileProduct pContext, TaskEntry pTE, CompilationOptions pOptions) {
		if(!pContext.startFeeder()) {
			// TODELETE No report any more since there is nothing wrong with having no code to compile
			// Report error
			// pContext.reportFatalError("Unknown Error: The compile product is unable to start a task for CodeFeeder.", null);
			return false;
		}
		do {
			// Perform the task
			if(!this.doTask(pContext, pContext, pTE, pOptions)) return false;
		} while(pContext.nextFeeder());
		return true;
	}
	
}
