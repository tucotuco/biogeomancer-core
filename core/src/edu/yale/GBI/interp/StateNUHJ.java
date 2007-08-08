package edu.yale.GBI.interp;

final class StateNUHJ extends ParsingState{
	public StateNUHJ(ClauseData data,Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.toUpperCase().replaceAll("[(,)]"," ").trim().matches(parser.regx_FPOH_JPOH))
		{
			pd.state="JPOH";
			StateFinish state=new StateFinish(pd,"JPOH",parser);
			pd=null;
			return state;
			
		}
		else
		{
			pd.state="JOH";
			StateFinish state=new StateFinish(pd,"JOH",parser);
			pd=null;
			return state;
			
		}
	}

}
