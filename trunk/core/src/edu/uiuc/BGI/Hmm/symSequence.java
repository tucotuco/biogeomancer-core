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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class symSequence {

  int[] symArray;
  String[] tokenArray;
  Hashtable wordList = new Hashtable();

  public symSequence() throws IOException, FileNotFoundException {

    // read the wordlist file into a hashtable.
    // the file's format is Word:symbolId

    String aLine;
    int hNum = 0;
    String[] hList;
    // TODO: Need to have this configured from bg.config. JRW 2006-08-27
    // BufferedReader br = new BufferedReader(new FileReader("data/wordList"));
    BufferedReader br = new BufferedReader(
        new FileReader(
            "/Users/tuco/Documents/workspace/BGCore/src/UIUCLocInterpData/wordList"));
    while ((aLine = br.readLine()) != null) {
      hList = aLine.split(":");
      hNum = Integer.parseInt(hList[1].trim());
      wordList.put(new String(hList[0].trim()), new Integer(hNum));
    }

  }

  public void parse(String inStr) {

    String resStr = sepNonWordDigLet(inStr.trim());

    tokenArray = resStr.trim().split("\\s+");

    int numToken = tokenArray.length;
    symArray = new int[numToken];

    int wIndex = -1;
    int i = 0;
    for (int k = 0; k < numToken; k++) {
      wIndex = getSymbol(tokenArray[k]);
      symArray[i++] = wIndex;
    }

  } // end of parse()

  private int getSymbol(String ss) {
    int nLen = ss.length();
    int nn = -1;
    String s = new String(ss);

    // System.out.println("getSymbol: s is " + s);

    if (Character.isLetter(s.charAt(0))) { // char

      if (s.matches("[a-zA-Z]+\\.")) {
        int tmp = s.length();
        s = s.substring(0, tmp - 1);
        nLen -= 1;
      }

      Integer n = (Integer) wordList.get(s.toLowerCase());

      if (n != null) { // in the wordlist, symbol 0 ~ 11
        nn = n.intValue();
        return nn;
      } else { // not in the wordlist

        if (nLen == 1) {
          return 12;

        }
        if (nLen > 1) {
          if (Character.isUpperCase(s.charAt(0))
              && Character.isLowerCase(s.charAt(1))) {
            return 13;
          } else {
            return 14;
          }
        }
      }
    } else if (Character.isDigit(s.charAt(0))) { // number, this includes
                                                  // fractions like 5.4 and 1/5
      return 15;
    } else if ((s.length() > 1) && (s.matches("\\.\\d+"))) {
      return 15;
    } else { // delimeter
      if (s.matches("&")) {
        return 11;
      } else if (s.matches(",")) {
        return 16;
      } else if (s.matches(";")) {
        return 17;
      } else if (s.matches(":")) {
        return 18;
      } else if (s.matches("\\.")) {
        return 19;
      } else if (s.matches("\'")) {
        return 20;
      } else if (s.matches("\"")) {
        return 21;
      } else {
        return 22;
      }
    }

    return -1;
  }

  private String sepNonWordDigLet(String s) {

    int len = s.length();
    char[] charTmp = new char[len * 2];
    int j = 0;

    if (len == 1) {
      return s;
    }
    for (int i = 0; i < len; i++) {
      if ((i < len - 1)
          && ((Character.isDigit(s.charAt(i)) && Character.isLetter(s
              .charAt(i + 1))) || (Character.isLetter(s.charAt(i)) && Character
              .isDigit(s.charAt(i + 1))))) {
        charTmp[j++] = s.charAt(i);
        charTmp[j++] = ' ';
      } else if ((i < len - 2)
          && (Character.isDigit(s.charAt(i)) && s.charAt(i + 1) == '/' && Character
              .isDigit(s.charAt(i + 2)))) {
        charTmp[j++] = s.charAt(i);
        charTmp[j++] = s.charAt(i + 1);
        charTmp[j++] = s.charAt(i + 2);
        i += 2;
      } else if (!Character.isLetterOrDigit(s.charAt(i))
          && !Character.isSpaceChar(s.charAt(i)) && s.charAt(i) != '.') {

        charTmp[j++] = ' ';
        charTmp[j++] = s.charAt(i);
        charTmp[j++] = ' ';
      } else {
        charTmp[j++] = s.charAt(i);
      }
    }

    String ss = new String(charTmp, 0, j);

    return ss;
  }

}
