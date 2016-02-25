package phsrm.cphsrm;

import phsrm.common.*;

final public class EMCPHSRM implements EMRunnable {

	int ndim, dsize;
	EMControl em;
	CPHSRM cph;
	GroupData dat;
	
	double omega;
	CanonicalPHParameters cpara;
	CanonicalUniformization cuni;

	// work
	double eno;
	double[] eb, eb2, ey;
	double[] tmp, pi2;
	double[] h0, en;

	double barblf;
	double[] blf, blf2;
	
	double[][] vb, vb2;
	double[] vf, vf2;
	
	EMCPHSRM() {
		em = new EMControlRelativeLF();
	}

	EMCPHSRM(EMControl em) {
		this.em = em;
	}
	
	public void setEMControl(EMControl em) {
		this.em = em;
	}
	
	public EMControl getEMControl() {
		return em;
	}

	public void setCPHSRM(CPHSRM cph) {
		this.cph = cph;
	}
	
	public void setGroupData(GroupData dat) {
		this.dat = dat;
	}
	
	public GroupData getGourpData() {
		return dat;
	}
	
	public void doEstimation() {
		omega = cph.getTotal();
		cpara = cph.getCPHParameter();
		cuni = cph.getCPHSolver();
		ndim = cpara.getPhaseSize();

		dsize = dat.getNumberOfRecords();

		vb = new double [dsize+2][ndim];
		vb2 = new double [dsize+2][ndim];
		blf = new double [dsize+2];
		blf2 = new double [dsize+2];
		
		tmp = new double [ndim];
		h0 = new double [ndim*2];
		pi2 = new double [ndim];
		
		vf = new double [ndim];
		vf2 = new double [ndim];

		eb = new double [ndim];
		eb2 = new double [ndim];
		en = new double [ndim*2];
		ey = new double [ndim];
		
		em.runEMRunnableModel(this);
	}
	
	public double doEMstep() {
		double t, x, llf, tmpv;
		
		// initialize for estep
		eno = 0.0;
		MABlas.fill(ndim, eb, 0.0);
		MABlas.fill(ndim, eb2, 0.0);
		MABlas.fill(ndim*2, en, 0.0);
		
		// backward: compute eb
		MABlas.fill(ndim, vb[0], 1.0);
		MABlas.fill(ndim, vb2[0], 0.0);
		vb2[0][ndim-1] = cpara.getRateParameter()[ndim-1];
		
		llf = 0.0;
		for (int k=1; k<=dsize; k++) {
			t = dat.getTime(k);
			x = dat.getNumber(k);
			
			MABlas.dcopy(ndim, vb[k-1], vb[k]);
			cuni.doBackward(t, vb[k]);
			if (x != 0.0) {
				MABlas.dcopy(ndim, vb[k-1], tmp);
				MABlas.daxpy(ndim, -1.0, vb[k], tmp);
				blf[k] = MABlas.ddot(ndim, cpara.getInitialVector(), tmp);
				llf += x * Math.log(omega * blf[k]) - Numlib.loggamma(x+1);
				
				eno += x;
				MABlas.daxpy(ndim, x/blf[k], tmp, eb);
			} else {
				blf[k] = 1.0; // to avoid NaN
			}
			
			MABlas.dcopy(ndim, vb2[k-1], vb2[k]);
			cuni.doBackward(t, vb2[k]);
			if (dat.getType(k) == 1) {
				blf2[k] = MABlas.ddot(ndim, cpara.getInitialVector(), vb2[k]);
				llf += Math.log(omega * blf2[k]);
				eno += 1.0;
				MABlas.daxpy(ndim, 1.0/blf2[k], vb2[k], eb2);
		    }
		}
		barblf = MABlas.ddot(ndim, cpara.getInitialVector(), vb[dsize]);
		llf += - omega * (1.0 - barblf);
		MABlas.daxpy(ndim, omega, vb[dsize], eb);
		
		// compute pi2
		tmpv = 0.0;
		for (int i=0; i<ndim-1; i++) {
			tmpv += cpara.getInitialVector()[i];
			pi2[i] = tmpv / cpara.getRateParameter()[i];
		}
		pi2[ndim-1] = 1.0 / cpara.getRateParameter()[ndim-1];
		
		// sojourn:
		MABlas.fill(ndim, tmp, 0.0);
		MABlas.daxpy(ndim, -dat.getNumber(dsize)/blf[dsize] + omega, pi2, tmp);
		if (dat.getType(dsize) == 1) {
			MABlas.daxpy(ndim, 1.0/blf2[dsize], cpara.getInitialVector(), tmp);
		}
		cuni.doSojournForward(dat.getTime(dsize), tmp, vb2[dsize-1], h0);
//		ker->doSojournForward(dat->getIntervalTime(dsize), 
//					tmp, vb2[dsize-1], H0);
		MABlas.daxpy(ndim*2, 1.0, h0, en);
		for(int k=dsize-1; k>=1; k--) {
			MABlas.daxpy(ndim, dat.getNumber(k+1)/blf[k+1] - dat.getNumber(k)/blf[k], pi2, tmp);
			if (dat.getType(k) == 1) {
				MABlas.daxpy(ndim, 1.0/blf2[k], cpara.getInitialVector(), tmp);
		    }
			cuni.doSojournForward(dat.getTime(k), tmp, vb2[k-1], h0);
//		    ker->doSojournForward(dat->getIntervalTime(k), 
//					  tmp, vb2[k-1], H0);
			MABlas.daxpy(ndim*2, 1.0, h0, en);
		}

		/* concrete algorithm: M-step */
		omega = eno + omega * barblf;
		for (int i=0; i<ndim-1; i++) { // <-- not <=ndim!
			ey[i] = cpara.getRateParameter()[i] 
			        * (en[2*i+1] + eb[i+1] * pi2[i])
			        / (en[2*i] + eb[i] * pi2[i]);
		}
		tmpv = en[2*(ndim-1)] + eb[ndim-1] * pi2[ndim-1];
		double sum = 0.0;
		for (int i=0; i<ndim; i++) {
			eb[i] = cpara.getInitialVector()[i] * (eb[i] + eb2[i]);
			sum += eb[i];
		}
		ey[ndim-1] = sum / tmpv;
		for (int i=0; i<ndim; i++) {
			eb[i] /= sum;
		}
		MABlas.dcopy(ndim, eb, cpara.getInitialVector());
		MABlas.dcopy(ndim, ey, cpara.getRateParameter());
		cph.setParameters(omega, cpara.getInitialVector(), cpara.getRateParameter());
		return llf;
	}
	
	public String getParameterString() {
		return cph.getParameterString();
	}
}
