package org.schtief.whereisschtief;

import java.util.Calendar;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONWriter;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Location {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private Long time;
	
	@Persistent
	private String latitude;
	
	private Double dLatitude;
	
	@Persistent
	private String longitude;

	private Double dLongitude;

	public Location( Long time, String latitude, String longitude) {
		super();
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	protected Location() {
	}

	public Long getId() {
		return id;
	}

	public Long getTime() {
		return time;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	public Double getDoubleLongitude() {
		if(null==dLongitude)
		{
			try{
				dLongitude = Double.parseDouble(longitude);
			}catch(NumberFormatException nfe){};
		}
		return dLongitude;
	}

	public Double getDoubleLatitude() {
		if(null==dLatitude)
		{
			try{
				dLatitude = Double.parseDouble(latitude);
			}catch(NumberFormatException nfe){};
		}
		return dLatitude;
	}


	@Override
	public String toString() {
		return "Id: "+getId()+", Time: "+getTime()+", Lat: "+latitude+", Long: "+longitude;
	}
	public String toCSV() {
		return getId()+","+getTime()+","+latitude+","+longitude;
	}

	@SuppressWarnings("deprecation")
	public void toJSON(JSONWriter writer) throws JSONException {
		writer.key("id");
		writer.value(id);
		writer.key("date");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.HOUR, 2);
		writer.value(cal.getTime().toGMTString());
		
		writer.key("latitude");
		if(null!=latitude)
			writer.value(latitude);
		else
			writer.value(dLatitude.toString());
			
		writer.key("longitude");
		if(null!=longitude)
			writer.value(longitude);
		else
			writer.value(dLatitude.toString());
	}
	public Double distance(Location location)
	{
		if(null==getDoubleLatitude() || null == getDoubleLongitude() || 
				null == location.getDoubleLatitude() || null== location.getDoubleLongitude())
			return null;
		
		return distance(getDoubleLatitude(), getDoubleLongitude(), 
				location.getDoubleLatitude(), location.getDoubleLongitude())*1000;
	}

	protected double distance(double lat1, double lon1, double lat2, double lon2) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  //if (unit == "K") {
		    dist = dist * 1.609344;
//		  } else if (unit == "N") {
//		  	dist = dist * 0.8684;
//		    }
		  return (dist);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts decimal degrees to radians             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts radians to decimal degrees             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double rad2deg(double rad) {
		  return (rad * 180.0 / Math.PI);
		}	
}
