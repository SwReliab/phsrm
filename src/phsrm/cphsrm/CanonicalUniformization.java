package phsrm.cphsrm;

public class CanonicalUniformization {

	double epsi;
	double factor;
	PoissonDistribution poi;

	// rate
	int ndim;
	double[] tmp;
	double[] xi;
	double[] scaledRate;
	double lambda;
	
	CanonicalUniformization(double epsi, double factor) {
		this.epsi = epsi;
		this.factor = factor;
		poi = new PoissonDistribution(epsi);
	}

	public void setPhaseSize(int m) {
		ndim = m;
		scaledRate = new double [ndim];
		tmp = new double [ndim];
		xi = new double [ndim];
	}

	public void setRate(double[] rate) {
		double mx = 0.0;
		for (int i=0; i<ndim; i++) {
			mx = Math.max(mx, rate[i]);
		}
		lambda = mx * factor;
		for (int i=0; i<ndim; i++) {
			scaledRate[i] = rate[i] / lambda;
		}
	}

	// private vector matrix operations
	private void dgemvNoTrans(double a, double[] x, double b, double[] y) {
		for (int i=0; i<ndim-1; i++) {
			y[i] = b * y[i] + (1.0-scaledRate[i]) * x[i] + scaledRate[i] * x[i+1];
		}
		y[ndim-1] = b * y[ndim-1] + (1.0-scaledRate[ndim-1]) * x[ndim-1];
	}
	
	private void dgemvTrans(double a, double[] x, double b, double[] y) {
		y[0] = b * y[0] + (1.0-scaledRate[0]) * x[0];
		for (int i=1; i<ndim; i++) {
			y[i] = b * y[i] + scaledRate[i-1] * x[i-1] + (1.0-scaledRate[i]) * x[i];
		}
	}
	
	private void dger(double a, double[] x, double[] y, double[] h) {
		for (int i=0; i<ndim-1; i++) {
			h[2*i] += a * x[i] * y[i];
			h[2*i+1] += a * x[i] * y[i+1];
		}
		h[2*(ndim-1)] += a * x[ndim-1] * y[ndim-1];
		h[2*(ndim-1)+1] += 0.0;
	}

	// markov operation
	public void doBackward(double t, double[] x) {
		int right;
		poi.initializeProb(lambda * t);
		right = poi.getRightBound();
		
		MABlas.dcopy(ndim, x, xi);
		MABlas.fill(ndim, x, 0.0);
		MABlas.daxpy(ndim, poi.pmf(0), xi, x);
		
		for (int l=1; l<=right; l++) {
			MABlas.dcopy(ndim, xi, tmp);
			dgemvNoTrans(1.0, tmp, 0.0, xi);
			MABlas.daxpy(ndim, poi.pmf(l), xi, x);
		}
	}

	public void doForward(double t, double[] x) {
		int right;
		poi.initializeProb(lambda * t);
		right = poi.getRightBound();
		
		MABlas.dcopy(ndim, x, xi);
		MABlas.fill(ndim, x, 0.0);
		MABlas.daxpy(ndim, poi.pmf(0), xi, x);
		
		for (int l=1; l<=right; l++) {
			MABlas.dcopy(ndim, xi, tmp);
			dgemvTrans(1.0, tmp, 0.0, xi);
			MABlas.daxpy(ndim, poi.pmf(l), xi, x);
		}
	}

	public void doSojournForward(double t, double[] f, double[] b, double[] h) {
		int right;
		poi.initializeProb(lambda * t);
		right = poi.getRightBound();

		// initialization
		double[][] vc = new double [right+2][ndim];

		// forward and backward
		MABlas.fill(ndim, vc[right+1], 0.0);
		MABlas.daxpy(ndim, poi.pmf(right+1), b, vc[right+1]);
		for (int l=right; l>=1; l--) {
			dgemvNoTrans(1.0, vc[l+1], 0.0, vc[l]);
			MABlas.daxpy(ndim, poi.pmf(l), b, vc[l]);
		}
		
		MABlas.dcopy(ndim, f, xi);
		MABlas.fill(ndim, f, 0.0);
		MABlas.daxpy(ndim, poi.pmf(0), xi, f);

		MABlas.fill(ndim*2, h, 0.0);
		dger(1.0/lambda, xi, vc[1], h);
		
//		  MAdger(ndim, ndim, 1.0/uniformizationRate, xi, 1, 
//			 vc[1], 1, H, ndim);  // transpose

		for (int l=1; l<=right; l++) {
			MABlas.dcopy(ndim, xi, tmp);
			dgemvTrans(1.0, tmp, 0.0, xi);
			MABlas.daxpy(ndim, poi.pmf(l), xi, f);
			dger(1.0/lambda, xi, vc[l+1], h);
//		    MAdger(ndim, ndim, 1.0/uniformizationRate, xi, 1, 
//			   vc[l+1], 1, H, ndim);  // transpose
		}
	}
}
