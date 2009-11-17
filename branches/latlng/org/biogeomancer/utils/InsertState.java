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
import java.sql.Statement;
import java.util.Calendar;

public class InsertState extends DBState {

  public InsertState() {
    super();
  }

  public String getTable() {
    return this.table;
  }

  public String processDataRequest(String sqlVals) {

    try {

      System.out.println("Create statement");
      // this.conn =
      // DriverManager.getConnection("jdbc:postgresql://localhost:5432/biogeomancer","Owner",
      // "Owner");
      Statement stmt = this.conn.createStatement();

      String recFields = null;
      String transdate = null;
      Calendar cal = null;

      recFields = "\"uid\",\"object_key\",\"locality\",\"the_geom\"";

      String sqlUpdate = "INSERT INTO \"" + this.getTable() + "\" ("
          + recFields + ") VALUES (" + sqlVals + " )";

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

  public void stateEnter(Connection conn, String table, String uid) {
    this.conn = conn;
    this.table = table;
    this.uid = uid;

    try {
      // Statement stmt = this.conn.createStatement();
      // System.out.println("delete from " + this.table);
      // System.out.println("delete: " + stmt.executeUpdate("delete from " +
      // this.table));

    } catch (Exception e) {
      System.out
          .println("Exception creating statement during insert state enter");
    }

  }

  public void stateExit() {
  }

}
