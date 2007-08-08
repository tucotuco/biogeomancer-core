/*
 * InsertState.java
 * Created on Mar 25, 2005
 */
package org.biogeomancer.utils;

import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;




public class InsertState extends DBState {
    
    public InsertState() {
	  super();
    } 

    public void stateEnter(Connection conn, String table, String uid) {
	  this.conn = conn;
	  this.table = table;
	  this.uid = uid;
	  
	  try {
	    //Statement stmt = this.conn.createStatement();
	    //System.out.println("delete from " + this.table);
	    //System.out.println("delete: " + stmt.executeUpdate("delete from " + this.table));

	  } catch (Exception e) {
	    System.out.println("Exception creating statement during insert state enter");
	  }
	  
    }
    
    public String processDataRequest(String sqlVals) {
        
        try {
            
            System.out.println("Create statement");
            //this.conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/biogeomancer","Owner", "Owner");
            Statement stmt = this.conn.createStatement();                      

          	String recFields = null;
            String transdate = null;
            Calendar cal = null;

            recFields = "\"uid\",\"object_key\",\"locality\",\"the_geom\"";

            String sqlUpdate = "INSERT INTO \"" + this.getTable() + "\" (" + recFields + ") VALUES (" + sqlVals + " )";

            stmt.executeUpdate(sqlUpdate);            
            System.out.println(sqlUpdate);   
            stmt.close();
            stmt = null;
            
            return "Success";
	} catch (Exception e) {
	    System.out.println("Exception in processDataRequest");
	    e.printStackTrace();
	    return "Failure";
	}

	
    }

    public void stateExit() 
    {
    }
    
    public String getTable() {
	  return this.table;
    }

}
