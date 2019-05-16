package com.jio.lbs.model;

import java.util.ArrayList;
import java.util.List;

import com.google.maps.model.LatLng;

public class PerRequestPathModel {
	
	LatLng origin;
	
	LatLng destination;
	
	List<LatLng> waypoints;

	public LatLng getOrigin() {
		return origin;
	}

	public void setOrigin(LatLng origin) {
		this.origin = origin;
	}

	public LatLng getDestination() {
		return destination;
	}

	public void setDestination(LatLng destination) {
		this.destination = destination;
	}

	public List<LatLng> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<LatLng> waypoints) {
		this.waypoints = waypoints;
	}
	
	public void addWaypoints(LatLng waypoint) {
		if(null == this.waypoints) {
			this.waypoints = new ArrayList<LatLng>();
		}
		this.waypoints.add(waypoint);
	}

}
