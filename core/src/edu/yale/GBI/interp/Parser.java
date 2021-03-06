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

import org.biogeomancer.managers.GeorefDictionaryManager;
import org.biogeomancer.utils.SupportedLanguages;

import edu.yale.GBI.BGMUtil;
import edu.yale.GBI.LocalityInfo;
import edu.yale.GBI.LocalityRec;

public class Parser {

  private static Parser englishInstance;
  private static Parser frenchInstance;
  private static Parser portugueseInstance;
  private static Parser spanishInstance;

  public static Parser getInstance(GeorefDictionaryManager gdm,
      SupportedLanguages lang) {
    switch (lang) {
    case english: {
      return (englishInstance == null ? new Parser(gdm, lang) : englishInstance);
    }
    case spanish: {
      return (spanishInstance == null ? new Parser(gdm, lang) : spanishInstance);
    }
    case french: {
      return (frenchInstance == null ? new Parser(gdm, lang) : frenchInstance);
    }
    case portuguese: {
      return (portugueseInstance == null ? new Parser(gdm, lang)
          : portugueseInstance);
    }
    default:
      return null;
    }

  }

  public static void main(String args[]) {
    Parser p = new Parser(GeorefDictionaryManager.getInstance(),
        SupportedLanguages.spanish);
    // System.out.println(p.regx_BF);
    // System.out.println(p.isUnit("m"));
    // System.out.println(p.isHeading("nw"));
    LocalityRec rc = new LocalityRec();
    // rc.localityString="Zone 14 1357e 2468n";
    rc.localityString = "12 km N (por ruta) Charlo";
    p.process(rc);
    System.out.println(rc.results[0].locType);
    System.out.println(rc.results[0].offset);
    System.out.println(rc.results[0].offsetEW);
    System.out.println(rc.results[0].offsetNS);
    System.out.println(rc.results[0].unit);
    System.out.println(rc.results[0].heading);
    System.out.println(rc.results[0].headingEW);
    System.out.println(rc.results[0].headingNS);
    System.out.println(rc.results[0].subdivision);
    System.out.println(rc.results[0].feature1);
    System.out.println(rc.results[0].feature2);
    System.out.println(rc.results[0].feature3);
    System.out.println(rc.results[0].lat);
    System.out.println(rc.results[0].lng);
    System.out.println(rc.results[0].town);
    System.out.println(rc.results[0].range);
    System.out.println(rc.results[0].section);
    System.out.println(rc.results[0].utmz);
    System.out.println(rc.results[0].utme);
    System.out.println(rc.results[0].utmn);
  }

  final String keyword_AND;
  final String keyword_BY_RD;
  final String keyword_OF;
  final SupportedLanguages lng;
  final GeorefDictionaryManager mgr;
  final String regx_AND;
  final String regx_BF;
  final String regx_BF_MASK;
  final String regx_BY_RD;
  final String regx_FO_MASK;
  // final String regx_FPH;
  // final String regx_JOH_JPOH;
  // final String regx_JOO;
  final String regx_FOP;
  final String regx_FOP_MASK;

  // final String regx_JPOH;
  final String regx_FPOH_JPOH;
  final String regx_FPOH_MASK;
  final String regx_FS;
  final String regx_FS_MASK;
  final String regx_HEADING_EW;
  final String regx_J;
  final String regx_J_MASK;
  // final String regx_JH;
  final String regx_JH_NJ;
  final String regx_JO;
  final String regx_JPOH_MASK;

  final String regx_NEAR_MASK;
  // final String regx_FH_JH;
  final String regx_NF_NJ_NPOM;

  final String regx_NN;
  final String regx_OF;
  final String regx_PBF;

  final String regx_PBF_MASK;
  final String regx_Q;
  final String regx_TRS;
  final String regx_TRSS;

  // final String regx_INNER_COMMA;
  final String regx_TRSS_MASK;

  final String regx_UTM;

  private Parser(GeorefDictionaryManager gdm, SupportedLanguages lang) {

    mgr = gdm;
    lng = lang;

    keyword_AND = mgr.lookup("keyword_AND", lng);
    keyword_OF = mgr.lookup("keyword_OF", lng);
    keyword_BY_RD = mgr.lookup("keyword_BY_RD", lng);

    regx_AND = mgr.lookup("regx_AND", lng);
    regx_OF = mgr.lookup("regx_OF", lng);
    regx_BY_RD = mgr.lookup("regx_BY_RD", lng);

    regx_BF = mgr.lookup("regx_BF", lng);
    // regx_FH_JH=mgr.lookup("regxs_FH_JH", lng, Concepts.regxs, false);
    regx_NF_NJ_NPOM = mgr.lookup("regx_NF_NJ_NPOM", lng);
    regx_J = mgr.lookup("regx_J", lng);
    regx_FS = mgr.lookup("regx_FS", lng);
    regx_NN = mgr.lookup("regx_NN", lng);
    // regx_JH=mgr.lookup("regxs_JH", lng, Concepts.regxs, false);
    regx_JH_NJ = mgr.lookup("regx_JH_NJ", lng);
    regx_TRS = mgr.lookup("regx_TRS", lng);
    regx_TRSS = mgr.lookup("regx_TRSS", lng);
    regx_UTM = mgr.lookup("regx_UTM", lng);
    regx_Q = mgr.lookup("regx_Q", lng);
    regx_PBF = mgr.lookup("regx_PBF", lng);
    // regx_FPH=mgr.lookup("regxs_FPH", lng, Concepts.regxs, false);
    // regx_JOH_JPOH=mgr.lookup("regxs_JOH_JPOH", lng, Concepts.regxs, false);
    // regx_JOO=mgr.lookup("regxs_JOO", lng, Concepts.regxs, false);
    regx_FOP = mgr.lookup("regx_FOP", lng);
    regx_JO = mgr.lookup("regx_JO", lng);
    // regx_JPOH=mgr.lookup("regxs_JPOH", lng, Concepts.regxs, false);
    regx_FPOH_JPOH = mgr.lookup("regx_FPOH_JPOH", lng);

    regx_HEADING_EW = mgr.lookup("regx_HEADING_EW", lng);
    // regx_INNER_COMMA=mgr.lookup("regxs_INNER_COMMA", lng, Concepts.regxs,
    // false);

    regx_TRSS_MASK = mgr.lookup("regx_TRSS_MASK", lng);
    regx_BF_MASK = mgr.lookup("regx_BF_MASK", lng);
    regx_FO_MASK = mgr.lookup("regx_FO_MASK", lng);
    regx_FOP_MASK = mgr.lookup("regx_FOP_MASK", lng);
    regx_J_MASK = mgr.lookup("regx_J_MASK", lng);
    regx_FPOH_MASK = mgr.lookup("regx_FPOH_MASK", lng);
    regx_JPOH_MASK = mgr.lookup("regx_JPOH_MASK", lng);
    regx_NEAR_MASK = mgr.lookup("regx_NEAR_MASK", lng);
    regx_PBF_MASK = mgr.lookup("regx_PBF_MASK", lng);
    regx_FS_MASK = mgr.lookup("regx_FS_MASK", lng);
  }

  final public boolean isHeading(String s) {
    return mgr.lookupConcept(s, lng) == "heading";
  }

  /*
   * final boolean isBetweenKeyword(String s) { return false; }
   * 
   * final boolean isJunctonKeyword(String s) { return false; }
   * 
   * final boolean isNearKeyword(String s) { return false; }
   * 
   * final boolean isPartialKeyword(String s) { return false; }
   */
  final public boolean isUnit(String s) {
    return mgr.lookupConcept(s, lng) == "unit";

  }

  final public ParsingState parseClause(String s, Parser p) {

    ParsingState ps = new StateBegin(new ClauseData(s), p);
    try {
      do {
        ps = ps.parse();

      } while (!(ps instanceof StateFinish));
      ps.parse();
    } catch (Exception e) {
      System.out.println(e.toString());
      StateFinish sf = new StateFinish(ps.pd, "UNK", this);
      sf.li.locType = "UNK";
      ps = sf;
    }

    return ps;
  }

  public final void postprocess(LocalityRec rc, int count) {

  }

  final public String[] preprocess(String queryString) {
    String qs = queryString.trim().replaceAll("\\sal\\s|^Al\\s", " ").replace(
        "&", " " + keyword_AND + " ").trim().replaceAll("\\[", "").trim()
        .replaceAll("\\]", "").trim().replaceAll(" at ", ", ");
    String s = "";

    // Make sure all numbers and letters are separated from each other
    if (qs.matches(".*\\d[\\D\\w].*")) {
      String[] t = qs.split("");
      qs = "";
      for (int i = 1; i < t.length - 1; i++) {

        // Remove any periods after letters, since they are unneccessary
        if (t[i].matches("[a-zA-Z]") && t[i + 1].matches("\\.")) {
          // t[i+1] = t[i];
          t[i + 1] = " ";
          qs = qs + t[i] + " ";
          continue;
        }
        // put a space between letter and number, such as 1A -> A 1
        else if (isNum(t[i]) && t[i + 1].matches("[a-zA-Z]")) {
          qs = qs + t[i] + " ";
        }
        // put a space between letter and number, such as A1 -> A 1
        else if (isNum(t[i + 1]) && t[i].matches("[a-zA-Z]")) {
          qs = qs + t[i] + " ";
        } else {
          qs = qs + t[i];
        }
      }
      qs = qs + t[t.length - 1];
    }

    String[] words = qs.split(" ");

    // Combining numbers and what not... lets add a unit check above this
    for (int i = 0; i < words.length - 1; i++) {
      if (words[i].equals("")) {
        continue;
      }

      // TODO: JRW - Test all locType interps with the following code block
      // turned off
      // if(isNum(words[i])){
      // // Why? This is destroying DecimalLatitude coming through (ex. 36.34563
      // -> 3634563).
      // if(words[i].matches(".*,\\d\\d$") ||
      // words[i].matches(".*\\.\\d\\d\\d.*")){
      // words[i] = words[i].replaceAll("\\.", "");
      // words[i] = words[i].replaceAll(",", ".");
      // }
      // else{
      // words[i] = words[i].replaceAll(",", "");
      // }
      // }

      /*
       * if (isNum(words[i])) {
       * 
       * if(lng.equals(SupportedLanguages.english)){ if(words[i].contains(","))
       * words[i] = words[i].replaceAll(",", ""); } else{
       * if(words[i].contains(".")){ words[i] = words[i].replaceAll("\\.", ""); }
       * if(words[i].contains(",")){ words[i] = words[i].replaceAll(",", "."); } } }
       */

      s = words[i] + words[i + 1];
      if (isNum(s)) {
        words[i] = "";
        words[i + 1] = s;
        // i++;
      }

    }
    qs = buildString(0, words, " ");

    // CHECK FOR THE NEAR MASKS AND REBUILD IF APPROPRIATE
    // Specifically this is checking for "<Feature>, <near>"
    // Designed also so that: "<Feature1>, <Feature2>, <near>" parses as
    // "<Feature1>, <near> <Feature2>"
    if (qs.matches(".*," + regx_NEAR_MASK)) {
      String tokens[] = qs.split(",");
      qs = "";
      if (tokens.length > 1) {
        for (int i = 0; i < tokens.length; i++) {
          if (i + 1 < tokens.length && tokens[i + 1].matches(regx_NEAR_MASK)) {
            tokens[i] = " " + tokens[i + 1].trim() + " " + tokens[i].trim();
            qs = qs + tokens[i] + ",";
            i++;
            continue;
          }
          qs = qs + tokens[i] + ",";
        }
      }
      qs = qs.substring(0, qs.length() - 1);
    }

    /*
     * String [] phases=qs.trim().split(", "); for(int i=0;i<phases.length-1;i++){
     * String s=phases[i]+", "+phases[i+1];
     * 
     * if(s.matches(regx_INNER_COMMA)) { words[i]=phases[i]+" "+phases[i+1];
     * words[i+1]=""; } }
     * 
     * qs=buildString(0,phases,", ");
     */
    return BGMUtil.recoverClauses(qs.trim().replaceAll("[;,:]", ", ")
        .replaceAll("  ", " ").split("[;,:]\\s"), this);
    // return BGMUtil.recoverClauses(qs.trim().split("[;,:]\\s"), this);

  }

  public final void process(LocalityRec rc) {

    // separate the clauses from the locality
    rc.clauseSet = preprocess(rc.localityString);
    rc.results = new LocalityInfo[rc.clauseSet.length];
    int count = 0;

    for (int i = 0; i < rc.clauseSet.length; i++) {

      StateFinish sf = (StateFinish) parseClause(rc.clauseSet[i], this);
      rc.results[i] = sf.li;
      if (rc.results[i].locType.matches("FOP|PBF|FPH"))
        count++;
    }
    // postprocess(rc,count);

  }

  final String buildString(int index, String[] tokens, String delmt) {
    String s = "";

    if (tokens.length > index) {
      for (int i = index; i < tokens.length; i++) {
        if (tokens[i].length() > 0 && !tokens[i].matches("\\s+")) {
          if (i == tokens.length - 1)
            s += tokens[i];
          else
            s += tokens[i] + delmt;
        }
      }
    }
    return s;
  }

  final boolean containNU(ClauseData cd) {
    String[] tokens = cd.words;
    boolean bl = false;
    for (int i = 0; i < tokens.length - 1; i++) {
      if (isNum(tokens[i]) && (tokens.length > i + 1 && isUnit(tokens[i + 1]))) {
        if (!bl) {
          cd.posN1 = i;
          cd.posU1 = i + 1;
          bl = true;
          if (tokens.length > i + 2 && isHeading(tokens[i + 2]))
            cd.posH1 = i + 2;
        } else {
          cd.posN2 = i;
          cd.posU2 = i + 1;
          if (tokens.length > i + 2 && isHeading(tokens[i + 2]))
            cd.posH2 = i + 2;
        }
      }
    }
    if (!bl) {
      for (int i = 0; i < tokens.length - 1; i++) {
        if (isNum(tokens[i]) && cd.posN1 < 0)
          cd.posN1 = i;
        if (isUnit(tokens[i]) && cd.posU1 < 0)
          cd.posU1 = i;
        if (isHeading(tokens[i]) && cd.posH1 < 0)
          cd.posH1 = i;

      }
    }
    return bl;
  }

  final boolean containNUH(ClauseData cd) {
    return cd.posH1 > 0;
  }

  final boolean isNum(String s) {
    if (s == null) {
      return false;
    }
    if (s.length() == 0) {
      return false;
    }
    if (s.charAt(0) == ',' | s.charAt(s.length() - 1) == ',')
      return false;
    s = s.replaceAll(",", "").replace("/", "");
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

}
