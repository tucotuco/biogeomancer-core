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

import junit.framework.TestCase;

public class RecSetTest // recset unit testing
    extends TestCase {
  /*
   * private RecSet recset; private BufferedWriter writer; private static
   * String[] originalHeader = {"locality", "elevation", "lat", "lng",
   * "country"}; private String[] row = {"\"5 miles \"west of\" berkeley\"",
   * "500", "12.3", "33.4", "\"usa\""};
   * 
   * public static void main(String[] args) { TestRunner.run(suite()); }
   * 
   * public static Test suite() { return new TestSuite(RecSetTest.class); }
   * 
   * protected void setUp() { }
   *  // TEST METHODS...
   * 
   * public void testConstructorWithFilename() { try { String testfile =
   * "testfile.txt"; makeTestFile(testfile); recset = new RecSet(testfile);
   * recset.load(); assertRecords(); } catch(Exception e) {
   * System.out.println("Ack! " + e.toString()); }
   * System.out.println("testConstructorWithFilename() passed!"); }
   * 
   * public void testLoadWithoutFile() { try { recset = new RecSet();
   * recset.load(); } catch(Exception e) { fail("Should fail! \n" +
   * e.toString()); } }
   * 
   * public void testLoadWithFile() { try { String testfile = "testfile.txt";
   * makeTestFile(testfile); recset = new RecSet(); recset.load(testfile);
   * assertRecords(); } catch(Exception e) { System.out.println(e.toString()); }
   * System.out.println("testLoadWithFile() passed!"); }
   * 
   * public void testLoadWithBogusFile() { try { recset = new
   * RecSet("THIS_FILE_DOES_NOT_EXIST"); // bogus file name }
   * catch(RecSet.RecSetException e) { fail("Should fail! \n" + e.toString()); } }
   *  // HELPERS...
   * 
   * public void writeLine(String[] line) throws Exception { // helper: write a
   * line using writer for (int i=0; i<line.length; i++) {
   * writer.write(line[i]); if (i != line.length - 1) // don't write comma if
   * last column writer.write(", "); } }
   * 
   * public void makeTestFile(String filename) { // helper: creates test data
   * file try { String testfile = filename; File file = new File(testfile);
   * writer = new BufferedWriter(new FileWriter(file));
   * writeLine(originalHeader); writer.newLine(); writeLine(row);
   * writer.close(); } catch(Exception e) { System.out.println("Ack! " +
   * e.toString()); } }
   * 
   * public void assertRecords() {// helper: tests record properties for (Rec
   * rec: recset.recs) { for (int i=0; i<originalHeader.length; i++) { if
   * (row[i].startsWith("\"") && row[i].endsWith("\""))
   * assertTrue(rec.get(originalHeader[i]).equals(row[i].substring(1,
   * row[i].length() - 1))); // remove quotes else
   * assertTrue(rec.get(originalHeader[i]).equals(row[i])); } } }
   */
}
