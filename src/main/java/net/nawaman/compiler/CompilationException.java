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

/**
 * Exception related to the compilation
 *
 * @author Nawapunth Manusitthipol (https://github.com/NawaMan)
 */
public class CompilationException extends RuntimeException {
    
    private static final long serialVersionUID = 6358030649874779799L;
    
    public CompilationException()                                  { super();                 }
	public CompilationException(String pMessage)                   { super(pMessage);         }
	public CompilationException(String pMessage, Throwable pCuase) { super(pMessage, pCuase); }

}
