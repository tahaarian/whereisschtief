package org.schtief.whereisschtief;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONWriter;
import org.schtief.flickr.Photo;
import org.schtief.twitter.Tweet;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Location {
	
	@NotPersistent
	protected static SimpleDateFormat df	=	new SimpleDateFormat("dd MMM yyyy HH:mm");
 	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private Long time;
	
	@Persistent
	private String latitude;

	@NotPersistent
	private Double dLatitude;
	
	@Persistent
	private String longitude;

	@NotPersistent
	private Double dLongitude;

	@Persistent
	private Integer accuracy;
	
	@NotPersistent
	protected String type;
	
	@Persistent
	private Photo photo;

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	@NotPersistent
	protected List<Tweet> tweets;

	@Persistent
	private String user;
	
	public Location( long time, String latitude, String longitude, int accuracy,String user) {
		super();
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy	=	accuracy;
		this.user=user;
		this.photo=null;
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

	public Integer getAccuracy() {
		return accuracy;
	}


	@Override
	public String toString() {
		return "User: "+user+" Id: "+getId()+", Time: "+getTime()+", Lat: "+latitude+", Long: "+longitude+", Accuracy: "+accuracy;
	}
	public String toCSV() {
		return getId()+","+getTime()+","+latitude+","+longitude+","+user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void addTweet(Tweet tweet) {
		if(null==tweets)
			tweets=new ArrayList<Tweet>();
		tweets.add(tweet);
		setType("actual");
	}	
	
	public void toJSON(JSONWriter writer) throws JSONException {
		if(null!=photo)
			setType("actual");
		
		writer.key("type");
		if(null==type)
			writer.value("location");
		else
			writer.value(type);

		writer.key("info");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.HOUR, 2);
		writer.value(df.format(cal.getTime()));

		writer.key("tweets");
		writer.array();
		if(null!=tweets)
		{
			for (Tweet tweet : tweets) {
				writer.object();
				tweet.toJSON(writer);
				writer.endObject();
			}
		}
		writer.endArray();
		
		if(null!=photo)
		{
			writer.key("flickr");
			writer.object();
			photo.toJSON(writer);
			writer.endObject();
		}
		
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
