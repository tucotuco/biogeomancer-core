package edu.berkeley;
import java.util.*;
import java.io.*;
import java.net.*;  // Has URL class

import org.biogeomancer.records.*;
 

public class MapInterface {

  public static void main(String[] args) {
	MapInterface mif = new MapInterface();
	System.out.println(mif.execute(null));
  }

  /**
  Get URL of map image to use
  **/
  public String execute(RecSet p_recset) {
	try {
		// Location of Output Image File
		//String l_strTmpDir="/usr/local/apache2/htdocs/tmp/";
		String l_strTmpDir="/var/tmp/";
		String l_strTmpFile="jbdtest.html";
		String l_strTmpURL="http://bg.berkeley.edu/tmp/"+l_strTmpFile;
		String l_strTmpFilePath=l_strTmpDir+l_strTmpFile;
		String l_strMapservURL="http://chignik.berkeley.edu/cgi-bin/mapserv440";

		// Construct URL to call Mapserv MapFile and Template
		String l_strURL="";
		l_strURL+=l_strMapservURL;
		l_strURL+="?map=//usr/local/web/html/test/test.map";
		l_strURL+="&mode=browse";
		l_strURL+="&layers=all";
		// HTML elements to pass to template file
		l_strURL+="&mapservurl="+l_strMapservURL; 
		l_strURL+="&T1=0.24+mi.+N+of+Micanopy";
		l_strURL+="&T2=10+mi+S+of+Gainesville";

		// Other variables in use
		String l_strContents = "";
		String l_strReturn = "";

		// Output File  (this is as read from the mapserv map file & template file)
		File l_fileOutput= new File(l_strTmpFilePath);
		FileWriter l_fwOut=new FileWriter(l_fileOutput);

		// Read in URL
		URL l_urlMap = new URL(l_strURL);
		BufferedReader l_bfrIn = new BufferedReader(new InputStreamReader(l_urlMap.openStream()));
		while ((l_strContents = l_bfrIn.readLine()) != null) {
			l_fwOut.write(l_strContents);
			l_strReturn+=l_strContents;
		}
		
		l_bfrIn.close();
		l_fwOut.close();

		// Return URL for output file
		return "See Output at the following URL:"+l_strTmpURL+"<p>"+l_strReturn;
/*
		URL l_urlMap = new URL(l_strURL);
		BufferedReader l_bfrIn = new BufferedReader(new InputStreamReader(l_urlMap.openStream()));
		while ((l_strContents = l_bfrIn.readLine()) != null) {
			l_strReturn = l_strReturn + l_strContents;
		}
		l_bfrIn.close();
		return l_strReturn;
*/

	

	} catch (MalformedURLException e) { System.out.println(e); } 
	catch (IOException e) { System.out.println(e); } 
	return "";
  }
}
