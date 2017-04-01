package model;

import java.util.ArrayList;

public class CloudVector {
	private double Ex, En, He;

	public double getEx() {
		return Ex;
	}

	public void setEx(double ex) {
		Ex = ex;
	}

	public double getEn() {
		return En;
	}

	public void setEn(double en) {
		En = en;
	}

	public double getHe() {
		return He;
	}

	public void setHe(double he) {
		He = he;
	}

	public void createVectorBCGNew(Double[] arr1, int m) {
		int n = arr1.length;
		ArrayList<Double> listArr = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (arr1[i] > 0) {
				listArr.add(arr1[i]);
			}
		}
		n = listArr.size();
		// Step 1
		int sum = 0;
		for (int i = 0; i < n; i++) {
			sum += listArr.get(i);
		}
		Ex = (double) sum / n;
		// Step 2
		int r = n / m;
		double X[][] = new double[m][r];
		double Xmean[] = new double[m];
		double Yi2[] = new double[m];
		int count = 0;
		for (int i = 0; i < m; i++) {
			double sumXi = 0;
			for (int j = 0; j < r; j++) {
				X[i][j] = (double) listArr.get(count);
				sumXi += X[i][j];
				count++;
			}
			Xmean[i] = (double) sumXi / r;
			double sumY2 = 0;
			for (int j = 0; j < r; j++) {
				sumY2 += (double) Math.pow((X[i][j] - Xmean[i]), 2);
			}
			Yi2[i] = (double) sumY2 / (r - 1);
		}
		// step 3
		double Ey2 = 0;
		double sumEy2 = 0;
		double sumDy2 = 0;
		for (int i = 0; i < m; i++) {
			sumEy2 += Yi2[i];
		}
		Ey2 = (double) sumEy2 / m;
		for (int i = 0; i < m; i++) {
			sumDy2 += (double) Math.pow((Ey2 - Yi2[i]), 2);
		}
		double Dy2 = (double) sumDy2 / (m - 1);
		double En2 = (double) (Math.sqrt(4 * Ey2 * Ey2 - 2 * Dy2) / 2);
		He = (double) Math.sqrt(Ey2 - En2);
		En = (double) Math.sqrt(En2);
	}

	public void createVectorBCG1(Double[] arr1) {
		int n = arr1.length;
		ArrayList<Double> listArr = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (arr1[i] > 0) {
				listArr.add(arr1[i]);
			}
		}
		// Step 1
		int sum = 0;
		for (int i = 0; i < listArr.size(); i++) {
			sum += listArr.get(i);
		}
		Ex = (double) sum / listArr.size();
		// Step 2
		double sumM = 0;
		double sumS2 = 0;
		for (int i = 0; i < listArr.size(); i++) {
			sumM += Math.abs(listArr.get(i) - Ex);
			sumS2 += (listArr.get(i) - Ex) * (listArr.get(i) - Ex);
		}
		double M = (double) sumM / listArr.size();
		En = (double) (Math.sqrt(Math.PI / 2) * M);
		if(listArr.size()>1){
			double S2 = (double) sumS2 / (listArr.size() - 1);
			// Step 3
			if (S2 >= En * En) {
				He = (double) Math.sqrt(S2 - En * En);
			} else {
				He = En * 0.98f;
			}
		}else{
			He = En * 0.98f;
		}
		

	}
}
