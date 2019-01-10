package lnzone.lib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lnzone.lib.utils.exceptions.StoredException;

public class SystemExec {

	static class StreamGrabber extends Thread {
		private final InputStream is;
		private String output = null;

		StreamGrabber(InputStream is) {
			this.is = is;
		}

		public String getOutput() {
			return output;
		}

		public void run() {
			try {
				// StringBuilder sb = new StringBuilder();
				//
				// InputStreamReader isr = new InputStreamReader(is);
				// BufferedReader br = new BufferedReader(isr);
				// String line = null;
				// while ((line = br.readLine()) != null) {
				// sb.append(line);
				// sb.append("\n");
				// }
				output = IOUtils.toString(is); // sb.toString();
				if (StringUtils.isEmpty(output)) {
					output = null;
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public SystemExec() {
		// TODO Auto-generated constructor stub
	}

	public static ExecResponse exec(String cmd, long timeout) throws StoredException {
		Process proc = null;
		try {
			Runtime rt = Runtime.getRuntime();

			proc = rt.exec(cmd);

			StreamGrabber errorGrabber = new StreamGrabber(proc.getErrorStream());
			StreamGrabber outputGrabber = new StreamGrabber(proc.getInputStream());

			errorGrabber.start();
			outputGrabber.start();

			proc.waitFor(timeout, TimeUnit.MILLISECONDS);
			errorGrabber.join(1000);
			outputGrabber.join(1000);

			ExecResponse resp = new ExecResponse();
			resp.exitCode = proc.exitValue();
			resp.errout = errorGrabber.getOutput();
			resp.output = outputGrabber.getOutput();

			return resp;
		} catch (Throwable th) {
			throw new StoredException("Cannot run command: " + cmd, th);
		} finally {
			if (proc != null) {
				try {
					proc.destroy();
				} catch (Throwable th) {
					throw new StoredException("Cannot destroy process of command: " + cmd, th);
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		SystemExec ses = new SystemExec();
		SystemExec.exec("pwd", 10000);
	}

}
