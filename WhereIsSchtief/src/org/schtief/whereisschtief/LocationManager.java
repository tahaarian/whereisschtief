package org.schtief.whereisschtief;

import java.util.List;

import javax.jdo.PersistenceManager;

public class LocationManager 
{

	@SuppressWarnings("unchecked")
	public static List<Location> getLocations(PersistenceManager pm, String name)
	{
		String jdoql = "SELECT FROM " + Location.class.getName()+" ORDER BY time DESC";
		return (List<Location>) pm.newQuery(jdoql).execute();
	}

}
