/*
 * DBState.java
 * Created on Mar 25, 2005
 */
package org.biogeomancer.utils;

import java.sql.Connection;
import java.sql.ResultSet;

import org.biogeomancer.records.*;
 
abstract public class DBState {
    
    protected RecSet rs = null;
    protected Connection conn;
    protected String table;
    public String uid;
    public ResultSet sqlRs;

    
    protected DBState() {
	  //System.out.println("DBState constructed...");
    }
    
    abstract public String processDataRequest(String sqlStrVal);
    //abstract public String processDataRequest(RecSet recs);
    abstract public void stateEnter(Connection conn, String table, String uid);
    abstract public void stateExit();
    
}
