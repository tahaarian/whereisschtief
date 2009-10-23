package org.schtief.flickr;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.schtief.whereisschtief.Location;
import org.schtief.whereisschtief.PMF;
import org.schtief.whereisschtief.User;
import org.schtief.whereisschtief.UserManager;

@SuppressWarnings("serial")
public class FlickrUpdateServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("FlickrUpdateServlet");
		// loop over all users
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try 
		{
			List<User> users = UserManager.listUsers(pm);
			
			String jdoql	=	"select from "+Photo.class.getName()+" order by timeUpload desc range 0,1";
    		List<Photo> lastPhoto = (List<Photo>) pm.newQuery(jdoql).execute();
			long lastFlickr = 0;
    		if(null!=lastPhoto && lastPhoto.size()==1)
    		{
    			lastFlickr = lastPhoto.get(0).getTimeUpload();
    		}
				
			Flickr flickr = Flickr.get();
//			Calendar cal=Calendar.getInstance();
//			cal.setTimeInMillis(lastFlickr);
			
			for (User user : users) 
			{
				if(null==user.getFlickrName())
					continue;
				
				Flickr.User fuser = flickr.findByUserName(user.getFlickrName());
//				if(!flickr.hasUpdates(fuser, cal))
//				{
//					System.out.println("no updates for "+user.getFlickrName());
//					continue;
//				}
				System.out.println("updating "+user.getFlickrName());
				resp.getWriter().println("updating "+user.getFlickrName());
				Flickr.PhotoList photos	=	flickr.getPublicPhotos(fuser, 10, 1);
				for (int i = 0; i < photos.getCount(); i++)
				{
					Flickr.Photo fp	=photos.get(i);
					if(fp.getDateUpload()<=lastFlickr){
						System.out.println("ignored "+fp.getId()+" "+fp.getDateUpload()+"<"+lastFlickr);
						continue;
					}
					//TODO later auch bilder ohne location, dann zuordnung
					if(null==fp.getLatitude() || fp.getLatitude().length()<=1)
					{
						System.out.println("ignored no location");
						continue;
					}

					Photo p=	new Photo(user.getFlickrName(),fp);
//					pm.makePersistent(p);
					
					Location location = new Location(fp.getDateTaken(),fp.getLongitude(),fp.getLatitude(),-1,user.getName());
					location.setPhoto(p);
					pm.makePersistent(location);
					resp.getWriter().println("added" + p.toString());
					System.out.println("Flickr added :" + p.toString());
				}
			}
		} finally {
			pm.close();
		}

	}
}
