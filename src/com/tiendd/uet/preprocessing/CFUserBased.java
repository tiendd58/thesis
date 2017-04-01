package com.tiendd.uet.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import model.CloudSimilar;
import model.Constant;

public class CFUserBased {

	public static int K_NEAREST = 20;

	public static double[][] createMatrix() throws FileNotFoundException, IOException {
		// Initialize the matrix with -1 for all elements
		double[][] matrix = new double[Constant.SIZE_USER][Constant.SIZE_ITEM];
		// for (int i = 0; i < matrix.length; ++i) {
		// for (int j = 0; j < matrix[0].length; ++j) {
		// matrix[i][j] = 0.0;
		// }
		// }

		// Read the input values and form the full matrix
		BufferedReader br = new BufferedReader(new FileReader(Constant.URL_FILE_RATING_TRAINSET));
		StringTokenizer st = null;
		String row;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, Constant.COMMA_DELIMITER);
			while (st.hasMoreTokens()) {
				int user = Integer.parseInt(st.nextToken());
				int movie = Integer.parseInt(st.nextToken());
				double rating = Double.parseDouble(st.nextToken());
				matrix[user - 1][movie - 1] = rating;
				st.nextToken();
			}
		}
		br.close();
		return matrix;
	}

	public static boolean[][] createMatrixTrust() throws FileNotFoundException, IOException {
		// Initialize the matrix with -1 for all elements
		boolean[][] matrix = new boolean[Constant.SIZE_USER][Constant.SIZE_USER];
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[0].length; ++j) {
				matrix[i][j] = false;
			}
		}

		// Read the input values and form the full matrix
		BufferedReader br = new BufferedReader(new FileReader("dataset/trust.txt"));
		StringTokenizer st = null;
		String row;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, " ");
			while (st.hasMoreTokens()) {
				int user1 = Integer.parseInt(st.nextToken());
				int user2 = Integer.parseInt(st.nextToken());
				if(user1<=Constant.SIZE_USER && user2<=Constant.SIZE_USER){
					matrix[user1 - 1][user2 - 1] = true;
					matrix[user2 - 1][user1 - 1] = true;	
				}
				st.nextToken();
			}
		}
		br.close();
		return matrix;
	}
	
	public static double[][] createMatrixUpdated() throws FileNotFoundException, IOException {
		// Initialize the matrix with -1 for all elements
		double[][] matrix = new double[Constant.SIZE_USER][Constant.SIZE_ITEM];
		// for (int i = 0; i < matrix.length; ++i) {
		// for (int j = 0; j < matrix[0].length; ++j) {
		// matrix[i][j] = 0.0;
		// }
		// }

		// Read the input values and form the full matrix
		BufferedReader br = new BufferedReader(new FileReader(Constant.URL_FILE_RATING_UPDATED));
		StringTokenizer st = null;
		String row;
		int u = 0;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, Constant.COMMA_DELIMITER);
			while (st.hasMoreTokens()) {
				for(int i=0;i<Constant.SIZE_ITEM;i++){
					matrix[u][i]=Double.parseDouble(st.nextToken());
				}
				u++;
			}
		}
		br.close();
		return matrix;
	}
	
	public static double[][] caculateCCSimilar() throws IOException {
		System.out.println("Caculate SIMCC");
		double matrixCCSim[][] = new double[Constant.SIZE_USER][Constant.SIZE_USER];
		Dataset dataset = FileHandler.loadDataset(new File(Constant.URL_FILE_RATING_ROOT), Constant.COMMA_DELIMITER);
		PrintWriter writer = new PrintWriter("dataset/matrix_cloud.txt", "UTF-8");
		for (int i = 0; i < Constant.SIZE_USER; i++) {
			System.out.println("Caculate SIMCC of "+i);
			for (int j = i; j < Constant.SIZE_USER; j++) {
				CloudSimilar cloudSimilar = new CloudSimilar();
				matrixCCSim[i][j] = cloudSimilar.measure(dataset.get(i), dataset.get(j));
				matrixCCSim[j][i] = cloudSimilar.measure(dataset.get(j), dataset.get(i));
			}
		}
		for (int i = 0; i < Constant.SIZE_USER; i++) {
			for (int j = i; j < Constant.SIZE_USER; j++) {
				writer.println(i + " " + j + " " + matrixCCSim[i][j]);
			}
		}
		writer.close();
		return matrixCCSim;
	}

	public static double[][] predictUnrated(double matrix[][]) throws IOException {
		System.out.println("Predicting...");
		int lenUser = matrix.length;
		int lenItem = matrix[0].length;
		double[][] matrixUpdate = new double[Constant.SIZE_USER][Constant.SIZE_ITEM];
		double [][] similarUser = caculateCCSimilar();
//		boolean[][] matrixTrust = createMatrixTrust();
		for (int u1 = 0; u1 < lenUser; u1++) {
			System.out.println("Predicting... " + u1);
			for (int i = 0; i < lenItem; i++) {
				if (matrix[u1][i] == 0) {
					double num = 0;
					double denom = 0;
					boolean flag = false;
					for (int u2 = 0; u2 < lenUser; u2++) {
						if (matrix[u2][i] > 0 && similarUser[u1][u2] >0.5) {
							flag = true;
							num += similarUser[u1][u2] * matrix[u2][i];
							denom += similarUser[u1][u2];
						}
					}
					if (flag) {
						matrixUpdate[u1][i] = num / denom;
						matrixUpdate[u1][i]= (double)(Math.round(matrixUpdate[u1][i]*100))/100;
					}
				}else{
					matrixUpdate[u1][i]=matrix[u1][i];
				}
			}
		}
		return matrixUpdate;
	}

	// public static void main(String[] args) {
	// try {
	// double matrix[][] = createMatrix();
	// PrintWriter writerRoot = new PrintWriter(
	// Constant.URL_FILE_RATING_ROOT, "UTF-8");
	// for (int i = 0; i < Constant.SIZE_USER; i++) {
	// for (int j = 0; j < Constant.SIZE_ITEM; j++) {
	// writerRoot.print(matrix[i][j]);
	// if (j < Constant.SIZE_ITEM - 1) {
	// writerRoot.print(Constant.COMMA_DELIMITER);
	// } else {
	// if (i < Constant.SIZE_USER - 1) {
	// writerRoot.println(Constant.COMMA_DELIMITER);
	// }
	// }
	// }
	// }
	// writerRoot.close();
	//
	// matrix = predictUnrated(matrix);
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}