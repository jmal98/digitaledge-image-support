package com.deleidos.rtws.ami.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.deleidos.rtws.ami.model.PropertyProvider;

@Component
public class LocalInstanceImageIdProperyProvider implements
		PropertyProvider<String> {

	public String provide() {
		Logger logger = LoggerFactory.getLogger(getClass());
		InputStream is = null;
		String amiId = null;
		try {
			URL metadataService = new URL(
					"http://169.254.169.254/latest/meta-data/ami-id");

			is = metadataService.openStream();
			String id = IOUtils.toString(is);

			if (id != null) {
				amiId = id;
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(is);
		}

		return amiId;
	}

}
