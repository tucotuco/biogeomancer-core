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

final class StateNUnH extends ParsingState {
  public StateNUnH(ClauseData data, Parser p) {
    parser = p;
    pd = data;
  }

  public ParsingState parse() {
    // TODO Auto-generated method stub
    if (pd.words.length <= 3) {
      pd.state = "E";
      StateFinish state = new StateFinish(pd, "E", parser);
      pd = null;
      return state;

    }

    else if (pd.clause.toUpperCase().matches(parser.regx_JO)) {
      pd.state = "JO";
      StateFinish state = new StateFinish(pd, "JO", parser);
      pd = null;
      return state;

    }

    else if (pd.clause.toUpperCase().matches(parser.regx_FOP)) {
      pd.state = "FOP";
      StateFinish state = new StateFinish(pd, "FOP", parser);
      pd = null;
      return state;

    }

    else if (pd.clause.toUpperCase().matches(parser.regx_FPOH_JPOH)) {
      pd.state = "FPOH";
      pd.words = pd.clause.replaceAll(parser.regx_BY_RD, " ").trim().split(
          "\\s+");
      parser.containNU(pd);
      StateFinish state = new StateFinish(pd, "FPOH", parser);
      pd = null;
      return state;
    } else {
      pd.state = "FO";
      StateFinish state = new StateFinish(pd, "FO", parser);
      pd = null;
      return state;

    }
  }

}
