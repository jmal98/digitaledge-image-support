package com.deleidos.rtws.ami.utils;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.deleidos.rtws.ami.model.PropertyProvider;

@Component
public class LocalTenantIdPropertyProvider implements PropertyProvider<String> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier(value = "bootProperties")
	private Properties bootProps;

	public String getTemporaryId() {
		String id = null;
		id = UUID.randomUUID().toString().split("-")[0];
		logger.info("Returning temporary id '{}'", id);
		return id;
	}

	public String provide() {

		// Rely on RTWS_BUCKET_NAME to extract out the tenant id
		String tenantId = null;

		String rtwsBucketName = bootProps.getProperty("RTWS_BUCKET_NAME");
		if (rtwsBucketName == null) {			
			tenantId = getTemporaryId();
			logger.warn("Defaulting id '{}' due to missing RTWS_BUCKET_NAME property containing the tenant id.", tenantId);
		} else {
			String[] tokens = rtwsBucketName.split("\\.");
			if (tokens.length == 3)
				tenantId = tokens[2];
		}

		return tenantId;
	}

}
