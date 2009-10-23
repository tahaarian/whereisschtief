package org.schtief.flickr;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Photo {
	@NotPersistent
	protected static SimpleDateFormat df	=	new SimpleDateFormat("HH:mm");

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
	private String flickrId;
	
	@Persistent
	private Long timeTaken;

	@Persistent
	private Long timeUpload;

	public String getFlickrId() {
		return flickrId;
	}

	public Long getTimeTaken() {
		return timeTaken;
	}

	public Long getTimeUpload() {
		return timeUpload;
	}

	@Persistent
	private String user;

	@Persistent
	private String title;
	
	@Persistent
	private String thumbnail;
	

	public String getThumbnail() {
		return thumbnail;
	}

	public Photo(String user, Flickr.Photo photo) {
		this.flickrId = photo.getId();
		this.user=user;
		this.timeTaken=photo.getDateTaken();
		this.timeUpload=photo.getDateUpload();
		this.title=photo.getTitle();
		this.thumbnail=photo.getThumbnail();
	}



	@Override
	public String toString() {
		return "User: "+user+" Id: "+flickrId+", TimeTaken: "+timeTaken+", TimeUpload: "+timeUpload+", title: "+title;
	}
	public String toCSV() {
		return flickrId+","+user+","+timeTaken+","+timeUpload+","+title;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void toJSON(JSONWriter writer) throws JSONException {
		writer.key("id");
		writer.value(flickrId);
//		writer.key("user");
//		writer.value(user);
		writer.key("timeTaken");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(timeTaken);
		cal.add(Calendar.HOUR, 2);
		writer.value(df.format(cal.getTime()));
//		
//		writer.key("timeUpload");
//		cal=Calendar.getInstance();
//		cal.setTimeInMillis(timeUpload);
//		cal.add(Calendar.HOUR, 2);
//		writer.value(df.format(cal.getTime()));
//		
		writer.key("title");
		writer.value(title);
		writer.key("thumbnail");
		writer.value(thumbnail);
	}
}
