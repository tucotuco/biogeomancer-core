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

// Java Packages
import java.sql.Connection;

public class DBHandler {

  private Connection conn = null;
  private String dataOp = null;
  private String table = null;
  private String uid = null;

  /**
   * Constructor for DBHandler.
   * 
   * @param connthe
   *          connection to the database
   * @param table
   *          the table in the database
   * @param dataOp
   *          CRUD operation
   * @param rs
   *          the recset
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
