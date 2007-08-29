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

import edu.yale.GBI.LocalityInfo;

final class StateFinish extends ParsingState {
  // unfinished rearrange trss by the oder t-r-s-s
  private static String reOrderTrss(Parser parser, String trss) {
    // TODO Auto-generated method stub
    switch (parser.lng) {
    case english: {

    }

    }
    return null;
  }

  String localityType;

  LocalityInfo li;

  public StateFinish(ClauseData data, String locType, Parser p) {
    li = new LocalityInfo();
    parser = p;
    pd = data;
    localityType = locType;
  }

  public ParsingState parse() {

    // TODO Auto-generated method stub

    // fill a li with data from pd
    li.locStr = pd.clause;
    li.locType = localityType;
    if (localityType == "F") {
      li.feature1 = pd.clause;
    } else if (localityType == "ADDR") {
      li.feature1 = pd.clause.trim();
    } else if (localityType == "E") {
      if (pd.words.length == 1) {
        String[] es = pd.clause.split("\\d+");
        li.unit = es[es.length - 1];
        li.evelation = pd.clause.replace(li.unit, "");
      } else {
        li.evelation = pd.words[0];
        li.unit = pd.words[1];
      }
    } else if (localityType == "LL") {
      String[] ll;
      if (pd.clause.contains(",") && !pd.clause.matches(".*\\d,\\d.*"))
        ll = pd.clause.split(",");
      else
        ll = pd.clause.split("\\s");
      if (ll.length == 4) {
        ll[0] += " " + ll[1];
        ll[3] = ll[2] + " " + ll[3];
      }
      li.lat = (ll[0].toUpperCase().matches(".+" + parser.regx_HEADING_EW) ? ll[ll.length - 1]
          : ll[0]);
      li.lng = (ll[0].toUpperCase().matches(".+" + parser.regx_HEADING_EW) ? ll[0]
          : ll[ll.length - 1]).trim();
    } else if (localityType == "POM") {
      li.subdivision = pd.words[0] + " " + pd.words[1];
      li.feature1 = pd.clause.replace(pd.words[0], "").replace(pd.words[1], "")
          .trim();
    } else if (localityType == "TRS" || localityType == "TRSS") {
      // pd.clause=reOrderTrss(parser, pd.clause);
      String[] trss = pd.clause.split(parser.regx_TRSS_MASK);
      li.town = trss[1].trim();
      li.range = trss[2].trim();
      li.section = trss[3].trim();
      if (pd.posH1 >= 0)
        li.subdivision = parser.buildString(pd.posH1, pd.words, " ");
    } else if (localityType == "UTM") {
      String[] utm = pd.clause.replace(",", "").split(parser.regx_TRSS_MASK);
      li.utmz = utm[1];
      li.utme = utm[2];
      li.utmn = utm[3];
    } else if (localityType == "BF") {
      String[] b = pd.clause.split(parser.regx_BF_MASK);
      for (int i = 0; i < b.length; i++) {
        if (b[i].length() > 2) {
          if (li.feature1.length() < 2)
            li.feature1 = b[i];
          else
            li.feature2 = b[i];
        }
      }
    } else if (localityType == "FH") {
      li.heading = pd.words[pd.posH1];

      li.feature1 += pd.clause.replace(li.heading, "").replaceAll(
          parser.regx_OF, "").trim();

    } else if (localityType == "FS") {

      String[] fs = pd.clause.split(parser.regx_OF);
      if (fs.length > 1)
        li.feature1 = fs[1];
      else
        li.feature1 = pd.clause.toUpperCase().replaceAll(parser.regx_FS_MASK,
            "").trim();
      li.subdivision = pd.clause.toLowerCase().replace(
          li.feature1.toLowerCase(), "").trim();

    } else if (localityType == "FO") {
      String[] fo = pd.clause.split(parser.regx_FO_MASK);
      li.feature1 = fo[fo.length - 1];
      if (li.feature1.length() == 1)
        li.feature1 = parser.buildString(pd.posU1 + 2, pd.words, " ");
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];
    } else if (localityType == "FOP") {
      String[] fop = pd.clause.split(parser.regx_FOP_MASK);
      li.feature1 = fop[1];
      li.feature2 = fop[2];
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];
    } else if (localityType == "FOH" || localityType == "JOH") {
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];
      li.heading = pd.words[pd.posH1];
      li.feature1 = pd.clause.replace(li.offset, " ").replace(li.unit, " ")
          .replace(li.heading, " ").replaceAll(parser.regx_OF, " ").trim();
      if (localityType == "JOH") {
        String[] joh = li.feature1.split(parser.regx_J_MASK);
        li.feature1 = (joh[0].length() == 0 ? joh[1] : joh[0]);
        li.feature2 = joh[joh.length - 1];
      }
    } else if (localityType == "FOO" || localityType == "JOO") {
      li.unit = pd.words[pd.posU1];
      if (pd.words[pd.posH1].toUpperCase().matches(parser.regx_HEADING_EW)) {
        li.offsetEW = pd.words[pd.posN1];
        li.headingEW = pd.words[pd.posH1];
        li.offsetNS = pd.words[pd.posN2];
        li.headingNS = pd.words[pd.posH2];
      } else {
        li.offsetNS = pd.words[pd.posN1];
        li.headingNS = pd.words[pd.posH1];
        li.offsetEW = pd.words[pd.posN2];
        li.headingEW = pd.words[pd.posH2];
      }
      li.feature1 = pd.clause.replace(li.offsetEW, "").replace(li.unit, "")
          .replace(li.headingEW, "").replace(li.offsetNS, "").replace(li.unit,
              "").replace(li.headingNS, "").replaceAll(parser.regx_OF, " ")
          .replaceAll(parser.regx_AND, " ").trim();
      if (localityType == "JOO") {
        String[] joh = (" " + parser.buildString(pd.posH2 + 1, pd.words, " "))
            .replaceAll(parser.regx_OF, " ").trim().split(parser.regx_J_MASK);
        if (joh.length > 2)
          li.feature1 = joh[joh.length - 2];
        li.feature2 = joh[joh.length - 1];
      }
    } else if (localityType == "FPOH") {
      String[] fpoh = pd.clause.replaceAll("[()]", " ").split(
          parser.regx_FPOH_MASK);
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];
      li.heading = pd.words[pd.posH1];
      li.feature1 = fpoh[0].replace(li.offset, "").replace(li.unit, "")
          .replace(" " + li.heading, "").replaceAll(parser.regx_OF, "").trim();
      li.feature2 = fpoh[1].replace(li.offset, "").replace(li.unit, "")
          .replace(li.heading, "").replaceAll(parser.regx_OF, "").trim();
    } else if (localityType == "JPOH") {
      String[] jpoh = pd.clause.replaceAll(parser.regx_OF, " ").replaceAll(
          "[(|)]", " ").trim().split(parser.regx_JPOH_MASK);
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];
      li.heading = pd.words[pd.posH1];
      li.feature1 = jpoh[1];
      li.feature2 = jpoh[2];
      // if(jpoh.length>2)li.feature3=jpoh[2];
    } else if (localityType == "J") {
      String[] j = pd.clause.replaceAll(parser.regx_OF, " ").trim().split(
          parser.regx_J_MASK);
      String s = parser.buildString(0, j, ";").trim();
      j = s.split(";");
      li.feature1 = j[0];
      li.feature2 = j[1];
    } else if (localityType == "JO") {
      String[] jo = pd.clause.split(parser.regx_FOP_MASK);
      li.feature1 = jo[1];
      li.feature2 = jo[2];
      li.offset = pd.words[pd.posN1];
      li.unit = pd.words[pd.posU1];

    } else if (localityType == "JH") {
      li.heading = pd.words[pd.posH1];
      String[] jh = pd.clause.split(parser.regx_AND);
      li.feature1 = jh[0].replace(li.heading, " ").replaceAll(
          parser.regx_J_MASK, " ").replaceAll(parser.regx_OF, " ").trim();
      li.feature2 = jh[1];
    } else if (localityType == "NF") {
      li.feature1 = pd.clause.replaceAll(parser.regx_NEAR_MASK, "").replaceAll(
          parser.regx_OF, " ").trim();
    } else if (localityType == "NJ") {
      String[] nj = pd.clause.split(parser.regx_J_MASK);
      // nj=nj[nj.length-1].split(parser.regx_AND);
      li.feature1 = nj[nj.length - 2]
      // .replaceAll(parser.regx_J_MASK, "")
          .replaceAll(parser.regx_OF, " ").trim();
      li.feature2 = nj[nj.length - 1];
    } else if (localityType == "NPOM") {
      String[] npom = pd.clause.replaceAll(parser.regx_NEAR_MASK, "").split(
          "\\s");
      li.subdivision = npom[0] + " " + npom[1];
      li.feature1 = pd.clause.replaceAll(parser.regx_NEAR_MASK, "").replace(
          npom[0], "").replace(npom[1], "").trim();
    } else if (localityType == "PBF") {
      String[] pbf = pd.clause.split(parser.regx_PBF_MASK);
      li.feature1 = pbf[0];
      li.feature2 = pbf[1];
      li.feature3 = pbf[2];
    } else if (localityType == "FPH") {

      li.feature2 = parser.buildString(pd.posH1 + 2, pd.words, " ");
      li.heading = pd.words[pd.posH1];
      li.feature1 = parser.buildString(0, pd.words, " ").replaceAll(
          parser.regx_OF, " ").replace(li.heading, " ").replace(li.feature2,
          " ").trim();
    }

    return null;
  }

}
