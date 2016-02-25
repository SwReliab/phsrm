package phsrm.ui;

import phsrm.cphsrm.*;
import phsrm.common.*;

public class CPHSRMControl extends SRMControl {

	//srm
	CPHSRM[] cphsrms;	
	EMControlWithPrint[] cphem;
	
	CPHSRMControl(LogPanel logbuf, int m) {
		super(logbuf);
		numberOfSRMs = m;
		srm = new NHPPSoftwareReliabilityModels [numberOfSRMs];		
		cphsrms = new CPHSRM [numberOfSRMs];
		cphem = new EMControlWithPrint [numberOfSRMs];
		for (int i=0; i<numberOfSRMs; i++) {
			srm[i] = cphsrms[i] = new CPHSRM(i+2);
			cphem[i] = new EMControlRelativeLF(1.0e-7);
			cphem[i].setBuffer(logbuf);
		}
		flagDataSet = false;
	}
	
	public void doFitting() {
		if (flagDataSet) {
			for (int i=0; i<numberOfSRMs; i++) {
				cphsrms[i].doEstimation(dat, cphem[i]);
			}
		}
	}
	
	public CPHSRM[] getCPHSRM() {
		return cphsrms;
	}

	public CPHSRM getCPHSRM(int i) {
		return cphsrms[i];
	}
}
