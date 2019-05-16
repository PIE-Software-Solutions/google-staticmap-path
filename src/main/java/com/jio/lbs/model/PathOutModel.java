package com.jio.lbs.model;

import com.google.maps.model.EncodedPolyline;

public class PathOutModel {

	
	EncodedPolyline encodedPolyline;
	
	double distance;

	public EncodedPolyline getEncodedPolyline() {
		return encodedPolyline;
	}

	public void setEncodedPolyline(EncodedPolyline encodedPolyline) {
		this.encodedPolyline = encodedPolyline;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
}
