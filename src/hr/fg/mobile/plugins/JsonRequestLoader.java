package hr.fg.mobile.plugins;

import hr.fg.mobile.interfaces.IFgRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h2>JsonRequestLoader</h2> Executes the requests to the web server and
 * implements IFgRequest
 * 
 * @author Fenster Gang
 * 
 */
public class JsonRequestLoader implements IFgRequest {

	/**
	 * Used to define the response from the web server
	 */
	private String responseString;

	public boolean postRequestStatus(String Url, List<NameValuePair> pairs) {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);

		HttpClient httpClient = new DefaultHttpClient(params);
		httpClient.getParams().setParameter("http.protocol.content-charset",
				HTTP.UTF_8);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost post = new HttpPost(Url);

		try {
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(pairs,
					"UTF-8");
			post.setEntity(urlEntity);
			responseString = httpClient.execute(post, responseHandler);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			JSONObject json;
			json = new JSONObject(responseString);
			JSONArray response = json.getJSONArray("response");
			String status = response.getJSONObject(0).optString("status");
			if (status.compareTo("OK") == 0) {
				return true;
			} else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

	}

	public JSONObject postRequestData() {
		JSONObject data = null;
		try {
			JSONObject json;
			json = new JSONObject(responseString);
			JSONArray response = json.getJSONArray("response");
			data = response.getJSONObject(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		;
		return data;
	}

	public boolean getRequestStatus(String Url) {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
		HttpClient httpClient = new DefaultHttpClient(params);
		httpClient.getParams().setParameter("http.protocol.content-charset",
				HTTP.UTF_8);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpGet get = new HttpGet(Url);

		try {
			responseString = httpClient.execute(get, responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			JSONObject json;
			json = new JSONObject(responseString);
			JSONArray response = json.getJSONArray("response");

			String status = response.getJSONObject(0).optString("status");
			if (status.compareTo("OK") == 0) {
				return true;
			} else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public JSONArray getRequestData() {
		JSONArray data = null;
		try {
			JSONObject json;
			json = new JSONObject(responseString);
			JSONArray response = json.getJSONArray("response");
			data = response.getJSONArray(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		;
		return data;
	}
}