package hr.fg.mobile.tasks;

import hr.fg.mobile.RegActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * <h2>RegAsyncTask</h2> AsyncTask for registring a new user
 * 
 * @author Fenster Gang
 * 
 */
public class RegAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the Activity from which we use some methods
	 */
	private RegActivity reg;

	/**
	 * Used to define the ProgressDialog which shows when logging in the
	 * application
	 */
	private ProgressDialog d;

	/**
	 * <h3>RegAsyncTask</h3> The constructor for the class RegAsyncTask
	 * 
	 * @param context
	 *            The context to set
	 * @param prefs
	 *            Preference to set
	 */
	public RegAsyncTask(Context context, FgPrefs prefs) {
		c = context;
		pref = prefs;
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
				"http://fenstergang.com/users/android_register", pairs);

		this.reg = (RegActivity) params[2];
		d = (ProgressDialog) params[3];
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		d.dismiss();
		System.out.println(request.postRequestData());
		if (result) {
			JSONObject data = request.postRequestData();
			pref.setPref("sessionKey", data.opt("key").toString(), c);
			pref.setPref("userId", data.opt("userId").toString(), c);
			pref.setPref("password", pairs.get(1).getValue(), c);
			pref.setPref("p_profile", data.optString("p_picture"), c);
			reg.redirectPost();

		} else {
			reg.redirectReg();
			Toast.makeText(reg, "Pogriješio si kod unosa", Toast.LENGTH_LONG)
					.show();
		}

	}

}
