package phsrm.common;

abstract public class EMControlWithPrint extends EMControl {

	int logSegment;
	int logMin;

//	PrintStream buf;
//	StringBuffer buf;
	SRMBufferedMsg buf;

	public EMControlWithPrint() {
		super();
		setLogSegment(1);
		setMinSegment(1);
		buf = new SRMBufferSystemOut();
	}

	public void setBuffer(SRMBufferedMsg buf) {
		this.buf = buf;
	}
	
	public SRMBufferedMsg getBuffer() {
		return buf;
	}

	public void setLogSegment(int s) {
		logSegment = s;
	}
	
	public void setMinSegment(int m) {
		logMin = m;
	}
}
