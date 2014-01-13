package hr.fg.mobile.tasks;

import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

/**
 * <h2>VotersAsyncTask</h2> AsyncTask for displaying who has voted
 * 
 * @author Fenster Gang
 * 
 */
public class VotersAsyncTask extends AsyncTask<Object, Void, Boolean> {

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
	 * Used to define the dialog which shows when displaying who has voted
	 */
	private ProgressDialog dialog;

	/**
	 * Used to define the button which displays who has voted
	 */
	private Button votes;

	/**
	 * Used to define the post ID
	 */
	private Integer postId;

	/**
	 * <h3>VotersAsyncTask</h3> The constructor for the class VotersAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 */
	public VotersAsyncTask(Context c, FgPrefs pref) {
		this.context = c;
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

		this.dialog = (ProgressDialog) params[1];
		this.votes = (Button) params[2];
		this.postId = (Integer) params[3];
		Boolean result = request
				.getRequestStatus("http://fenstergang.com/votes/android_voters?key="
						+ pref.getPrefs("sessionKey", context)
						+ "&userId="
						+ pref.getPrefs("userId", context)
						+ "&postId="
						+ this.postId);
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
			dialog.cancel();
			try {
				JSONArray array = request.getRequestData();
				String userVoted = new String();

				System.out.println(array.length());

				if (array.length() != 0) {

					for (int i = 0; i < array.length(); i++) {
						JSONObject user = array.getJSONObject(i).optJSONObject(
								"User");

						if (i == 0) {
							userVoted = userVoted.concat(user
									.optString("username"));
						} else {
							userVoted = userVoted.concat(", ");
							userVoted = userVoted.concat(user
									.optString("username"));
						}
					}
					Toast.makeText(context, userVoted, Toast.LENGTH_LONG)
							.show();

				} else {
					Toast.makeText(context, "Nitko nije hraènuo..",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(context, "Došlo je do pogreške, pokušaj ponovo..", Toast.LENGTH_SHORT).show();
		}
		votes.setEnabled(true);
	}

}
