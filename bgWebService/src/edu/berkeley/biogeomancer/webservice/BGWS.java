package edu.berkeley.biogeomancer.webservice;
import java.io.*;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.biogeomancer.*;
import org.biogeomancer.managers.*;
import org.biogeomancer.managers.GeorefManager.*;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.*;

public class BGWS extends HttpServlet{

	/**
	 * @param args
	 */
	
	public void doGet(HttpServletRequest request, 
    HttpServletResponse response) throws ServletException,
    java.io.IOException
    {
		 //set the MIME type of the response, "text/html"
	    response.setContentType("text/xml");	    
	    //use a PrintWriter to send text data to the client who has requested the
	    //servlet
	    java.io.PrintWriter out = response.getWriter( );

	    //Begin assembling the HTML content
	   /* out.println("<html><head>");
	    
	    out.println("<title>Welcome Page</title></head><body>");
	    out.println("<h2>Welcome to my page</h2>");*/
	   
	   //make sure method="post" so that the servlet service method
	   //calls doPost in the response to this form submit
	    String locality = request.getParameter("l");
	    String highergeography = request.getParameter("hg");
	    String interpreter = request.getParameter("i");
	    String output ="";
	    
	    final Rec rec = new Rec();
	    rec.put("locality",  locality);
	    rec.put("highergeography", highergeography);
	    GeorefManager gm;
		try {
			gm = new GeorefManager();
			gm.georeference(rec, new GeorefPreferences(interpreter));
		} catch (GeorefManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Build the XML header */
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<georeferences xmlns=\"bg\">");
		out.println("<locality>" + locality + "</locality>");
		out.println("<higherGeography>" + highergeography + "</higherGeography>");
		out.println("<interpreter>" + interpreter + "</interpreter>");

		//Loop through the georefs, adding each one
		
		for(Georef g : rec.georefs){
			out.println("<georeference lat=\"" + g.pointRadius.x + "\" lng=\"" + g.pointRadius.y + "\" radius=\"" + g.pointRadius.extent + "\"/>");
		}
		
		//Close out the georef list
		
		out.println("</georeferences>");
		
		
	    
	   /* if(locality != null)
	    	output = "Hello " + locality;
	    else
	    	output += "?l was not given ";
	    if (highergeography != null)
    		output += " " + highergeography;
	    else
	    	output += "?hg was not given ";
	    if (interpreter != null)
			output += " " + interpreter;
		else
			output += " ?=i was not given";
	    out.println("<h2>" + output + "</h2>");
	    out.println(request.getParameter("l"));
	    out.println("</body></html>");*/
    }//doGet
    
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

	}*/

}
