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

import java.util.*;

import net.nawaman.compiler.CompilerMessage.MessageKind;
import net.nawaman.task.*;

/**
 * Abstract collection for data that compiler handle.
 * 
 * The compiler handle data in many forms: input code, adjusted codes, parse trees, outputs. The data are differ but its
 * structure are similar. The data may also be attached to different artifact in the compilation like the code feeders,
 * the codes or may not be attached to any thing. CompilationData separate the data based on what it is attached (or
 * belonged to) and allow the data to be accessed using one unified reference.
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
@SuppressWarnings("unchecked")
abstract public class CompilationData implements ProcessDatas {

	static final HashMap<String, Object>[]   EmptyFeederDatas = new HashMap[0];
	static final String[][]                  EmptyCodeNames   = new String[0][];
	static final HashMap<String, Object>[][] EmptyCodeDatas   = new HashMap[0][];
	
	/** Local constructors */
	CompilationData() {}
	
	/** Returns the feeder data array */ abstract protected HashMap<String, Object>[]   getFeederDatas();
	/** Returns the code names array */  abstract protected String[][]                  getCodeNames();
	/** Returns the code data array */   abstract protected HashMap<String, Object>[][] getCodeDatas();
	
	/** Returns if the code data array is created (create it if it does not and returns if that is a success) */
	abstract protected boolean ensureCodeDatas();
	
	/** Returns the arbitrary data name array */ abstract protected Vector<String> getArbitraryDataNames();
	/** Returns the arbitrary data array */      abstract protected Vector<Object> getArbitraryDatas();
	
	/** Returns if the arbitrary data name array is created (create it if it does not and returns if that is a success) */
	abstract protected boolean ensureArbitraryDatas();
	
	// Satisfy ProcessDatas ------------------------------------------------------------------------

	/** Checks if this process data contains the data referred by the ref  */
	public boolean contains(net.nawaman.task.DataRef pRef) {
		if(!(pRef instanceof CompileDataRef)) throw new ClassCastException("The reference must be nawa.regcompile.DataRef.");
		return this.contains((CompileDataRef)pRef);
	}
	/** Checks if this process data contains the data referred by the ref  */
	public boolean contains(CompileDataRef pRef) {
		if(pRef.isRefToArbitary())
			return (this.getArbitraryDataNames() == null)?false:(this.getArbitraryDataNames().indexOf(pRef.DataName) != -1);
		
		int FIndex = 
			(pRef.getFeederRef() != null)
				?pRef.getFeederRef().getFeederIndex()
				:pRef.getCodeRef().getFeederIndex();
				
		//if(FDs == null) return false;
		if(pRef.isRefToFeeder()) {
			HashMap<String, Object>[] FDs = this.getFeederDatas();
			if(FIndex >= FDs.length) return false;
			HashMap<String, Object> FD = FDs[FIndex];
			if(FD == null) return false;
			return FD.containsKey(pRef.getDataName());
			
		} else if(pRef.isRefToCode()) {
			String     CN  = pRef.getCodeRef().getCodeName();
			String[][] CNs = this.getCodeNames();
			if(CNs == null) return false;
			String[] Ns = CNs[FIndex];
			if(Ns == null) return false;
			for(int i = Ns.length; --i >= 0; ) {
				String N = Ns[i];
				if(N == null) continue;
				if(CN.equals(N)) {
					HashMap<String, Object> Datas = this.getCodeDatas()[FIndex][i];
					return (Datas != null) && Datas.containsKey(pRef.getDataName());
				}
			}
			return false;
			
		} else {
			// Not any of this, so return false;
			return false;
		}
	}
	
	/** Returns the data at the reference */
	public Object getData(net.nawaman.task.DataRef pRef) {
		if(!(pRef instanceof CompileDataRef))
			throw new ClassCastException("The reference must be CompileDataRef.");
		return this.getData((CompileDataRef)pRef);
	}
	/** Returns the data at the reference */
	public Object getData(CompileDataRef pRef) {
		if(pRef.isRefToCode())     return this.getCodeData(  pRef.getCodeRef(),   pRef.getDataName());
		if(pRef.isRefToFeeder())   return this.getFeederData(pRef.getFeederRef(), pRef.getDataName());
		if(pRef.isRefToArbitary()) return this.getArbitraryData(pRef.getDataName());
		return null;
	}
	/** Change the data at the reference - Returns if that success */
	public Object setData(net.nawaman.task.DataRef pRef, Object pValue) {
		if(!(pRef instanceof CompileDataRef))
			throw new ClassCastException("The reference must be CompileDataRef.");
		return this.setData((CompileDataRef)pRef, pValue);
	}
	/** Change the data at the reference - Returns if that success */
	public Object setData(CompileDataRef pRef, Object pValue) {
		if(pRef.isRefToCode())     return this.setCodeData(  pRef.getCodeRef(),   pRef.getDataName(), pValue);
		if(pRef.isRefToFeeder())   return this.setFeederData(pRef.getFeederRef(), pRef.getDataName(), pValue);
		if(pRef.isRefToArbitary()) return this.setArbitraryData(pRef.getDataName(), pValue);
		return pValue;
	}

	/** Freeze the whole container  - Returns if success */
	public boolean freeze() {
		// TODOLATER - This is not support at the moment.
		return true;
	}
	
	/** Freeze that data - Returns if success */
	public boolean freezeData(CompileDataRef pRef) {
		// TODOLATER - This is not support at the moment.
		return true;
	}
	/** Freeze that data - Returns if success */
	public boolean freezeData(net.nawaman.task.DataRef pRef) {
		// TODOLATER - This is not support at the moment.
		return true;
	}

	// Message -----------------------------------------------------------------------
	
	Vector<CompilerMessage> Messages = null;
	
	int ErrMessageCount  = 0;
	int FErrMessageCount = 0;
	
	public boolean hasErrMessage()      { return this.ErrMessageCount  != 0; }
	public boolean hasFatalErrMessage() { return this.FErrMessageCount != 0; }
	
	/** Returns the number of compilation message */
	public int getMessageCount() { return (this.Messages == null)?0:this.Messages.size(); }
	/** Returns the compilation message at the index */
	public CompilerMessage getMessage(int I) { return ((I < 0) || (I >= this.getMessageCount()))?null:this.Messages.get(I); }
	
	/** Returns the error message count */
	public int getErrorMessageCount() {
		return this.ErrMessageCount;
	}
	/** Returns the error message count */
	public int getFatalErrorMessageCount() {
		return this.FErrMessageCount;
	}
	
	/** Convert messages to string */
	public String getMessagesToString() {
		// Display Error -------------------------------------------------------------------------------------------
		if(this.getMessageCount() != 0) {
			StringBuffer SB = new StringBuffer();
			SB.append(this.getMessageCount()).append(" message(s):");
			for(int i = 0; i < this.getMessageCount(); i++) {
				SB.append("\nMessage #");
				SB.append(i);
				SB.append(": ");
				CompilerMessage M = this.Messages.get(i);
				SB.append(M);
			}
			return SB.toString();
		} else return "0 message(s)\n";
		
	}
	
	// Error Report ----------------------------------------------------------------------------------------------------

	public void reportMessage(CompilerMessage pCMessage) {
		if(pCMessage     == null) return;
		if(this.Messages == null) this.Messages = new Vector<CompilerMessage>();
		if(pCMessage.isError())        this.ErrMessageCount++;
		if(pCMessage.isFatalError()) { this.FErrMessageCount++; this.ErrMessageCount++; }
		this.Messages.add(pCMessage);
	}

	public void reportMessage(String pMessage, Throwable pCause) {
		if(this.Messages == null) this.Messages = new Vector<CompilerMessage>();
		this.Messages.add(new CompilerMessage(MessageKind.MESSAGE, pMessage, pCause));
	}

	public void reportWarning(String pMessage, Throwable pCause) {
		if(this.Messages == null) this.Messages = new Vector<CompilerMessage>();
		this.Messages.add(new CompilerMessage(MessageKind.WARNING, pMessage, pCause));
	}

	public void reportError(String pMessage, Throwable pCause) {
		if(this.Messages == null) this.Messages = new Vector<CompilerMessage>();
		this.ErrMessageCount++;
		this.Messages.add(new CompilerMessage(MessageKind.ERROR, pMessage, pCause));
	}

	public void reportFatalError(String pMessage, Throwable pCause) {
		if(this.Messages == null) this.Messages = new Vector<CompilerMessage>();
		this.ErrMessageCount++;
		this.FErrMessageCount++;
		this.Messages.add(new CompilerMessage(MessageKind.FATALERROR, pMessage, pCause));
	}
	
	// Local lock ------------------------------------------------------------------------------------------------------
	
	/** A lock to limit the interface to be used only internally */
	static class LocalLock {}
	
	public LocalLock getLocalLock() { return null; }
	
	// SubClass --------------------------------------------------------------------------------------------------------
	
	/** A single self contain compilation data */
	static public class Simple extends CompilationData {
		
		HashMap<String, Object>[]   FeederDatas = EmptyFeederDatas;
		String[][]                  CodeNames   = EmptyCodeNames;
		HashMap<String, Object>[][] CodeDatas   = EmptyCodeDatas;

		Vector<String> AbitaryDataNames = null;
		Vector<Object> AbitaryDatas     = null;
	
		/**{@inheritDoc}*/ @Override
		protected HashMap<String, Object>[] getFeederDatas() {
			return this.FeederDatas;
		}
		/**{@inheritDoc}*/ @Override
		protected String[][] getCodeNames()   {
			return this.CodeNames;
		}
		/**{@inheritDoc}*/ @Override
		protected HashMap<String, Object>[][] getCodeDatas() {
			return this.CodeDatas;
		}
	
		/**{@inheritDoc}*/ 
		@Override protected boolean ensureCodeDatas() {
			if(this.CodeDatas == null) this.CodeDatas = new HashMap[this.getFeederCount()][];
			return true;
		}

		/**{@inheritDoc}*/ @Override
		protected Vector<String> getArbitraryDataNames() {
			return this.AbitaryDataNames;
		}
		/**{@inheritDoc}*/ @Override
		protected Vector<Object> getArbitraryDatas() {
			return this.AbitaryDatas;
		}
	
		/**{@inheritDoc}*/ 
		@Override protected boolean ensureArbitraryDatas() {
			if(this.AbitaryDataNames == null) {
				this.AbitaryDataNames = new Vector<String>();
				this.AbitaryDatas     = new Vector<Object>();
			}
			return true;
		}
	}
	
	/** Sharable is a mark interface to mark a Compilation that it can be shared. */
	static public interface Linkable {
		public LocalLock getLocalLock();
	}
	
	/** A compilation data that links to other compilation data */
	static public class Link extends CompilationData {
		
		public Link(Linkable pLinked) {
			if(pLinked == null) throw new NullPointerException();
			this.Linked = (CompilationData)pLinked;
		}
		
		private CompilationData Linked;
	
		/**{@inheritDoc}*/ @Override
		protected HashMap<String, Object>[] getFeederDatas() {
			return this.Linked.getFeederDatas();
		}
		/**{@inheritDoc}*/ @Override
		protected String[][] getCodeNames() {
			return this.Linked.getCodeNames();
		}
		/**{@inheritDoc}*/ @Override
		protected HashMap<String, Object>[][] getCodeDatas() {
			return this.Linked.getCodeDatas();
		}
		/**{@inheritDoc}*/ @Override
		protected boolean ensureCodeDatas() {
			return this.Linked.ensureCodeDatas();
		}
		/**{@inheritDoc}*/ @Override
		protected Vector<String> getArbitraryDataNames() {
			return this.Linked.getArbitraryDataNames();
		}
		/**{@inheritDoc}*/ @Override
		protected Vector<Object> getArbitraryDatas() {
			return this.Linked.getArbitraryDatas();
		}
		
		/**{@inheritDoc}*/ @Override
		protected boolean ensureArbitraryDatas() {
			return this.Linked.ensureArbitraryDatas();
		}
		
	}
	
	/** CompilationData that holds its own data by derive the structure (number of feeder and codes) from others */
	static public class Derive extends CompilationData.Simple {
		
		public Derive(CompilationData pDerived) {
			if(pDerived == null) throw new NullPointerException();
			this.FeederDatas = new HashMap[pDerived.getFeederCount()];
			this.CodeNames   = new String[ pDerived.getFeederCount()][];
			this.CodeDatas   = new HashMap[pDerived.getFeederCount()][];

			// Duplicate the code name
			for(int i = pDerived.getFeederCount(); --i >= 0; ) {
				this.CodeNames[i] = new String[   pDerived.getCodeCount(i)];
				for(int j = this.CodeNames[i].length; --j >= 0; ) {
					this.CodeNames[i][j] = pDerived.getCodeName(i, j);
				}
			}
		}
	}
	
	// Services --------------------------------------------------------------------------------------------------------
	
	/** Returns the number of the feeder */
	public int getFeederCount() {
		return (this.getFeederDatas() == null)?0:this.getFeederDatas().length;
	}

	/** Add or change the value of the feeder data associated with pDataName */
	public Object setFeederData(FeederRef pFeederRef, String pDataName, Object pValue) {
		if(pFeederRef == null) throw new NullPointerException();
		return this.setFeederData(pFeederRef.getFeederIndex(), pDataName, pValue);
	}
	/** Add or change the value of the feeder data associated with pDataName */
	public Object setFeederData(int pFeederIndex, String pDataName, Object pValue) {
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount()))
			throw new IllegalArgumentException("Feeder index is out of length.");
		
		HashMap<String, Object> FDs = this.getFeederDatas()[pFeederIndex];
		if(FDs == null) {
			FDs = new HashMap<String, Object>();
			this.getFeederDatas()[pFeederIndex] = FDs;
		}
		FDs.put(pDataName, pValue);
		return pValue;
	}
	/** Get the feeder data with the name */
	public Object getFeederData(FeederRef pFeederRef, String pDataName) {
		if(pFeederRef == null) return null;
		return this.getFeederData(pFeederRef.getFeederIndex(), pDataName);
	}
	/** Get the feeder data with the name */
	public Object getFeederData(int pFeederIndex, String pDataName) {
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount())) return null;
		HashMap<String, Object> FDs = this.getFeederDatas()[pFeederIndex];
		return (FDs == null)?null:FDs.get(pDataName);
	}
	
	/** Returns the number of code in the feeder */
	public int getCodeCount(int pFeederIndex) {
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount())) return -1;
		String[] Names = this.getCodeNames()[pFeederIndex];
		return (Names == null)?0:Names.length;
	}
	
	/** Returns the code name from the feeder and the index */
	public String getCodeName(int pFeederIndex, int pCodeIndex) {
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount())) return null;
		String[] Names = this.getCodeNames()[pFeederIndex];
		if(Names == null) return null;
		if((pCodeIndex < 0) || (pCodeIndex >= Names.length)) return null;
		return Names[pCodeIndex];
	}
	
	/** Add or change the value of the code data associated with pDataName */
	public Object setCodeData(CodeRef pCodeRef, String pDataName, Object pValue) {
		return this.setCodeData(pCodeRef.getFeederIndex(), pCodeRef.getCodeName(), pDataName, pValue);
	}
	/** Add or change the value of the code data associated with pDataName */
	public Object setCodeData(int pFeederIndex, String pCodeName, String pDataName, Object pValue) {
		if((pCodeName == null) || (pDataName == null)) throw new NullPointerException();
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount()))
			throw new IllegalArgumentException("Feeder index is out of length.");
		
		String[] Names = this.getCodeNames()[pFeederIndex];
		for(int i = Names.length; --i >= 0; ) {
			if(!pCodeName.equals(Names[i])) continue;
			
			this.ensureCodeDatas();
			
			HashMap<String, Object>[] CDs_s = this.getCodeDatas()[pFeederIndex];
			if(CDs_s == null) {
				CDs_s = new HashMap[Names.length];
				this.getCodeDatas()[pFeederIndex] = CDs_s;
			}
			
			HashMap<String, Object> CDs = CDs_s[i];
			if(CDs == null) {
				CDs = new HashMap<String, Object>();
				CDs_s[i] = CDs;
			}
			CDs.put(pDataName, pValue);
			return true;
		}
		return pValue;
	}
	/** Get the code data with the name */
	public Object getCodeData(CodeRef pCodeRef, String pDataName) {
		return this.getCodeData(pCodeRef.getFeederIndex(), pCodeRef.getCodeName(), pDataName);
	}
	/** Get the code data with the name */
	public Object getCodeData(int pFeederIndex, String pCodeName, String pDataName) {
		if(pCodeName == null) return null;
		if(pDataName == null) return null;
		if((pFeederIndex < 0) || (pFeederIndex >= this.getFeederCount())) return null;
		String[] Names = this.getCodeNames()[pFeederIndex];
		for(int i = Names.length; --i >= 0; ) {
			if(!pCodeName.equals(Names[i])) continue;
			
			if(this.getCodeDatas() == null) return null;
			HashMap<String, Object>[] CDs_s = this.getCodeDatas()[pFeederIndex];
			if(CDs_s == null) return null;
			
			HashMap<String, Object> CDs = CDs_s[i];
			return (CDs == null)?null:CDs.get(pDataName);
		}
		return null;
	}
	
	/** Returns the number of the arbitrary data */
	public int getArbitraryDataCount() {
		Vector<String> ANames = this.getArbitraryDataNames();
		return (ANames == null)?0:ANames.size();
	}
	
	public String getArbitraryDataName(int pIndex) {
		if((pIndex < 0) || (pIndex >= this.getArbitraryDataCount())) return null;
		return this.getArbitraryDataNames().get(pIndex);
	}
	
	/** Adds or changes the value of a arbitrary data */
	public Object setArbitraryData(String pName, Object pValue) {
		this.ensureArbitraryDatas();
		Vector<String> ANames = this.getArbitraryDataNames();
		int Index = ANames.indexOf(pName);
		if(Index != -1) {
			this.getArbitraryDatas().set(Index, pValue);
		} else {
			this.getArbitraryDataNames().add(pName);
			this.getArbitraryDatas()    .add(pValue);
		}
		return pValue;
	}
	
	/** Returns the arbitrary data */
	public Object getArbitraryData(int pIndex) {
		if((pIndex < 0) || (pIndex >= this.getArbitraryDataCount())) return null;
		return this.getArbitraryDatas().get(pIndex);
	}
	/** Returns the arbitrary data */
	public Object getArbitraryData(String pName) {
		if(pName                        == null) return null;
		if(this.getArbitraryDataNames() == null) return null;
		int Index = this.getArbitraryDataNames().indexOf(pName);
		return this.getArbitraryData(Index);
	}
}
