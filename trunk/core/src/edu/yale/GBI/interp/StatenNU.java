package edu.yale.GBI.interp;

final class StatenNU extends ParsingState{
	public StatenNU(ClauseData data, Parser p)
	{   
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.toUpperCase().matches(parser.regx_BF))
		{
			pd.state="BF";
			StateFinish state=new StateFinish(pd,"BF",parser);
			pd=null;
			return state;
			
		}
		else if(parser.isHeading(pd.words[0])){
			pd.state="nNUH";
			StatenNUH state=new StatenNUH(pd,parser);
			pd=null;
			return state;
			
		}
		else if(pd.clause.toUpperCase().matches(parser.regx_NF_NJ_NPOM)){
			pd.state="nNUN";
			StatenNUN state=new StatenNUN(pd,parser);
			pd=null;
			return state;
			
		}		
		else if(pd.clause.toUpperCase().matches(parser.regx_FS)){
			pd.state="FS";
			StateFinish state=new StateFinish(pd,"FS",parser);
			pd=null;
			return state;
			
		}
		else if(pd.clause.toUpperCase().matches(parser.regx_J)){
			pd.state="J";
			StateFinish state=new StateFinish(pd,"J", parser);
			pd=null;
			return state;
			
		}
		else{
			pd.state="nNUSpecial";
			StatenNUSpecial state=new StatenNUSpecial(pd,parser);
			pd=null;
			return state;
		}
	}

}
