package org.schtief.whereisschtief;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String name;
	
	@Persistent
	private String jsonUrl;


	public User( String name, String url) {
		super();
		this.name = name;
		this.jsonUrl = url;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getJsonUrl() {
		return jsonUrl;
	}

	@Override
	public String toString() {
		return "Id: "+getId()+", Name: "+getName()+", URL: "+jsonUrl;
	}

	
}
