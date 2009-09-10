package org.schtief.whereisschtief;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;
import org.schtief.whereisschtief.LatitudeJSONParser.LatitudeJSONParserException;

@SuppressWarnings("serial")
public class WhereIsSchtiefServlet extends HttpServlet {

	private static final String PARAMETER_ACTION	=	"action";
	private static final String ACTION_ADD_USER		=	"adduser";
	private static final String ACTION_GET_DATA		=	"getdata";
	private static final String ACTION_GET_CLUSTER	=	"getcluster";
	private static final String ACTION_EXPORT		=	"export";

	private static final String PARAMETER_NAME 		= 	"name";
	private static final String PARAMETER_DATE 		= 	"date";
	private static final String PARAMETER_RADIUS 	= 	"radius";
	private static final String PARAMETER_THRESHOLD	= 	"threshold";
	private static final String PARAMETER_CALLBACK	= 	"callback";
	private static final String PARAMETER_JSON_URL 	= 	"jsonurl";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String action	=	req.getParameter(PARAMETER_ACTION);
		if(null==action)
		{
			error(resp,"Missing parameter "+PARAMETER_ACTION);
			return;
		}
		//select actions
		//add user
		if(ACTION_ADD_USER.equals(action))
		{
			addUser(req,resp);
		}
		//get data
		else if(ACTION_GET_DATA.equals(action))
		{
			getData(req,resp);
		}
		//get cluster
		else if(ACTION_GET_CLUSTER.equals(action))
		{
			getCluster(req,resp);
		}
		//export
		else if(ACTION_EXPORT.equals(action))
		{
			export(req,resp);
		}
		else
			error(resp,"Wrong action!");
	}
	private void getData(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		//optional name
		String name	=	req.getParameter(PARAMETER_NAME);
	
		String dateS	=	req.getParameter(PARAMETER_DATE);

		Calendar startCal=Calendar.getInstance();
		//By Default -1 Day
		startCal.add(Calendar.DATE, -1);
		Calendar endCal=Calendar.getInstance();
		if(null!=dateS)
		{
			try
			{
				//TODO try catch logging
				//parse to calendar
				DateFormat formatter = new SimpleDateFormat("dd.MM.yy");
				Date     date = (Date)formatter.parse(dateS); 
				startCal.setTime(date);
				endCal.setTime(date);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Parse Date error "+dateS,e);
			}
		}
		// 00:00
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		
		//By Default 23:59
		endCal.set(Calendar.HOUR, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);

		
		//callback
		String callback	=	req.getParameter(PARAMETER_CALLBACK);
		PersistenceManager pm = PMF.get().getPersistenceManager();

		List<Location> locations	=	LocationManager.getLocations(pm,name,startCal,endCal);
		//write javascript+json
		resp.setContentType("text/javascript");
		resp.getWriter().append(callback+"(");
		JSONWriter writer = new JSONWriter(resp.getWriter());
		
		try
		{
			writer.object();
			writer.key("locations");
			writer.array();
			for (Location location : locations) {
				writer.object();
				location.toJSON(writer);
				writer.endObject();
			}
			writer.endArray();
			writer.endObject();
			resp.getWriter().append(");");
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			error(resp,"could not write JSON");
		}
		
	}
	private void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		//get params
		String name	=	req.getParameter(PARAMETER_NAME);
		if(null==name)
		{
			error(resp,"Missing parameter "+PARAMETER_NAME);
			return;
		}
		String jsonurl	=	req.getParameter(PARAMETER_JSON_URL);
		if(null==jsonurl)
		{
			error(resp,"Missing parameter "+PARAMETER_JSON_URL);
			return;
		}

		//test if user already existing
		User user	=	UserManager.getUser(name,jsonurl);
		if(null!=user)
		{
			error(resp,"User "+name+" already exists.");
			return;
		}
		//test if json feed is available
		try {
			Location loc	=	LatitudeJSONParser.getPosition(jsonurl);
			//test if too old
			log(Level.FINEST," now: "+System.currentTimeMillis()+" location "+loc.getTime() +" diff: "+(System.currentTimeMillis()/1000-loc.getTime()));
			if((System.currentTimeMillis()/1000-loc.getTime())>1000*60*60*24)
			{
				error(resp,"Please activate Google Latitude, your last location is older than 24h!");
				return;
			}
		} catch (LatitudeJSONParserException e) {
			error(resp,e.getMessage());
			return;
		}
		
		//add user
		UserManager.addUser(name,jsonurl);
		
		//return ok
		return;
	}
	
	private void getCluster(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		//optional name
		String name		=	req.getParameter(PARAMETER_NAME);
		String radius	=	req.getParameter(PARAMETER_RADIUS);
		String threshold=	req.getParameter(PARAMETER_THRESHOLD);
		
		//callback
		String callback	=	req.getParameter(PARAMETER_CALLBACK);
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String dateS	=	req.getParameter(PARAMETER_DATE);

		Calendar startCal=Calendar.getInstance();
		//By Default -1 Day
		startCal.add(Calendar.MONTH, -1);
		Calendar endCal=Calendar.getInstance();
		if(null!=dateS)
		{
			try
			{
				//TODO try catch logging
				//parse to calendar
				DateFormat formatter = new SimpleDateFormat("dd.MM.yy");
				Date     date = (Date)formatter.parse(dateS); 
				startCal.setTime(date);
				endCal.setTime(date);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Parse Date error "+dateS,e);
			}
		}
		// 00:00
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		
		//By Default 23:59
		endCal.set(Calendar.HOUR, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		
		List<Location> locations	=	LocationManager.getLocations(pm,name,startCal,endCal);
		
		//build cluster
		//punkte mit längster dauer suchen
		//write javascript+json
		resp.setContentType("text/javascript");
		resp.getWriter().append(callback+"(");
		JSONWriter writer = new JSONWriter(resp.getWriter());
		
		try
		{
			writer.object();
			writer.key("locations");
			writer.array();
			for (Location location : locations) {
				writer.object();
				location.toJSON(writer);
				writer.endObject();
			}
			writer.endArray();
			writer.endObject();
			resp.getWriter().append(");");
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			error(resp,"could not write JSON");
		}
	}
	
	private void export(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		//optional name
		String name	=	req.getParameter(PARAMETER_NAME);
	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		List<Location> locations	=	LocationManager.getLocations(pm,name,startCal,endCal);
		//write javascript+json
		resp.setContentType("text/javascript");
		resp.getWriter().append(callback+"(");
		JSONWriter writer = new JSONWriter(resp.getWriter());
		
		try
		{
			writer.object();
			writer.key("locations");
			writer.array();
			for (Location location : locations) {
				writer.object();
				location.toJSON(writer);
				writer.endObject();
			}
			writer.endArray();
			writer.endObject();
			resp.getWriter().append(");");
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			error(resp,"could not write JSON");
		}
		
	}
	
	private void error(ServletResponse resp, String message) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println(message);
	}
	
	private static void log(Level level, Object log) 
	{
		//TODO fucking logging in google app engine
		System.out.println(log.toString());
	}
}
