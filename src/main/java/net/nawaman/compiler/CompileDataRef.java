package net.nawaman.compiler;

import java.io.Serializable;

/** Reference to data in CompilerData */
final public class CompileDataRef implements net.nawaman.task.DataRef, Serializable {
	
    private static final long serialVersionUID = 5350765069157825493L;
    
    CompileDataRef() {}
	public CompileDataRef(FeederRef pTargetRef, String pDataName) {
		if(pTargetRef == null) throw new NullPointerException();
		this.TargetRef = pTargetRef;
		this.DataName  = pDataName;
	}
	
	String DataName;
	/** Returns the data name */
	public String getDataName() { return this.DataName; }
	
	FeederRef TargetRef = null;

	/** Checks if this reference is to an arbitrary data */
	public boolean isRefToArbitary() { return this.TargetRef == null; }
	
	/** Checks if this reference is to a code data */
	public boolean isRefToCode() { return (this.TargetRef instanceof CodeRef); }
	/** Returns the reference to the code */
	public CodeRef getCodeRef()  { return (this.TargetRef instanceof CodeRef)?(CodeRef)this.TargetRef:null; }
	
	/** Checks if this reference is to a code feeder */
	public boolean   isRefToFeeder() { return !(this.TargetRef instanceof CodeRef) && (this.TargetRef != null); }
	/** Returns the reference to the code feeder */
	public FeederRef getFeederRef()  { return !(this.TargetRef instanceof CodeRef)?this.TargetRef:null; }

}
