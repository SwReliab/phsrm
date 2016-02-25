package phsrm.common;

import java.io.PrintStream;

public class SRMBufferSystemOut implements SRMBufferedMsg {
	static PrintStream out = System.out;
	
	public void append(String str) {
		out.append(str);
	}
}
