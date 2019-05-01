/*----------------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2008 Nawapunth Manusitthipol. Implements with and for Sun Java 1.6 JDK.
 *----------------------------------------------------------------------------------------------------------------------
 * LICENSE:
 * 
 * This file is part of Nawa's SimpleCompiler.
 * 
 * The project is a free software; you can redistribute it and/or modify it under the SIMILAR terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * You are only required to inform me about your modification and redistribution as or as part of commercial software
 * package. You can inform me via nawaman<at>gmail<dot>com.
 * 
 * The project is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * ---------------------------------------------------------------------------------------------------------------------
 */

package net.nawaman.compiler;

import java.util.HashMap;

import net.nawaman.compiler.CompilerMessage.MessageKind;
import net.nawaman.regparser.CompilationContext;
import net.nawaman.task.ProcessContext;

/**
 * The product of a compilation.
 * 
 * The Compile Product transparently direct the data access to a current feeder or code. In this way, the accessor does
 * not need to be aware what feeder or code is currently be compiled. It can ask for a data and CompileProduct will
 * redirect that.
 * 
 * The compile product also allow easy iteration of the current feeder or code.
 * 
 * In order to simulate single data collection (as net.nawaman.task.Process see it), Current Code Ref and Current Feeder Ref.
 * The Current Code will be iterate by the compiler (each iteration for each Task).
 * 
 * This class is call Compilation product because it manage data for the compilation and the result of the compilation
 * is one of those managed by this class.
 *
 * @author Nawapunth Manusitthipol
 */
public class CompileProduct extends CompilationData.Simple implements CompilationContext, ProcessContext {
	
	/** Create a single Code CompileProduct */
	public CompileProduct(String pCode) {
		this(new CodeFeeders(new CodeFeeder.CFCharSequence("TheFeeder", "TheCode", pCode)));
	}
	
	/** Create a single Code CompileProduct */
	public CompileProduct(String pFeederName, String pCodeName, String pCode) {
		this(new CodeFeeders(new CodeFeeder.CFCharSequence(pFeederName, pCodeName, pCode)));
	}
	
	@SuppressWarnings("unchecked")
	protected CompileProduct(CodeFeeders pTheInput) {
		if(pTheInput == null) throw new NullPointerException();
		this.FeederDatas = new HashMap[pTheInput.getFeederCount()];
		this.CodeNames   = new String[ pTheInput.getFeederCount()][];
		this.CodeDatas   = new HashMap[pTheInput.getFeederCount()][];

		this.CFeederRef = new FeederRef.Simple(0);
		this.CCodeRef   = new CodeRef.Share( this.CFeederRef, null);
		this.CCode      = new CompileDataRef(this.CCodeRef,   null);
		this.CFeeder    = new CompileDataRef(this.CFeederRef, null);
		
		// Duplicate the code name
		for(int i = pTheInput.getFeederCount(); --i >= 0; ) {
			this.FeederDatas[i]= new HashMap<String, Object>(pTheInput.FeederDatas[i]);
			this.CodeNames[i] = pTheInput.CodeNames[i].clone();
			this.CodeDatas[i] = new HashMap[this.CodeNames[i].length];
			for(int j = this.CodeNames[i].length; --j >= 0; ) {
				this.CodeDatas[i][j] = new HashMap<String, Object>();
				this.CodeDatas[i][j].put(CodeFeeders.DataName_Code,       pTheInput.CodeDatas[i][j].get(CodeFeeders.DataName_Code));
				this.CodeDatas[i][j].put(CodeFeeders.DataName_SourceCode, pTheInput.CodeDatas[i][j].get(CodeFeeders.DataName_SourceCode));
			}
		}
	}
	
	// Current Position ----------------------------------------------------------------------------
	
	private boolean isRunning = false;
	private boolean isCode    =  true;
	
	private CodeRef.Share    CCodeRef   = null;
	private FeederRef.Simple CFeederRef = null;
	private CompileDataRef   CCode      = null;
	private CompileDataRef   CFeeder    = null;
	
	private CompileDataRef ReUse_CodeRef     = new CompileDataRef();
	private CompileDataRef ReUse_ArbitaryRef = new CompileDataRef();
	
	private int            CCodeIndex = 0;
	private CompileDataRef CPos       = null;
	
	void reset() {
		this.CPos       = null;
		this.CCodeIndex =    0;
	}
	boolean startCode() {
		if(this.getFeederCount() == 0) return false;
		
		this.CPos                   = this.CCode;
		this.CFeederRef.FeederIndex =          0;
		this.CPos.DataName          =       null;
		this.CCodeIndex             =          0;
		
		// Ensure that the first feeder has code
		while(this.getCodeCount(this.CFeederRef.FeederIndex) == 0) this.CFeederRef.FeederIndex++;
		
		this.CCodeRef.CodeName = this.getCodeName(this.CFeederRef.FeederIndex, this.CCodeIndex);

		this.isRunning = true;
		this.isCode    = true;
		return true;
	}
	boolean startFeeder() {
		if(this.getFeederCount() == 0) return false;
		this.CPos                   = this.CFeeder;
		this.CFeederRef.FeederIndex =            0;
		this.CPos.DataName          =         null;
		
		this.isRunning =  true;
		this.isCode    = false;
		return true;
	}
	
	boolean nextCode() {
		if(!this.isRunning) return false;
		if(!this.isCode)    return false;	// TODOLATER - Should we throw an internal error
		this.CCodeIndex++;
		if(this.CCodeIndex >= this.getCodeCount(this.CFeederRef.FeederIndex)) {
			this.CFeederRef.FeederIndex++;
			if(this.getCurrentFeederIndex() >= ((this.FeederDatas == null)?0:this.FeederDatas.length)) {
				this.isRunning = false;
				return false;
			}
			this.CCodeIndex = 0;
		}
		this.CCodeRef.CodeName = this.getCodeName(this.CFeederRef.FeederIndex, this.CCodeIndex);
		return true;
	}
	boolean nextFeeder() {
		if(!this.isRunning) return false;
		if(this.isCode)     return false;	// TODOLATER - Should we throw an internal error
		this.CFeederRef.FeederIndex++;
		if(this.getCurrentFeederIndex() >= ((this.FeederDatas == null)?0:this.FeederDatas.length)) {
			this.isRunning = false;
			return false;
		}
		return true;
	}
	
	public int getCurrentFeederIndex() {
		if(!this.isRunning) return -1;
		return this.CFeederRef.FeederIndex;
	}
	public String getCurrentFeederName() {
		CodeFeeder CF = this.getCurrentFeeder();
		if(CF == null) return null;
		return CF.getFeederName();
	}
	public String getCurrentCodeName() {
		if(!this.isRunning) return null;
		if(!this.isCode)    return null;
		return ((CodeRef)this.CPos.TargetRef).getCodeName();
	}
	
	CompileDataRef getTheRef(net.nawaman.task.DataRef pRef) {
		if(pRef instanceof DataRef) {	// Simulating
			if(!this.isRunning) return null;
			String DName = ((DataRef)pRef).DataName;
			// Specific Data
			if((DName.length() >= 2) && (DName.charAt(1) == '.')) {
				char FirstC = DName.charAt(0);
				DName = DName.substring(2);
				switch(FirstC) {
					case 'C':{	// Force to Code
						if(this.isCode) break;
						return null;	// No code but want to access code
					}
					case 'F':{	// Force to CodeFeeder
						if(this.isCode) {
							this.ReUse_CodeRef.TargetRef = this.CFeederRef;
							this.ReUse_CodeRef.DataName  = DName;
							return this.ReUse_CodeRef;	// Force to code
						}
						break;	// Else, just usual
					}
					case 'A':{	// Force to Arbitrary
						this.ReUse_ArbitaryRef.DataName = DName;
						return this.ReUse_ArbitaryRef;
					}
				} 
			}
			this.CPos.DataName = DName;
			return this.CPos;
		} else if(pRef instanceof CompileDataRef) {	// Direct 
			return (CompileDataRef)pRef;
		}
		return null;
	}
	
	/** Check if this compile product is being iterated (in the compilation) */
	public boolean isRunning() {
		return this.isRunning;
	}
	/** Check if this compile product is being iterated by code (in the compilation) */
	public boolean isCode() {
		return this.isRunning && this.isCode;
	}
	/** Check if this compile product is being iterated by code feeder (in the compilation) */
	public boolean isFeeder() {
		return this.isRunning && !this.isCode;
	}
	
	/** Returns the Current feeder */
	public CodeFeeder getCurrentFeeder() {
		if(!this.isRunning) return null;
		int I = this.CFeederRef.FeederIndex;
		return (CodeFeeder)this.getFeederData(I, CodeFeeders.DataName_Feeder);
	}
	/** Returns the current code */
	public Code getCurrentCode() {
		if(!this.isRunning) return null;
		if(!this.isCode)    return null;
		return (Code)this.getCodeData(
					this.CCodeRef.getFeederIndex(),
					this.CCodeRef.getCodeName(),
					CodeFeeders.DataName_Code);
	}
	
	public int[] getLocationAsColRow(int pPosition) {
		if(!this.isRunning) return null;
		if(this.isCode) {
			Code C = this.getCurrentCode();
			if(C == null) return null;
			return new int[] { C.getColOf(pPosition), C.getLineNumberOf(pPosition) };
		}
		return null;
	}
	public String getLocationAsString(int pPosition) {
		if(!this.isRunning) return null;
		if(this.isCode) {
			CodeFeeder CF = (CodeFeeder)this.getFeederData(this.CCodeRef.getFeederIndex(), CodeFeeders.DataName_Feeder);
			Code       C  = this.getCurrentCode();
			if(C == null) return null;			
			return String.format("<%s%s> at %s%s",
				(((CF == null)||(CF.getFeederName() == CodeFeeder.UnknownCodeFeederName))?"":CF.getFeederName() + " -> "),
				C.getCodeName(),
				((pPosition == -1)?"(x,x)":C.getCodePosition(pPosition)),
				((pPosition == -1)?""     :C.getCodePositionByCursor(pPosition))
			);
		} else {
			CodeFeeder CF = (CodeFeeder)this.getFeederData(this.CCodeRef.getFeederIndex(), CodeFeeders.DataName_Feeder);
			return (CF == null)?null:CF.toString();
		}
	}
	
	// Error Report ----------------------------------------------------------------------------------------------------
	
	/** Returns the problem messages formated in a way that it can be retrieved back */
	protected String getProblemMessage(String pMessage, Throwable pCause, int pPosition) {
		return String.format("%s:\nNear %s", pMessage, this.getLocationAsString(pPosition));
	}

	/** Returns the problem messages formated in a way that it can be retrieved back */
	protected String getProblemMessage(String pMessage, Throwable pCause, int pCol, int pRow) {
		return this.getProblemMessage(pMessage, pCause, this.getCurrentCode().getNearestValidPositionOf(pCol, pRow));
	}
	
	/** Reports a message in a specific format that each information can be retrieved back */
	public void reportMessage(String pMessage, Throwable pCause, int pPosition) {
		String Message = this.getProblemMessage(pMessage, pCause, pPosition);
		super.reportMessage(new CompilerMessage(MessageKind.MESSAGE, Message, pCause, this, pPosition));
	}
	/** Reports a warning in a specific format that each information can be retrieved back */
	public void reportWarning(String pMessage, Throwable pCause, int pPosition) {
		String Message = this.getProblemMessage(pMessage, pCause, pPosition);
		super.reportMessage(new CompilerMessage(MessageKind.WARNING, Message, pCause, this, pPosition));
	}
	/** Reports a error in a specific format that each information can be retrieved back */
	public void reportError(String pMessage, Throwable pCause, int pPosition) {
		String Message = this.getProblemMessage(pMessage, pCause, pPosition);
		super.reportMessage(new CompilerMessage(MessageKind.ERROR, Message, pCause, this, pPosition));
	}
	/** Reports a fatal error in a specific format that each information can be retrieved back */
	public void reportFatalError(String pMessage, Throwable pCause, int pPosition) {
		String Message = this.getProblemMessage(pMessage, pCause, pPosition);
		super.reportMessage(new CompilerMessage(MessageKind.FATALERROR, Message, pCause, this, pPosition));
	}

	/** Reports a message in a specific format that each information can be retrieved back */
	public void reportMessage(String pMessage, Throwable pCause, int pCol, int pRow) {
		int    Position = this.getCurrentCode().getNearestValidPositionOf(pCol, pRow);
		String Message  = this.getProblemMessage(pMessage, pCause, Position); 
		super.reportMessage(new CompilerMessage(MessageKind.MESSAGE, Message, pCause, this, Position));
	}
	/** Reports a warning in a specific format that each information can be retrieved back */
	public void reportWarning(String pMessage, Throwable pCause, int pCol, int pRow) {
		int    Position = this.getCurrentCode().getNearestValidPositionOf(pCol, pRow);
		String Message  = this.getProblemMessage(pMessage, pCause, Position); 
		super.reportMessage(new CompilerMessage(MessageKind.WARNING, Message, pCause, this, Position));
	}
	/** Reports a error in a specific format that each information can be retrieved back */
	public void reportError(String pMessage, Throwable pCause, int pCol, int pRow) {
		int    Position = this.getCurrentCode().getNearestValidPositionOf(pCol, pRow);
		String Message  = this.getProblemMessage(pMessage, pCause, Position); 
		super.reportMessage(new CompilerMessage(MessageKind.ERROR, Message, pCause, this, Position));
	}	
	/** Reports a fatal error in a specific format that each information can be retrieved back */
	public void reportFatalError(String pMessage, Throwable pCause, int pCol, int pRow) {
		int    Position = this.getCurrentCode().getNearestValidPositionOf(pCol, pRow);
		String Message  = this.getProblemMessage(pMessage, pCause, Position); 
		super.reportMessage(new CompilerMessage(MessageKind.FATALERROR, Message, pCause, this, Position));
	}
	
	/** Retrieves the message from a formated problem message */
	static public String RetrieveMessage(String pProblemMessage) {
		if(pProblemMessage == null) return null;
		int Index = pProblemMessage.indexOf(":\nNear <");
		if(Index == -1) return null;
		return pProblemMessage.substring(0, Index);
	}

	/** Retrieves the message from a formated problem message */
	static public String[] RetrieveCodeName(String pProblemMessage) {
		if(pProblemMessage == null) return null;
		int IndexBegin  = pProblemMessage.indexOf(":\nNear <");          if(IndexBegin == -1) return null;
		int IndexEnd    = pProblemMessage.indexOf("> at (", IndexBegin); if(IndexEnd   == -1) return null;

		String FullName = pProblemMessage.substring(IndexBegin, IndexEnd);
		int IndexMiddle = FullName.indexOf(" -> "); if(IndexEnd   == -1) return new String[] { null, FullName };
		
		return new String[] {
			FullName.substring(0, IndexMiddle),
			FullName.substring(IndexMiddle + " -> ".length())
		};
	}

	/** Retrieves the message from a formated problem message */
	static public int[] RetrievePosition(String pProblemMessage) {
		if(pProblemMessage == null) return null;
		int IndexBegin = pProblemMessage.indexOf(":\nNear <");          if(IndexBegin == -1) return null;
		IndexBegin     = pProblemMessage.indexOf("> at (", IndexBegin); if(IndexBegin == -1) return null;
		int IndexEnd   = pProblemMessage.indexOf(")",      IndexBegin); if(IndexEnd   == -1) return null;
		
		String PosStr = pProblemMessage.substring(IndexBegin + "> at (".length(), IndexEnd);
		int Index = PosStr.indexOf(",");
		if(Index == -1) return null;
		String ColStr = PosStr.substring(0, Index);
		String RowStr = PosStr.substring(Index + ",".length());
		return new int[] { Integer.parseInt(ColStr.trim()), Integer.parseInt(RowStr.trim())};
	}
	
	// Simulate single dimension data collection ---------------------------------------------------

	/** Checks if this process data contains the data referred by the ref  */
	@Override public boolean contains(net.nawaman.task.DataRef pRef) {
		if(pRef == null) return false;
		CompileDataRef Ref = this.getTheRef(pRef);
		if(Ref == null) return false;
		return super.contains(Ref);
	}
	/** Checks if this process data contains the data referred by the ref  */
	@Override public boolean contains(CompileDataRef pRef) {
		return this.contains((net.nawaman.task.DataRef)pRef);
	}
	
	/** Returns the data at the reference */
	@Override public Object getData(net.nawaman.task.DataRef pRef) {
		if(pRef == null) return null;
		CompileDataRef Ref = this.getTheRef(pRef);
		if(Ref == null) return null;
		return super.getData(Ref);
	}
	/** Returns the data at the reference */
	@Override public Object getData(CompileDataRef pRef) {
		return this.getData((net.nawaman.task.DataRef)pRef);
	}
	/** Change the data at the reference - Returns if that success */
	@Override public Object setData(net.nawaman.task.DataRef pRef, Object pValue) {
		CompileDataRef Ref = this.getTheRef(pRef);
		return super.setData(Ref, pValue);
	}	// Only allowed when it is running.
	/** Change the data at the reference - Returns if that success */
	@Override public Object setData(CompileDataRef pRef, Object pValue) {
		return this.setData((net.nawaman.task.DataRef)pRef, pValue);
	}

	/** Get the code data with the name */
	public Object getCurrentFeederData(String pDataName) {
		return this.getFeederData(this.getCurrentFeederIndex(), pDataName);
	}
	/** Get the code data with the name */
	public Object setCurrentFeederData(String pDataName, Object pValue) {
		return this.setFeederData(this.getCurrentFeederIndex(), pDataName, pValue);
	}

	/** Get the code data with the name */
	public Object setCurrentCodeData(String pDataName, Object pValue) {
		return this.setCodeData(this.getCurrentFeederIndex(), this.getCurrentCodeName(), pDataName, pValue);
	}
	/** Get the code data with the name */
	public Object getCurrentCodeData(String pDataName) {
		return this.getCodeData(this.getCurrentFeederIndex(), this.getCurrentCodeName(), pDataName);
	}
	
	/** Returns this code feeder as a string (show list of all code names in the feeder) */
	@Override public String toString() {
		StringBuffer SB = new StringBuffer();
		SB.append("CompileProduct ===================================================================================");
		for(int i = 0; i < this.getFeederCount(); i++) {
			SB.append("\nCodeFeeder#");
			SB.append(i);
			SB.append(" => ");
			SB.append(this.FeederDatas[i].keySet().toString());
			SB.append(" {\n");
			
			int MaxLength = 0;
			for(int j = 0; j < this.getCodeCount(i); j++) {
				int S = this.getCodeName(i, j).length();
				if(S > MaxLength) MaxLength = S;
			}
			MaxLength += 2;
			
			for(int j = 0; j < this.getCodeCount(i); j++) {
				StringBuilder CSB = new StringBuilder();
				CSB.append("`").append(this.getCodeName(i, j)).append("`");
				while(CSB.length() < MaxLength) CSB.append(' ');
				
				SB.append("\tCode: ");
				SB.append(CSB);
				SB.append(" => ");
				SB.append(this.CodeDatas[i][j].keySet().toString());
				SB.append("\n");
			}
			SB.append("}");
		}
		SB.append("\nArbitrary Datas => ");
		SB.append((this.AbitaryDataNames == null)?"[]":this.AbitaryDataNames.toString());
		SB.append("\n");
		SB.append("--------------------------------------------------------------------------------------------------");
		SB.append("\n");
		SB.append("Messages: ");
		String Str = this.getMessagesToString();
		SB.append(Str);
		if(!Str.endsWith("\n")) SB.append("\n");
		SB.append("==================================================================================================");
		return SB.toString();
	}

} 