package com.deleidos.rtws.ami.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IamUserData {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String username;
	private String accessKey;
	private String secretKey;
	private String certId;
	private File pk;
	private File cert;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public File getPk() {
		return pk;
	}

	public String getPkAsString() {
		String pkStr = null;
		try {
			pkStr = IOUtils.toString(
					new URI("file:///" + pk.getAbsolutePath().replace('\\', '/')), "UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}

		return pkStr;

	}

	public void setPk(File pk) {
		this.pk = pk;
	}

	public File getCert() {
		return cert;
	}

	public String getCertAsString() {
		String certStr = null;
		try {
			certStr = IOUtils.toString(
					new URI("file:///" + cert.getAbsolutePath().replace('\\', '/')), "UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}

		return certStr;
	}

	public void setCert(File cert) {
		this.cert = cert;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IamUserData [username=").append(username)
				.append(", accessKey=").append(accessKey)
				.append(", secretKey=").append(secretKey).append(", certId=")
				.append(certId).append(", pk=").append(pk).append(", cert=")
				.append(cert).append("]");
		return builder.toString();
	}
}
