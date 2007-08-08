package edu.yale.GBI.interp;

class ClauseData {
   
	String[] words;
	String clause;
	String state;
	
	int posN1;
	int posU1;
	int posH1;
	int posN2;
	int posU2;
	int posH2;
	
	public ClauseData(String s)
   {
		posN1=-1;
		posU1=-1;
		posH1=-1;
		posN2=-1;
		posU2=-1;
		posH2=-1;
		state="BEGIN";
		clause=s;
	    words=clause.split("\\s+");
   }
	
	
   
}
