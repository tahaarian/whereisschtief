package org.schtief.whereisschtief;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LatitudeJSONParser {
	//TODO fucking logging in google app engine
//	private static Logger LOG = Logger.getLogger(LatitudeJSONParser.class
//			.getName());

	public static Location getPosition(String jsonurl)
			throws LatitudeJSONParserException {
		try {
			URL kml = new URL(jsonurl);
			URLConnection jsonCon = kml.openConnection();
			// read into String
			String json = convertStreamToString(jsonCon.getInputStream());
			log(Level.FINEST, json);

			JSONObject jsono = new JSONObject(json);
//			for (int i = 0; i < JSONObject.getNames(jsono).length; i++) {
//				log(Level.FINEST, JSONObject.getNames(jsono)[i]);
//			}
//			log(Level.FINEST, "NEXT");
			JSONArray features = (JSONArray)jsono.get("features");
			JSONObject feature = features.getJSONObject(0);
//			for (int i = 0; i < JSONObject.getNames(jsono).length; i++) {
//				log(Level.FINEST, JSONObject.getNames(jsono)[i]);
//			}
//			log(Level.FINEST, "NEXT");
			JSONObject geometry = (JSONObject)feature.get("geometry");
//			for (int i = 0; i < JSONObject.getNames(jsono).length; i++) {
//				log(Level.FINEST, JSONObject.getNames(jsono)[i]);
//			}
//			log(Level.FINEST, "NEXT");
			JSONArray coordinates = (JSONArray)geometry.get("coordinates");
			log(Level.FINEST, coordinates.getDouble(0));
			log(Level.FINEST, coordinates.getDouble(1));
			
			JSONObject properties = (JSONObject)feature.get("properties");
			log(Level.FINEST, properties.getLong("timeStamp"));
			log(Level.FINEST, properties.get("accuracyInMeters"));

			Location location = new Location(properties.getLong("timeStamp")*1000,Double.toString(coordinates.getDouble(0)),Double.toString(coordinates.getDouble(1)),properties.getInt("accuracyInMeters"));
			return location;
		} catch (IOException ioe) {
			throw new LatitudeJSONParserException("Could not connect to url "
					+ jsonurl, ioe);
		} catch (JSONException e) {
			throw new LatitudeJSONParserException("Could not parse json ", e);
		}
	}

	private static void log(Level level, Object log) 
	{
		//TODO fucking logging in google app engine
//		System.out.println(log.toString());
	}

	@SuppressWarnings("serial")
	public static class LatitudeJSONParserException extends Exception {

		public LatitudeJSONParserException(String message, Exception ioe) {
			super(message, ioe);
		}

	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
