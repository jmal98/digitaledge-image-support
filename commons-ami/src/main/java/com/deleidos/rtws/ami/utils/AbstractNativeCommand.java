package com.deleidos.rtws.ami.utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.ami.exception.NativeCommandExecutionException;
import com.deleidos.rtws.webapp.amiapi.model.NativeCommand;
import com.deleidos.rtws.webapp.amiapi.model.NativeCommandExecutionResult;

public class AbstractNativeCommand implements NativeCommand {

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected String command = null;

	
	public NativeCommandExecutionResult run(List<String> arguments) throws NativeCommandExecutionException {
		NativeCommandExecutionResult result = new NativeCommandExecutionResult();

		if (command == null) {
			throw new NativeCommandExecutionException("No command provided.");
		}

		if (arguments.size() == 0) {
			throw new NativeCommandExecutionException("No command parameters provided.");
		}
		List<String> commandToExecute = new LinkedList<String>();
		commandToExecute.add(command);
		commandToExecute.addAll(arguments);

		ProcessBuilder pb = new ProcessBuilder();
		pb.command(commandToExecute);

		// Map<String, String> env = pb.environment();

		try {
			logger.info("Executing: " + pb.command());
			Process p = pb.start();
			int rtn = p.waitFor();

			String stdout = IOUtils.toString(p.getInputStream(), "UTF-8");
			String stderr = IOUtils.toString(p.getErrorStream(), "UTF-8");

			result.setStdout(stdout);
			result.setStderr(stderr);

			if (rtn == 0)
				result.setSuccessful(true);

			if (logger.isDebugEnabled()) {
				logger.debug("return code: " + rtn);
				logger.debug("stdout:" + stdout);
				logger.debug("stderr:" + stderr);
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} finally {
		}

		return result;
	}

}
