package com.deleidos.rtws.webapp.amiapi.model;

public class NativeCommandExecutionResult {

	private boolean successful = false;
	private String stderr = "";
	private String stdout = "";

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getStderr() {
		return stderr;
	}

	public void setStderr(String stderr) {
		this.stderr = stderr;
	}

	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NativeCommandExecutionResult [successful=").append(successful).append(", stderr=").append(stderr)
				.append(", stdout=").append(stdout).append("]");
		return builder.toString();
	}
}
