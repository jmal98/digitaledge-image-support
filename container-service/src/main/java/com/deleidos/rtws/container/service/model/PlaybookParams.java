package com.deleidos.rtws.container.service.model;

public class PlaybookParams {

	private String rtws_release;
	private String rtws_system;

	public PlaybookParams(String rtws_release, String rtws_system) {
		super();

		this.rtws_release = rtws_release;
		this.rtws_system = rtws_system;
	}

	public String getRtws_release() {
		return rtws_release;
	}

	public String getRtws_system() {
		return rtws_system;
	}
}
