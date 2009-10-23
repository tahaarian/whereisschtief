/*
 * (c) 2004, Novacom GmbH. All rights reserved.
 * 
 *  created on 07.10.2009
 */
package org.schtief.flickr;

public class FlickrTest
{

	/**
	 * TODO: add javadoc-comment for method
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		Flickr flickr	=	Flickr.get();
		Flickr.User user = flickr.findByUserName(".schtieF");
		System.out.println(user.getId());
		Flickr.PhotoList photos	=	flickr.getPublicPhotos(user, 10, 1);
		for (int i = 0; i < photos.getCount(); i++)
		{
			Flickr.Photo p	=photos.get(i);
			System.out.println(photos.get(i).getId()+":"+photos.get(i).getTitle()+" "+photos.get(i).getDateTaken()+" "+photos.get(i).getDateUpload());
			System.out.println("["+p.getLatitude()+":"+p.getLongitude()+"]");
		}
	}

}
