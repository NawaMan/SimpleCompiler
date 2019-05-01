package net.nawaman.compiler;

import java.io.InputStream;
import java.io.OutputStream;

import net.nawaman.regparser.Checker;
import net.nawaman.regparser.PType;
import net.nawaman.regparser.PTypeProvider;
import net.nawaman.regparser.PTypeRef;
import net.nawaman.regparser.RegParser;

/**
 * Task for code that use RegParser.
 * 
 * This class provide necessary service dealing with RegParser
 **/
abstract public class TaskForCodeUsingRegParser extends TaskForCode.Simple {

	/** Constructs a TastParse */
	protected TaskForCodeUsingRegParser(String pName, Class<?>[] pInputTypes, Object[] pInputDefaults,
			Class<?>[] pOutputTypes) {
		super(pName, pInputTypes, pInputDefaults, pOutputTypes);
	}
	
	RegParser Parser = null;

	/** Sets a checker as a parser (for ParseTask only) */
	final protected void setParser(Checker pChecker) {
		if(this.Parser != null) return;
		this.Parser = (pChecker instanceof RegParser)?(RegParser)pChecker:RegParser.newRegParser(pChecker);
	}
	/** Set the Parser type and other types it may use */
	final protected void setParserType(PType T, PTypeProvider[] pTProviders, PType[] pTypes) {
		// Set the parser
		this.Parser = RegParser.newRegParser(new PTypeRef.Simple(T.getName()));
		// Don't care if provider already exist
		PType[] Ts = new PType[((pTypes == null)?0:pTypes.length) + 1];
		if(pTypes != null) System.arraycopy(pTypes, 0, Ts, 0, pTypes.length);
		Ts[Ts.length - 1] = T;
		this.Provider = (pTProviders == null)?new PTypeProvider.Simple(Ts):new PTypeProvider.Library(Ts, pTProviders); 
	}
	/**
	 * Set the Parser type using the type name. The type and other types it may need MUST be in the TypeProvider set by
	 * <code>setTypeProvider(PTypeProvider)<code>;
	 **/
	final protected void setParserType(String pTypeName) {
		// Set the parser
		this.Parser = RegParser.newRegParser(new PTypeRef.Simple(pTypeName));
	}
	/** Returns the parser used to perform the task */
	final public RegParser getParser() {
		if(this.Parser == null) this.Parser = RegParser.newRegParser(new PTypeRef.Simple(this.getName()));
		return this.Parser;
	}
	/** Returns the parser type used to perform the task */
	final public PType getParserType() {
		return this.Provider.getType(this.getName());
	}
	
	
	PTypeProvider Provider = null;
	
	/** Specifically set the type provider */
	final protected void setTypeProvider(PTypeProvider pProvider) {
		if(this.Provider != null) return;
		this.Provider = pProvider;
	}
	public PTypeProvider getTypeProvider() { return this.Provider; }
	
	// Load and save ---------------------------------------------------------------------------------------------------
	
	final protected boolean tryToLoadFrom(InputStream pIS) {
		if(this.Provider != null) return false;
		try {
			this.Provider = PTypeProvider.Simple.loadTypeProviderFromStream(pIS);
			if(this.Provider.getType(this.getName()) == null) {
				// Revert and return false;
				this.Provider = null;
				this.Parser   = null;
				return false;
			}
			// Set the parser and return true
			this.Parser = RegParser.newRegParser(new PTypeRef.Simple(this.getName()));
			return true;
		} catch(Exception E) {}
		return false;
	}
	
	final protected boolean tryToSaveTo(OutputStream pOS) {
		if(this.Provider == null) return false;
		try {
			PTypeProvider.Simple.saveRPTypeProviderToStream(pOS, this.Provider);
			return true;
		} catch(Exception E) {
			System.out.println(E.toString());
		}
		return false;
	}
	
}
