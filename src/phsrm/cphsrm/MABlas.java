package phsrm.cphsrm;


public class MABlas {

	// common static method
	static void daxpy(int ndim, double a, double[] x, double[] y) {
		for (int i=0; i<ndim; i++) {
			y[i] += a * x[i];
		}
	}

	static void dcopy(int ndim, double[] x, double[] y) {
		System.arraycopy(x, 0, y, 0, ndim);
	}
	
	static void fill(int ndim, double[] x, double v) {
		for (int i=0; i<ndim; i++) {
			x[i] = v;
		}
	}
	
	static double ddot(int ndim, double[] x, double[] y) {
		double sum = 0.0;
		for (int i=0; i<ndim; i++) {
			sum += x[i] * y[i];
		}
		return sum;
	}

}
