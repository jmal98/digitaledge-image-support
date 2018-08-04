package com.deleidos.rtws.ami.model;

import com.amazonaws.auth.AWSCredentials;

public interface CredentialsUploadUtil {

	public void storeCredentialsInS3(String amiId, AWSCredentials credentials,IamUserData iamData);
}