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

package org.biogeomancer.records;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream; // import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;

// import org.biogeomancer.ws.HttpClient;

//import com.thoughtworks.xstream.XStream;

public class RecSet {
  public class RecSetException extends Exception { // recset exception
    public RecSetException(Exception exception) {
      super(exception);
    }

    public RecSetException(String errormsg) {
      super(errormsg);
    }

    public RecSetException(String errormsg, Exception exception) {
      super(errormsg, exception);
    }
  }

  public static void main(String[] args) { // loads a data file and prints out
    // each record
    final int TOMARKUP = 1;
    final int TOXML = 2;
    final int FEATURE_REPORT = 3;
    if (args.length < 3) {
      System.out
          .println("Usage: main inputfile delimiter testtorun\nExample: main /Users/tuco/Documents/MaNISGeorefUploadTest1-500.txt tab 2");
      return;
    }
    String filename = new String(args[0]);
    String delimiter = new String(args[1]);
    Integer z = new Integer(args[2]);
    String arg1 = args[3];

    int test = z.intValue();
    try {
      RecSet recset = new RecSet(filename, delimiter);
      // RecSet recset = new
      // RecSet("http://bg.berkeley.edu/MaNISGeorefUploadTest1-500.txt",
      // delimiter, "lastloadedrecset.txt");
      switch (test) {
      case TOMARKUP:
        System.out.println("filename = " + recset.file.getAbsolutePath());
        System.out.println("RecSet header line:");
        for (int i = 0; i < recset.originalheader.length; i++) {
          if (i == 0)
            System.out.print(recset.originalheader[i]);
          else
            System.out.print(delimiter + recset.originalheader[i]);
          System.out.println(recset.toMarkup());
        }
        break;
      case TOXML:
 //       System.out.println(recset.toXML());
        break;

      case FEATURE_REPORT:
        try {
          GeorefManager gm = new GeorefManager();
          RecSet rs = null;

          // Grab test data from bg.config and create RecSet from it.
          String localData, remoteData, downloadData = null;
          localData = gm.getProperty("testdata.local.georefmanager");
          remoteData = gm.getProperty("testdata.remote.georefmanager");
          downloadData = gm.getProperty("downloads");
          if (localData != null) {
            rs = new RecSet(localData, "tab");
          } else if (remoteData != null) {
            if (downloadData != null)
              rs = new RecSet(remoteData, "tab", downloadData);
            else
              System.out
                  .println("No download location configured in "
                      + System.getProperty("user.home")
                      + "/bg.config... Please define downloads as a path to where these files should be stored.");
          } else
            System.out
                .println("No test data configured in "
                    + System.getProperty("user.home")
                    + "/bg.config... Please define testdata.local.georefmanager or testdata.remote.georefmanager.");

          gm.setRecSet(rs);
          GeorefPreferences gp = null;
          if (arg1 == null)
            gp = new GeorefPreferences("all");
          else
            gp = new GeorefPreferences(arg1);

          gm.newGeoreference(gp);

          rs.getFeatures();
        } catch (Exception e) {
        }

      }
    } catch (RecSet.RecSetException e) {
      System.out.println(e);
    }
  }

  public ArrayList<Rec> recs; // The list of records for processing in this
  // RecSet.
  public String filename; // The file name where the RecSet input is stored.
  public String outfilename; // The file name where the RecSet output is
  // stored.
  public String delineator; // user defined delineator for rec file
  public RecSetState state; // The processing state of the RecSet

  /*
   * @gwt.typeArgs <org.biogeomancer.records.Rec>
   */

  public String[] originalheader; // The ordered list of columns in the input
  // data.
  // public int priority; // A relative value used to assess the priority for
  // processing this RecSet.
  // public String language; // The ISO 639 three-letter language code for the
  // language in which the localities are in.

  private File file;

  /*
   * RecSet()
   * 
   * Constructs a completely blank recset. This is a dummy constructor meant
   * only for when you fill the recs manually
   * 
   */

  /*
   * RecSet()
   * 
   * Constructs a recset given data in the local filesystem.
   * 
   * @param filename the recset filename in the local filesystem @param
   * delineator the recset delineator character
   */
  public RecSet(String filename, String delineator)
      throws RecSet.RecSetException { // construct recset from rec file on disk

    this.recs = new ArrayList();
    this.filename = new String(filename);
    this.file = new File(filename);
    this.delineator = delineator;
    this.state = RecSetState.RECSET_CREATED;
    this.load();
  }

  /**
   * RecSet()
   * 
   * Constructs a recset given remote data.
   * 
   * @param fileurl
   *          the remote recset file url
   * @param delineator
   *          the recset delineator character
   */
  public RecSet(String fileurl, String delineator, String destination)
      throws RecSet.RecSetException { // construct recset from rec file at URL
    try {
      destination = destination + "/bg_recset_." + System.currentTimeMillis();
      URL url = new URL(fileurl);
      InputStream data = url.openStream();
      FileOutputStream file = new FileOutputStream(destination);

      byte[] buffer = new byte[4096];
      int chunkSize;
      while ((chunkSize = data.read(buffer)) != -1)
        file.write(buffer, 0, chunkSize);
      file.close();
      this.file = new File(destination);
      this.delineator = delineator;
      this.recs = new ArrayList<Rec>();
      this.filename = new File(destination).getName();
      this.load();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private RecSet() {

    this.recs = new ArrayList();
    this.state = RecSetState.RECSET_CREATED;

  }

  // Retrieves a list of unique features from the record set
  public RecSet getFeatures() {

    Set<String> features = new HashSet<String>();

    boolean exists = false;

    for (Rec rec : this.recs) {// loop through each record
      for (Clause clause : rec.clauses) {// and each clause
        for (LocSpec spec : clause.locspecs) { // and each spec
          // add the featurename string, set will prevent duplicates
          features.add(spec.featurename);
        }
      }
    }

    RecSet recset = new RecSet();
    Rec rec;
    for (String feature : features) {
      rec = new Rec();
      rec.clauses.add(new Clause());
      rec.clauses.get(0).locspecs.add(new LocSpec());
      rec.clauses.get(0).locspecs.get(0).featurename = feature;
      rec.clauses.get(0).locType = "F";
      rec.clauses.get(0).uLocality = feature;
      rec.clauses.get(0).iLocality = feature;
      rec.uFullLocality = feature;
      recset.recs.add(rec);
    }

    System.out.println(recset.toString());

    return recset; // return feature list
  }

  /**
   * load()
   * 
   * Loads a recset file from the local filesystem.
   * 
   * @param filename
   *          the recset filename
   * @param delineator
   *          the recset delineator character
   */
  public boolean load(String filename, String delineator)
      throws RecSet.RecSetException {
    this.file = new File(filename);
    return this.load();
  }

  /**
   * load()
   * 
   * Loads a remote recset file.
   * 
   * @param fileurl
   *          the remote recset file url
   * @param delineator
   *          the recset delineator character
   */
  public boolean load(URL fileurl, String delineator, String destination)
      throws RecSet.RecSetException {
    this.file = this.getFile(fileurl, destination);
    this.delineator = delineator;
    return this.load();
  }

  public void printRecs() { // just print the rec values
    String name, value;
    for (Rec rec : this.recs) { // print out each record
      Iterator keys = rec.keySet().iterator();
      while (keys.hasNext()) {
        name = (String) keys.next();
        value = rec.get(name);
        System.out.println(name + " = " + value);
      }
      // System.out.println("...................................");
    }
  }

  public String toMarkup() {
    String s = new String("<RECSET>\n");
    for (Rec r : recs) {
      s = s.concat(r.toMarkup());
    }
    s = s.concat("</RECSET>");
    return s;
  }

  public String toString() {
    String s = new String("<RECSET>\n");
    s = s.concat("RecSet state: " + state + "\n");
    if (filename != null) {
      s = s.concat("FileName: " + filename + "\n");
    } else
      s = s.concat("FileName not given\n");
    if (delineator != null) {
      if (delineator.equals("\t")) {
        s = s.concat("Delineator: \\t\n");
      } else {
        s = s.concat("Delineator: " + delineator + "\n");
      }
    } else
      s = s.concat("Delineator not given\n");

    if (this.recs == null || this.recs.size() == 0) {
      s = s.concat("Record count: 0\n<RECORDS>\n");
    } else {
      s = s.concat("Record count: " + this.recs.size() + "\n<RECORDS>\n");
    }
    String name, value;
    for (Rec rec : this.recs) { // print out each record
      Iterator keys = rec.keySet().iterator();
      while (keys.hasNext()) {
        name = (String) keys.next();
        value = rec.get(name);
      }
      s = s.concat(rec.toString());
    }
    s = s.concat("</RECORDS>\n</RECSET>\n");

    return s;
  }

 /* 
  * 
  public String toXML() {
    XStream xstream = new XStream();
    xstream.alias("RECSET", RecSet.class);
    xstream.alias("REC", Rec.class);
    xstream.alias("CLAUSE", Clause.class);
    xstream.alias("LOCSPEC", LocSpec.class);
    xstream.alias("GEOREF", Georef.class);
    String xml = xstream.toXML(this);
    return xml;
  }
*/
  /**
   * getFile()
   * 
   * Downloads and saves recset data from a url, then returns it as a new file.
   */
  private File getFile(URL fileurl, String destination) {
    try {
      String filedata = fileurl.getContent().toString();
      String filename = destination + fileurl.getFile();
      System.out.println("DATA FILE NAME = " + filename);
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      writer.write(filedata);
      writer.close();
      return new File(filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private boolean load() throws RecSet.RecSetException { // load a file from
    // disk
    if (delineator.equalsIgnoreCase("tab") || delineator.equalsIgnoreCase("\t"))
      delineator = "\t";
    else if (delineator.equalsIgnoreCase("comma"))
      delineator = ",";

    try { // opening file
      // BufferedReader reader = new BufferedReader(new FileReader(this.file));
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          new FileInputStream(this.file), "UTF-8"));
      Pattern delineation = Pattern.compile(this.delineator);
      Pattern lineTerminators = Pattern.compile("(?m)$^|[\\r\\n]+\\z");
      String line = reader.readLine(); // read header definition from file

      if (lineTerminators.matcher(line.subSequence(0, line.length())).matches()) { // get
        // rid
        // of
        // control
        // characters
        line = lineTerminators.matcher(line.subSequence(0, line.length()))
            .replaceAll("");
      }

      line = line + "\n";

      String[] header = delineation.split(line); // create header definition
      this.originalheader = delineation.split(line);
      // for (String name : header)
      // System.out.println("Header: " + name);
      for (int i = 0; i < header.length; i++) { // remove leading/trailing white
        // space
        header[i] = header[i].toLowerCase().trim();
        this.originalheader[i] = this.originalheader[i].trim();
      }
      String[] row;
      Rec rec;
      int rowcount = 0;
      while ((line = reader.readLine()) != null) { // load each row of file
        // data
        if (lineTerminators.matcher(line.subSequence(0, line.length()))
            .matches()) { // get rid of control characters
          line = lineTerminators.matcher(line.subSequence(0, line.length()))
              .replaceAll("");
          line = line + "\n";
        }

        row = delineation.split(line);
        rec = new Rec();
        for (int i = 0; i < header.length; i++) {
          if (i < row.length) {
            // remove leading/trailing white space
            row[i] = row[i].trim();
            if (row[i].startsWith("\"") && row[i].endsWith("\"")) // row value
              // is
              // surrounded
              // by double
              // quotes
              rec.put(header[i], row[i].substring(1, row[i].length() - 1)); // remove
            // double
            // quotes
            else
              rec.put(header[i], row[i]);
          } else
            rec.put(header[i], null);
        }
        // Provide an id if no id record is given in the input.
        if (rec.get("id") == null) {
          rowcount++;
          rec.put("id", String.valueOf(rowcount));
        }
        rec.getFullLocality();
        this.recs.add(rec);
      }
    } catch (Exception e) {
      this.state = RecSetState.RECSET_LOADED;
      throw this.new RecSetException("Problem loading the data file: "
          + e.toString(), e);
    }
    this.state = RecSetState.RECSET_LOADED;
    return true;
  }
}