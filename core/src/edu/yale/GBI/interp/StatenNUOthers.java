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

class StatenNUOthers extends ParsingState {

  public StatenNUOthers(ClauseData data, Parser p) {
    parser = p;
    pd = data;
  }

  public ParsingState parse() {
    // TODO Auto-generated method stub

    if (pd.clause.toUpperCase().matches(parser.regx_PBF)) {
      pd.state = "PBF";
      StateFinish state = new StateFinish(pd, "PBF", parser);
      pd = null;
      return state;

    }

    else if (pd.posH1 >= 0 && pd.words.length > pd.posH1 + 2
        && pd.words[pd.posH1 + 1].toUpperCase().matches(parser.keyword_OF)) {
      pd.state = "FPH";
      StateFinish state = new StateFinish(pd, "FPH", parser);
      pd = null;
      return state;

    }

    else if (!pd.clause.replaceAll("\\'|\\.|-", "").matches(".*\\p{Punct}.*")
        && pd.clause.matches("[A-Z].*")) {
      pd.state = "F";
      StateFinish state = new StateFinish(pd, "F", parser);
      pd = null;
      return state;

    }

    else {
      pd.state = "UNK";
      StateFinish state = new StateFinish(pd, "UNK", parser);
      pd = null;
      return state;

    }
  }

}
