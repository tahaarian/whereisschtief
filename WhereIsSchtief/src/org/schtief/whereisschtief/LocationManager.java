package org.schtief.whereisschtief;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

public class LocationManager 
{

	@SuppressWarnings("unchecked")
	public static List<Location> getLocations(PersistenceManager pm, String name)
	{
		//timestamps by today
		Calendar today= Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		long startTimeStamp	=	today.getTimeInMillis();
		
		today.set(Calendar.HOUR, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);
		long endTimeStamp	=	today.getTimeInMillis();
		
		//TODO schtief german timezone
		String jdoql = "SELECT FROM " + Location.class.getName()+" WHERE time >= "+startTimeStamp+" && time <= "+endTimeStamp+" ORDER BY time DESC";
		System.out.println("HALLO"+jdoql);
		return (List<Location>) pm.newQuery(jdoql).execute();
	}

}
