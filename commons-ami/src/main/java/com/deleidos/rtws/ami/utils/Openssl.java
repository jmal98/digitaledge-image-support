package com.deleidos.rtws.ami.utils;

import org.springframework.stereotype.Component;

@Component
public class Openssl extends AbstractNativeCommand {

	public Openssl() {
		super();
		command = "openssl";
	}

}
