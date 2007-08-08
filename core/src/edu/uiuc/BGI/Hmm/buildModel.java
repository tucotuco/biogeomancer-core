package edu.uiuc.BGI.Hmm;

import java.util.*;
import java.io.*;
import java.util.Vector;
import java.lang.String;
import java.util.regex.*;

//import org.w3c.dom.*;
//import javax.xml.parsers.*;

public class buildModel{

    Hashtable wordList = new Hashtable();
    Hashtable stateList = new Hashtable();

    int N = 194;  //number of states 
    int M = 23;  //number of symbols 

    double[][] A = new double[N][N];   //A: NxN
    double[][] B = new double[N][M];  //B: NxM
    double[] Pi = new double[N];

    public static void main(String[] args) throws IOException, FileNotFoundException, Exception {
	buildModel bm = new buildModel();
	
	bm.initResources();
	bm.parse();
    }

    public void parse() throws IOException, FileNotFoundException, Exception {

	BufferedReader br = new BufferedReader(new FileReader("data/training"));
	String aLine;
	String[] strArr;
	String[] tokenArr;
	int curState = -1;
	int preState = -1;
	int nf = 0;  //number of training examples

// parse the training file

	int s1 = 0;
	int s2 = 0;
	String parentTag;
	String childTag;
	String childStr;
	String[] childArr;
	String tagStr;
	String tokenStr;

	while((aLine = br.readLine()) != null) {

	    // separate the parent tags
System.out.println("=======new line=========");
System.out.println("the new line is: " + aLine);
	    strArr = aLine.trim().split("</[A-Z]+>");

	    for(int i = 0; i < strArr.length; i++) {
System.out.println("enter parent tags:::::");
System.out.println("strArr.length is:" + strArr.length + "and i is: " + i);

		s1 = strArr[i].indexOf("<");
		s2 = strArr[i].indexOf(">");
		parentTag = strArr[i].substring(s1+1, s2);

		// get the children tags

		childStr = (strArr[i].substring(s2+1)).trim();	
System.out.println("childStr is:" + childStr);
		childArr = childStr.split("</[a-z]+>");
System.out.println("childArr.length is:" + childArr.length);
		for(int j = 0; j < childArr.length; j++) {
System.out.println("enter children tags. j is:" + j);

		   s1 = childArr[j].indexOf("<");
		   s2 = childArr[j].indexOf(">");
		   childTag = childArr[j].substring(s1+1, s2);
System.out.println("now: " + childArr[j] + ", s2 is: " + s2); 
		   tokenStr = (childArr[j].substring(s2+1)).trim();
System.out.println("tokenStr is: " + tokenStr); 
 
		   tagStr = parentTag + "_" + childTag;

// parse the parent-child tag and fill matrix A and vector Pi

		   curState = getStateId(tagStr);  

		   if(curState == -1) {
		       System.out.println("Unknown state label:" + tagStr);
		   }
		   if(preState == -1) {
		       Pi[curState] += 1;
		   }
		   else {
		       A[preState][curState] += 1;
		   }

// parse the tokens following the child tag
/* submit version

		   String inStr = new String(tokenStr);

		   String resStr = new String("");

		   for(int r = 0; r < inStr.length(); r++) {
			if( !Character.isLetterOrDigit(inStr.charAt(r)) && !Character.isWhitespace(inStr.charAt(r)) ) {
			   resStr = resStr + " " + inStr.charAt(r) + " "; 
			}
			else {
			   resStr = resStr + inStr.charAt(r);
			}
		   }
*/
//end of submit version 


//add space around non-word character except period . and between digit/letter
		   String resStr = sepNonWordDigLet(tokenStr);

System.out.println("resStr is: " + resStr);

		   tokenArr = resStr.trim().split("\\s+");

		  int numToken = tokenArr.length;
System.out.println("numToken is " + numToken);

		  int wIndex = -1;
		  for(int k = 0; k < numToken; k++) {
		    wIndex = getSymbol(tokenArr[k]);
System.out.println("wIndex is " + wIndex);
		    if(wIndex > -1) {
			B[curState][wIndex] += 1;
			A[curState][curState] += 1;
		    }
		  }
		  preState = curState;

	       } //end of FOR children tags

System.out.println("out of children tags.");


	   } //end of FOR parent tags

	    nf++;
	    preState = -1; 

	}  // end of the training file 

// normalize matrix B

	for(int i = 0; i < N; i++) {
	    double Tj  = 0;
	    double mj = 0;
	    double x = 0;
	    for(int j = 0; j < M; j++) {
	        if(B[i][j] > 0) {
		    Tj += B[i][j];
		    mj += 1;
	        }
	    }
	    x = 1/(3 * Tj + M);

	    double unknown = (mj * x) / (M - mj);
	    for(int j = 0; j < M; j++) {
	        if(B[i][j] > 0) {
		    B[i][j] = (B[i][j] / Tj) - x;
	        }
	        else {
		    B[i][j] = unknown;
	        }
	    }
        }

// normalize matrix A

	for (int i = 0; i < N; i++) {
	    double Tj = 0;
	    for (int j = 0; j < N; j++) {
		Tj += A[i][j];
	    }
	    for (int j = 0; j < N; j++) {
		if(A[i][j] > 0) {
		    A[i][j] = A[i][j] / Tj;
		}
	    }
	}

// print the matrix A, B, and array Pi to a model file

/*
	try {
    	    BufferedWriter bw = new BufferedWriter(new FileOutputStream("data/model.hmm"));
	}
	catch(IOException e) {
	    System.out.println("Can't open model.hmm for writing");
	}

	bw.write("M= " + M + "\nN= " + N + "\nA:\n");
	for(int i = 0; i < N; i++) {
	    for(int j = 0; j < N; j++) {
	        bw.write(A[i][j] + " ");
	    }
	    bw.write("\n");
	}
	bw.write("B:\n");
	for(int i = 0; i < N; i++) {
	    for(int j = 0; j < M; j++) {
	    	bw.write(B[i][j] + " ");
	    }
	    bw.write("\n");
	}
	bw.write("pi:\n");
	for(int i = 0; i < N; i++) {
	    Pi[i] = Pi[i] / nf;
	    bw.write(Pi[i] + " ");
	}

*/
//  another way is to use printstream(fileoutputstream). see http://www.javacoffeebreak.com/java103/java103.html. also see java tutorial under sun.

	try {
	    PrintStream p = new PrintStream(new FileOutputStream("data/model.hmm"));
	
	    p.print("M= " + M + "\nN= " + N + "\nA:\n");
	    for(int i = 0; i < N; i++) {
	        for(int j = 0; j < N; j++) {
	            p.print(A[i][j] + " ");
	    	}
	        p.print("\n");
	    }
	    p.print("B:\n");
	    for(int i = 0; i < N; i++) {
	        for(int j = 0; j < M; j++) {
	    	    p.print(B[i][j] + " ");
	        }
	        p.print("\n");
	    }
	    p.print("pi:\n");
	    for(int i = 0; i < N; i++) {
	        Pi[i] = Pi[i] / nf;
	        p.print(Pi[i] + " ");
	    }

	    p.close();
	}
	catch (Exception e) {
	    System.out.println("Error in writing matrix file!");
	}
	    
	
    }  //end of parse()

    private int getStateId(String s) {
	Integer n = (Integer)stateList.get(s);
	if(n != null) {
	    return n.intValue();
	}
	else {
	    return -1;
	}

    }

    private void initResources() throws IOException, FileNotFoundException {

// read the wordlist file into a hashtable. 
// the file's format is Word:symbolId

	String aLine;
	int hNum = 0;	
	String[] hList;	
	BufferedReader br = new BufferedReader(new FileReader("data/wordList"));
	while( (aLine = br.readLine()) != null) {
	    hList = aLine.split(":");
	    hNum = Integer.parseInt(hList[1].trim()); 
	    wordList.put(new String(hList[0].trim()), new Integer(hNum));
	}

// read the state id to a hashtable
// the file's format is type_subtype:stateId. E.g. foh_offset:2

	br = new BufferedReader(new FileReader("data/stateList"));
	while( (aLine = br.readLine()) != null) {
	    hList = aLine.split(":");
	    hNum = Integer.parseInt(hList[1].trim());
	    stateList.put(new String(hList[0].trim()), new Integer(hNum));
	}
	

    }

   private String sepNonWordDigLet(String s) {

	int len = s.length();
	char[] charTmp = new char[len*2];
	int j = 0;

System.out.println("sepDigLet: orig s is: " + s);

	if(len == 1) {
	    return s;
	}
	for(int i = 0; i < len; i++) {
	    if((i < len-1) && ((Character.isDigit(s.charAt(i)) && Character.isLetter(s.charAt(i+1))) || (Character.isLetter(s.charAt(i)) && Character.isDigit(s.charAt(i+1))))) {
		charTmp[j++] = s.charAt(i);
		charTmp[j++] = ' ';
	    }
	    else if((i < len-2) && (Character.isDigit(s.charAt(i)) && s.charAt(i+1) == '/' && Character.isDigit(s.charAt(i+2)))) {
		charTmp[j++] = s.charAt(i);
		charTmp[j++] = s.charAt(i+1);
		charTmp[j++] = s.charAt(i+2);
		i += 2;
	    }
	    else if(!Character.isLetterOrDigit(s.charAt(i)) && !Character.isSpaceChar(s.charAt(i)) && s.charAt(i) != '.') {

		charTmp[j++] = ' ';
		charTmp[j++] = s.charAt(i);
		charTmp[j++] = ' ';
	    } 
	    else {
		charTmp[j++] = s.charAt(i);
	    }
	}

	String ss = new String(charTmp, 0, j);
System.out.println("sepDigLet: new ss is: " + ss);

	return ss;

   } 	

    private int getSymbol(String ss) {
	int nLen = ss.length();
	int nn = -1;
	String s = new String(ss);	

System.out.println("getSymbol: s is " + s);

	if(Character.isLetter(s.charAt(0))) {  //char

	    if(s.matches("[a-zA-Z]+\\.")) {
		int tmp = s.length();
		s = s.substring(0, tmp-1);
		nLen -= 1;
	    }

	    Integer n = (Integer)wordList.get(s.toLowerCase());

	    if(n != null) {   // in the wordlist, symbol 0 ~ 11
		nn = n.intValue(); 	
		return nn;
	    }
	    else {  // not in the wordlist

		if(nLen == 1) {
		    return 12;
		   
		}
	        if( nLen > 1)  {
	            if(Character.isUpperCase(s.charAt(0)) && Character.isLowerCase(s.charAt(1))) {
		        return 13;
		    }
		    else {
		        return 14;
		    }
	        }
	    }
	}
	else if(Character.isDigit(s.charAt(0))) {  //number, this includes fractions like 5.4 and 1/5
		return 15;
	}
	else if((s.length() > 1) && (s.matches("\\.\\d+"))) {
		return 15;
	}
	else { //delimeter
	    if(s.matches("&")) {
		    return 11;
	    }
            else if(s.matches(",")) {
		    return 16;
	    }
	    else if(s.matches(";")) {
		    return 17;
	    }
	    else if(s.matches(":")) {
		    return 18;
	    }
	    else if(s.matches("\\.")) {
	    	    return 19;
	    }
	    else if(s.matches("\'")) {
		    return 20;
	    }
	    else if(s.matches("\"")) {
		    return 21;
  	    }
	    else {
		    return 22;
	    }
	} 

	return -1;

    }  //end of getSymbol

} 

 
		 


		
	    

