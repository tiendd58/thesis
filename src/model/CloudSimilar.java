package model;


import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractSimilarity;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;

public class CloudSimilar extends AbstractSimilarity {
	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Measures the Pearson Correlation Coefficient between the two supplied
	 * 
	 * instances.
	 * 
	 * 
	 * 
	 * @param a
	 * 
	 *            the first instance
	 * 
	 * @param b
	 * 
	 *            the second instance
	 */
	
	

	public double measure(Instance a, Instance b) {
		if (a.noAttributes() != b.noAttributes())

			throw new RuntimeException(
					"Both instances should have the same length");

		double xy = 0, x = 0, x2 = 0, y = 0, y2 = 0;
		int count = 0;
		boolean flag = false;
		for (int i = 0; i < a.noAttributes(); i++) {
			if (a.value(i) > 0 && b.value(i) > 0) {
				xy += a.value(i) * b.value(i);
				x += a.value(i);
				y += b.value(i);
				x2 += a.value(i) * a.value(i);
				y2 += b.value(i) * b.value(i);
				count++;
				flag = true;
			}
		}

//		int n = a.noAttributes();
		double resultPearson;
		if(flag && (x2 - (x * x) / count) * (y2 - (y * y) / count)>0){
			resultPearson =(xy - (x * y) / count)
					/ Math.sqrt((x2 - (x * x) / count) * (y2 - (y * y) / count));
		}else{
			resultPearson = 0;
		}
		resultPearson=(resultPearson+1)/2;
		
		//similar vectorcloud
		CloudVector cVector1 = new CloudVector();
		Double arr1[] = new Double[a.noAttributes()];
		arr1 = a.values().toArray(arr1);
		cVector1.createVectorBCG1(arr1);
		double[] arrC1 = new double[]{cVector1.getEx(),cVector1.getEn(), cVector1.getHe()};
		Instance i1 = new DenseInstance(arrC1);
		
		CloudVector cVector2 = new CloudVector();
		Double arr2[] = new Double[b.noAttributes()];
		arr2 = b.values().toArray(arr2);
		cVector2.createVectorBCG1(arr2);
		double[] arrC2 = new double[]{cVector2.getEx(),cVector2.getEn(), cVector2.getHe()};
		Instance i2 = new DenseInstance(arrC2);
		
		CosineSimilarity cloudSim = new CosineSimilarity();
		double similarCloud = cloudSim.measure(i1, i2);
		if(i2.getID()==17){
			System.out.println(similarCloud);
		}
		double result = resultPearson*0.8+similarCloud*0.2;
		return result;

	}
	
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

}