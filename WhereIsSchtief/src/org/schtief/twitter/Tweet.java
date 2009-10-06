package org.schtief.twitter;

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
import org.schtief.twitter.Twitter.Status;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Tweet {
	@NotPersistent
	protected static SimpleDateFormat df	=	new SimpleDateFormat("HH:mm");
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private Long twitterId;
	
	@Persistent
	private Long time;
	
	@Persistent
	private String user;

	@Persistent
	private String text;


	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Tweet(Long id, String text, Long time, String user) {
		super();
		this.twitterId=id;
		this.text = text;
		this.time = time;
		this.user = user;
	}

	public Tweet(Status status) {
		this.twitterId = status.id;
		this.user=status.user.screenName;
		this.time=status.getCreatedAt().getTime();
		this.text=status.text;
	}

	public Long getId() {
		return id;
	}

	public Long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "User: "+user+" Id: "+getId()+", Time: "+getTime()+", text: "+text;
	}
	public String toCSV() {
		return getId()+","+getTime()+","+getUser()+","+text;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void toJSON(JSONWriter writer) throws JSONException {
		writer.key("id");
		writer.value(twitterId);
		writer.key("user");
		writer.value(user);
		writer.key("time");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.HOUR, 2);
		writer.value(df.format(cal.getTime()));
		writer.key("text");
		writer.value(text);
	}
}
