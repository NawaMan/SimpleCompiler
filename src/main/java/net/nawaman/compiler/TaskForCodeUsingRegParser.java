package net.nawaman.compiler;

import java.io.InputStream;
import java.io.OutputStream;

import net.nawaman.regparser.Checker;
import net.nawaman.regparser.ParserType;
import net.nawaman.regparser.ParserTypeProvider;
import net.nawaman.regparser.ParserTypeRef;
import net.nawaman.regparser.RegParser;

/**
 * Task for code that use RegParser.
 * 
 * This class provide necessary service dealing with RegParser
 **/
abstract public class TaskForCodeUsingRegParser extends TaskForCode.Simple {

    private static final long serialVersionUID = -5307127890652207316L;

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
	final protected void setParserType(ParserType T, ParserTypeProvider[] pTProviders, ParserType[] pTypes) {
		// Set the parser
		this.Parser = new ParserTypeRef.Simple(T.name()).asRegParser();
		// Don't care if provider already exist
		ParserType[] Ts = new ParserType[((pTypes == null)?0:pTypes.length) + 1];
		if(pTypes != null) System.arraycopy(pTypes, 0, Ts, 0, pTypes.length);
		Ts[Ts.length - 1] = T;
		this.Provider = (pTProviders == null)?new ParserTypeProvider.Simple(Ts):new ParserTypeProvider.Library(Ts, pTProviders); 
	}
	/**
	 * Set the Parser type using the type name. The type and other types it may need MUST be in the TypeProvider set by
	 * <code>setTypeProvider(PTypeProvider)<code>;
	 **/
	final protected void setParserType(String pTypeName) {
		// Set the parser
		this.Parser = new ParserTypeRef.Simple(pTypeName).asRegParser();
	}
	/** Returns the parser used to perform the task */
	final public RegParser getParser() {
		if(this.Parser == null) this.Parser = ParserTypeRef.of(this.getName()).asRegParser();
		return this.Parser;
	}
	/** Returns the parser type used to perform the task */
	final public ParserType getParserType() {
		return this.Provider.type(this.getName());
	}
	
	
	ParserTypeProvider Provider = null;
	
	/** Specifically set the type provider */
	final protected void setTypeProvider(ParserTypeProvider pProvider) {
		if(this.Provider != null) return;
		this.Provider = pProvider;
	}
	public ParserTypeProvider getTypeProvider() { return this.Provider; }
	
	// Load and save ---------------------------------------------------------------------------------------------------
	
	final protected boolean tryToLoadFrom(InputStream pIS) {
		if(this.Provider != null) return false;
		try {
			this.Provider = ParserTypeProvider.Simple.loadTypeProviderFromStream(pIS);
			if(this.Provider.type(this.getName()) == null) {
				// Revert and return false;
				this.Provider = null;
				this.Parser   = null;
				return false;
			}
			// Set the parser and return true
			this.Parser = new ParserTypeRef.Simple(this.getName()).asRegParser();
			return true;
		} catch(Exception E) {}
		return false;
	}
	
	final protected boolean tryToSaveTo(OutputStream pOS) {
		if(this.Provider == null) return false;
		try {
			ParserTypeProvider.Simple.saveTypeProviderToStream(pOS, this.Provider);
			return true;
		} catch(Exception E) {
			System.out.println(E.toString());
		}
		return false;
	}
	
}
