package com.deleidos.rtws.ami.model;

import com.amazonaws.auth.AWSCredentials;

public interface AmiCreationPreconditions<D> {

	public D execute(AWSCredentials credentials);
}
