package edu.yale.GBI.interp;

final class StatenNUH extends ParsingState{
	public StatenNUH(ClauseData data, Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.toUpperCase().matches(parser.regx_JH_NJ))
		{
			pd.state="JH";
			StateFinish state=new StateFinish(pd,"JH",parser);
			pd=null;
			return state;
			
		}
		else if ((" "+pd.words+" ").matches(parser.regx_OF)){
			pd.state="FH";
			StateFinish state=new StateFinish(pd,"FH",parser);
			pd=null;
			return state;
			
		}
		else if(pd.clause.trim().equalsIgnoreCase("no conocido"))
		{
			pd.state="UNK";
			StateFinish state=new StateFinish(pd,"UNK",parser);
			pd=null;
			return state;
		}
		else{
			pd.state="FS";
			StateFinish state=new StateFinish(pd,"FS",parser);
			pd=null;
			return state;
		}
	}
	

}
