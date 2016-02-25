package phsrm.original;

import phsrm.common.*;

abstract public class EMCommonSRM implements EMRunnable {

	GroupData fdat;
	EMControl em;
	
	EMCommonSRM() {
		em = new EMControlRelativeLF();
	}

	EMCommonSRM(EMControl em) {
		this.em = em;
	}
	
	public void setEMControl(EMControl em) {
		this.em = em;
	}
	
	public EMControl getEMControl() {
		return em;
	}

	public void setGroupData(GroupData dat) {
		this.fdat = dat;
	}
	
	public GroupData getGroupData() {
		return fdat;
	}
	
	abstract public void doEstimation();
}
