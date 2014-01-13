package hr.fg.mobile.tasks;

import hr.fg.mobile.ProfileActivity;
import hr.fg.mobile.models.UserModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * <h2>ProfileAsyncTask</h2> AsyncTask for showing the profile of a user
 * 
 * @author Fenster Gang
 * 
 */
public class ProfileAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the View of the profile
	 */
	private View profileLayout;

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define the ProfileActivity from which we use some methods
	 */
	private ProfileActivity main;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define the id of the user
	 */
	private Integer user;

	/**
	 * <h3>ProfileAsyncTask</h3> The constructor for the class ProfileAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param main
	 *            ProfileActivity to set
	 * @param pref
	 *            Preference to set
	 */
	public ProfileAsyncTask(Context c, ProfileActivity main, FgPrefs pref) {
		this.c = c;
		this.main = main;
		this.pref = pref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(Object... params) {
		request = (JsonRequestLoader) params[0];
		profileLayout = (View) params[1];
		user = (Integer) params[2];

		Boolean result = request
				.getRequestStatus("http://fenstergang.com/users/android_user?key="
						+ pref.getPrefs("sessionKey", c)
						+ "&userId="
						+ pref.getPrefs("userId", c) + "&userp=" + user);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (result) {
			JSONArray data = request.getRequestData();

			try {
				UserModel user = main.createUserModel(data);
				profileLayout = main.getProfileView(user);
				main.setLayout(profileLayout);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(c, "Došlo je do pogreške, pokušaj ponovo..",
					Toast.LENGTH_LONG).show();
			main.banda(new View(c));
		}
	}

}
