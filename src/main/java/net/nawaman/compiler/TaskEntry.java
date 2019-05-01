package net.nawaman.compiler;

import java.io.Serializable;

import net.nawaman.task.Task;

/** TaskEntry for the compilation */
final public class TaskEntry extends net.nawaman.task.TaskEntry implements Serializable {
	
	public TaskEntry(TaskForCompiler pTask) {
		this(pTask, null, null);
	}
	
	public TaskEntry(TaskForCompiler pTask, String[] pInNames, String[] pOutNames) {
		if(pTask == null) throw new NullPointerException();
		this.Task = pTask;
		
		int InCount  = (pInNames  == null)?0:pInNames.length;
		int OutCount = (pOutNames == null)?0:pOutNames.length;
		if(InCount  != pTask.getInputCount())  throw new IllegalArgumentException("Incompatible input count '"+pTask.getName()+"'.");
		if(OutCount != pTask.getOutputCount()) throw new IllegalArgumentException("Incompatible Output count '"+pTask.getName()+"'.");
		
		this.DRIns  = new DataRef[InCount];
		this.DROuts = new DataRef[OutCount];
		for(int i = InCount; --i >= 0; ) {
			String IName = (pInNames == null)?null:pInNames[i];
			if(IName == null) throw new NullPointerException();
			this.DRIns[i] = new DataRef(IName);
		}
		for(int i = OutCount; --i >= 0; ) {
			String OName = (pOutNames == null)?null:pOutNames[i];
			if(OName == null) throw new NullPointerException();
			this.DROuts[i] = new DataRef(OName);
		}
	}
	
	DataRef[]       DRIns;
	DataRef[]       DROuts;
	TaskForCompiler Task;
	
	/** Returns the DataRef of the input  */
	@Override public DataRef getInputRef(int I) {
		if((I < 0) || (I >= this.getInputCount())) return null;
		return this.DRIns[I];
	}
	/** Returns the DataRef of the output */
	@Override public DataRef getOutputRef(int I) {
		if((I < 0) || (I >= this.getOutputCount())) return null;
		return this.DROuts[I];
	}
	
	/** Returns the task to perform this entry */
	@Override public Task getTask() { return this.Task; }

}
