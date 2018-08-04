package com.deleidos.rtws.ami.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AmiUtils {

	public static String getAmiNameSuffix() {
		String suffix = null;
		SimpleDateFormat name = new SimpleDateFormat("yyyy-MM-dd_HHmmssSz");
		try {
			suffix = name.format(Calendar.getInstance().getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return suffix;
	}

	public static String getAmiPrefixSuffix() {
		String suffix = null;
		SimpleDateFormat prefix = new SimpleDateFormat("yyyyMMdd_HHmmssSz");
		try {
			suffix = prefix.format(Calendar.getInstance().getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return suffix;
	}
}
