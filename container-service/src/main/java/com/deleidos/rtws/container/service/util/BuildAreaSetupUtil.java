package com.deleidos.rtws.container.service.util;

import java.io.File;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;

public interface BuildAreaSetupUtil {
	
	public File setupBuildTarget(ImageBuildRequest buildRequest);

}
