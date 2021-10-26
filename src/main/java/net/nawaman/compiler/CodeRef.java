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

/** Reference to the code
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
public interface CodeRef extends FeederRef {
	
	/** Returns the code feeder index */
	public int    getFeederIndex();
	/** Returns the code name */
	public String getCodeName();
	
	/** A simple implementation of the reference to the code */
	static public class Simple extends FeederRef.Simple implements CodeRef {
	    
        private static final long serialVersionUID = -4249986692032041372L;
        
        public Simple(int pFeederIndex, String pCodeName) {
			super(pFeederIndex);
			this.CodeName = pCodeName;
		}
		String CodeName;
		/**{@inheritDoc}*/ @Override public String getCodeName() { return this.CodeName; }
		/**{@inheritDoc}*/ @Override public String toString()    { return "CODE:" + this.getCodeName(); }
	}
	/** A simple implementation of the reference to the code */
	static public class Share implements CodeRef {
        private static final long serialVersionUID = -2733485597822827007L;

        public Share(FeederRef pFeederRef, String pCodeName) {
			this.FeederRef = pFeederRef;
			this.CodeName  = pCodeName;
		}
		FeederRef FeederRef;
		String    CodeName;

		/**{@inheritDoc}*/ @Override public int    getFeederIndex() { return this.FeederRef.getFeederIndex(); }
		/**{@inheritDoc}*/ @Override public String getCodeName()    { return this.CodeName; }
		/**{@inheritDoc}*/ @Override public String toString()       { return "CODE:" + this.getCodeName(); }
	}
	
}