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

	@Persistent
	private String longitude;

	public Location( Long time, String latitude, String longitude) {
		super();
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
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

	@Override
	public String toString() {
		return "Id: "+getId()+", Time: "+getTime()+", Lat: "+latitude+", Long: "+longitude;
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
		writer.value(latitude);
		writer.key("longitude");
		writer.value(longitude);
	}

	
}
