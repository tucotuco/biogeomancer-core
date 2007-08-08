package edu.yale.GBI.interp;

final class StatenNUN extends ParsingState{
	public StatenNUN(ClauseData data,Parser p)
	{
	    parser=p;
		pd=data;	
	}
	
	public ParsingState parse() {
		if(pd.clause.toUpperCase().matches(parser.regx_JH_NJ))
		{
			pd.state="NJ";
			StateFinish state=new StateFinish(pd,"NJ",parser);
			pd=null;
			return state;
			
		}
		
		else if(pd.posN1-pd.posU1==1)
		{
			pd.state="NPOM";
			StateFinish state=new StateFinish(pd,"NPOM",parser);
			pd=null;
			return state;
			
		}
		
		else {
			pd.state="NF";
			StateFinish state=new StateFinish(pd,"NF",parser);
			pd=null;
			return state;
			
		}
	}

}
