package phsrm.common;

public class Numlib {

	static double PI = 3.14159265358979324;
	static double LOG_2PI = 1.83787706640934548;
	static double LOG_PI = 1.14472988584940017;

	static int N = 8;
	static double B0 = 1;
	static double B1 = (-1.0 / 2.0);
	static double B2 = ( 1.0 / 6.0);
	static double B4 = (-1.0 / 30.0);
	static double B6 = ( 1.0 / 42.0);
    static double B8 = (-1.0 / 30.0);
    static double B10 = ( 5.0 / 66.0);
	static double B12 = (-691.0 / 2730.0);
	static double B14 = ( 7.0 / 6.0);
	static double B16 = (-3617.0 / 510.0);

	public static double loggamma(double x) {
		double v, w;
		v = 1;
		while (x<N) { v *=x; x++; }
		w = 1 / (x * x);
		return ((((((((B16 / (16 * 15)) * w + (B14 / (14 * 13))) * w
				+ (B12 / (12 * 11))) * w + (B10 / (10 * 9))) * w
				+ (B8 / (8 * 7))) * w + (B6 / (6 * 5))) * w
				+ (B4 / (4 * 3))) * w + (B2 / (2 * 1))) / x
				+ 0.5 * LOG_2PI - Math.log(v) - x + (x - 0.5) * Math.log(x);
	}

	public static double gamma(double x) {
		if (x < 0) {
			return PI / (Math.sin(PI * x) * Math.exp(loggamma(1-x)));
		}
		return Math.exp(loggamma(x));
	}


	public static double psi(double x) {
		double v, w;
		v = 0;
		while (x < N) { v += 1 / x; x++; }
		w = 1 / (x * x);
		v += ((((((((B16 / 16) * w + (B14 /14)) * w
			+ (B12 / 12)) * w + (B10 / 10)) * w
			+ (B8 / 8)) * w + (B6 / 6)) * w
			+ (B4 / 4)) * w + (B2 / 2)) * w + 0.5 / x;
		return Math.log(x) - v;
	}

	public static double polygamma(int n, double x) {
		int k;
		double t, u, v, w;
		u = 1;
		for(k=1-n; k<0; k++) u *= k;
		v = 0;
		while (x<N) { v +=1 / Math.pow(x, n+1); x++; }
		w = x * x;
		t = (((((((B16
			* (n + 15.0) * (n + 14) / (16 * 15 * w) + B14)
			* (n + 13.0) * (n + 12) / (14 * 13 * w) + B12)
			* (n + 11.0) * (n + 10) / (12 * 11 * w) + B10)
			* (n + 9.0) * (n + 8) / (10 * 9 * w) + B8)
			* (n + 7.0) * (n + 6) / (8 * 7 * w) + B6)
			* (n + 5.0) * (n + 4) / (6 * 5 * w) + B4)
			* (n + 3.0) * (n + 2) / (4 * 3 * w) + B2)
			* (n + 1.0) * n / (2 * 1 * w)
			+ 0.5 * n / x + 1;
		return u * (t / Math.pow(x, n) + n * v);
	}

	/* Distributions */

	public static double p_gamma(double a, double x, double loggamma_a) {
		int k;
		double result, term, previous;
		if (x >= 1+a) return 1 - q_gamma(a, x, loggamma_a);
		if (x == 0)   return 0;
		result = term = Math.exp(a * Math.log(x) - x - loggamma_a) / a;
		for (k=1; k<1000; k++) {
			term *= x / (a+k);
			previous = result;
			result += term;
			if (result == previous) return result;
		}
		return result;
	}

	public static double q_gamma(double a, double x, double loggamma_a) {
		int k;
		double result, w, temp, previous;
		double la = 1, lb = 1 + x - a;
		if (x < 1+a) return 1 - p_gamma(a, x, loggamma_a);
		w = Math.exp(a * Math.log(x) - x - loggamma_a);
		result = w/lb;
		for (k=2; k<1000; k++) {
			temp = ((k-1-a)*(lb-la)+(k+x)*lb)/k;
			la = lb;
			lb = temp;
			w *= (k-1-a)/k;
			temp = w/(la*lb);
			previous = result;
			result += temp;
			if (result == previous) return result;
		}
		return result;
	}

	public static double p_normal(double x) {
		if (x >= 0)
			return 0.5 * (1 + p_gamma(0.5, 0.5 * x * x, LOG_PI / 2));
		else
			return 0.5 * q_gamma(0.5, 0.5 * x * x, LOG_PI / 2);
	}

	public static double q_normal(double x) {
		if (x >= 0)
			return 0.5 * q_gamma(0.5, 0.5 * x * x, LOG_PI / 2);
		else
			return 0.5 * (1 + p_gamma(0.5, 0.5 * x * x, LOG_PI / 2));
	}

	public static double d_normal(double x) {
		return 1/Math.sqrt(2.0*PI)*Math.exp(-x*x/2.0);
	}

	static int const_tolc = 5;
	static double const_tole = 1.0e-8;
	static int const_maxcount = 50;

	static double findshape(double a, double c) {
		int count, good;
		double fx, gx, nx;
		count = 0;
		good = 0;
		while(good < const_tolc) {
			fx = Math.log(a) - Numlib.psi(a) - c;
			gx = 1.0/a - Numlib.polygamma(1,a);
			nx = a - fx/gx;
			if (nx <= 0.0) {
				return a;
			}
			if (Math.abs(fx) < const_tole) {
				good ++;
			} else {
				good = 0;
			}
			a = nx;
			count++;
			if (count > const_maxcount) {
				return a;
			}
		}
		return a;
	}

	static double eps = 1.0e-8;
	static double tolz = 1.0e-5;
	static int maxcnt = 1024;
	
	static double findshape2(double init, double v) {
		int cnt;
		double a, b, c;
		a = init/2;
		b = init;
		cnt = 0;
		while (Math.log(b) - Numlib.psi(b) > v 
				&& cnt++ < maxcnt) {
			a = b;
			b *= 2.0;
		}
		while (Math.log(a) - Numlib.psi(a) <= v 
				&& cnt++ < maxcnt) {
			b = a;
			a = a/2.0;
		}
		cnt = 0;
		c = (a+b)/2;
		while (Math.abs(a - b)/a > eps
				&& cnt++ < maxcnt) {
			c = (a+b)/2;
			if (Math.log(c) - Numlib.psi(c) < v) {
				b = c;
			} else {
				a = c;
			}
		}
		return c;
	}
}
