package com.jio.lbs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.reinert.jjschema.Attributes;

public class AESDetails {

	@JsonProperty(value = "secretKey")
	@Attributes(required = true)
	private String secretKey;
	
	@JsonProperty(value = "salt")
	@Attributes(required = true)
	private String salt;
	
	@JsonProperty(value = "clearpass")
	@Attributes(required = true)
	private String clearpass;

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getClearpass() {
		return clearpass;
	}

	public void setClearpass(String clearpass) {
		this.clearpass = clearpass;
	}
	
}
