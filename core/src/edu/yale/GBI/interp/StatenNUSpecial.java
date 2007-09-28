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

final class StatenNUSpecial extends ParsingState {

  public StatenNUSpecial(ClauseData data, Parser p) {
    parser = p;
    pd = data;
  }

  public ParsingState parse() {
    // TODO Auto-generated method stub
    if (pd.words.length < 3
        && parser.isUnit(pd.clause.replaceAll("\\d+", "").trim())) {
      pd.state = "E";
      StateFinish state = new StateFinish(pd, "E", parser);
      pd = null;
      return state;

    } else if (pd.clause.toUpperCase().matches(parser.regx_NN)) {
      pd.state = "NN";
      StateFinish state = new StateFinish(pd, "NN", parser);
      pd = null;
      return state;

    } else if (parser.isNum(pd.clause.replaceAll(
        "[nNsSeEwWMmDdoO.',:\\u00B0\\s]", ""))) {
      pd.state = "LL";
      StateFinish state = new StateFinish(pd, "LL", parser);
      pd = null;
      return state;

    } else if (pd.posN1 == 0 && pd.words.length >= 3) {
      pd.state = "ADDR";
      StateFinish state = new StateFinish(pd, "ADDR", parser);
      pd = null;
      return state;

    }

    else if (pd.posN1 == 1 && pd.posU1 == 0) {
      pd.state = "POM";
      StateFinish state = new StateFinish(pd, "POM", parser);
      pd = null;
      return state;

    } 

    else if (pd.clause.toUpperCase().trim().matches(parser.regx_TRS + parser.regx_TRSS + "?")) {
      pd.state = "TRS";
      StateFinish state = new StateFinish(pd, "TRS", parser);
      pd = null;
      return state;

    }
    
    else if (pd.clause.toUpperCase().trim().matches(parser.regx_TRS + parser.regx_TRSS + ".*")) {
        pd.state = "TRSS";
        StateFinish state = new StateFinish(pd, "TRSS", parser);
        pd = null;
        return state;

      }
    
    else if (pd.clause.toUpperCase().trim().matches(parser.regx_UTM)) {
      pd.state = "UTM";
      StateFinish state = new StateFinish(pd, "UTM", parser);
      pd = null;
      return state;

    }

    else if (pd.clause.toUpperCase().matches(parser.regx_Q)) {
      pd.state = "F";
      StateFinish state = new StateFinish(pd, "F", parser);
      pd = null;
      return state;

    }

    else {
      pd.state = "nNUOthers";
      StatenNUOthers state = new StatenNUOthers(pd, parser);
      pd = null;
      return state;
    }
  }

}
