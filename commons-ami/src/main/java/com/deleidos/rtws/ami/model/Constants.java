package com.deleidos.rtws.ami.model;

import java.util.Locale;
import java.util.ResourceBundle;

public class Constants {

	public static final String STACK_NAME_PREFIX = "DigitalEdge-Ami-Creation-";

	public static final String CREATED_AMI_NAME = "DigitalEdge AMI ";

	public static final String CREATED_AMI_PREFIX = "de-image-";

	public static final String IAM_USERNAME = "DigitalEdge_AMI";

	public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("amiapi", Locale.getDefault());

	public static final String TARGET_BUCKET_NAME_PREFIX = "rtws-account-";

	public static final String TARGET_BUCKET_NAME_SUFFIX = "-ami-";

}
