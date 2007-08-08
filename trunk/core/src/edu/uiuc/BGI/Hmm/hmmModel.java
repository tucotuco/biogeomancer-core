package edu.uiuc.BGI.Hmm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import bg.edu.uiuc.resource.FileFormatException;
import bg.edu.uiuc.resource.OpdfInteger;


public class hmmModel {
    double[][] A;
    double[][] B;
    double[] Pi;
    OpdfInteger[] opdfs;

    private BufferedReader loadFileData(String fileName) {
		BufferedReader reader = null;
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			reader = new BufferedReader(new InputStreamReader(
					loader.getResourceAsStream(fileName)));
		}
		catch (Exception e) {
			System.out.println("could not read data file " + fileName + ": "+ e.toString());
		}
		return reader;
	}
    
    public hmmModel(String filename) throws FileFormatException, IOException {
	
// read in the hmm model  

	BufferedReader br = loadFileData(filename);
	String aLine = br.readLine();
	if (!aLine.startsWith("M= ")) {
	    System.err.println("format error with number M!");
	}
	String mString = aLine.substring(3);
	aLine = br.readLine();
	if (!aLine.startsWith("N= ")) {
	    System.err.println("format error with number N!");
	}
	String nString = aLine.substring(3);
	int M = Integer.parseInt(mString);
	int N = Integer.parseInt(nString);

	aLine = br.readLine();
	if (!(aLine.startsWith("A:"))) {
	    System.err.println("format error with matrix A!");
	}

	A = new double[N][N];
	for (int i = 0; i < N; i++) {
	    aLine = br.readLine();
	    String[] nums = aLine.split(" ");
	    if (nums.length != N) {
		System.err.println("format error with length of a line in matrix A!");
	    }

	    for (int j = 0; j < N; j++) {
		A[i][j] = Double.parseDouble(nums[j]);
	    }			

	}

	aLine = br.readLine();
	if (!(aLine.startsWith("B:"))) {
	    System.err.println("format error with matrix B!");
	}

	B = new double[N][M];
	for (int i = 0; i < N; i++) {
	    aLine = br.readLine();
	    String[] nums = aLine.split(" ");
	    if (nums.length != M) {
		System.err.println("format error with length of a line in matrix B!");
	    }

	    for (int j = 0; j < M; j++) {
		B[i][j] = Double.parseDouble(nums[j]);
	    }			

	}


	aLine = br.readLine();
	if (!(aLine.startsWith("pi:"))) {
	    System.err.println("format error with pi!");
	}
	
	Pi = new double[N];
	aLine = br.readLine();
	String[] nums = aLine.split(" ");
	if (nums.length != N) {
            System.err.println("format error with length of a line in matrix Pi!");
	}

	for (int j = 0; j < N; j++) {
		Pi[j] = Double.parseDouble(nums[j]);
	}



// set the hmm parameters

	opdfs = new OpdfInteger[N];
	for(int i = 0; i < N; i++) {
	    opdfs[i] = new OpdfInteger(B[i]);
	}

    }

}
