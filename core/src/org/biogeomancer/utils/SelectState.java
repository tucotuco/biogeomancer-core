/*
 * SelectState.java
 * Created on Apr 28, 2005
 */
package org.biogeomancer.utils; 


import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;

import org.biogeomancer.records.*;

 
public class SelectState extends DBState {

    public SelectState() 
    {
  	  super();
    }

    public void stateEnter(Connection conn, String table, String uid) {
  	  this.conn = conn;
  	  this.table = table;
  	  this.uid = uid;
    }
    
	public String processDataRequest(String whereClause) {
		try {
            Statement stmt = this.conn.createStatement();
            ResultSet sqlRs = null;
            //sqlRs = stmt.executeQuery("select * from " + this.getTable() + " where feature_name like '" + locality + "'");
            sqlRs = stmt.executeQuery("select * from " + this.getTable() + " " + whereClause);
            this.sqlRs = sqlRs;
            return "Success";    
		} catch (Exception e) {
			System.out.println("Exception in processDataRequest");
			e.printStackTrace();
			return "Failure";
		}                       
	}
    	
    
    public void stateEnter(Connection conn, String table) 
    {
  	  this.conn = conn;
	  this.table = table;
    }
    
    public void stateExit() 
    {
    }

    public String getTable() 
    {
  	  return this.table;
    }    

}
