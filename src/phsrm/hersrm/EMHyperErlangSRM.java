package phsrm.hersrm;

import phsrm.common.*;

public class EMHyperErlangSRM implements EMRunnable {
	int nmix, dsize;
	EMControl em;
	HyperErlangSRM hysrm;
	GroupData dat;
	
	HyperErlangParameters hpara;
	double omega;
	double[] shape;
	double[] rate;
	double[] pi;

	// work
	private double[] tmp1;
	private double[] tmp2;
	private double[] a0;
	private double[] a1;
	private double[] gam10;
	private double[] gam11;
	private double[] gam20;
	private double[] gam21;
	private double en1;
	private double[] en2;
	private double[] en3;
	
	EMHyperErlangSRM() {
		em = new EMControlRelativeLF();
	}

	EMHyperErlangSRM(EMControl em) {
		this.em = em;
	}
	
	public void setEMControl(EMControl em) {
		this.em = em;
	}
	
	public EMControl getEMControl() {
		return em;
	}

	public void setHErSRM(HyperErlangSRM cph) {
		this.hysrm = cph;
	}
	
	public void setGroupData(GroupData dat) {
		this.dat = dat;
	}
	
	public GroupData getGroupData() {
		return dat;
	}
	
	public void doEstimation() {
		omega = hysrm.getTotal();
		hpara = hysrm.getHErParameter();
		nmix = hpara.getNumberOfErlangDists();
		shape = hpara.getShapeParameters();
		rate = hpara.getRateParameters();
		pi = hpara.getInitialVector();

		dsize = dat.getNumberOfRecords();

		tmp1 = new double[nmix];
		tmp2 = new double[nmix];
		a0 = new double[nmix];
		a1 = new double[nmix];
		gam10 = new double[nmix];
		gam11 = new double[nmix];
		gam20 = new double[nmix];
		gam21 = new double[nmix];
		en2 = new double[nmix];
		en3 = new double[nmix];

		em.runEMRunnableModel(this);
	}
	
	public double doEMstep() {
		int i;
		double x1, t1;
		double llf, tmp1sum;

		// E-step
		// initialize
		for (i=0; i<nmix; i++) {
			a0[i] = Numlib.loggamma(shape[i]);
			a1[i] = Numlib.loggamma(shape[i] + 1.0);
		}
		en1 = 0.0;
		for (i=0; i<nmix; i++) {
			en2[i] = 0.0;
			en3[i] = 0.0;
		}
		llf = 0.0;

		t1 = dat.getTime(1);
		x1 = dat.getNumber(1);
		for (i=0; i<nmix; i++) {
			gam10[i] = Numlib.q_gamma(shape[i], rate[i]*t1, a0[i]);
			gam11[i] = Numlib.q_gamma(shape[i]+1, rate[i]*t1, a1[i]);
		}
		if (x1 != 0.0) {
			tmp1sum = 0.0;
			for (i=0; i<nmix; i++) {
				tmp1[i] = 1.0 - gam10[i];
				tmp1sum += pi[i] * tmp1[i];
				tmp2[i] = shape[i] / rate[i] * (1.0 - gam11[i]);
			}
			en1 += x1;
			for (i=0; i<nmix; i++) {
				en2[i] += x1 * pi[i] * tmp1[i] / tmp1sum;
				en3[i] += x1 * pi[i] * tmp2[i] / tmp1sum;
			}
			llf = x1 * Math.log(omega * tmp1sum) - Numlib.loggamma(x1+1.0);
		}
		if (dat.getType(1) == 1) {
			tmp1sum = 0.0;
			for (i=0; i<nmix; i++) {
				tmp1[i] = hpara.getPDF(i, t1);
				tmp1sum += pi[i] * tmp1[i];
			}
			en1 += 1.0;
			for (i=0; i<nmix; i++) {
				en2[i] += pi[i] * tmp1[i] / tmp1sum;
				en3[i] += t1 * pi[i] * tmp1[i] / tmp1sum;
			}
			llf += Math.log(omega * tmp1sum);
		}
		for (int j=2; j<=dat.getNumberOfRecords(); j++) {
			t1 += dat.getTime(j);
			x1 = dat.getNumber(j);
			for (i=0; i<nmix; i++) {
				gam20[i] = Numlib.q_gamma(shape[i], rate[i]*t1, a0[i]);
				gam21[i] = Numlib.q_gamma(shape[i]+1, rate[i]*t1, a1[i]);
			}
			if (x1 != 0.0) {
				tmp1sum = 0.0;
				for (i=0; i<nmix; i++) {
					tmp1[i] = gam10[i] - gam20[i];
					tmp1sum += pi[i] * tmp1[i];
					tmp2[i] = (shape[i]/rate[i]) * (gam11[i] - gam21[i]);
				}
				en1 += x1;
				for (i=0; i<nmix; i++) {
					en2[i] += x1 * pi[i] * tmp1[i] / tmp1sum;
					en3[i] += x1 * pi[i] * tmp2[i] / tmp1sum;
				}
				llf += x1 * Math.log(omega * tmp1sum) - Numlib.loggamma(x1+1);
			}
			if (dat.getType(j) == 1) {
				tmp1sum = 0.0;
				for (i=0; i<nmix; i++) {
					tmp1[i] = hpara.getPDF(i, t1);
					tmp1sum += pi[i] * tmp1[i];
				}
				en1 += 1.0;
				for (i=0; i<nmix; i++) {
					en2[i] += pi[i] * tmp1[i] / tmp1sum;
					en3[i] += t1 * pi[i] * tmp1[i] / tmp1sum;
				}
				llf += Math.log(omega * tmp1sum);
			}
			for (i=0; i<nmix; i++) {
				gam10[i] = gam20[i];
				gam11[i] = gam21[i];
			}
		}
		tmp1sum = 0.0;
		for (i=0; i<nmix; i++) {
			tmp1sum += pi[i] * gam10[i];      // gam10 is the last time
			en2[i] += omega * pi[i] * gam10[i];  // gam10 is the last time
			en3[i] += omega * pi[i] * (shape[i]/rate[i]) * gam11[i];  // gam11 is the last time
		}
		en1 += omega * tmp1sum;
		llf += - omega * (1.0 - tmp1sum);
		
		// M-step
		omega =  en1;
		hysrm.setOmega(omega);
		for (i=0; i<nmix; i++) {
			pi[i] = en2[i] / en1;
			rate[i] = shape[i] * en2[i] / en3[i];
		}
		return llf;
	}
	
	public String getParameterString() {
		return hysrm.getParameterString();
	}
}
