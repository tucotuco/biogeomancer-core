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

final class StatenNU extends ParsingState {
  public StatenNU(ClauseData data, Parser p) {
    parser = p;
    pd = data;
  }

  public ParsingState parse() {
    // TODO Auto-generated method stub
    if (pd.clause.toUpperCase().matches(parser.regx_BF)) {
      pd.state = "BF";
      StateFinish state = new StateFinish(pd, "BF", parser);
      pd = null;
      return state;

    } else if (parser.isHeading(pd.words[0])) {
      pd.state = "nNUH";
      StatenNUH state = new StatenNUH(pd, parser);
      pd = null;
      return state;

    } else if (pd.clause.toUpperCase().matches(parser.regx_NF_NJ_NPOM)) {
      pd.state = "nNUN";
      StatenNUN state = new StatenNUN(pd, parser);
      pd = null;
      return state;

    } else if (pd.clause.toUpperCase().matches(parser.regx_FS)) {
      pd.state = "FS";
      StateFinish state = new StateFinish(pd, "FS", parser);
      pd = null;
      return state;

    } else if (pd.clause.toUpperCase().matches(parser.regx_J)) {
      pd.state = "J";
      StateFinish state = new StateFinish(pd, "J", parser);
      pd = null;
      return state;

    } else {
      pd.state = "nNUSpecial";
      StatenNUSpecial state = new StatenNUSpecial(pd, parser);
      pd = null;
      return state;
    }
  }

}
