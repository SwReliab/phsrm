package phsrm.ui;

import phsrm.common.*;
import phsrm.cphsrm.CPHSRM;
import phsrm.hersrm.*;

public class HErSRMControl extends SRMControl {

	//srm
	HyperErlangSRM[] hysrms;	
	EMControlWithPrint[] hyem;
	
	HErSRMControl(LogPanel logbuf, int m) {
		super(logbuf);
		numberOfSRMs = m;
		srm = new NHPPSoftwareReliabilityModels [numberOfSRMs];		
		hysrms = new HyperErlangSRM [numberOfSRMs];
		hyem = new EMControlWithPrint [numberOfSRMs];
		for (int i=0; i<numberOfSRMs; i++) {
			srm[i] = hysrms[i] = new HyperErlangSRM(i+2);
			hyem[i] = new EMControlRelativeLF(1.0e-7);
			hyem[i].setBuffer(logbuf);
		}		
		flagDataSet = false;
	}
	
	public void doFitting() {
		if (flagDataSet) {
			for (int i=0; i<numberOfSRMs; i++) {
				hysrms[i].doEstimation(dat, hyem[i]);
			}
		}
	}

	public HyperErlangSRM[] getHErSRM() {
		return hysrms;
	}

	public HyperErlangSRM getHErSRM(int i) {
		return hysrms[i];
	}
}
