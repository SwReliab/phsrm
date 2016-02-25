package phsrm.common;

public interface EMRunnable {
	public abstract double doEMstep();
	public abstract String getParameterString();
}