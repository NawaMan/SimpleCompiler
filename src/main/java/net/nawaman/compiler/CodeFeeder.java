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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import net.nawaman.regparser.Util;

/**
 * Code feeder provides source code based on name access
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
abstract public class CodeFeeder {
	
	static public final String UnknownCodeFeederName = "<UNKNOWN>";
	
	/** The base (path or collections) where we can looks for this code feeder */
	abstract public String getBase();
	/** The name of the code feeder (this name will not be used as reference by the compiler) */
	abstract public String getFeederName();
	/** The number of code file */
	abstract public int    getCodeCount();
	/** The name of each code file */
	abstract public String getCodeName(int pIndex);
	/** The name of each code file */
	abstract public Code getCode(int pIndex);
	/** The source code file */
	abstract public Code getCode(String pName);
	
	/** The source code file */
	final public CharSequence getSourceCode(String pName) {
		Code C = this.getCode(pName);
		return (C == null)?null:C.getSource();
	}
	/** The source code file */
	final public String getSourceCodeAsString(String pName) {
		CharSequence CS = this.getSourceCode(pName);
		return (CS == null)?null:CS.toString();
	}
	
	/** Returns this code feeder as a string (show list of all code names in the feeder) */
	@Override public String toString() {
		StringBuffer SB = new StringBuffer();
		SB.append("CodeFeeder ");
		SB.append(this.getFeederName());
		SB.append("(");
		SB.append(this.getCodeCount());
		SB.append(") {\n");
		for(int i = 0; i < this.getCodeCount(); i++) {
			String Name = this.getCodeName(i);
			SB.append("\t");
			SB.append(Name);
			SB.append("\n");
		}
		SB.append("}");
		return SB.toString();
	}
	
	/** Returns this code feeder as a string (show all codes in the feeder) */
	public String toDetail() {
		StringBuffer SB = new StringBuffer();
		SB.append("CodeFeeder ");
		SB.append(this.getFeederName());
		SB.append("(");
		SB.append(this.getCodeCount());
		SB.append(") {");
		for(int i = 0; i < this.getCodeCount(); i++) {
			String Name = this.getCodeName(i);
			SB.append("\n");
			for(int c = ((80 - Name.length() - 2)/2); --c >= 0; ) SB.append('*');
			SB.append(" ");
			SB.append(Name);
			SB.append(" ");
			for(int c = ((80 - Name.length() - 2)/2); --c >= 0; ) SB.append('*');
			SB.append("\n");
			for(int c = 80; --c >= 0; ) SB.append('-');
			SB.append("\n");
			SB.append(this.getCode(Name));
			SB.append("\n");
			for(int c = 80; --c >= 0; ) SB.append('*');
			SB.append("\n");
		}
		SB.append("}");
		return SB.toString();
	}
	
	// Predefine sub classes -----------------------------------------------------------------------
	
	/** Code feeder from a char sequence */
	static public class CFCharSequence extends CodeFeeder {

		public CFCharSequence(String pName, CharSequence pCode) {
			this(null, null, pName, pCode);
		}
		public CFCharSequence(String pFeederName, String pName, CharSequence pCode) {
			this(null, pFeederName, pName, pCode);
		}
		public CFCharSequence(String pBase, String pFeederName, String pName, CharSequence pCode) {
			if(pFeederName == null) pFeederName = CodeFeeder.UnknownCodeFeederName;
			if(pName       == null) throw new NullPointerException();
			if(pCode       == null) throw new NullPointerException();
			this.Base = (pBase == null) ? "" : pBase;
			this.Name = pFeederName;
			this.Code = new Code.Simple(pName, pCode);
		}
		
		final String Base;
		final String Name;
		final Code   Code;

		/**{@inherDoc}*/ @Override
		public String getBase() {
			return this.Base;
		}
		/**{@inherDoc}*/ @Override
		public String getFeederName() {
			return this.Name;
		}
		/**{@inherDoc}*/ @Override
		public int getCodeCount() {
			return 1;
		}
		/**{@inherDoc}*/ @Override
		public String getCodeName(int pIndex) {
			return ((pIndex == 0)&&(this.Code != null))?this.Code.getCodeName():null;
		}
		
		/**{@inherDoc}*/ @Override
		public Code getCode(int pIndex) {
			return (pIndex == 0)?this.Code:null;
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(String pName) {
			if((this.Code != null) && (this.Code.getCodeName() != null) && this.Code.getCodeName().equals(pName)) return this.Code;
			return null;
		}
	}
	
	/** Code feeder out of a folder */
	static public class CFCharSequences extends CodeFeeder {

		public CFCharSequences(String pName, HashMap<String, ? extends CharSequence> pCodes) {
			this("", pName, pCodes);
		}
		public CFCharSequences(String pBase, String pName, HashMap<String, ? extends CharSequence> pCodes) {
			if(pCodes == null) throw new NullPointerException();
			if(pName  == null) throw new NullPointerException();
			this.Base = (pBase == null) ? "" : pBase;
			this.Name = pName;
			
			this.CNames = new Vector<String>();
			this.Codes  = new Code[pCodes.size()];
			int I       = 0;
			for(String N : pCodes.keySet()) {
				this.CNames.add(N);
				this.Codes[I++] = new Code.Simple(N, pCodes.get(N));
			}
		}

		final String         Base;
		final String         Name;
		final Vector<String> CNames;
		final Code[]         Codes;

		/**{@inherDoc}*/ @Override
		public String getBase() {
			return this.Base;
		}
		/**{@inherDoc}*/ @Override
		public String getFeederName() {
			return this.Name;
		}
		/**{@inherDoc}*/ @Override
		public int getCodeCount() {
			return this.CNames.size();
		}
		/**{@inherDoc}*/ @Override
		public String getCodeName(int pIndex) {
			if((pIndex < 0) || (pIndex >= this.getCodeCount())) return null;
			return this.CNames.get(pIndex);
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(int pIndex)   {
			return this.getCode(this.CNames.get(pIndex));
			
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(String pName)   {
			if(pName == null) return null;
			int Index = this.CNames.indexOf(pName);
			if((Index < -1) || (Index >= this.CNames.size())) return null;
			return this.Codes[Index];
		}
	}
	
	/** Code feeder of a single file */
	static public class CFFile extends CodeFeeder {

		public CFFile(String pName) {
			this(pName, null);
		}
		public CFFile(String pName, String pPath) {
			if(pName == null) throw new NullPointerException();
			this.Name = pName;
			this.Path = (pPath == null) ? "" : pPath;
		}

		final String Name;
		final String Path;
		
		Code Code = null;

		/**{@inherDoc}*/ @Override
		public String getBase() {
			return this.Path;
		}
		/**{@inherDoc}*/ @Override
		public String getFeederName() {
			return this.Name;
		}
		/**{@inherDoc}*/ @Override
		public int getCodeCount() {
			return 1;
		}
		/**{@inherDoc}*/ @Override
		public String getCodeName(int pIndex) {
			return (pIndex == 0)?this.Name:null;
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(int pIndex)   {
			if(pIndex != 0) return null;
			
			if(this.Code != null) return this.Code;
			String TheName = this.getCodeName(pIndex);
			try {
				this.Code = new Code.Simple(TheName, Util.loadTextFile(this.Path + File.separator + TheName));
			} catch(IOException IOE) {
				throw new CompilationException(
						"Error reading the source file: `" + this.Path + File.separator + TheName + "`.", IOE);
			}
			return this.Code;
		}
		/**{@inherDoc}*/ @Override public Code getCode(String pName)   {
			if(!this.Name.equals(pName)) return null;
			return this.getCode(0);
		}
	}
	
	/** Code feeder out of a folder */
	static public class CFFolder extends CodeFeeder {
		
		public CFFolder(String pPathName, String pSuffix) {
			if(pPathName == null) throw new NullPointerException();
			if(pSuffix   == null) throw new NullPointerException();
			this.Name = pPathName;
			
			File P = new File(pPathName);
			if(!P.isDirectory()) throw new IllegalArgumentException("Invalid part name '"+pPathName+"'.");
			
			File[] Files = P.listFiles();
			Vector<String> FNames = new Vector<String>();
			for(File F : Files) {
				if(F.isDirectory()) continue;
				String FName = F.getName();
				if(!FName.endsWith(pSuffix)) continue;
				if(!F.canRead())             continue;
				FNames.add(FName);
			}
			this.CNames = (FNames.size() == 0)?null:new Vector<String>(FNames);
		}
		
		final String         Name;
		final Vector<String> CNames;
		
		Code[] Codes = null;

		/**{@inherDoc}*/ @Override
		public String getBase() {
			return "";
		}
		/**{@inherDoc}*/ @Override
		public String getFeederName() {
			return this.Name;
		}
		/**{@inherDoc}*/ @Override
		public int getCodeCount() {
			return (this.CNames == null)?0:this.CNames.size();
		}
		/**{@inherDoc}*/ @Override
		public String getCodeName(int pIndex) {
			if((pIndex < 0) || (pIndex >= this.getCodeCount())) return null;
			return this.CNames.get(pIndex);
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(int pIndex)   {
			return this.getCode(this.CNames.get(pIndex));			
		}
		/**{@inherDoc}*/ @Override
		public Code getCode(String pName)   {
			if(pName == null) return null;
			int Index = this.CNames.indexOf(pName);
			if(Index == -1) return null;
			
			if(this.Codes == null) this.Codes = new Code[this.CNames.size()];
			Code Code = this.Codes[Index];
			if(Code != null) return Code;
			
			try {
				Code = new Code.Simple(pName, Util.loadTextFile(this.Name + File.separator + pName));
				this.Codes[Index] = Code;
			} catch(IOException IOE) {
				throw new CompilationException("Error reading the source file: `" + this.Name + File.separator + pName + "`.", IOE);
			}
			return Code;
		}
	}
}