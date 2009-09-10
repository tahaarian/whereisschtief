package org.schtief.whereisschtief;

import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;

public class LocationManager 
{

	@SuppressWarnings("unchecked")
	public static List<Location> getLocations(PersistenceManager pm, String name, Calendar fromDate, Calendar toDate)
	{
		StringBuffer jdoql	=	new StringBuffer("SELECT FROM ");
		jdoql.append(Location.class.getName());
		if(null!=fromDate || null!= toDate)
		{			
			jdoql.append(" WHERE");
			if(null!=fromDate)
			{
				jdoql.append(" time >= ");
				jdoql.append(fromDate.getTimeInMillis());
			}
			if(null!=toDate)
			{
				jdoql.append(" && time <= ");
				jdoql.append(toDate.getTimeInMillis());
			}
		}
		//TODO name
		jdoql.append(" ORDER BY time DESC");

//		String jdoql = "SELECT FROM " + Location.class.getName()+" WHERE time >= "+startTimeStamp+" && time <= "+endTimeStamp+" ORDER BY time DESC";
		System.out.println("HALLO"+jdoql);
		return (List<Location>) pm.newQuery(jdoql).execute();
	}

}
