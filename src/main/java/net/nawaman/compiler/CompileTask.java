package net.nawaman.compiler;

import net.nawaman.regparser.ParseResult;
import net.nawaman.task.TaskOptions;

// IN  = (String or ParserResult) depending on pIsFromCode
// OUT = (pOutputClass or [pOutputClass, ParseResule]) depending on pIsToSaveParseResult
public class CompileTask extends TaskForCodeUsingRegParser {

	/** Constructs a TastParse */
	public CompileTask(String pName, Class<?> pOutputClass) {
		this(pName, true, true, pOutputClass);
	}
	/** Constructs a TastParse */
	public CompileTask(String pName, boolean pIsFromCode, boolean pIsToSaveParseResult, Class<?> pOutputClass) {
		super(pName,
				// Select input type
				pIsFromCode
					?new Class<?>[] { String.class      }
					:new Class<?>[] { ParseResult.class },
				null,
				pIsToSaveParseResult
					?new Class<?>[] { pOutputClass, ParseResult.class }
					:new Class<?>[] { pOutputClass }
			);
		this.IsFromCode = pIsFromCode;
		this.IsToSavePR = pIsToSaveParseResult;
	}
	
	boolean IsFromCode = false;
	boolean IsToSavePR = false;
	
	// Do Task -------------------------------------------------------------------------------------
	
	/** Performs the task */
	@Override final public Object[] doTask(CompileProduct pContext, TaskEntry pTE, TaskOptions pOptions, Object[] pIns) {
		if(pIns[0] == null) return null;
		
		ParseResult PResult = null;
		if(this.IsFromCode) {	// P
			CharSequence Source = (pIns[0] instanceof CharSequence)?(CharSequence)pIns[0]:pIns[0].toString();
			PResult = this.getParser().parse(Source, this.Provider);
			if(PResult == null) {
				pContext.reportFatalError("Unmatch!!", null);
				return null;
			}
			if(PResult.getEndPosition() != Source.length()) {
				
				String S = Source.subSequence(PResult.getEndPosition(), Source.length()).toString().trim();
				if(S.length() != 0) {
					pContext.reportError("Left over token <CompileTask:51>: " +
							pContext.getCurrentCode().getCodePositionByCursor(PResult.getEndPosition())
						, null);
					return null;
				}
			}
		} else {
			PResult = (ParseResult)pIns[0];
		}
		
		Object CResult = this.getParserType().compile(PResult, 0, null, pContext, this.getTypeProvider());
		
		// Compile
		return this.IsToSavePR
					?new Object[] { CResult, PResult }	// Returns with parse result
					:new Object[] { CResult };			// Returns only the compile result
	}

}