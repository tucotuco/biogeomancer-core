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

import org.biogeomancer.records.RecSet;

abstract public class DBState {

  public String uid;
  public ResultSet sqlRs;
  protected RecSet rs = null;
  protected Connection conn;
  protected String table;

  protected DBState() {
    // System.out.println("DBState constructed...");
  }

  abstract public String processDataRequest(String sqlStrVal);

  // abstract public String processDataRequest(RecSet recs);
  abstract public void stateEnter(Connection conn, String table, String uid);

  abstract public void stateExit();

}
