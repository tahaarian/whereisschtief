package org.schtief.whereisschtief;

import java.util.List;

import javax.jdo.PersistenceManager;

public class UserManager {

	@SuppressWarnings("unchecked")
	public static User getUser(String name, String jsonurl) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String jdoql = "select from " + User.class.getName()
					+ " where name == '" + name + "' && jsonUrl == '" + jsonurl
					+ "'";
			List<User> users = (List<User>) pm.newQuery(jdoql).execute();
			if (null == users || users.size() == 0)
				return null;
			return users.get(0);
		} finally {
			pm.close();
		}
	}

	public static void addUser(String name, String jsonurl) {
		User user = new User(name, jsonurl);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(user);
		} finally {
			pm.close();
		}

	}

	@SuppressWarnings("unchecked")
	public static List<User> listUsers(PersistenceManager pm) {
		String jdoql = "select from " + User.class.getName();
		return (List<User>) pm.newQuery(jdoql).execute();
	}

}
