package phsrm.hersrm;

import java.util.Random;
import phsrm.common.*;

final public class HyperErlangSRM extends NHPPSoftwareReliabilityModels {

	int totalPhase;
	double totaltime;
	HyperErlangParameters hyerl;
	
	public HyperErlangSRM() {
		totalPhase = 2;
		hyerl = new HyperErlangParameters(totalPhase);
	    int[] tmp = new int [totalPhase];
	    for (int i=0; i<totalPhase; i++) {
	    	tmp[i] = 1;
	    }
	    hyerl.setShapeParameters(tmp);
	}
	
	public HyperErlangSRM(int m) {
		totalPhase = m;
		hyerl = new HyperErlangParameters(totalPhase);
	    int[] tmp = new int [totalPhase];
	    for (int i=0; i<totalPhase; i++) {
	    	tmp[i] = 1;
	    }
	    hyerl.setShapeParameters(tmp);
	}
	
	public String getModelString() {
		return "" + totalPhase + "-HErSRM";
	}

	public void setParameters(double omega, double[] initv, int[] shape, double[] rate) {
		setOmega(omega);
		hyerl.setInitalVector(initv);
		hyerl.setShapeParameters(shape);
		hyerl.setRateParameters(rate);
	}
	
	public HyperErlangParameters getHErParameter() {
		return hyerl;
	}
	
	public String getParameterString() {
		String str = "omega: " + SRMText.decimalE8.format(omega) + SRMText.ln
			+ hyerl.getParameterString();
		return str;
	}
	
	// mean, reli, frate
	public MVFData getMeanValueFunction(double[] intervals, double t) {
		double cur, geta;
		MVFData res = new MVFData();
		if (t == 0.0) {
			cur = 0.0;
			geta = 0.0;
		} else {
			cur = t;
			geta = hyerl.getCDF(cur);
		}
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, omega * (hyerl.getCDF(cur) - geta));
		}
		return res;
	}
	
	public MVFData getReliabilityFunction(double s, double[] intervals) {
		double cur;
		cur = s;
		MVFData res = new MVFData(s, 1.0);
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, Math.exp(-omega*(hyerl.getCDF(cur) - hyerl.getCDF(s))));
		}
		return res;
	}

	public MVFData getFailureRate(double[] intervals) {
		double cur;
		MVFData res = new MVFData(0, hyerl.getPDF(0));
		cur = 0.0;
		for (int i=0; i<intervals.length; i++) {
			cur += intervals[i];
			res.addDataRecord(cur, hyerl.getPDF(cur) / (1.0 - hyerl.getCDF(cur)));
		}
		return res;
	}
	
	// simple estimation
	public void setInitialParameters(GroupData dat) {
		Random rnd = new Random(1);
		omega = dat.getTotalNumber();
		double[] tmp = new double [hyerl.getNumberOfErlangDists()];
		double sum = 0.0;
		for (int i=0; i<hyerl.getNumberOfErlangDists(); i++) {
			sum += tmp[i] = rnd.nextDouble();
			hyerl.setRateParameters(i, hyerl.getShapeParameters()[i]/dat.getTotalTime());
		}
		for (int i=0; i<hyerl.getNumberOfErlangDists(); i++) {
			hyerl.setInitalVector(i, tmp[i]/sum);
		}
	}
	
	// estimation
	EMHyperErlangSRM emcph;
	double max;
	int maxm;
	int[] maxshape;
	
	public void doEstimation(GroupData dat) {
		emcph = new EMHyperErlangSRM();
		emcph.setHErSRM(this);
		emcph.setGroupData(dat);
		doEstimationShape(dat);
	}
	
	public void doEstimation(GroupData dat, EMControl em) {
		emcph = new EMHyperErlangSRM(em);
		emcph.setHErSRM(this);
		emcph.setGroupData(dat);
		doEstimationShape(dat);
	}
	
	public EMControl getEMControl() {
		return emcph.getEMControl();
	}

    public void doEstimationShape(GroupData dat) {
        int m = totalPhase;
        long stt = System.currentTimeMillis();
        
        max = -1.0e200;

        int list[] = new int [m];
        maxshape = new int [m];
        makeshape(0, list, 1, m, dat);

        hyerl.setNumberOfErlangDists(maxm);
        for (int i=0; i<maxm; i++) {
            hyerl.setShapeParameters(i, maxshape[i]);
        }
		this.setInitialParameters(dat);
		emcph.doEstimation();
		totaltime = (System.currentTimeMillis() - stt)/1000.0;
        String str = "ctime = " + totaltime + " (sec)" + SRMText.ln;
		((EMControlWithPrint) emcph.getEMControl()).getBuffer().append(str);
    }
    
    public double getTotalComputationTime() {
    	return totaltime;
    }

    public void makeshape(int m, int list[], int u, int res, GroupData dat) {
    	if (u > res) {
    		return;
    	}
        for (int i=u; i<res; i++) {
            list[m] = i;
            makeshape(m+1, list, i, res-i, dat);
        }
        list[m] = res;

        hyerl.setNumberOfErlangDists(m+1);
        for (int i=0; i<=m; i++) {
            hyerl.setShapeParameters(i, list[i]);
//            System.out.print("" + list[i] + " ");
        }
//        System.out.println();
		this.setInitialParameters(dat);
		emcph.doEstimation();
        double tmp = this.getLogLikelihood();
//        System.out.println("Log-likelihood = " + tmp);
        if (tmp > max) {
            max = tmp;
            maxm = m+1;
            for (int i=0; i<=m; i++) {
                maxshape[i] = list[i];
            }
        }
        list[m] = 0;
    }
    
	// information criteria
    
    public int getNumberOfParameters() {
//    	return 3*hyerl.getNumberOfErlangDists() - 1;
//    	return 3*hyerl.getNumberOfErlangDists();
    	return 2*hyerl.getNumberOfErlangDists();
//    	return 2*hyerl.getNumberOfErlangDists();
//    	return 3*totalPhase - 2;
    }

	public double getLogLikelihood() {
		return emcph.getEMControl().getLLF();
	}

	public double getAIC() {
		return ((-2.0) * emcph.getEMControl().getLLF() + 2.0 * getNumberOfParameters());
	}

	public double getBIC() {
		int fn = emcph.getGroupData().getNumberOfRecords();
		return ((-2.0) * emcph.getEMControl().getLLF() + Math.log(fn) * getNumberOfParameters());
	}
}
