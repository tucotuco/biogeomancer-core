/*
 * DBHandler.java
 * Created on Mar 25, 2005
 */
package org.biogeomancer.utils;

//Java Packages
import java.sql.Connection;

import org.biogeomancer.records.*;

public class DBHandler {
    
    private Connection conn = null;   
    private String dataOp = null;
    private String table = null;
    private String uid = null;
    
    /**
     * Constructor for DBHandler.
     *
     * @param connthe connection to the database
     * @param table the table in the database
     * @param dataOp CRUD operation
     * @param rs the recset
     */
    public DBHandler(Connection conn, String table, String dataOp, String uid) {
      
	  this.conn = conn;
	  this.table = table;
	  this.dataOp = dataOp;
	  this.uid = uid;

    }
    
    public DBState getState() {
	  DBState dbState = null;

	  if (this.dataOp.equalsIgnoreCase("insert")) {
	    dbState = new InsertState();
	    dbState.stateEnter(this.conn, this.table, this.uid);
	  }
      if (this.dataOp.equalsIgnoreCase("select")) {
	    dbState = new SelectState();
	    dbState.stateEnter(this.conn, this.table, this.uid);
	  }	

	
	  return dbState;

    }
}
