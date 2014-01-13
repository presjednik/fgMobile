package hr.fg.mobile.tasks;

import hr.fg.mobile.NotificationsActivity;
import hr.fg.mobile.models.NotifyModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <h2>NotifyAsyncTask</h2> AsyncTask for showing all notification
 * 
 * @author Fenster Gang
 * 
 */
public class NotifyAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define the NotificationsActivity from which we use some methods
	 */
	private NotificationsActivity main;

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define the LinearLayout for the notifications
	 */
	private LinearLayout l;

	/**
	 * Used to define the Button for deleting all notification
	 */
	private Button delAll;

	/**
	 * Used to define the View for the notifications
	 */
	private View notifyView;

	/**
	 * <h3>NotifyAsyncTask</h3> The constructor for the class NotifyAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 */
	public NotifyAsyncTask(Context c, FgPrefs pref) {
		this.c = c;
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
		main = (NotificationsActivity) params[1];
		l = (LinearLayout) params[2];
		delAll = (Button) params[3];
		notifyView = (View) params[4];

		Boolean result = request
				.getRequestStatus("http://fenstergang.com/notifications/android_notify?key="
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
			int len = data.length();
			main.notifyNum = len;
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					try {
						NotifyModel notification = main.createNotifyModel(data,
								i);
						LinearLayout notifyView = main
								.getNotifyView(notification);
						l.addView(notifyView);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (len > 1)
					l.addView(delAll);
				main.setLayout(notifyView);
			} else {
				main.finish();
				Toast.makeText(c, "Nema novih poruka..", Toast.LENGTH_SHORT)
						.show();
				main.buksa(new View(c));
			}

		} else {
			//dodan finish()
			main.finish();
			main.buksa(new View(c));
		}
	}

}
