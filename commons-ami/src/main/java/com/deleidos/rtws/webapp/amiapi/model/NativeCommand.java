package com.deleidos.rtws.webapp.amiapi.model;

import java.util.List;

import com.deleidos.rtws.ami.exception.NativeCommandExecutionException;

public interface NativeCommand {

	public NativeCommandExecutionResult run(List<String> arguments) throws NativeCommandExecutionException;

}
