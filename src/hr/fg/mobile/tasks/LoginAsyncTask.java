package hr.fg.mobile.tasks;

import hr.fg.mobile.LoginActivity;
import hr.fg.mobile.MainActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * <h2>LoginAsyncTask</h2> AsyncTask for logging into the application
 * 
 * @author Fenster Gang
 * 
 */
public class LoginAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * Used to define the Activity
	 */
	private Activity main;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define if the LoginActivity or MainActivity is used
	 */
	private boolean redirect;

	/**
	 * <h3>LoginAsyncTask</h3> The constructor for the class LoginAsyncTask
	 * 
	 * @param context
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 * @param redirect
	 *            Type of the activity to use. False for MainActivity, true for
	 *            LoginActivity
	 */
	public LoginAsyncTask(Context context, FgPrefs pref, boolean redirect) {
		this.context = context;
		this.pref = pref;
		this.redirect = redirect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Boolean doInBackground(Object... params) {
		request = (JsonRequestLoader) params[0];
		pairs = (List<NameValuePair>) params[1];
		Boolean result = request.postRequestStatus(
				"http://fenstergang.com/users/android_login", pairs);
		this.main = (Activity) params[2];
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {

			JSONObject data = request.postRequestData();
			pref.setPref("sessionKey", data.opt("key").toString(), context);

			pref.setPref("userId", data.opt("userId").toString(), context);
			pref.setPref("p_profile", data.optString("p_picture"), context);
			
			if (!redirect) {
				((MainActivity) main).intentPosts();
			} else {
				pref.setPref("username", pairs.get(0).getValue(), context);

				pref.setPref("password", pairs.get(1).getValue(), context);
				((LoginActivity) main).intentPosts();
			}
		} else {

			if (!redirect)
				((MainActivity) main).loginRedirect(pref.getPrefs("username", context), pref.getPrefs("password", context));
			else{
				((LoginActivity) main).loginRedirect(pref.getPrefs("username", context), pref.getPrefs("password", context));
			}

			Toast.makeText(main,
					"Krivi ili username ili pass! \nDaj se skuliraj..",
					Toast.LENGTH_LONG).show();

		}
	}
}
