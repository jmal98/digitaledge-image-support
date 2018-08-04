package com.deleidos.rtws.ami.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Image;
import com.deleidos.rtws.ami.model.BootProperties;
import com.deleidos.rtws.ami.model.PropertyProvider;

@Component
public class ImageIdAccountPropertyProvider implements PropertyProvider<String> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("bootTimeCredentialsProvider")
	private AWSCredentialsProvider credentialsProvider;

	@Autowired
	@Qualifier("localInstanceImageIdProperyProvider")
	private PropertyProvider<String> propProvider;

	@Autowired
	private BootProperties bootProps;

	public String provide() {

		String accountId = null;

		if (bootProps.getProperty("RTWS_SERVICE_NUMBER") != null) {
			accountId = bootProps.getProperty("RTWS_SERVICE_NUMBER");
		} else {

			// Relies on the currently running amiid's account id
			String hostAmiId = propProvider.provide();
			AmazonEC2 ec2 = null;
			try {
				ec2 = new AmazonEC2Client(credentialsProvider);
				DescribeImagesResult describeResult = ec2
						.describeImages(new DescribeImagesRequest()
								.withImageIds(hostAmiId));
				for (Image image : describeResult.getImages()) {
					accountId = image.getOwnerId();
				}
			} catch (AmazonServiceException e) {
				logger.error(e.getMessage());
			} catch (AmazonClientException e) {
				logger.error(e.getMessage());
			} finally {
				if (ec2 != null)
					ec2.shutdown();
			}
		}
		return accountId;
	}
}
