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

final class StateNUH2 extends ParsingState {
  public StateNUH2(ClauseData data, Parser p) {
    parser = p;
    pd = data;
  }

  public ParsingState parse() {
    // TODO Auto-generated method stub
    if (pd.clause.toUpperCase().matches(parser.regx_JH_NJ)) {
      pd.state = "JOO";
      StateFinish state = new StateFinish(pd, "JOO", parser);
      pd = null;
      return state;

    } else {
      pd.state = "FOO";
      StateFinish state = new StateFinish(pd, "FOO", parser);
      pd = null;
      return state;

    }
  }

}
