package edu.yale.GBI.interp;

final class StateNUHnJ extends ParsingState{
	public StateNUHnJ(ClauseData data,Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.toUpperCase().matches(parser.regx_FPOH_JPOH))
		{
			pd.state="FPOH";
			StateFinish state=new StateFinish(pd,"FPOH",parser);
			pd=null;
			return state;
			
		}
		
		else
		{
			pd.state="FOH";
			StateFinish state=new StateFinish(pd,"FOH",parser);
			pd=null;
			return state;
			
		}
	}

}
