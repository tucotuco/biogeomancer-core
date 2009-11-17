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

package edu.yale.GBI;

import java.util.Hashtable;

import edu.yale.GBI.interp.Parser;

public class BGMUtil {

  /**
   * parseLocality()
   * 
   * Parses the locality
   * 
   * @param LocalityRec
   *          the locality record.
   */
  public static void parseLocality(LocalityRec r, Parser p) {
    try {
      String s = r.localityString;
      s = s.replace("(by air)", "").replace("(BY AIR)", "");
      s = s.replace("(", "").replace(")", "").replace("[", "").replace("]", "");
      String[] clauses = null;
      // if (s.contains(";") && s.matches(".*\\d.*"))
      // clauses=s.split(";");
      // else
      clauses = s.split(";|,");
      for (int i = 0; i < clauses.length; i++) {
        if (clauses[i] == null)
          clauses[i] = "";
        clauses[i] = nmlClause(clauses[i]);
      }
      if (clauses.length > 1)
        clauses = recoverClauses(clauses,p);
      r.clauseSet = clauses;
      LocalityInfo[] li = new LocalityInfo[clauses.length];
      if (s.length() > 0)
        r.results = popIntepData(li, r);
      else
        r.results = new LocalityInfo[0];

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * recoverClauses()
   * 
   * Recover coma seperated NUH, and ll.
   * 
   * @param clauses
   *          string array of clauses
   */
  public static String[] recoverClauses(String[] clauses, Parser parser) {
    int clauseCount = clauses.length;
    for (int i = 0; i < clauses.length; i++) {

      if (clauses.length > i + 1
          && isNum((clauses[i] + clauses[i + 1]).replaceAll(
              "[nNsSeEwWMmDd.',:\\u00B0\\s]", ""))) {
        clauses[i] = clauses[i].trim() + ", " + clauses[i + 1].trim();
        clauses[i + 1] = "NULL";
        clauseCount--;
        i++;
      } else if (clauses[i].matches("\\d+[dD\\u00B0].+[nNsS]")
          && clauses[i + 1].matches("\\d+[dD\\u00B0].+[eEwW]")) {
        clauses[i] += ", " + clauses[i + 1];
        clauses[i + 1] = "NULL";
        clauseCount--;
        i++;
      } else if (clauses[i].matches("[Ss][Ee][Cc]\\.?\\s?\\d+.*")
          || clauses[i].matches("[Tt]\\.?\\d+\\s?[SsNn].*")
          || clauses[i].matches("[ESWNeswn]+\\d/\\d+.*")
          || clauses[i].matches("[Tt][Oo][Ww][Nn][Ss][Hh][Ii][Pp]\\s?\\d+.*")
          || clauses[i].matches(".*[Rr][Aa][Nn][Gg][Ee]\\s?\\d+.*")) {
        for (int j = i + 1; j < clauses.length; j++) {
          if (clauses[j]
              .matches("[Ss][Ee][Cc]\\.?\\s?\\d+.*|[Tt]\\.?\\d+\\s?[SsNn].*|[Rr]\\.?\\d+\\s?[EeWw].*|[ESWNeswn]+\\d/\\d+.*")
              || clauses[j].matches("[ESWNeswn]+\\d/\\d+.*")
              || clauses[j]
                  .matches(".*[Tt][Oo][Ww][Nn][Ss][Hh][Ii][Pp]\\s?\\d+.*")
              || clauses[j].matches(".*[Rr][Aa][Nn][Gg][Ee]\\s?\\d+.*")
              || clauses[j].toUpperCase().matches(
                  "[NW|NE|SW|SE].*\\d.*[of the].*\\d.*")) {
            clauses[i] += ", " + clauses[j];
            clauses[j] = "NULL";
            clauseCount--;
          }
        }
        i++;
      } else if (i > 0 && !clauses[i].matches(".*\\d+.*")
          && (clauses[i].endsWith("OF") || clauses[i].endsWith("of"))) {
        clauses[i - 1] = clauses[i] + " " + clauses[i - 1];
        clauses[i] = "NULL";
        clauseCount--;
        i++;
      } else if (isNUH(clauses[i],parser) == 0) {
        if (i == clauses.length - 1) {
          clauses[i - 1] += " " + clauses[i];
          clauses[i] = "NULL";
          clauseCount--;
        } else if (i < clauses.length - 1 && isNUH(clauses[i + 1],parser) == 0) {
          if (i == 0) {
            clauses[i] += " " + clauses[i + 1];
            clauses[i] += " " + clauses[i + 2];
            clauses[i + 1] = "NULL";
            clauseCount--;
            clauses[i + 2] = "NULL";
            clauseCount--;
          } else {
            clauses[i - 1] += " " + clauses[i];
            clauses[i - 1] += " " + clauses[i + 1];
            clauses[i] = "NULL";
            clauseCount--;
            clauses[i + 1] = "NULL";
            clauseCount--;
          }
        } else if (i < clauses.length - 1 && isNUH(clauses[i + 1],parser) == 1) {
          clauses[i] += " " + clauses[i + 1];
          clauses[i + 1] = "NULL";
          clauseCount--;
        } else {
          if (i == 0) {
            clauses[i] += " " + clauses[i + 1];
            clauses[i + 1] = "NULL";
            clauseCount--;
          } else {
            clauses[i - 1] += " " + clauses[i];
            clauses[i] = "NULL";
            clauseCount--;
          }
        }
        i++;
      }
    }

    String[] nmClauses = new String[clauseCount];
    for (int i = 0, j = 0; i < nmClauses.length; i++, j++) {
      while (clauses[j] == "NULL")
        j++;
      if (clauses[j].matches(".*\\d+[a-zA-Z]+.*")) {
        String[] token = clauses[j].split("\\s+");
        clauses[j] = "";
        for (int k = 0; k < token.length; k++) {
          if (token[k].trim().matches(".*\\d+[a-zA-Z]+.*")) {
            String n = token[k].replaceAll("\\d?\\.?\\,?\\d+", "").trim();
            if (parser.isUnit(n))
              token[k] = token[k].replace(n, "") + " " + n;
          }
          clauses[j] += token[k] + " ";
        }
        clauses[j].trim();
      }
      nmClauses[i] = clauses[j];
    }
    return nmClauses;
  }

  // special formats
  private static boolean c1(LocalityInfo lInfo, String clause, LocalityRec r) {

    if (clause == null)
      clause = "";
    String nClause = nmlClause(clause);
    String[] tokens = nClause.split("\\s+");

    if (clause.length() == 0) {

      // lInfo.locType="ADM";
      // lInfo.feature1=nClause;
      return true;

    } else if (tokens.length < 3
        && nClause.replaceAll("[.,]", "").matches("\\d+\\s?\\w+")) {

      lInfo.locType = "E";
      if (tokens.length == 1) {
        String[] es = nClause.split("\\d+");
        lInfo.unit = es[es.length - 1];
        lInfo.evelation = nClause.replace(lInfo.unit, "");
      } else {
        lInfo.evelation = tokens[0];
        lInfo.unit = tokens[1];
      }
      return true;

    } else if (nClause.equalsIgnoreCase("UNKNOWN")
        || nClause.contains("no specific locality")) {

      lInfo.locType = "NN";
      return true;

    } else if (nClause.matches("\\d+[dD\\u00B0].+\\d+[dD\\u00B0].+")) {

      lInfo.locType = "LL";
      String[] ll;
      if (nClause.contains(","))
        ll = nClause.split(",");
      else
        ll = nClause.split("\\s");
      lInfo.lat = ll[0];
      lInfo.lng = ll[ll.length - 1];
      return true;

    } else if (isNum(tokens[0]) && !inTable(BGI.units, tokens[1])
        && (!(tokens.length >= 3 && inTable(BGI.units, tokens[2])))) {

      lInfo.locType = "ADDR";
      lInfo.feature1 = nClause.replace(tokens[0], "").trim();
      return true;

    } else if (tokens.length > 2 && isNum(tokens[1])
        && inTable(BGI.units, tokens[0])) {
      // pom_1 unit ahead of number

      lInfo.locType = "POM";
      lInfo.unit = tokens[0];
      lInfo.subdivision = tokens[0] + " " + tokens[1];
      lInfo.feature1 = nClause.replace(tokens[0], "").replace(tokens[1], "")
          .trim();
      return true;

    } else if (nClause.matches(".*[Tt]\\.?\\d+\\s?[SsNn].*")
        && nClause.matches(".*[Rr]\\.?\\d+\\s?[EeWw].*")
        || nClause.matches("[Tt][Oo][Ww][Nn][Ss][Hh][Ii][Pp]\\s?\\d+.*")
        || nClause.matches(".*[Rr][Aa][Nn][Gg][Ee]\\s?\\d+.*")) {

      nClause = nClause.replace("ownship ", "");
      nClause = nClause.replace("ange ", "");
      nClause = nClause.replace("ection ", "");
      nClause = nClause.replace("OWNSHIP ", "");
      nClause = nClause.replace("ANGE ", "");
      nClause = nClause.replace("ECTION ", "");
      nClause = nClause.replace(" S ", " S");
      nClause = nClause.replace("T ", " T");
      nClause = nClause.replace("R ", " R");
      nClause = nClause.replace(".", "");

      lInfo.locType = "TRS";
      String[] trs;
      String sub = "";
      if (nClause.contains(",") || nClause.contains(","))
        trs = nClause.split(",|and");
      else
        trs = nClause.split("\\s+");
      for (int i = 0; i < trs.length; i++) {
        String s = trs[i].trim();
        if (s.startsWith("S"))
          lInfo.section = s.replace("SECTION", "").replace("Section", "")
              .replace("SEC.", "").replace("SEC", "").replace("Sec", "")
              .replace("S", "").trim();

        else if (s.startsWith("T")) {
          lInfo.town = s.replace("TOWNSHIP", "").replace("T", "").replace("N",
              "").replace("n", "").replace("S", "").replace("s", "").trim();
          lInfo.towndir = s.replace("TOWNSHIP", "").replace("T", "").replace(
              lInfo.town, "").trim();
        }

        else if (s.startsWith("R")) {
          lInfo.range = s.replace("RANGE", "").replace("R", "")
              .replace("W", "").replace("w", "").replace("E", "").replace("e",
                  "").trim();
          lInfo.rangedir = s.replace("RANGE", "").replace("R", "").replace(
              lInfo.range, "");
        } else
          sub += " " + s;
      }
      if (sub.length() > 0) {
        lInfo.locType = "TRSS";

        lInfo.subdivision = sub.trim();
      }
      return true;

    } else if (tokens.length > 3 && tokens[0].matches("Zone")
        && BGMUtil.isNum(tokens[1]) && tokens[2].matches("\\d+[EeWwNnSs]")
        && tokens[3].matches("\\d+[NnSsEeWw]")) {

      lInfo.locType = "UTM";
      String[] utm = nClause.replace(",", "").split(" ");
      lInfo.utmz = utm[0] + " " + utm[1];
      lInfo.utme = utm[2];
      lInfo.utmn = utm[3];
      return true;

    }

    return false;
  }

  // bf,bp
  private static boolean c2(LocalityInfo lInfo, String clause, LocalityRec r) {

    if (clause == null)
      clause = "";
    String nClause = nmlClause(clause);
    String[] tokens = nClause.split("\\s+");

    if (tokens[0].equalsIgnoreCase("between") && (nClause.contains("and"))) {
      String[] f = nClause
          .split("Between\\s|BETWEEN\\s|between\\s|\\sand\\s|\\sAND\\s");
      lInfo.feature1 = f[1].trim();
      lInfo.feature2 = f[2].trim();

      String cb = callback(f[1].trim(), r);

      if (cb == "p")
        lInfo.locType = "bp";
      else
        lInfo.locType = "bf";

      return true;
    }
    return false;
  }

  // nu
  private static boolean c3(LocalityInfo lInfo, String clause, LocalityRec r) {

    if (clause == null)
      clause = "";
    String nClause = nmlClause(clause.replace(",", " "));
    String[] NU = null;
    String f1 = null, f2 = null;

    if (nClause.contains(" by road ") || nClause.contains(" BY ROAD ")
        || nClause.contains(" by rd. ") || nClause.contains(" BY RD "))
      return false;

    if (containsNU(nClause) == 0) {
      NU = getNU(nClause);
      String[] tokens = nClause.split("\\s?" + NU[0] + "\\s" + NU[1] + "\\s+"
          + "\\s?");

      f1 = tokens[tokens.length - 1];

      // po
      if (f1.matches("on\\s.*|along\\s.*")) {
        lInfo.locType = "po";
        lInfo.offset = NU[0];
        lInfo.unit = NU[1];
        lInfo.feature1 = f1.replaceAll("on\\s.*|along\\s.*", "").trim();
      }

      // fo
      else if (f1.matches("outside\\s.*|from\\s.*")
          && !(f1.contains(" along ") || f1.contains(" on "))) {
        lInfo.locType = "fo";
        lInfo.offset = NU[0];
        lInfo.unit = NU[1];
        lInfo.feature1 = f1.replaceAll("outside\\s.*|from\\s.*", "").trim();
      }

      // fpo
      else if (f1.matches("from\\s.*")
          && (f1.contains(" along ") || f1.contains(" on "))) {
        lInfo.locType = "fpo";
        lInfo.offset = NU[0];
        lInfo.unit = NU[1];
        String temp = f1.replaceAll("from\\s.*", "").trim();
        String[] f_p = temp.split("\\son\\s|\\salong\\s");
        lInfo.feature1 = f_p[0];
        lInfo.feature2 = f_p[1];
      }
      // pom_2
      else if (callback(f1.trim(), r) == "p") {
        lInfo.locType = "POM";
        lInfo.subdivision = NU[0] + " " + NU[1];
        lInfo.feature1 = f1.trim();
        return true;
      } else {
        lInfo.locType = "UNK";

      }

      return true;
    }

    return false;
  }

  // nuh
  private static boolean c4(LocalityInfo lInfo, String clause, LocalityRec r) {

    if (clause == null)
      clause = "";
    String nClause = nmlClause(clause.replace(",", " "));
    String[] NUH1 = null, NUH2 = null;
    String f = null;

    if (containsNU(nClause) == 1 || nClause.contains(" by road ")
        || nClause.contains(" BY ROAD ") || nClause.contains(" by rd. ")
        || nClause.contains(" BY RD ")) {
      NUH1 = getNU(nClause);
      String dlt = NUH1[0] + " " + NUH1[1] + " " + NUH1[2];
      if (NUH1[3].length() > 0)
        dlt += " " + NUH1[3];
      dlt = dlt.replace("+", "\\+");
      String[] tokens1 = nClause.split("\\s?" + dlt + "\\s?");
      f = tokens1[tokens1.length - 1].trim();
      // foo
      if (tokens1.length > 1 && containsNU(f) == 1) {

        lInfo.locType = "foo";
        NUH2 = getNU(f);
        lInfo.unit = NUH1[1];

        if (tokens1[0].trim().length() > 1)
          lInfo.feature1 = tokens1[0].trim();
        else {
          String[] tokens2 = f
              .split(("\\s?" + NUH2[0] + "\\s" + NUH2[1] + "\\s" + NUH2[2]
                  + "\\s" + NUH2[3] + "\\s?").replace("+", "\\+"));
          lInfo.feature1 = tokens2[tokens2.length - 1].trim();
        }

        // Note: (JRW 20060923)
        // add .*O|O.*|O|.*o|o.*|o for Spanish, Portuguese, and French West
        // add .*L|L.*|L|.*l|l.*|l for Portuguese East
        // But these additions aren't sufficient to do the whole FOO
        // interpretation for those other languages.

        if (NUH1[2].matches(".*(w|W|e|E).*")) {
          lInfo.offsetEW = NUH1[0];
          lInfo.headingEW = NUH1[2];
          lInfo.offsetNS = NUH2[0];
          lInfo.headingNS = NUH2[2];
        } else {
          lInfo.offsetEW = NUH2[0];
          lInfo.headingEW = NUH2[2];
          lInfo.offsetNS = NUH1[0];
          lInfo.headingNS = NUH1[2];
        }
        // joo
        if (lInfo.feature1.toUpperCase().contains("INTERSECTION")
            || lInfo.feature1.toUpperCase().contains("JUNCTION")
            || lInfo.feature1.toUpperCase().contains("CONFLUENCE")) {
          lInfo.locType = "joo";
          String[] sa = lInfo.feature1.replaceAll(
              "INTERSECTION|JUNCTION|CONFLUENCE", "").split("and|AND");
          lInfo.feature1 = sa[0];
          if (sa.length > 1)
            lInfo.feature2 = sa[1];
        }

      }
      // fpoh
      else if (f.contains(" on ") || f.contains(" ON ")
          || f.contains(" along ") || f.contains(" ALONG ")
          || nClause.contains(" by road ") || nClause.contains(" BY ROAD ")
          || nClause.contains(" by rd. ") || nClause.contains(" BY RD ")) {
        lInfo.locType = "fpoh";
        String[] f_p = f.trim().split(
            "\\sALONG\\s|\\sON\\s|\\son\\s|BY RD|BY ROAD|\\salong\\s|by road");
        if (f_p.length < 2) {
          lInfo.locType = "unk";
          return true;
        }

        if (nClause.contains(" by road ") || nClause.contains(" BY ROAD ")
            || nClause.contains(" by rd. ") || nClause.contains(" BY RD ")) {
          lInfo.unit = NUH1[1];
          lInfo.offset = NUH1[0];
          lInfo.heading = NUH1[2];
          String[] s = f_p[f_p.length - 1].trim().split("\\s");
          lInfo.feature1 = f
              .replaceAll("by road|BY ROAD|by rd|BY RD|BY Rd", "").trim();
          return true;
        }

        lInfo.unit = NUH1[1];
        lInfo.offset = NUH1[0];
        lInfo.heading = NUH1[2];
        lInfo.feature1 = f_p[0];
        lInfo.feature2 = f_p[1];
        return true;
      }

      // jpoh
      else if (nClause.toLowerCase().contains("junction")
          || nClause.toLowerCase().contains(" jct ")) {
        lInfo.locType = "jpoh";
        String[] p_p = f.replace("junction ", "").replace("jct ", "").split(
            "and|on|along|On");
        lInfo.unit = NUH1[1];
        lInfo.offset = NUH1[0];
        lInfo.heading = NUH1[2];
        for (int i = 0; i < p_p.length; i++) {
          if (p_p[i].length() > 0 && lInfo.feature1.length() == 0)
            lInfo.feature1 = p_p[i];
          else if (p_p[i].length() > 0 && lInfo.feature2.length() == 0)
            lInfo.feature2 = p_p[i];
          else
            lInfo.feature3 = p_p[i];
        }
        return true;
      }

      // joh
      else if (f.contains(" and ")) {

        lInfo.locType = "joh";
        String[] p_p = f.split("\\sand\\s");
        if (p_p.length < 2) {
          lInfo.locType = "unk";
          return true;
        }
        lInfo.unit = NUH1[1];
        lInfo.offset = NUH1[0];
        lInfo.heading = NUH1[2];
        lInfo.feature1 = p_p[0];
        lInfo.feature2 = p_p[1];
      }
      // foh or poh
      else {
        String cb = callback(f, r);

        if (cb == "p")
          lInfo.locType = "poh";
        else
          lInfo.locType = "foh";
        lInfo.unit = NUH1[1];
        lInfo.offset = NUH1[0];
        lInfo.heading = NUH1[2];
        lInfo.feature1 = f;
      }

      return true;
    }

    return false;
  }

  // no #
  private static boolean c5(LocalityInfo lInfo, String clause, LocalityRec r) {

    if (clause == null)
      clause = "";
    String nClause = nmlClause(clause);
    String cb;

    // featrue name have to begin with a capital letter
    String[] tokens = nClause.split("\\s+");
    String[] Fs = (" " + nClause + " ").split("\\s[a-z]+\\s?[a-z]+\\.?\\s");

    // fh ph
    if (inTable(BGI.headings, tokens[0]) && tokens[1].matches("[A-Z].*|of|OF")
        && !(tokens.length > 2 && tokens[2].matches("of|OF"))) {

      cb = callback(Fs[Fs.length - 1], r);

      if (cb == "p")
        lInfo.locType = "ph";
      else
        lInfo.locType = "fh";
      lInfo.heading = tokens[0];
      lInfo.feature1 = nClause.replaceFirst(tokens[0], "").replaceFirst("of",
          "").trim();
      return true;
    }

    Fs[0] = Fs[0].trim();
    Fs[Fs.length - 1] = Fs[Fs.length - 1].trim();

    String[] keys = (nClause + " ").replaceAll(
        "[A-Z]+[\\s,]|[A-Z][a-z]+[\\s,]", " ~~ ").split("\\s+~+[^a-z]*~+\\s+");
    if (keys.length > 0)
      keys[keys.length - 1] = keys[keys.length - 1].trim();

    // j
    if ((tokens[0].equalsIgnoreCase("junction")
        || tokens[0].equalsIgnoreCase("confluence") || tokens[0]
        .equalsIgnoreCase("intersection"))
        && nClause.contains(" and ")) {
      lInfo.locType = "j";
      lInfo.feature1 = Fs[Fs.length - 2];
      lInfo.feature2 = Fs[Fs.length - 1];
      return true;
    }

    // n*
    if (tokens[0].matches("vic.|vicinity|near|area|above|below|off"
        .toUpperCase())
        || tokens[0].matches("vic.|vicinity|near|area|above|below|off") 
        || tokens[tokens.length - 1].toLowerCase()
            .matches("vic.|vicinity|area")) {
      // nj
      if ((nClause.contains(" junction ") || nClause.contains(" confluence ") || nClause
          .contains(" intersection "))
          && nClause.contains(" and ")) {
        lInfo.locType = "nj";
        lInfo.feature1 = Fs[Fs.length - 2];
        lInfo.feature2 = Fs[Fs.length - 1];
      }
      // np or nf
      else {
        String fn = nClause.replaceAll(
            "vic.|vicinity|near|area|above|below|off|of", "").trim();
        fn = fn.replaceAll(
            "vic.|vicinity|near|area|above|below|off".toUpperCase(), "").trim();

        cb = callback(fn, r);

        if (cb == "p")
          lInfo.locType = "np";
        else
          lInfo.locType = "nf";
        lInfo.feature1 = fn;
      }
      return true;
    }

    // eliminate all capital clause
    // if(!(" "+nClause).matches(".*\\s[a-z]+[\\.,]?.*") &&
    // !inTable(BGI.headings,tokens[0]))return false;

    nClause = nClause.replace(" OF ", " of ");
    String[] OF = nClause.split(" of ");
    String[] TH = OF[0].split("\\s");

    // fph
    if (OF.length == 2 && inTable(BGI.headings, TH[TH.length - 1])) {
      lInfo.locType = "fph";
      lInfo.heading = TH[TH.length - 1];
      lInfo.feature1 = OF[0].replace(TH[TH.length - 1], "").trim();
      lInfo.feature2 = OF[1];
      return true;
    }

    // fs ps
    if (tokens[0]
        .matches(".*\\spart|center|central|northern|southern|eastern|western|Mouth|mouth|Headwaters|headwaters|head|Head")
        || tokens[tokens.length - 1].matches("mouth||head|headwaters")
        || (inTable(BGI.headings, tokens[0]) && tokens[1].matches("[A-Za-z]+") && tokens[2]
            .matches("[A-Z]+.*|of"))) {
      String fn = null;

      if (Fs.length <= 1)
        fn = nClause
            .replaceAll(
                "central|northern|southern|eastern|westernv|Mouth|mouth|Headwaters|headwaters|head|Head",
                "");
      else
        fn = Fs[1];

      cb = callback(fn, r);

      if (cb == "p")
        lInfo.locType = "ps";
      else
        lInfo.locType = "fs";

      lInfo.subdivision = nClause.replace(fn, "").replace(" of ", "").trim();
      lInfo.feature1 = fn;
      return true;
    }

    // pbf
    if (nClause.matches(".*trail\\sfrom\\s.*\\sto\\s.*")
        || (Fs.length > 2 && callback(Fs[Fs.length - 3], r) == "p"
            && callback(Fs[Fs.length - 2], r) == "f" && callback(
            Fs[Fs.length - 1], r) == "f")) {
      lInfo.locType = "pbf";
      if (Fs[0].length() > 1)
        lInfo.feature1 = Fs[0];
      else
        lInfo.feature1 = tokens[0];
      lInfo.feature2 = Fs[Fs.length - 2];
      lInfo.feature3 = Fs[Fs.length - 1];
      return true;
    }

    cb = callback(nClause, r);
    // p with some lowcase words
    if (cb == "p") {
      lInfo.locType = "p";
      lInfo.feature1 = nClause;
      return true;
    }

    // all capital, P or f
    if (tokens[0].matches("[A-Z]+.*")) {
      cb = callback(nClause, r);
      if (cb == "p") {
        lInfo.locType = "p";
        lInfo.feature1 = nClause;
        return true;
      }

      if (cb == "f") {
        lInfo.locType = "f";
        lInfo.feature1 = nClause;
        return true;
      }
      return false;

    }

    return false;

  }

  // unk
  private static void fOrUnk(LocalityInfo lInfo, String clause, LocalityRec r) {
    lInfo.locType = "unk";

  }

  // fill in parsed result
  private static LocalityInfo[] popIntepData(LocalityInfo[] li, LocalityRec r) {

    String[] clauses = r.clauseSet;
    for (int i = 0; i < clauses.length; i++) {

      li[i] = new LocalityInfo();
      try {
        if (!c1(li[i], clauses[i], r))
          if (!c2(li[i], clauses[i], r))
            if (!c3(li[i], clauses[i], r))
              if (!c4(li[i], clauses[i], r))
                if (!c5(li[i], clauses[i], r))
                  fOrUnk(li[i], clauses[i], r);
      } catch (Exception e) {
        li[i].locType = "unk";
      }

    }

    return li;

  }

  // internal use to tell p form f
  static String callback(String fname, LocalityRec r) {

    String[] ps = fname.replaceAll("\\d+", "").split("\\s+");

    if (inTable(BGI.path, ps[0]) || inTable(BGI.path, ps[ps.length - 1]))
      return "p";

    if (!fname.replaceAll("\\'|\\.|-", "").matches(".*\\p{Punct}.*")
        && !fname.matches(".*\\d.*") && fname.matches("[A-Z]+.*"))
      return "f";

    return null;
  }

  // un only retn 0, unh retn 1
  static int containsNU(String s) {

    String[] tokens = s.split("\\s+");

    for (int i = 0; i < tokens.length - 1; i++)
      if (isNum(tokens[i]))
        if (inTable(BGI.units, tokens[i + 1])) {
          if (tokens.length > i + 2 && inTable(BGI.headings, tokens[i + 2]))
            return 1;
          return 0;
        }
    return -1;
  }

  // retn u, n, h,(of)
  static String[] getNU(String s) {
    String[] NU = null;

    String[] tokens = s.split("\\s+");

    for (int i = 0; i < tokens.length - 1; i++)
      if (isNum(tokens[i]))
        if (inTable(BGI.units, tokens[i + 1])) {
          NU = new String[4];
          NU[0] = tokens[i];
          NU[1] = tokens[i + 1];
          if (tokens.length > i + 2 && inTable(BGI.headings, tokens[i + 2]))
            NU[2] = tokens[i + 2];
          else
            NU[2] = "";
          if (tokens.length > i + 3
              && (tokens[i + 3].equalsIgnoreCase("of") || tokens[i + 3]
                  .equalsIgnoreCase("and")))
            NU[3] = tokens[i + 3];
          else
            NU[3] = "";
          i = tokens.length;
        }
    return NU;
  }

  // check if a key is exits in a hashtable
  static boolean inTable(Hashtable t, String s) {
    return t.containsKey(s.replaceAll("\\s|\\p{Punct}", "").toUpperCase());
  }

  /**
   * isNUH()
   * 
   * Return 0 if string is combined of number unit and heading(of), 1 if string
   * contain not only nuh
   * 
   * @param s
   *          input string
   */
  static int isNUH(String s, Parser parser) {
    s = s.replace(",", " ");
    

    if (s.matches(".*\\d+[a-zA-Z]+.*")) {
        String[] token = s.split("\\s+");
        s = "";
        for (int k = 0; k < token.length; k++) {
          if (token[k].trim().matches(".*\\d+[a-zA-Z]+.*")) {
            String n = token[k].replaceAll("\\d?\\.?\\,?\\d+", "").trim();
            if (parser.isUnit(n))
              token[k] = token[k].replace(n, "") + " " + n;
          }
          s += token[k] + " ";
        }
        s.trim();
    }
        
    String[] tokens = s.split("\\s+");
    if (tokens.length < 3)
      return -1;
    if (isNum(tokens[0]) && parser.isUnit(tokens[1])
        && parser.isHeading(tokens[2])) {
      if (parser.isHeading(tokens[tokens.length - 1]))
        return 0;
      else if (tokens[tokens.length - 1].equalsIgnoreCase("of"))
        return 0;
      return 1;
    }
    return -1;
  }

  // if string can convert to a number
  static boolean isNum(String s) {
    s = s.replaceAll(",", "").replaceAll("/", "").replace("+", "");
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }

  }

  /**
   * nmlClause()
   * 
   * Eliminates "\\s+" and converts "&" to "and".
   * 
   * @param s
   *          the input string.
   */
  static String nmlClause(String s) {
    String[] sa = s.trim().split(" ");
    if (sa.length > 1 && isNum(sa[0]) && isNum(sa[1]) && sa[1].contains("/"))
      s = s.trim().replaceFirst(" ", "+");
    return s.replaceAll("[\\[\\(].*[\\]\\)]", " ").replace(" OF", " of")
        .replace("OF ", "of ").replaceAll("&", " and ").trim().replaceAll(
            "\\s+", " ").replace("air mi", "mi").replace("*", "");
  }

}
