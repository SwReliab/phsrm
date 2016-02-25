package phsrm.ui;

import phsrm.cphsrm.*;
import phsrm.common.*;
import phsrm.hersrm.HyperErlangSRM;
import phsrm.original.CommonSRM;

public class ALLSRMControl extends SRMControl {

	int mcph, mhysrm, mcsrm;
	
	ALLSRMControl(LogPanel logbuf, int mcph, int mhysrm, int mcsrm) {
		super(logbuf);
		this.mcph = mcph;
		this.mhysrm = mhysrm;
		this.mcsrm = mcsrm;
		numberOfSRMs = mcph + mhysrm + mcsrm;
		srm = new NHPPSoftwareReliabilityModels [numberOfSRMs];		
		flagDataSet = false;
	}
	
	public void setCPHSRM(CPHSRM[] cphsrm) {
		for (int i=0; i<cphsrm.length; i++) {
			srm[i] = cphsrm[i];
		}
	}

	public void setHErSRM(HyperErlangSRM[] cphsrm) {
		for (int i=0; i<cphsrm.length; i++) {
			srm[mcph + i] = cphsrm[i];
		}
	}
	
	public void setCommonSRM(CommonSRM[] csrm) {
		for (int i=0; i<csrm.length; i++) {
			srm[mcph + mhysrm + i] = csrm[i];
		}		
	}
}
