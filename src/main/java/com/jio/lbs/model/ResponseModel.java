package com.jio.lbs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel {
	
	@JsonProperty(value = "distance")
	double distance;
	
	@JsonProperty(value = "image")
	byte[] imageStr;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public byte[] getImageStr() {
		return imageStr;
	}

	public void setImageStr(byte[] imageStr) {
		this.imageStr = imageStr;
	}

}
