package net.nawaman.compiler;

import net.nawaman.regparser.PTypeProvider;
import net.nawaman.regparser.ParseResult;
import net.nawaman.task.TaskOptions;

public class PartialParseTask extends ParseTask {

	/** Constructs a TastParse */
	public PartialParseTask(String pName) {
		super(pName,
			new Class<?>[] {      String.class, Integer.class, Integer.class },
			new Object  [] {                "",            -1,            -1 },
			new Class<?>[] { ParseResult.class }
		);
	}
	
	/** Constructs a PartialParseTask */
	public PartialParseTask(String pName, PTypeProvider pTProvider) {
		this(pName);
		this.setTypeProvider(pTProvider);
	}

	// Do Task -------------------------------------------------------------------------------------
	
	/** Performs the task */
	@Override public Object[] doTask(CompileProduct pContext, TaskEntry pTE, TaskOptions pOptions, Object[] pIns) {
		if(pIns[0] == null) return null;
		CharSequence Source = (pIns[0] instanceof CharSequence)?(CharSequence)pIns[0]:(pIns[0] == null)?"":pIns[0].toString();
		int          Offset = (pIns[1] instanceof Integer     )?(Integer)pIns[1]     :0;
		int          EndPos = (pIns[2] instanceof Integer     )?(Integer)pIns[2]     :Source.length();
		ParseResult  PR     = this.getParser().parse(Source, ((Offset < 0)?0:Offset), this.Provider);
		if(PR == null) {
			pContext.reportFatalError("Unmatch!!", null);
			return null;
		}
		if(PR.getEndPosition() != ((EndPos < 0)?Source.length():EndPos)) {
			((CompileProduct)pContext).reportError("Left over token <ParialParseTask:38>: ", null, PR.getEndPosition());
			return null;
		}
		return new Object[] { PR };
	}

}
