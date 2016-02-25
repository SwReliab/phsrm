package phsrm.ui;

import java.util.Arrays;
import java.util.Comparator;

import phsrm.common.*;

public class SRMControl {

	LogPanel logbuf;
	GroupData dat;

	boolean flagDataSet;

	//srm
	protected int numberOfSRMs;
	protected NHPPSoftwareReliabilityModels[] srm;
	
	SRMControl(LogPanel logbuf) {
		this.logbuf = logbuf;
		numberOfSRMs = 1;
		srm = new NHPPSoftwareReliabilityModels [numberOfSRMs];
	}
	
	public int getNumberOfSRMs() {
		return numberOfSRMs;
	}
	
	public void setGroupData(GroupData d) {
		this.dat = d;
		for (int i=0; i<numberOfSRMs; i++) {
			srm[i].setInitialParameters(d);
		}
		logbuf.appendln("All SRM parameters are initialized.");
		flagDataSet = true;
	}
	
	public NHPPSoftwareReliabilityModels[] getSortedSRMs() {
		return srm;
	}

	public NHPPSoftwareReliabilityModels getSortedSRMs(int i) {
		return srm[i];
	}
}
