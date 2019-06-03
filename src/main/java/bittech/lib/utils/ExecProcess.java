package bittech.lib.utils;

public class ExecProcess {
	
	private Process process;
	
	public ExecProcess() {
		
	}
	
	public synchronized void setProcess(Process process) {
		this.process = Require.notNull(process, "process");
	}
	
	public synchronized void terminate() {
		if(process != null) {
			process.destroy();
		}
	}

}
