package com.jio.lbs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.reinert.jjschema.Attributes;
import com.google.maps.model.LatLng;

@Attributes(title = "StaticMap", description = "Schema for an StaticMap")
public class RequestModel {
	
	@JsonProperty(value = "color", defaultValue = "#06327a" )
	@Attributes(required = true, description = "Color of Path")
	private String pathColor;
	
	@JsonProperty(value = "origin", required = true)
	@Attributes(required = true, description = "origin of map path")
	private LatLng startLocation;
	
	@JsonProperty(value = "destination", required = true)
	@Attributes(required = true, description = "destination of map path")
	private LatLng endLocation;
	
	@JsonProperty(value = "width", defaultValue="400")
	@Attributes(description = "width of google static map")
	private int width;
	
	@JsonProperty(value = "height", defaultValue="400")
	@Attributes(description = "height of google static map")
	private int height;
	
	@JsonProperty(value = "weight", defaultValue="1")
	@Attributes(description = "weight of google static map path")
	private int weight;
	
	@JsonProperty(value = "zoom", defaultValue="0")
	private int zoom;
	
	@JsonProperty(value = "center")
	private LatLng centerZoom;
	
	@JsonProperty(value = "maptype")
	@Attributes(description = "mapType", enums = {"roadmap","satellite","terrain","hybrid"})
	private String mapType;
	
	@JsonProperty("waypoint")
	@Attributes(required = false, minItems = 0, maxItems = 23, description = "waypoint")
	private List<LatLng> waypoint;

	public String getPathColor() {
		return pathColor;
	}

	public void setPathColor(String pathColor) {
		this.pathColor = pathColor;
	}

	public LatLng getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(LatLng startLocation) {
		this.startLocation = startLocation;
	}

	public LatLng getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(LatLng endLocation) {
		this.endLocation = endLocation;
	}

	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<LatLng> getWaypoint() {
		return waypoint;
	}

	public void setWaypoint(List<LatLng> waypoint) {
		this.waypoint = waypoint;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public LatLng getCenterZoom() {
		return centerZoom;
	}

	public void setCenterZoom(LatLng centerZoom) {
		this.centerZoom = centerZoom;
	}
	

	public String getMapType() {
		return mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	@Override
	public String toString() {
		return "RequestModel [pathColor=" + pathColor + ", startLocation=" + startLocation + ", endLocation="
				+ endLocation + ", width=" + width + ", height=" + height + ", weight=" + weight + ", zoom=" + zoom
				+ ", centerZoom=" + centerZoom + ", mapType=" + mapType + ", waypoint=" + waypoint + "]";
	}

}
