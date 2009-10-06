package org.schtief.twitter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;

public class TweetManager 
{

	@SuppressWarnings("unchecked")
	public static List<Tweet> getTweets(PersistenceManager pm, String user, Calendar fromDate, Calendar toDate)
	{
		StringBuffer jdoql	=	new StringBuffer("SELECT FROM ");
		jdoql.append(Tweet.class.getName());
		if(null!=fromDate || null!= toDate || null!=user)
		{			
			jdoql.append(" WHERE");
		}
		if(null!=fromDate || null!= toDate)
		{			
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
		if(null==user || user.length()==0)
		{
//			jdoql.append(" && user=='schtief' ");
		}
		else
		{
			if(null!=fromDate || null!= toDate)
				jdoql.append(" && ");
			jdoql.append(" user=='");			
			jdoql.append(user);			
			jdoql.append("' ");			
		}
		jdoql.append(" ORDER BY time ASC");

//		String jdoql = "SELECT FROM " + Location.class.getName()+" WHERE time >= "+fromDate.getTimeInMillis()+" && time <= "+toDate.getTimeInMillis()+" ORDER BY time DESC";
		List<Tweet> result = (List<Tweet>) pm.newQuery(jdoql.toString()).execute();
		System.out.println("getTweets #"+result.size()+" jdoql : "+jdoql);
		return result;
	}	
}
