package com.deleidos.rtws.ami.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deleidos.rtws.ami.exception.NativeCommandExecutionException;
import com.deleidos.rtws.webapp.amiapi.model.NativeCommandExecutionResult;

@Component
public class CredentialsManagement {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Openssl openssl;

	public File generatePk() {
		File pkFile = null;
		try {
			if(openssl==null)
				openssl = new Openssl();
			
			pkFile = File.createTempFile("pk-", ".pem");

			List<String> arguments = new LinkedList<String>();
			arguments.add("genrsa");
			arguments.add("-out");
			arguments.add(pkFile.getAbsolutePath());
			arguments.add("1024");
			NativeCommandExecutionResult rslt = openssl.run(arguments);
			if (!rslt.isSuccessful()) {
				logger.error(rslt.getStderr());
				logger.error(rslt.getStdout());
				throw new IOException("Failed to generate private key");
			}
		} catch (NativeCommandExecutionException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			pkFile.deleteOnExit();
		}

		return pkFile;
	}

	public File generateCert(File privateKey) {
		File certFile = null;
		try {
			certFile = File.createTempFile("cert-", ".pem");

			List<String> arguments = new LinkedList<String>();
			arguments.add("req");
			arguments.add("-out");
			arguments.add(certFile.getAbsolutePath());
			arguments.add("-batch");
			arguments.add("-new");
			arguments.add("-x509");
			arguments.add("-nodes");
			arguments.add("-sha1");
			arguments.add("-days");
			arguments.add("365");
			arguments.add("-key");
			arguments.add(privateKey.getAbsolutePath());
			arguments.add("-outform");
			arguments.add("PEM");
			NativeCommandExecutionResult rslt = openssl.run(arguments);
			if (!rslt.isSuccessful()) {
				logger.error(rslt.getStderr());
				logger.error(rslt.getStdout());
				throw new IOException("Failed to generate cert key");
			}
		} catch (NativeCommandExecutionException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			certFile.deleteOnExit();
		}
		return certFile;
	}
}
