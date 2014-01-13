package hr.fg.mobile.interfaces;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <h2>IFgRequest</h2> Interface for requests to server
 * 
 * @author Fenster Gang
 * 
 */
public interface IFgRequest {

	/**
	 * <h3>postRequestStatus</h3> Sends the POST request to web server and returns
	 * if the request was succeeded or not
	 * 
	 * @param Url
	 *            The URL to which the request is send
	 * @param pairs
	 *            Pairs which are send with the request
	 * @return Was the request succeeded or not
	 */
	public boolean postRequestStatus(String Url, List<NameValuePair> pairs);

	/**
	 * <h3>postRequestData</h3> Uses the JSONArray which is get by the
	 * postReqestStatus method and parses it to JSONObject
	 * 
	 * @return The JSONObject get with parsing the JSONArray
	 */
	public JSONObject postRequestData();

	/**
	 * <h3>getRequestStatus</h3> Sends the GET request to web server and returns if
	 * the request was succeeded or not
	 * 
	 * @param url
	 *            The URL to which the request is send
	 * @return Was the request succeeded or not
	 */
	public boolean getRequestStatus(String url);

	/**
	 * <h3>getRequestData</h3> Uses the JSONArray which is get by the
	 * getReqestStatus method and parses it to JSONObject
	 * 
	 * @return The JSONObject get with parsing the JSONArray
	 */
	public JSONArray getRequestData();
}
