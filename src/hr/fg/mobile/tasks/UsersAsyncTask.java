package hr.fg.mobile.tasks;

import hr.fg.mobile.UsersActivity;
import hr.fg.mobile.models.UserModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <h2>UsersAsyncTask</h2> AsyncTask for showing all users
 * 
 * @author Fenster Gang
 * 
 */
public class UsersAsyncTask extends AsyncTask<Object, Void, Boolean> {

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
	private Context c;

	/**
	 * Used to define the UsersActivity from which we use some methods
	 */
	private UsersActivity main;

	/**
	 * Used to define the LinearLayout which holds all the users
	 */
	private LinearLayout l;

	/**
	 * Used to define the View for the users
	 */
	private View usersView;

	/**
	 * <h2>UsersAsyncTask</h2> The constructor for the class UsersAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 * @param main
	 *            The UsersActivity to set
	 */
	public UsersAsyncTask(Context c, FgPrefs pref, UsersActivity main) {
		this.c = c;
		this.pref = pref;
		this.main = main;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(Object... params) {
		request = (JsonRequestLoader) params[0];
		l = (LinearLayout) params[1];
		usersView = (View) params[2];

		Boolean result = request
				.getRequestStatus("http://fenstergang.com/users/android_users?key="
						+ pref.getPrefs("sessionKey", c)
						+ "&userId="
						+ pref.getPrefs("userId", c));

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
				int usersLen = data.getJSONArray(0).length();

				for (int i = 0; i < usersLen; i++) {

					UserModel userView = main.createUserModel(data, i);
					LinearLayout userLayout = main.getUserView(userView);

					l.addView(userLayout);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			main.setLayout(usersView);

		} else {
			Toast.makeText(c, "Došlo je do pogreške, pokušaj ponovo..",
					Toast.LENGTH_SHORT).show();
			main.buksa(new View(c));
		}

	}

}
