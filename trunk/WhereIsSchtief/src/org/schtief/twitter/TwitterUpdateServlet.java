package org.schtief.twitter;

import java.io.IOException;
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
public class TwitterUpdateServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("TwitterUpdateServlet");
		PersistenceManager pm = PMF.get().getPersistenceManager();

		//get last saved tweet id
		String jdoql	=	"select from "+Tweet.class.getName()+" order by time desc range 0,1";
		List<Tweet> result = (List<Tweet>) pm.newQuery(jdoql).execute();
		long lastTweet =0;
		if(null!=result && result.size()==1)
			lastTweet	=	result.get(0).getTime();
		//get Home timeline
		Twitter twitter = new Twitter("whereisschtief","");
		List<Status> tweets	=	twitter.getHomeTimeline();
		try{
			//loop over all tweets
			for (int i=tweets.size()-1;i>=0;i--) {
				Status status = (Status) tweets.get(i);
				//ignore myself
				if("whereisschtief".equals(status.getUser().screenName))
					continue;
				//check if after lastTweet
				if(status.createdAt.getTime()>lastTweet && !status.text.startsWith("@")&& !status.text.startsWith("RT"))
				{
					Tweet tweet	=	new Tweet(status);
					//save it
					pm.makePersistent(tweet);
	            	resp.getWriter().println("added"+tweet.toString());
	            	System.out.println("Tweet added :"+tweet.toString());
				}
				else
				{
	            	resp.getWriter().println("ignored "+status.getId());
//	            	System.out.println("ignored :"+status.getId());
				}
			}
		}finally {
             pm.close();
         }
	}
}
