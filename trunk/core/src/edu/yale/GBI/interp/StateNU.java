package edu.yale.GBI.interp;

final class StateNU extends ParsingState{
	public StateNU(ClauseData data,Parser p)
	{
		parser=p;
	    pd=data;	
	}
	
	public ParsingState parse() {
		// TODO Auto-generated method stub
		if(pd.words.length<4){
			pd.state="E";
			StateFinish state=new StateFinish(pd,"E",parser);
			pd=null;
			return state;
		}
	    else if(pd.posH2>0)
		{
			pd.state="NUH2";
			StateNUH2 state=new StateNUH2(pd,parser);
			pd=null;
			return state;
			
		}else if(pd.posH1>0){
			pd.state="NUH";
			StateNUH state=new StateNUH(pd,parser);
			pd=null;
			return state;
			
		}else{
			pd.state="NUnH";
			StateNUnH state=new StateNUnH(pd,parser);
			pd=null;
			return state;
		}
		
	}

}
