package edu.yale.GBI.interp;

final class StateNUnH extends ParsingState{
	public StateNUnH(ClauseData data,Parser p)
	{
	    parser=p;
		pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.words.length<=3)
		{
			pd.state="E";
			StateFinish state=new StateFinish(pd,"E",parser);
			pd=null;
			return state;
			
		}
		
		else if(pd.clause.toUpperCase().matches(parser.regx_JO))
		{
			pd.state="JO";
			StateFinish state=new StateFinish(pd,"JO",parser);
			pd=null;
			return state;
			
		}
		
		else if(pd.clause.toUpperCase().matches(parser.regx_FOP))
		{
			pd.state="FOP";
			StateFinish state=new StateFinish(pd,"FOP",parser);
			pd=null;
			return state;
			
		}
		
		
		else if(pd.clause.toUpperCase().matches(parser.regx_FPOH_JPOH))
		{
			pd.state="FPOH";
		  	pd.words=pd.clause.replaceAll(parser.regx_BY_RD," ").trim().split("\\s+");
		  	parser.containNU(pd);
			StateFinish state=new StateFinish(pd,"FPOH",parser);
			pd=null;
			return state;
		}
		else 
		{
			pd.state="FO";
			StateFinish state=new StateFinish(pd,"FO",parser);
			pd=null;
			return state;
			
		}
	}

}
