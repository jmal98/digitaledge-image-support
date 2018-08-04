package com.deleidos.rtws.container.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public enum YamlMapperCache {
	instance;

	// mapper is thread safe and expensive to construct so do it once
	public static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

}
