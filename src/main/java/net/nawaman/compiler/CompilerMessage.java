package net.nawaman.compiler;

import net.nawaman.regparser.utils.Util;

/** Message generated from a compiler */
public class CompilerMessage {
	
	/** Kind of the Message */
	static public enum MessageKind {
		
		MESSAGE, WARNING, ERROR, FATALERROR;
		
		/** Checks if this message is not warning or error */
		public boolean isMessage() {
			return this == MESSAGE;
		}
		/** Checks if this message is a warning. */
		public boolean isWarning() {
			return this == WARNING;
		}
		/** Checks if this message is an error. */
		public boolean isError() {
			return this == ERROR;
		}
		/** Checks if this message is a fatal error. */
		public boolean isFatalError() {
			return this == FATALERROR;
		}
		
	} 
	
	/** Constructs a message */
	protected CompilerMessage(MessageKind pKind, String pMessage, Throwable pCause) {
		this(pKind, pMessage, pCause, null, -1);
	}
	/** Constructs a message */
	protected CompilerMessage(MessageKind pKind, String pMessage, Throwable pCause, CompileProduct pCProduct) {
		this(pKind, pMessage, pCause, pCProduct, -1);
	}
	/** Constructs a message */
	protected CompilerMessage(MessageKind pKind, String pMessage, Throwable pCause, CompileProduct pCProduct, int pPosition) {
		if(pKind == null) pKind = MessageKind.MESSAGE;
		
		this.Message = pMessage;
		this.Cause   = pCause;
		this.Kind    = pKind;
		
		if(pCProduct != null) {
			this.FeederIndex = pCProduct.getCurrentFeederIndex();
			this.CodeName    = pCProduct.getCurrentCodeName();
			if(pPosition >= 0) {
				this.Position = pPosition;
				
				// Get the code and used it to find the column and line number
				Code Code = (Code)pCProduct.getCodeData(this.FeederIndex, this.CodeName, CodeFeeders.DataName_Code);
				if(Code != null) this.ColRow = new int[] { Code.getColOf(this.Position), Code.getLineNumberOf(this.Position) };
			}
		}
	}
	String      Message;
	Throwable   Cause;
	MessageKind Kind;
	
	int    FeederIndex =   -1;
	String CodeName    = null;
	int    Position    =   -1;
	int[]  ColRow      = null;
	
	/** Returns the string message */
	public String getMessage() {
		return Message;
	}
	/** Returns the cause that generate the message */
	public Throwable getCause() {
		return Cause;
	}

	/** Checks if this message is not warning or error */
	public boolean isMessage() {
		return this.Kind.isMessage();
	}
	/** Checks if this message is a warning. */
	public boolean isWarning() {
		return this.Kind.isWarning();
	}
	/** Checks if this message is an error. */
	public boolean isError() {
		return this.Kind.isError();
	}
	/** Checks if this message is a fatal error. */
	public boolean isFatalError() {
		return this.Kind.isFatalError();
	}
	
	/** Returns the code feeder of the code that originate the problem */
	public int getCodeFeederIndex() {
		return this.FeederIndex;
		
	}
	/** Returns the code name of the code that originate the problem */
	public String getCodeName() {
		return this.CodeName;
	}
	
	/** Returns the position of the prblem */
	public int getPosition() {
		return this.Position;
	}
	/** Returns the column number of the prblem */
	public int getColumnNumber() {
		return (this.ColRow == null) ? -1 : this.ColRow[0];
	}
	/** Returns the line number of the prblem */
	public int getLineNumber() {
		return (this.ColRow == null) ? -1 : this.ColRow[1];
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		StringBuffer SB = new StringBuffer();
		if(     this.isMessage())    SB.append("Message: ");
		else if(this.isWarning())    SB.append("Warning: ");
		else if(this.isError())      SB.append("Error: ");
		else if(this.isFatalError()) SB.append("Fatal Error: ");
		SB.append(this.Message);
		if(this.Cause != null) {
			if(!this.Message.endsWith("\n")) SB.append("\n");
			SB.append("Cause By:\n").append(Util.getThrowableToString(this.Cause));
		}
		return SB.toString();
	}
}