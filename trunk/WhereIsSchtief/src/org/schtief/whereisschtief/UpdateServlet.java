package org.schtief.whereisschtief;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.schtief.whereisschtief.LatitudeJSONParser.LatitudeJSONParserException;

@SuppressWarnings("serial")
public class UpdateServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("UpdateServlet");

		//loop over all users
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
		List<User> users	=	UserManager.listUsers(pm);
		for (User user : users) 
		{
			//get location for each user
    		
            try {//http://www.google.com/latitude/apps/badge/api?user=-2071822615655660005&type=json
    			Location location	=	LatitudeJSONParser.getPosition(/*"http://www.google.com/latitude/apps/badge/api?user=-2071822615655660005&type=json"*/user.getJsonUrl());
        		location.setUser(user.getName());
    			//check if there is a location change
        		String jdoql	=	"select from "+Location.class.getName()+" where user=='"+user.getName()+"' order by time desc range 0,1";
        		List<Location> lastLocations = (List<Location>) pm.newQuery(jdoql).execute();
        		if(null!=lastLocations && lastLocations.size()==1)
        		{
        			resp.getWriter().println("LastLocation " +lastLocations.get(0).toString());
        			resp.getWriter().println("Location " +location.toString());
        			if(! lastLocations.get(0).getLatitude().equals(location.getLatitude()) || 
        				! lastLocations.get(0).getLongitude().equals(location.getLongitude()) ||
        				(lastLocations.get(0).getAccuracy()==null || 
        						lastLocations.get(0).getAccuracy()<location.getAccuracy()))
        			{
                    	pm.makePersistent(location);
                    	resp.getWriter().println("added");
                    	System.out.println("Update added :"+location.toString());
        			}
        			else
        			{
                    	resp.getWriter().println("ignored");
//                    	System.out.println("Update ignored :"+location.toString());
        			}
        		}
        		else
        		{
        			pm.makePersistent(location);
                	resp.getWriter().println("first");
        		}
        		

            } catch (LatitudeJSONParserException e) {
            	e.printStackTrace();
			}
		}    
		}finally {
             pm.close();
         }
	}
}
