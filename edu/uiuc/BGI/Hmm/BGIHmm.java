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

package edu.uiuc.BGI.Hmm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import org.biogeomancer.records.Clause;
import org.biogeomancer.records.ClauseState;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;

import bg.edu.uiuc.resource.Hmm;
import bg.edu.uiuc.resource.ObservationInteger;
import bg.edu.uiuc.resource.ViterbiCalculator;

public class BGIHmm {

  public class BGIHmmException extends Exception {
    public BGIHmmException(Exception e) {
      super(e.toString());
      e.printStackTrace();
    }
  }

  public static void main(String[] argv) {
    try {
      BGIHmm test = new BGIHmm();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  Hmm hmm;

  Hashtable stateRevList = new Hashtable();

  public BGIHmm() throws Exception {

    // using a relative directory works, but it has to be relative
    // to where the java program started. since i wasn't sure where
    // the program started, i used the following output to find out:
    // System.out.println("cwd = " + System.getProperty("user.dir"));
    // so i've updated the model.hmm path below...
    hmmModel h = new hmmModel("UIUCLocInterpData/model.hmm");
    hmm = new Hmm(h.Pi, h.A, h.opdfs);

    int hNum = 0;
    String[] hList;
    String aLine;
    BufferedReader br = loadFileData("UIUCLocInterpData/stateRevList");

    while ((aLine = br.readLine()) != null) {
      hList = aLine.split(":");
      hNum = Integer.parseInt(hList[0].trim());
      stateRevList.put(new Integer(hNum), new String(hList[1].trim()));
    }

  }

  // BufferedReader(InputStreamReader(InputStream, encoding)))

  public void doParsing(Rec r, String sType) throws BGIHmm.BGIHmmException {

    // String origStr = r.vLocality.trim();
    String origStr = r.get(sType);
    if (origStr == null) {
      return;
    }
    origStr = origStr.trim().replaceAll("\"", "");
    if (origStr.length() == 0) {
      return;
    }

    Clause nc;
    if ((origStr.toLowerCase().indexOf("no specific") > -1)
        || (origStr.toLowerCase().indexOf("unknown") > -1)) {
      nc = new Clause();
      nc.locType = "NN";
      nc.uLocality = origStr;
      LocSpec nls = new LocSpec();
      nc.locspecs.add(nls);
      nls.featurename = origStr;
      r.clauses.add(nc);
      return;
    }

    try {
      symSequence seq = new symSequence();
      seq.parse(origStr);

      int num = seq.symArray.length;

      Vector<ObservationInteger> vv = new Vector<ObservationInteger>();
      for (int i = 0; i < num; i++) {
        vv.add(new ObservationInteger(seq.symArray[i]));
      }
      Vector<Vector> v = new Vector<Vector>();
      v.add(vv);

      // computer the state sequence from the given hmm model and sequence

      ViterbiCalculator vit = new ViterbiCalculator(v.elementAt(0), hmm);

      int[] stateIdArr = new int[num];
      stateIdArr = vit.stateSequence(); // state id array

      // System.out.println(toString(vit.stateSequence()));

      // post-processing

      int sLength = stateIdArr.length;
      String[] stateArr = new String[sLength]; // state array
      for (int i = 0; i < sLength; i++) {
        stateArr[i] = getStateString(stateIdArr[i]);
      }

      String[] s = new String[2];
      String resultString = "";
      String preParentTag = null;
      String preChildTag = null;
      String curParentTag = "";
      String curChildTag = "";

      for (int i = 0; i < sLength; i++) {
        // System.out.println("stateArray is: " + stateArr[i]);
        s = stateArr[i].split("_");
        curParentTag = s[0];
        curChildTag = s[1];
        if (preParentTag == null) {
          resultString = resultString + "<" + curParentTag + "><" + curChildTag
              + ">" + seq.tokenArray[i];
          preParentTag = curParentTag;
          preChildTag = curChildTag;
        } else if (!preParentTag.equals(curParentTag)) {
          resultString = resultString + "</" + preChildTag + "></"
              + preParentTag + "><" + curParentTag + "><" + curChildTag + ">"
              + seq.tokenArray[i];
          preParentTag = curParentTag;
          preChildTag = curChildTag;

        } else {
          if (preChildTag.equals(curChildTag)) {
            resultString = resultString + " " + seq.tokenArray[i];
          } else {
            resultString = resultString + "</" + preChildTag + "><"
                + curChildTag + ">" + seq.tokenArray[i];
            preChildTag = curChildTag;
          }

        }

        if (i == sLength - 1) {
          resultString = resultString + "</" + curChildTag + "></"
              + curParentTag + ">";
        }

      }

      // return the result

      String[] clausesetArr;
      String clauseStr;
      String[] clauseArr;
      int s1 = 0;
      int s2 = 0;
      String pTag;
      String cTag;
      String cString;

      clausesetArr = resultString.split("</[A-Z]+>");
      for (int i = 0; i < clausesetArr.length; i++) {
        s1 = clausesetArr[i].indexOf("<");
        s2 = clausesetArr[i].indexOf(">");

        pTag = clausesetArr[i].substring(s1 + 1, s2);

        clauseStr = clausesetArr[i].substring(s2 + 1).trim();

        Clause cs = new Clause();
        cs.locType = pTag;
        if (pTag.equals("UNK")) {
          cs.state = ClauseState.CLAUSE_PARSE_ERROR;
        } else {
          cs.state = ClauseState.CLAUSE_PARSED;
        }
        r.clauses.add(cs);

        LocSpec ls = new LocSpec();
        cs.locspecs.add(ls);

        String tmpaddr = new String("");
        String tmptrss = new String("");

        clauseArr = clauseStr.split("</[a-z]+>");
        for (int j = 0; j < clauseArr.length; j++) {
          s1 = clauseArr[j].indexOf("<");
          s2 = clauseArr[j].indexOf(">");
          cTag = clauseArr[j].substring(s1 + 1, s2);
          cString = clauseArr[j].substring(s2 + 1).trim();

          if (pTag.equals("ADDR")) {
            tmpaddr = tmpaddr + " " + cString;
            if (j == clauseArr.length - 1) {
              ls.featurename = tmpaddr;
            }
          } else if (cTag.equals("feature") || cTag.equals("path")) {
            if (ls.featurename == null) {
              ls.featurename = cString;
            } else {
              LocSpec ls2 = new LocSpec();
              ls2.featurename = cString;
              cs.locspecs.add(ls2);
            }
          } else if (pTag.equals("FOO") && cTag.equals("heading")) {
            if (ls.vheadingns == null) {
              ls.vheadingns = cString;
            } else {
              ls.vheadingew = cString;
            }

          } else if (cTag.equals("heading")) {
            ls.vheading = cString;
          } else if (pTag.equals("UTM") && cTag.equals("offset")) {
            ls.vutmzone = cString;
          } else if (pTag.equals("E") && cTag.equals("offset")) {
            ls.velevation = cString;
          } else if (pTag.equals("FOO") && cTag.equals("offset")) {
            if (ls.voffsetns == null) {
              ls.voffsetns = cString;
            } else {
              ls.voffsetew = cString;
            }
          } else if (cTag.equals("offset")) {
            ls.voffset = cString;
          } else if (pTag.equals("E") && cTag.equals("unit")) {
            ls.velevationunits = cString;
          } else if (pTag.equals("FOO") && cTag.equals("unit")) {
            if (ls.voffsetnsunit == null) {
              ls.voffsetnsunit = cString;
            } else {
              ls.voffsetewunit = cString;
            }
          } else if (cTag.equals("unit")) {
            ls.voffsetunit = cString;
          } else if (cTag.equals("sub") || cTag.equals("marker")) {
            ls.vsubdivision = cString;
          } else if (cTag.equals("lat")) {
            ls.vlat = cString;
          } else if (cTag.equals("long")) {
            ls.vlng = cString;
          } else if (cTag.equals("h")) {
            ls.vutmzone = cString;
          } else if (cTag.equals("x")) {
            ls.vutme = cString;
          } else if (cTag.equals("y")) {
            ls.vutmn = cString;
          } else if (cTag.equals("t")) {
            String[] tmparr = cString.split(" ");
            for (int l = 0; l < tmparr.length; l++) {
              if (Character.isDigit(tmparr[l].charAt(0))
                  && ls.vtownship == null) {
                ls.vtownship = tmparr[l];
              } else if (tmparr[l].toLowerCase().startsWith("n")
                  || tmparr[l].toLowerCase().startsWith("s")) {
                ls.vtownshipdir = tmparr[l];
              }
            }
          } else if (cTag.equals("r")) {
            String[] tmparr = cString.split(" ");
            for (int l = 0; l < tmparr.length; l++) {
              if (Character.isDigit(tmparr[l].charAt(0)) && ls.vrange == null) {
                ls.vrange = tmparr[l];
              } else if (tmparr[l].toLowerCase().startsWith("e")
                  || tmparr[l].toLowerCase().startsWith("w")) {
                ls.vrangedir = tmparr[l];
              }
            }
          } else if (cTag.equals("s")) {
            if (Character.isDigit(cString.charAt(0))) {
              ls.vsection = cString;
            }
          } else if (pTag.equals("TRSS")) {
            tmptrss = tmptrss + " " + cString;
            if (j == clauseArr.length - 1) {

              ls.vsubdivision = tmptrss;
            }
          } else if (pTag.equals("UNK")) {
            cs.uLocality = cString;
            ls.featurename = cString;
          }
        }

      } // end of for parent tag
    } catch (Exception e) {
      throw this.new BGIHmmException(e);
    }

  } // end of doParsing

  public void doParsing(RecSet rs) throws BGIHmm.BGIHmmException {
    for (int i = 0; i < rs.recs.size(); i++) {
      doParsing(rs.recs.get(i), "highergeography");
      doParsing(rs.recs.get(i), "continent");
      doParsing(rs.recs.get(i), "waterbody");
      doParsing(rs.recs.get(i), "islandgroup");
      doParsing(rs.recs.get(i), "island");
      doParsing(rs.recs.get(i), "country");
      doParsing(rs.recs.get(i), "stateprovince");
      doParsing(rs.recs.get(i), "county");
      for (int j = 0; j < rs.recs.get(i).clauses.size(); j++) {
        if (rs.recs.get(i).clauses.get(j).locType.equalsIgnoreCase("F")
            || rs.recs.get(i).clauses.get(j).locType.equalsIgnoreCase("P")) {
          rs.recs.get(i).clauses.get(j).locType = new String("ADM");
        }
      }
      doParsing(rs.recs.get(i), "locality");
    }
  }

  private String getStateString(int id) {
    String stateStr = (String) stateRevList.get(id);
    if (stateStr != null) {
      return stateStr;
    } else {
      return "UNK_unk";
    }
  }

  private BufferedReader loadFileData(String fileName) {
    BufferedReader reader = null;
    try {
      ClassLoader loader = this.getClass().getClassLoader();
      reader = new BufferedReader(new InputStreamReader(loader
          .getResourceAsStream(fileName)));
    } catch (Exception e) {
      System.out.println("could not read data file " + fileName + ": "
          + e.toString());
    }
    return reader;
  }

}
