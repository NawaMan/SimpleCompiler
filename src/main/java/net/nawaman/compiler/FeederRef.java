package net.nawaman.compiler;

import java.io.Serializable;

/** Reference to a code feeder */
public interface FeederRef extends Serializable {
	
	/** Returns the feeder index */
	public int getFeederIndex();
	
	/** A simple implementation of the reference to the code */
	static public class Simple implements FeederRef {
	    
        private static final long serialVersionUID = -8354045055317562591L;
        
        public Simple(int pFeederIndex) {
			this.FeederIndex = pFeederIndex;
		}
		int FeederIndex;
		/**{@inheritDoc}*/ @Override public int    getFeederIndex() { return this.FeederIndex;                  }
		/**{@inheritDoc}*/ @Override public String toString()       { return "FEEDER:" + this.getFeederIndex(); }
	}

}
