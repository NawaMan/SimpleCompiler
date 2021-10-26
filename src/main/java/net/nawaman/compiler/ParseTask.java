package net.nawaman.compiler;

import net.nawaman.regparser.*;
import net.nawaman.task.TaskOptions;

/** Parsing Task */
public class ParseTask extends TaskForCodeUsingRegParser {

    private static final long serialVersionUID = 8614054055248957849L;

    /** Constructs a TastParse */
	public ParseTask(String pName) {
		super(pName, new Class<?>[] { String.class }, null, new Class<?>[] { ParseResult.class });
	}

	/** Constructs a TastParse */
	protected ParseTask(String pName, Class<?>[] pInputTypes, Object[] pInputDefaults, Class<?>[] pOutputTypes) {
		super(pName, pInputTypes, pInputDefaults, pOutputTypes);
	}

	// Do Task -------------------------------------------------------------------------------------
	
	/** Performs the task */ @Override
	public Object[] doTask(CompileProduct pContext, TaskEntry pTE, TaskOptions pOptions, Object[] pIns) {
		if(pIns[0] == null) return null;
		CharSequence Source = (pIns[0] instanceof CharSequence)?(CharSequence)pIns[0]:(pIns[0] == null)?"":pIns[0].toString();
		
		RegParser TheParser = RegParser.newRegParser(
				"#Sub",	// Need and extra group to parse properly
				RegParser.newRegParser(new PTypeRef.Simple(this.getName()))
			);
		ParseResult PR = TheParser.parse(Source, this.Provider);
		
		if(PR == null) {
			pContext.reportFatalError("Unmatch!!", null);
			return null;
		}
		if(PR.getEndPosition() != Source.length()) {
			String S = Source.subSequence(PR.getEndPosition(), Source.length()).toString().trim();
			if(S.length() != 0) {
				((CompileProduct)pContext).reportError("Left over token <CompileTask:33> ", null, PR.getEndPosition());
				return null;
			}
		}
		return new Object[] { PR };
	}

}
