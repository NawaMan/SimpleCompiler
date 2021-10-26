package net.nawaman.compiler;

import java.io.Serializable;

import net.nawaman.regparser.PType;
import net.nawaman.task.ProcessContext;
import net.nawaman.task.Task;
import net.nawaman.task.TaskOptions;

/** Tasks that will be performed on compiler */
abstract public class TaskForCompiler implements Task, Serializable {

    private static final long serialVersionUID = -1408230881426382652L;

    /** Returns the name of the task */
	abstract public String getName();
	
	// Kind ----------------------------------------------------------------------------------------
	
	/** Checks if the task is a compile task (not code and not feeder) */
	final public boolean isCompilerTask() { return !this.isCodeTask() && !this.isFeederTask(); }
	/** Checks if the task is a code task */
	public boolean isCodeTask()   { return false; }
	/** Checks if the task is a code feeder task */
	public boolean isFeederTask() { return false; }
	
	// Inputs and Outputs --------------------------------------------------------------------------
	
	/** Returns the types of the inputs */
	abstract protected Class<?>[] getInputTypes();
	/** Returns the default values of the inputs */
	abstract protected Object[]   getInputDefaults();
	
	/** Returns the number of input needed by this tasks */
	final public int getInputCount() {
		Class<?>[] ITypes = this.getInputTypes();
		if(ITypes == null) return 0;
		return ITypes.length;
	}
	/** Returns the DataType of the input */
	final public Class<?> getInputType(int I) {
		Class<?>[] ITypes = this.getInputTypes();
		if(ITypes == null) return null;
		if((I < 0) || (I >= ITypes.length)) return null;
		return ITypes[I];
	}
	/** Returns the default value if the input is missing */
	final public Object getInputDefault(int I) {
		Object[] IDefaults = this.getInputDefaults();
		if(IDefaults == null) return null;
		if((I < 0) || (I >= IDefaults.length)) return null;
		return IDefaults[I];
	}

	/** Returns the types of the outputs */
	abstract protected Class<?>[] getOutputTypes();
	
	/** Returns the number of output needed by this tasks */
	final public int getOutputCount() {
		Class<?>[] OTypes = this.getOutputTypes();
		if(OTypes == null) return 0;
		return OTypes.length;
	}
	/** Returns the DataType of the output */
	final public Class<?> getOutputType(int I) {
		Class<?>[] OTypes = this.getOutputTypes();
		if(OTypes == null) return null;
		if((I < 0) || (I >= OTypes.length)) return null;
		return OTypes[I];
	}
	
	// Do Task -------------------------------------------------------------------------------------

	/** Performs the task */
	final public Object[] doTask(ProcessContext pContext, net.nawaman.task.TaskEntry pTE, TaskOptions pOptions, Object[] pIns) {
		if(!(pContext instanceof CompileProduct))
			throw new IllegalArgumentException("Task For Compiler only accept CompileProduct as a Context.");
		if(!(pTE instanceof TaskEntry))
			throw new IllegalArgumentException("Task For Compiler only accept nawa.compiler.TaskEntry as a TaskEntry.");
		return this.doTask(((CompileProduct)pContext), (TaskEntry)pTE, pOptions, pIns);
	}
	/** Performs the task */
	abstract public Object[] doTask(CompileProduct pContext, TaskEntry pTE, TaskOptions pOptions, Object[] pIns);
	
	// Utilities -------------------------------------------------------------------------------------------------------
	
	// Types ---------------------------------------------------------------------------------------

	/** Create a entries. **/
	final public PType[] newTypes(PType ... pTypes) {
		return pTypes;
	}
	
	// SubClass --------------------------------------------------------------------------------------------------------
	
	/** Simple CompileTask */
	static abstract public class Simple extends TaskForCompiler {
		
        private static final long serialVersionUID = 7509734491476668310L;
        
        public Simple(String pName) {
			this(pName, null, null, null);
		}
		public Simple(String pName, Class<?>[] pInputTypes) {
			this(pName, pInputTypes, null, null);
		}
		public Simple(String pName, Class<?>[] pInputTypes, Class<?>[] pOutputTypes) {
			this(pName, pInputTypes, null, pOutputTypes);
		}
		public Simple(String pName, Class<?>[] pInputTypes, Object[] pInputDefaults, Class<?>[] pOutputTypes) {
			this.Name          = pName;
			this.InputTypes    = pInputTypes;
			this.InputDefaults = (pInputTypes == null)?null:pInputDefaults;
			this.OutputTypes   = pOutputTypes;
		}

		String Name;
		/** Returns the name of the task */
		@Override final public String getName() { return this.Name; }
		
		// Inputs and Outputs --------------------------------------------------------------------------
		
		Class<?>[] InputTypes;
		Object[]   InputDefaults;
		Class<?>[] OutputTypes;
		
		/** Returns the types of the inputs */
		@Override final protected Class<?>[] getInputTypes()    { return this.InputTypes;    }
		/** Returns the default values of the inputs */
		@Override final protected Object[]   getInputDefaults() { return this.InputDefaults; }

		/** Returns the types of the outputs */
		@Override final protected Class<?>[] getOutputTypes()   { return this.OutputTypes;   }
		
	}
	
}
