package com.deleidos.rtws.container.service.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.PlaybookParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlTest {

	private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unused")
	private static class Params extends PlaybookParams {

		private String rtws_release_bucket;
		private String aws_access_key;
		private String aws_secret_key;

		public Params(String rtws_release_bucket, String rtws_release, String rtws_system, String aws_access_key,
				String aws_secret_key) {
			super(rtws_release, rtws_system);

			this.rtws_release_bucket = rtws_release_bucket;
			this.aws_access_key = aws_access_key;
			this.aws_secret_key = aws_secret_key;
		}

		public String getRtws_release_bucket() {
			return rtws_release_bucket;
		}

		public String getAws_access_key() {
			return aws_access_key;
		}

		public String getAws_secret_key() {
			return aws_secret_key;
		}
	}

	@Test
	public void go() throws JsonProcessingException {
		Params params = new Params("bucket", "rtws-nightly:1", "acme.deleidos.com", "abcd", "1234");
		String val = mapper.writeValueAsString(params);
		assertNotNull(val);
		logger.info(val);
	}
}
