package net.nawaman.compiler;

/** Tasks that will be performed on Code's Data */
abstract public class TaskForCode extends TaskForCompiler {
	
	/** Checks if the task is a code task */
	@Override public boolean isCodeTask() { return true; }

	// SubClass --------------------------------------------------------------------------------------------------------
	
	/** Simple CodeTask */
	static abstract public class Simple extends TaskForCode {

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
		/** Returns the name of the task */@Override
		final public String getName() {
			return this.Name;
		}
		
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
