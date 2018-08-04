package com.deleidos.rtws.ami.utils;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

@Component
public class BootTimeCredentialsProvider implements AWSCredentialsProvider {

	private AWSCredentials credentials;

	@Autowired
	@Qualifier("bootProperties")
	private Properties bootProps;

	private void createCredentials() {

		if (bootProps.getProperty("RTWS_ACCESS_KEY") != null
				&& bootProps.getProperty("RTWS_SECRET_KEY") != null) {

			System.setProperty("RTWS_ACCESS_KEY",
					bootProps.getProperty("RTWS_ACCESS_KEY"));

			System.setProperty("RTWS_SECRET_KEY",
					bootProps.getProperty("RTWS_SECRET_KEY"));
			credentials = new AWSCredentials() {

				public String getAWSSecretKey() {
					return bootProps.getProperty("RTWS_SECRET_KEY");
				}

				public String getAWSAccessKeyId() {

					return bootProps.getProperty("RTWS_ACCESS_KEY");
				}
			};

		} else if (System.getProperty("RTWS_ACCESS_KEY") != null
				&& System.getProperty("RTWS_SECRET_KEY") != null) {
			credentials = new AWSCredentials() {

				public String getAWSSecretKey() {
					return System.getProperty("RTWS_SECRET_KEY");
				}

				public String getAWSAccessKeyId() {

					return System.getProperty("RTWS_ACCESS_KEY");
				}
			};
		} else {
			throw new IllegalStateException(
					"Unable to determine aws access/secret keys");
		}

	}

	public BootTimeCredentialsProvider() {
	}

	public AWSCredentials getCredentials() {

		if (credentials == null)
			createCredentials();

		return this.credentials;
	}

	public void refresh() {
		createCredentials();
	}
}
