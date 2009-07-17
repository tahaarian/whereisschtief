package org.schtief.whereisschtief;

import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;

public class LocationManager 
{

	@SuppressWarnings("unchecked")
	public static List<Location> getLocations(PersistenceManager pm, String name, Calendar date)
	{
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		long startTimeStamp	=	date.getTimeInMillis();
		
		date.set(Calendar.HOUR, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);
		long endTimeStamp	=	date.getTimeInMillis();
		

		String jdoql = "SELECT FROM " + Location.class.getName()+" WHERE time >= "+startTimeStamp+" && time <= "+endTimeStamp+" ORDER BY time DESC";
		System.out.println("HALLO"+jdoql);
		return (List<Location>) pm.newQuery(jdoql).execute();
	}

}
