package model;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractCorrelation;

public class PearsonCorrelationCustom extends AbstractCorrelation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2590347524132397180L;

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
		double result;
		if(flag && (x2 - (x * x) / count) * (y2 - (y * y) / count)>0){
			result =(xy - (x * y) / count)
					/ Math.sqrt((x2 - (x * x) / count) * (y2 - (y * y) / count));
		}else{
			result = 0;
		}
		result=(result+1)/2;
		return result;

	}

}