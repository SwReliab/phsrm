package phsrm.cphsrm;

public class PoissonDistribution {

	static double log2piOver2 = 0.9189385332046727417803297364056176398;

	double[] prob;  /* probability vector */
	double lambda;  /* Poisson parameter */
	int left;       /* range (left) */
	int right;      /* range (right) */
	double w;       /* weight to ensure total probability is 1 */
	double epsi;    /* tolerance error */
	double z;       /* quantile of normal dist to tolerance error */
	
	PoissonDistribution() {
		lambda = 1.0;
		left = 0;
		right = 0;
		setEpsilon(1.0e-8);
	}
	
	PoissonDistribution(double epsi) {
		lambda = 1.0;
		left = 0;
		right = 0;
		setEpsilon(epsi);
	}

	public void setEpsilon(double epsi) {
		this.epsi = epsi/2.0;
		z = getNormalQuantile(epsi);
	}
	
	/* for epsilon */
	double getNormalQuantile(double epsi) {
		double leps = Math.log(epsi);
		if (leps > -6.6 || leps < -689.0) {
			System.out.println("epsilon error");
		}
		double l = 3.0;
		double u = 37.0;
		double m = (l+u)/2.0;
		double fm = getApproximateNormalTail(m) - leps;
		while (Math.abs(fm) > 1.0e-8) {
			if (fm > 0) {
				l = m;
			} else {
				u = m;
			}
			m = (l+u)/2.0;
			fm = getApproximateNormalTail(m) - leps;
		}
		return m;
	}
	
	double getApproximateNormalTail(double x) {
		double x2 = x*x;
		double tmp = x;
		double sum;
		sum = 1.0/tmp;
		tmp *= x2;
		sum += -1.0/tmp;
		tmp *= x2;
		sum += 3.0/tmp;
		tmp *= x2;
		sum += -15.0/tmp;
		tmp *= x2;
		sum += 105.0/tmp;
		return Math.log(sum) - x2/2.0 - log2piOver2;
	}
	
	public void setParameter(double lambda) {
		this.lambda = lambda;
	}
	
	public int initializeRightBound() {
		if (lambda < 3) {
			int cnt = 0;
			double ll = Math.exp(-lambda);
			double total = ll;
			do {
				cnt++;
				ll *= lambda / cnt;
				total += ll;
			} while (total + epsi < 1.0);
			right = cnt;
		} else {
			right = (int) ((z + Math.sqrt(4.0*lambda - 1.0))
					* (z + Math.sqrt(4.0*lambda - 1.0)) / 4.0) + 1;
		}
		return right;
	}
	
	public int initializeLeftBound() {
		if (lambda <= 30.0) {
			left = 0;
		} else {
			left = (int) (((-z + Math.sqrt(4.0*lambda - 1.0))
					* (-z + Math.sqrt(4.0*lambda - 1.0))/4.0));
			left = (left <= 10)?0:(left - 10);
		}
		return left;
	}
	
	void compProb() {
		int mode = (int) lambda;
		
		if (mode >= 1) {
			prob[mode-left] = Math.exp(-lambda + mode * Math.log(lambda) 
					- log2piOver2 - (mode + 1.0/2.0) * Math.log(mode) + mode);
		} else {
			prob[mode-left] = Math.exp(-lambda);
		}

		/* Down */
		for (int j=mode; j>left; j--) {
			prob[j-1-left] = (j/lambda)*prob[j-left];
		}
		/* Up */
//		for (int j=mode; j<right; j++) {			
		for (int j=mode; j<right+1; j++) {
			prob[j+1-left] = (lambda/(j+1))*prob[j-left];
		}

		/* compute W */
		w = 0.0;
		int s = left;
//		int t = right;
		int t = right+1;
		while (s < t) {
			if (prob[s-left] <= prob[t-left]) {
				w += prob[s-left];
				s++;
			} else {
				w += prob[t-left];
				t--;
			}
		}
		w += prob[s-left];
	}
	
	public void initializeProb(double lambda) {
		setParameter(lambda);
		if (lambda == 0.0) {
			left = 0;
			right = 0;
			prob = new double [2];
			prob[0] = 1.0;
			prob[1] = 0.0;
			w = 1.0;
			return;
		} else {
//			initializeLeftBound();
			left = 0;
			initializeRightBound();
//			prob = new double [right - left + 1];
//			System.out.println("rignt=" + right);
			prob = new double [right - left + 2];
			compProb();
		}
	}
	
	public int getLeftBound() {
		return left;
	}
	
	public int getRightBound() {
		return right;
	}

	public double pmf(int i) {
		return prob[i-left]/w;
	}
}
