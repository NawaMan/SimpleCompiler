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

import java.io.*;
import java.util.*;

/**
 * Source code - It handle new line location and coordinate calculation
 *
 * @author Nawapunth Manusitthipol
 */
abstract public class Code implements Serializable {
	
	static public final String UnknownCodeName = "<UNKNOWN CODE>";
	
	protected Code() {}
	
	/** Returns the name of the code */
	abstract public String       getCodeName();
	/** Returns the source code */
	abstract public CharSequence getSource();
	/** Returns the source code as string */
	final public String getSourceString() {
		CharSequence CS = this.getSource();
		return (CS == null)?null:CS.toString();
	}
	
	/** A normal code */
	static public class Simple extends Code {
	
		/** Construct a normal code */
		public Simple(String pCodeName, CharSequence pSource) {
			this.CodeName  = pCodeName;
			this.Source = (pSource == null)?"":pSource.toString();
		}

		String       CodeName;
		CharSequence Source;
		
		/**{@inheritDoc}*/ @Override public String       getCodeName() { return this.CodeName; }
		/**{@inheritDoc}*/ @Override public CharSequence getSource()   { return this.Source;   }
	}
	
	/** The code that is an adjustment of another code */
	static abstract public class Adjusted extends Code {
		
		/** Returns the original code of the given position (null means local) */
		abstract public CodeRef getOriginalRef(int pThisPos);
		/** Returns the position of the given position of the original */
		abstract public int     getOriginalPos(int pThisPos);
		
		/** Returns the source code */
		abstract public CharSequence getAdjusted();
		/** Returns the source code as string */
		final public String getAdjustedString() {
			CharSequence CS = this.getAdjusted();
			return (CS == null)?null:CS.toString();
		}
	}
	
	// Internal services -----------------------------------------------------------------------------------------------
	
	int[] NewLinePos = null;
	
	/** Reset the new line position (use this in case the source code has changed) */
	protected void resetNLPs() {
		this.NewLinePos = null;
	}
	
	/** Ensure the the new line positions are calculated */
	protected void ensureNLPs() {
		if(this.NewLinePos != null) return;
		Vector<Integer> NLs = new Vector<Integer>();
		for(int i = 0; i < this.getSource().length(); i++) {
			if(this.getSource().charAt(i) == '\r') {
				if((this.getSource().length() > i) && (this.getSource().charAt(i + 1) == '\n')) i++;
			} else if(this.getSource().charAt(i) == '\n') {
			} else continue;
			NLs.add(i);
		}

		// Populate the array
        this.NewLinePos = new int[NLs.size()];
        for(int i = this.NewLinePos.length; --i >= 0; ) this.NewLinePos[i] = NLs.get(i);
	}
	
	/** Returns the line number of the position */
	public int getLineNumberOf(int pPos) {
		if((pPos < 0) || (pPos >= this.getSource().length())) return -1;
		this.ensureNLPs();
		for(int i = this.NewLinePos.length; --i >= 0; ) {
			if(pPos > this.NewLinePos[i]) return i + 1;
		}
		return 0;
	}
	/** Returns the line number of the position */
	public int getRowOf(int pPos) {
		return this.getLineNumberOf(pPos);
	}
	/** Returns the position of on a line number of the position (i.e., column) */
	public int getColOf(int pPos) {
		if((pPos < 0) || (pPos >= this.getSource().length())) return -1;
		this.ensureNLPs();
		for(int i = this.NewLinePos.length; --i >= 0; ) {
			if(pPos > this.NewLinePos[i]) return pPos - this.NewLinePos[i] - 1;
		}
		return pPos;
	}
	
	/** Returns the line count */
	public int getLineCount() {
		this.ensureNLPs();
		return this.NewLinePos.length + 1;
	}
	
	/** Returns the line index by pLine */
	public CharSequence getLine(int pLine) {
		if((pLine < 0) || (pLine >= this.getLineCount())) return null;
		return this.getSource().subSequence(this.getStartPosOfLine(pLine), this.getEndPosOfLine(pLine));
	}
	/** Returns the line index by pLine */
	public String getLineAsString(int pLine) {
		if((pLine < 0) || (pLine >= this.getLineCount())) return null;
		return this.getLine(pLine).toString();
	}
	
	/** Returns the start position of the line */
	public int getStartPosOfLine(int pLine) {
		if(pLine == 0) return  0;
		if(pLine <  0) return -1;
		this.ensureNLPs();
		if(pLine > this.NewLinePos.length) return -1;
		return this.NewLinePos[pLine - 1] + 1;
	}
	
	/** Returns the end position of the line */
	public int getEndPosOfLine(int pLine) {
		if(pLine <  0) return -1;
		this.ensureNLPs();
		if(pLine >  this.NewLinePos.length) return -1;
		if(pLine == this.NewLinePos.length) return this.getSource().length();
		return this.NewLinePos[pLine];
	}
	/** Returns the nearest position to the (col,row) that is a valid position (e.g., not beyond the end of a line) */
	public int getNearestValidPositionOf(int pCol, int pRow) {
		if(pCol <= 0)                   return 0;
		if(pRow >= this.getLineCount()) return this.getSource().length();
		if(pRow <= 0)                   return pCol;
		int PosLineStart = this.getStartPosOfLine(pRow);
		int PosLineEnd   = this.getEndPosOfLine(pRow);
		int LineLength = PosLineEnd - PosLineStart;
		if(pCol >= LineLength) return PosLineEnd;
		return PosLineStart + pCol;
	}
	
	/** Returns the position as a string '(col,row)' */
	public String getCodePosition(int pPos) {
		if(pPos >= this.getSource().length()) pPos = this.getSource().length() - 1;
		if(pPos < 0) return null;
		
		this.ensureNLPs();
		int Row = -1;
		int Col = -1;
		for(int i = this.NewLinePos.length; --i >= 0; ) {
			if(pPos > this.NewLinePos[i]) { Row = i + 1; Col = pPos - this.NewLinePos[i] - 1; break; }
		}
		if(Row == -1) { Row = 0; Col = pPos; }
		return String.format("(%d,%d)", (Col < 0)?"x":Col, (Row < 0)?"x":(Row + 1));
	}

	/** Returns the code position using a '^' sign as a cursor (2 lines of text) */
	public String getCodePositionByCursor(int pPos) {
		return this.getCodePositionByCursor(pPos, null);
	}
	/** Returns the code position using a '^' sign as a cursor (2 lines of text) */
	public String getCodePositionByCursor(int pPos, String pMessage) {
		if(pPos >= this.getSource().length()) pPos = this.getSource().length() - 1;
		if(pPos < 0) return null;
		
		this.ensureNLPs();
		int Row = -1;
		int Col = -1;
		for(int i = this.NewLinePos.length; --i >= 0; ) {
			if(pPos > this.NewLinePos[i]) { Row = i + 1; Col = pPos - this.NewLinePos[i] - 1; break; }
		}
		if(Row == -1) { Row = 0; Col = pPos; }
		CharSequence Line = this.getLine(Row);
		StringBuffer SB = new StringBuffer();
		SB.append("\n---------------------------------------------------------------------------------------------\n");
		// Line number
		SB.append('#');
		String RowStr = "" + (Row + 1);
		while(RowStr.length() < 4) RowStr = "0" + RowStr;
		SB.append(RowStr);
		SB.append(": ");
		
		// The text of the line
		SB.append(Line);
		SB.append('\n');
		
		// The cursor
		// Line number
		SB.append(' '); for(int i = RowStr.length(); --i >= 0; ) SB.append(' '); SB.append(": ");
		
		// Replace all non tab to space and leave the tab alone (so the display of tab is synchronize)
		for(int i = 0; i < Col; i++) SB.append((Line.charAt(i) == '\t')?'\t':' ');
		SB.append('^');
		
		// Extra message
		if(pMessage != null) SB.append(' ').append(pMessage);
		SB.append("\n---------------------------------------------------------------------------------------------\n");
		return SB.toString();
	}
	
	static int ColumnWidth = 120;
	static public int  getColumnWidth()       { return ColumnWidth;                    }
	static public void setColumnWidth(int CW) { if(CW < 80) CW = 80; ColumnWidth = CW; }
	
	
	/** Returns this code as a string (The code name) */
	@Override public String toString() {
		return "Code: " + this.getCodeName();
	}
	/** Returns this code as a string (full display with name and line number) */
	public String toDetail() {
		StringBuffer SB = new StringBuffer();
		String Name = this.getCodeName();
		SB.append("\n");
		for(int c = ((ColumnWidth - Name.length() - 2)/2); --c >= 0; ) SB.append('*');
		SB.append(" ");
		SB.append(Name);
		SB.append(" ");
		for(int c = ((ColumnWidth - Name.length() - 2)/2); --c >= 0; ) SB.append('*');
		SB.append("\n");
		for(int c = ColumnWidth; --c >= 0; ) SB.append('-');
		SB.append("\n");
		for(int i = 0; i < this.getLineCount(); i++) {
			SB.append(String.format("#%4d: ", i));
			SB.append(this.getLine(i));
			SB.append("\n");
		}
		for(int c = ColumnWidth; --c >= 0; ) SB.append('*');
		return SB.toString();
	}
}
