package com.deleidos.rtws.container.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.command.BuildImageResultCallback;

public class DockerImageBuildCallback extends BuildImageResultCallback {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onNext(BuildResponseItem item) {

		if (item.getStream() != null)
			logger.info(item.getStream());
		
		if (item.getErrorDetail() != null)
			logger.error(item.getErrorDetail().toString());
		
		super.onNext(item);
	}

}
