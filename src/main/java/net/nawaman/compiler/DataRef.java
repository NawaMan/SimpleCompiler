package net.nawaman.compiler;

/** DataRef for Task */
final public class DataRef implements net.nawaman.task.DataRef {
	
	/** Constructs a Task DataRef */
	public DataRef(String pDataName) { this.DataName = pDataName; }
	
	String DataName;
	/** Returns the data name */ public String getDataName() { return this.DataName;           }
	/**{@inheritDoc}*/ @Override public String toString()    { return "DATA:" + this.DataName; }

}
