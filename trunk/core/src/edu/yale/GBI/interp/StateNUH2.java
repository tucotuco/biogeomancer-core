package edu.yale.GBI.interp;

final class StateNUH2 extends ParsingState{
	public StateNUH2(ClauseData data,Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.toUpperCase().matches(parser.regx_JH_NJ))
		{
			pd.state="JOO";
			StateFinish state=new StateFinish(pd,"JOO",parser);
			pd=null;
			return state;
			
		}
		else
		{
			pd.state="FOO";
			StateFinish state=new StateFinish(pd,"FOO",parser);
			pd=null;
			return state;
			
		}
	}

}
