package phsrm.ui;

import phsrm.common.*;
import phsrm.cphsrm.CPHSRM;
import phsrm.original.*;

public class CommonSRMControl extends SRMControl {

	//srm
	CommonSRM[] csrm;	
	EMControlWithPrint[] cem;
	
	CommonSRMControl(LogPanel logbuf) {
		super(logbuf);
		numberOfSRMs = 11;
		srm = new NHPPSoftwareReliabilityModels [numberOfSRMs];
		csrm = new CommonSRM [numberOfSRMs];
		cem = new EMControlWithPrint [numberOfSRMs];
		srm[0] = csrm[0] = new ExponentialSRM ();
		srm[1] = csrm[1] = new GammaSRM ();
		srm[2] = csrm[2] = new ParetoSRM ();
		srm[3] = csrm[3] = new TruncatedNormalSRM ();
		srm[4] = csrm[4] = new LogNormalSRM ();
		srm[5] = csrm[5] = new TruncatedLogisticSRM ();
		srm[6] = csrm[6] = new LogLogisticSRM ();
		srm[7] = csrm[7] = new TruncatedExtremeValueMaxSRM ();
		srm[8] = csrm[8] = new LogExtremeValueMaxSRM ();
		srm[9] = csrm[9] = new TruncatedExtremeValueMinSRM ();
		srm[10] = csrm[10] = new LogExtremeValueMinSRM ();
		for (int i=0; i<numberOfSRMs; i++) {
			cem[i] = new EMControlRelativeLF(1.0e-7);
			cem[i].setBuffer(logbuf);
		}
		flagDataSet = false;
	}
	
	public void doFitting() {
		if (flagDataSet) {
			for (int i=0; i<numberOfSRMs; i++) {
				csrm[i].doEstimation(dat, cem[i]);
			}
		}
	}

	public CommonSRM[] getCommonSRM() {
		return csrm;
	}

	public CommonSRM getCommonSRM(int i) {
		return csrm[i];
	}
}
