package org.schtief.twitter;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.schtief.twitter.Twitter.Status;
import org.schtief.whereisschtief.LatitudeJSONParser;
import org.schtief.whereisschtief.Location;
import org.schtief.whereisschtief.PMF;
import org.schtief.whereisschtief.User;
import org.schtief.whereisschtief.UserManager;
import org.schtief.whereisschtief.LatitudeJSONParser.LatitudeJSONParserException;

@SuppressWarnings("serial")
public class TwitterTestServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String user	=	req.getParameter("user");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Calendar cal1 =	Calendar.getInstance();
		cal1.add(Calendar.HOUR_OF_DAY, -2);
		List<Tweet> tweets	=	TweetManager.getTweets(pm, user, cal1, Calendar.getInstance());
		for (Tweet tweet : tweets) {
	    	resp.getWriter().println(tweet.toString()+"\n");
	    	System.out.println(tweet.toString()+"\n");
			
		}

	}
}
