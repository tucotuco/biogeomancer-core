package edu.yale.GBI.interp;

final class StateBegin extends ParsingState {

	public StateBegin(ClauseData data, Parser p)
	{
	    pd=data;
	    parser=p;
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(parser.containNU(pd))
		{
			pd.state="NU";
			StateNU state=new StateNU(pd,parser);
			pd=null;
			return state;
			
		}else{
			pd.state="nNU";
			StatenNU state=new StatenNU(pd, parser);
			pd=null;
			return state;
			
		}
	}

}
