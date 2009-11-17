/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.biogeomancer.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectState extends DBState {

  public SelectState() {
    super();
  }

  public String getTable() {
    return this.table;
  }

  public String processDataRequest(String whereClause) {
    try {
      Statement stmt = this.conn.createStatement();
      ResultSet sqlRs = null;
      // sqlRs = stmt.executeQuery("select * from " + this.getTable() + "
      // where feature_name like '" + locality + "'");
      sqlRs = stmt.executeQuery("select * from " + this.getTable() + " "
          + whereClause);
      this.sqlRs = sqlRs;
      return "Success";
    } catch (Exception e) {
      System.out.println("Exception in processDataRequest");
      e.printStackTrace();
      return "Failure";
    }
  }

  public void stateEnter(Connection conn, String table) {
    this.conn = conn;
    this.table = table;
  }

  public void stateEnter(Connection conn, String table, String uid) {
    this.conn = conn;
    this.table = table;
    this.uid = uid;
  }

  public void stateExit() {
  }

}
