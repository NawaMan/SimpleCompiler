/*----------------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2008-2019 Nawapunth Manusitthipol. Implements with and for Sun Java 1.6 JDK.
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

/**
 * A collection of CodeFeeder used one single compilation.
 * 
 * CodeFeeders hold by this object does not have other relationship rather than one of them required another one to
 * compile. Each code feeder provides local namespaces to its code but not global. Meaning that if code in one feeder
 * refer to a another code (using the name), the compiler will look for it in that code feeder first then the other
 * feeders. Moreover, there is no way for a code to explicitly refer to another code in another feeder.
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
public class CodeFeeders extends CompilationData.Simple {

	/** Constructors from the code names */
	@SuppressWarnings("unchecked")
	public CodeFeeders(CodeFeeder ... pCodeFeeders) {
		if(pCodeFeeders == null) throw new NullPointerException();
		this.FeederDatas = new HashMap[pCodeFeeders.length];
		this.CodeNames   = new String[ pCodeFeeders.length][];
		this.CodeDatas   = new HashMap[pCodeFeeders.length][];
		
		// Duplicate the code name
		for(int i = pCodeFeeders.length; --i >= 0; ) {
			if(pCodeFeeders[i] == null) continue;
			this.FeederDatas[i]= new HashMap<String, Object>();
			this.FeederDatas[i].put(DataName_Feeder, pCodeFeeders[i]);
			this.CodeNames[i] = new String[pCodeFeeders[i].getCodeCount()];
			this.CodeDatas[i] = new HashMap[pCodeFeeders[i].getCodeCount()];
			for(int j = this.CodeNames[i].length; --j >= 0; ) {
				this.CodeNames[i][j] = pCodeFeeders[i].getCodeName(j);
				this.CodeDatas[i][j] = new HashMap<String, Object>();
				this.CodeDatas[i][j].put(DataName_Code,       pCodeFeeders[i].getCode(this.CodeNames[i][j]));
				this.CodeDatas[i][j].put(DataName_SourceCode, pCodeFeeders[i].getCode(this.CodeNames[i][j]).getSource());
			}
		}
	}
	
	/** Name of the data that actual feeder */
	static public final String DataName_Feeder     = "Feeder";
	/** Name of the code data that actual code */
	static public final String DataName_Code       = "Code";
	/** Name of the code data that actual source code */
	static public final String DataName_SourceCode = "Source";
	
	/** Returns the number of the feeder */
	@Override public int getFeederCount() {
		return super.getFeederCount();
	}
	
	/** Returns the code feeder at the index */
	public CodeFeeder getFeeder(int pFeederIndex) {
		return (CodeFeeder)this.getFeederData(pFeederIndex, DataName_Feeder);
	}
	
	/** Returns the code by the reference */
	public Code getCode(CodeRef pCodeRef) {
		return (Code)this.getCodeData(pCodeRef, DataName_Code);
	}
	/** Get the code data with the name */
	public Code getCode(int pFeederIndex, String pCodeName) {
		return (Code)this.getCodeData(pFeederIndex, pCodeName, DataName_Code);
	}

	/** Returns the code by the reference */
	public CharSequence getSourceCode(CodeRef pCodeRef) {
		return (CharSequence)this.getCodeData(pCodeRef, DataName_SourceCode);
	}
	/** Get the code data with the name */
	public CharSequence getSourceCode(int pFeederIndex, String pCodeName) {
		return (CharSequence)this.getCodeData(pFeederIndex, pCodeName, DataName_SourceCode);
	}
	
	/** Returns this code feeder as a string (show list of all code names in the feeder) */
	@Override public String toString() {
		StringBuffer SB = new StringBuffer();
		SB.append("CodeFeeders {\n");
		for(int i = 0; i < this.getFeederCount(); i++) {
			SB.append("\tCodeFeeder#");
			SB.append(i);
			SB.append(":\n");
			CodeFeeder CF = this.getFeeder(i);
			for(int j = 0; j < CF.getCodeCount(); j++) {
				SB.append("\t\t");
				SB.append(CF.getCodeName(j));	
			}
		}
		SB.append("\n}");
		return SB.toString();
	}
	/** Returns this code feeder as a string (show list of all code names in the feeder) */
	public String toDetail() {
		StringBuffer SB = new StringBuffer();
		SB.append("CodeFeeders {");
		SB.append("\n------------------------------------------------------------------------------------------------------------------------");
		SB.append("\n--------------------------------------------------------- List ---------------------------------------------------------");
		SB.append("\n------------------------------------------------------------------------------------------------------------------------");
		for(int i = 0; i < this.getFeederCount(); i++) {
			CodeFeeder CF = this.getFeeder(i);
			for(int j = 0; j < CF.getCodeCount(); j++) {
				SB.append("\n\tCodeFeeder#");
				SB.append(i);
				SB.append(":\n");
				SB.append("\t\t");
				SB.append(CF.getCodeName(j));
			}
		}
		SB.append("\n");
		SB.append("\n------------------------------------------------------------------------------------------------------------------------");
		SB.append("\n--------------------------------------------------------- Code ---------------------------------------------------------");
		SB.append("\n------------------------------------------------------------------------------------------------------------------------");
		SB.append("\n");
		for(int i = 0; i < this.getFeederCount(); i++) {
			SB.append("\tCodeFeeder#");
			SB.append(i);
			SB.append(":");
			CodeFeeder CF = this.getFeeder(i);
			for(int j = 0; j < CF.getCodeCount(); j++) {
				SB.append(CF.getCode(j).toDetail());
			}
			SB.append("\n************************************************************************************************************************");
		}
		SB.append("\n------------------------------------------------------------------------------------------------------------------------");
		SB.append("\n}");
		return SB.toString();
	}
}
