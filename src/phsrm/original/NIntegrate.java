package phsrm.original;

public class NIntegrate implements Integrable {

	static double pi = 3.14159265358979324;
	static double eps = 1.0e-8;
	
	int n;
	double[] x;
	double[] w;
	Integrable intfunc;

	public double func(double x) {
		return intfunc.func(x);
	}
	
	public NIntegrate() {
		this.n = 15;
		x = new double[n];
		w = new double[n];
		makeW();
	}

	public NIntegrate(int n) {
		this.n = n;
		x = new double[n];
		w = new double[n];
		makeW();
	}

	public NIntegrate(int n, Integrable f) {
		this.n = n;
		x = new double[n];
		w = new double[n];
		intfunc = f;
		makeW();
	}

	public void makeW() {
		int i, l, m;
		double p0, p1, p2;
		double q0, q1, q2;
		double tmp, dt;
	
		switch(n) {
		case 1:
			x[0] = 0.0;
			w[0] = 2.0;
			return;
		case 2:
			x[0] = Math.sqrt(1.0/3.0);
			w[0] = 1;
			x[1] = -x[0];
			w[1] = w[0];
			return;
		case 3:
			x[0] = Math.sqrt(0.6);
			w[0] = 5.0/9.0;
			x[1] = 0.0;
			w[1] = 8.0/9.0;
			x[2] = -x[0];
			w[2] = w[0];
			return;
		}

		m = n/2;
		for (i=0; i<m; i++) {
			tmp = Math.cos((i+1.0-1.0/4.0)/(n+1.0/2.0)*pi);
			do {
				p1 = tmp;
				p2 = (3.0*tmp*tmp-1.0)/2.0;
				q1 = 1.0;
				q2 = 3.0*tmp;
				for (l=3; l<=n; l++) {
					p0 = p1;
					p1 = p2;
					p2 = ((2.0*l-1)*tmp*p1-(l-1)*p0)/l;
					q0 = q1;
					q1 = q2;
					q2 = ((2.0*l-1)*(tmp*q1+p1)-(l-1)*q0)/l;
				}
				dt = p2/q2;
				tmp = tmp - dt;
			} while(Math.abs(dt) > Math.abs(tmp)*eps);
			x[i] = tmp;
			w[i] = 2.0/(n*p1*q2);
		}
		if (n % 2 != 0) {
			x[n/2] = 0.0;
			tmp = (double) n;
			for (i=1; i<=m; i++)
				tmp = tmp*(0.5 - i)/i;
			w[n/2] = 2.0/(tmp*tmp);
		}
		for (i=0; i<m; i++) {
			x[n-1-i] = -x[i];
			w[n-1-i] = w[i];
		}
		return;
	}

	double solve(double a, double b) {
		int i;
		double t1, t2;
		double x1, v, sum;
	
		t1 = (b - a)/2.0;
		t2 = (b + a)/2.0;
		sum = 0.0;
	
		for (i=0; i<n; i++) {
			x1 = t1*x[i] + t2;
			v = w[i]*func(x1);
			sum += v;
		}
		sum *= t1;
		return sum;
	}
}
