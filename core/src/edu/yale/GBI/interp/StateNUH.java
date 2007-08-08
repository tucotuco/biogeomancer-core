package edu.yale.GBI.interp;

final class StateNUH extends ParsingState{
	public StateNUH(ClauseData data,Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.clause.matches(".*"+parser.regx_AND+".*"))
		{
			pd.state="NUHJ";
			StateNUHJ state=new StateNUHJ(pd,parser);
			pd=null;
			return state;
			
		}
		else
		{
			pd.state="NUHnJ";
			StateNUHnJ state=new StateNUHnJ(pd,parser);
			pd=null;
			return state;
			
		}
	}

}
