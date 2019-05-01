package net.nawaman.compiler;

import net.nawaman.regparser.PType;
import net.nawaman.regparser.PTypeProvider;
import net.nawaman.regparser.ParseResult;
import net.nawaman.task.TaskOptions;

/** Task to parse a token (refered by ParserType name) */
public class TokenParseTask extends ParseTask {

	/** Constructs a TastParse */
	public TokenParseTask(String pName) {
		super(pName,
			new Class<?>[] { String.class, String.class, Integer.class, Integer.class },
			new Object  [] {           "",           "",            -1,            -1 },
			new Class<?>[] { ParseResult.class }
		);
	}
	/** Constructs a PartialParseTask */
	public TokenParseTask(String pName, PTypeProvider pTProvider) {
		this(pName);
		this.setTypeProvider(pTProvider);
	}

	// Do Task -------------------------------------------------------------------------------------
	
	/** Performs the task */ @Override
	public Object[] doTask(CompileProduct pContext, TaskEntry pTE, TaskOptions pOptions, Object[] pIns) {
		if(pIns[1] == null) return null;
		String       PName  = pIns[0].toString();
		CharSequence Source = (pIns[1] instanceof CharSequence)?(CharSequence)pIns[1]:(pIns[1] == null)?"":pIns[1].toString();
		int          Offset = (pIns[2] instanceof Integer     )?(Integer)pIns[2]     :0;
		int          EndPos = (pIns[3] instanceof Integer     )?(Integer)pIns[3]     :Source.length();
		PType        PT     = this.getTypeProvider().getType(PName);
		
		if(PT == null) {
			pContext.reportFatalError("Unknown token type '"+PName+"'", null);
			return null;
		}
		
		ParseResult PR = PT.parse(Source, ((Offset < 0)?0:Offset), this.Provider);
		if(PR == null) {
			pContext.reportFatalError("Unmatch!!", null);
			return null;
		}
		if(PR.getEndPosition() != ((EndPos < 0)?Source.length():EndPos)) {
			((CompileProduct)pContext).reportError("Left over token <TokenParseTask:47>: ", null, PR.getEndPosition());
			return null;
		}
		return new Object[] { PR };
	}

}
