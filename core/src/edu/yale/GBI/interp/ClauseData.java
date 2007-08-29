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

package edu.yale.GBI.interp;

class ClauseData {

  String[] words;
  String clause;
  String state;

  int posN1;
  int posU1;
  int posH1;
  int posN2;
  int posU2;
  int posH2;

  public ClauseData(String s) {
    posN1 = -1;
    posU1 = -1;
    posH1 = -1;
    posN2 = -1;
    posU2 = -1;
    posH2 = -1;
    state = "BEGIN";
    clause = s;
    words = clause.split("\\s+");
  }

}
