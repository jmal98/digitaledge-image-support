package com.deleidos.rtws.ami.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BootProperties extends Properties {

	@Override
	public String getProperty(String arg0) {
		String val = super.getProperty(arg0);
		
		if (val == null || val.length() == 0)
			val = System.getProperty(arg0);
		
		return val;
	}

	private static final long serialVersionUID = 1L;

	public BootProperties() {
		super();

		Logger logger = LoggerFactory.getLogger(getClass());
		InputStream rtwsrcReader = null;

		try {
			rtwsrcReader = new BufferedInputStream( new FileInputStream("/etc/rtwsrc"));
			load(rtwsrcReader);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(rtwsrcReader);
		}
	}

	public BootProperties(Properties defaults) {
		super(defaults);

	}

}
