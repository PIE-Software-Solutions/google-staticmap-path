package com.jio.lbs.controller;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.StaticMapsApi;
import com.google.maps.StaticMapsRequest;
import com.google.maps.DirectionsApiRequest.Waypoint;
import com.google.maps.StaticMapsRequest.ImageFormat;
import com.google.maps.StaticMapsRequest.Markers;
import com.google.maps.StaticMapsRequest.Path;
import com.google.maps.StaticMapsRequest.StaticMapType;
import com.google.maps.StaticMapsRequest.Markers.MarkersSize;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;
import com.jio.lbs.model.AESDetails;
import com.jio.lbs.model.PathOutModel;
import com.jio.lbs.model.PerRequestPathModel;
import com.jio.lbs.model.RequestModel;
import com.jio.lbs.model.ResponseModel;
import com.jio.lbs.utils.Decrypt;
import com.jio.lbs.utils.Encrypt;

import static com.jio.lbs.utils.AppConstants.ZOOM_DEFAULT;
import static com.jio.lbs.utils.AppConstants.SERVICE_CONN_TIMEOUT;
import static com.jio.lbs.utils.AppConstants.MAX_WAY_POINTS;

@Configuration
@RestController
@PropertySource(ignoreResourceNotFound=false,value="file:${app.confpath}/application.properties")
public class ImageController {
	public static GeoApiContext context;
	
	@Value("${app.ApiKey}")
	public String apiKey;
	
	
	@Value("${app.ConnTimeOut}")
	public String connTimeOut;
	
	private static ObjectMapper mapper = new ObjectMapper();
    
    static {
        // required for pretty printing
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public String getSchema()  {
    	JsonSchemaV4Factory schemaFactory = new JsonSchemaV4Factory();
    	schemaFactory.setAutoPutDollarSchema(true);
    	JsonNode schema = schemaFactory.createSchema(RequestModel.class);
    	try {
			return mapper.writeValueAsString(schema);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/generatePath", method = RequestMethod.POST)
	public ResponseEntity<ResponseModel>/*@ResponseBody byte[]*/ getFile(@RequestBody RequestModel requestModel)  {
		Markers[] markers = new Markers[3];
		markers[0] = new Markers();
	      markers[0].size(MarkersSize.normal);
	      markers[0].color(requestModel.getMarkerStart());
	      markers[0].label("S");
	      markers[0].addLocation(requestModel.getStartLocation());
	      
	      markers[1] = new Markers();
	      markers[1].size(MarkersSize.normal);
	      markers[1].color(requestModel.getMarkerEnd());
	      markers[1].label("D");
	      markers[1].addLocation(requestModel.getEndLocation());
	    if(context == null)
		{
	    	String decApiKey = Decrypt.decrypt(apiKey, null, null);
			context =
			        new GeoApiContext.Builder()
				        .apiKey(decApiKey)
				        .connectTimeout(Integer.parseInt(connTimeOut), TimeUnit.MILLISECONDS)
			            .build();
		}
		if(context == null)
		{
			return ResponseEntity.status(SERVICE_CONN_TIMEOUT).build();
		}
		else
		{
			if(!requestModel.getWaypoint().isEmpty())
			{
				List<LatLng> latLngs = requestModel.getWaypoint();
				markers[2] = new Markers();
				markers[2].size(MarkersSize.small);
				markers[2].color(requestModel.getMarkerIp());
				markers[2].label("L");
				for (LatLng latLng : latLngs) {
					markers[2].addLocation(latLng);
				}
			}
			try {
				PathOutModel pathOutModel = getAllConsolidatedPolyLine(requestModel.getWaypoint(), requestModel.getStartLocation(), requestModel.getEndLocation());
				
				if( null == pathOutModel)
				{
					return ResponseEntity.status(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE).build();
				}
				  Path path = new Path();
				  path.color(requestModel.getPathColor());
				  path.addEncPolyline(pathOutModel.getEncodedPolyline());
				  path.weight(requestModel.getWeight());
					
				StaticMapsRequest req = StaticMapsApi.newRequest(context, new Size(requestModel.getWidth(), requestModel.getHeight()))
											.path(path)
											.format(ImageFormat.jpg);
				if(null != markers[0])
					req.markers(markers[0]);
				if(null != markers[1])
					req.markers(markers[1]);
				if(null != markers[2])
					req.markers(markers[2]);
				if(null != requestModel.getCenterZoom())
					req.center(requestModel.getCenterZoom());
				req.scale(requestModel.getScale());
				if(null != requestModel.getMapType() && !requestModel.getMapType().isEmpty())
					req.maptype(StaticMapType.valueOf(requestModel.getMapType()));
				if(requestModel.getZoom() != ZOOM_DEFAULT)
				{
					req.zoom(requestModel.getZoom());
				}
				
			  ByteArrayInputStream bais = new ByteArrayInputStream(req.await().imageData);

			  BufferedImage img = ImageIO.read(bais);
			  ByteArrayOutputStream bao = new ByteArrayOutputStream();					  
			  ImageIO.write(img, "jpg", bao);
			  ResponseModel responseModel = new ResponseModel();
			  responseModel.setDistance(pathOutModel.getDistance());
			  responseModel.setImageStr(bao.toByteArray()/*Base64.encodeBase64(bao.toByteArray())*/);
				return ResponseEntity.ok(responseModel);
			      
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(SERVICE_CONN_TIMEOUT).build();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(SERVICE_CONN_TIMEOUT).build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}finally {

			}
		}
	}
	
	private PathOutModel getConsolidatedPolyLine(List<LatLng> waypoints, String origin, String destination) {
		
		PathOutModel pathOutModel = new PathOutModel();
		int waypointsCount = waypoints.size();
		if(waypointsCount <= MAX_WAY_POINTS) {
			Waypoint []waypointslist = null;
			waypointslist = new Waypoint[waypointsCount];
			int i = 0;
			for (LatLng latLng : waypoints) {				
				waypointslist[i++] = new Waypoint(latLng);
			}
			DirectionsResult result;
			try {
				result = DirectionsApi.getDirections(context, origin, destination)
						.waypoints(waypointslist)
						.await();
			
				if(result != null && result.routes != null && result.routes.length>0 && result.routes[0] != null) {
					if(null != result.routes[0].overviewPolyline)
						pathOutModel.setEncodedPolyline(result.routes[0].overviewPolyline);
					double distanceforEach = 0.00;
					for(int j=0; j< result.routes[0].legs.length; j++)
					{
						distanceforEach = distanceforEach + result.routes[0].legs[j].distance.inMeters;
					}
					pathOutModel.setDistance(distanceforEach/(1000*1.0));
				}
				
			}
			catch (ApiException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return pathOutModel;

	}
	
	private PathOutModel getAllConsolidatedPolyLine(List<LatLng> waypoints, LatLng origin, LatLng destination) {
		
		int totalWayPoints = waypoints.size();
		PathOutModel pathOutModel1 = new PathOutModel();
		if(totalWayPoints <= MAX_WAY_POINTS)
		{
			pathOutModel1 = getConsolidatedPolyLine(waypoints, origin.toString(), destination.toString());
		}
		else
		{
			double distance = 0;
			
			
			List<PerRequestPathModel> source1 = new ArrayList<PerRequestPathModel>();
			
			EncodedPolyline encodedPolyline = new EncodedPolyline();
			
			int i = 0;
			PerRequestPathModel perRequestPathModel = new PerRequestPathModel();
			for (LatLng latLng : waypoints) {
				
				if(i == 0) {
					perRequestPathModel.setOrigin(origin);
				}
				
				perRequestPathModel.addWaypoints(latLng);			
				
				if(i == totalWayPoints-1) {
					perRequestPathModel.setDestination(destination);
				}
				else if((i+1)%MAX_WAY_POINTS == 0) {
					//perRequestPathModel.setOrigin(latLng);
					perRequestPathModel.setDestination(latLng);
				}			
				
				if(i == totalWayPoints-1 || (i+1)%MAX_WAY_POINTS == 0)
				{
					LatLng storeLoc = perRequestPathModel.getDestination();
					source1.add(perRequestPathModel);
					if(i != totalWayPoints-1)
					{
						perRequestPathModel = new PerRequestPathModel();
						perRequestPathModel.setOrigin(storeLoc);
					}
				}
				i++;
			}
			
			for (PerRequestPathModel perRequestPathModel2 : source1) {
				PathOutModel pathOutModel = getConsolidatedPolyLine(perRequestPathModel2.getWaypoints(), perRequestPathModel2.getOrigin().toString(), perRequestPathModel2.getDestination().toString());
				encodedPolyline.add(pathOutModel.getEncodedPolyline().decodePath());
				distance += pathOutModel.getDistance();
			}
			pathOutModel1.setEncodedPolyline(encodedPolyline);
			pathOutModel1.setDistance(distance);
		}
		return pathOutModel1;
	}
	
	
	@RequestMapping(value = "/aes-encrypt", method = RequestMethod.GET)
	public String getAesEncrypt(@RequestBody AESDetails aesDetails)  {
    	try {
			return Encrypt.encrypt(aesDetails.getClearpass(), aesDetails.getSecretKey(), aesDetails.getSalt());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
