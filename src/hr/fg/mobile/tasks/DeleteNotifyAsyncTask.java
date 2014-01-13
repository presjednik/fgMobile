package hr.fg.mobile.tasks;

import hr.fg.mobile.NotificationsActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <h2>DeleteNotifyAsyncTask</h2> AsyncTask for deleting notification
 * 
 * @author Fenster Gang
 * 
 */
public class DeleteNotifyAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the LinearLayout which holds the notifications
	 */
	private LinearLayout notification;

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define the id of the post
	 */
	private String postid;

	/**
	 * Used to define NotificationsActivity from which we call some methods
	 */
	private NotificationsActivity main;

	/**
	 * Used to define if we have notifications or not
	 */
	private Boolean redirect;

	/**
	 * Used to define the id of the notification
	 */
	private String nid;

	/**
	 * Used to define the Button for deleting all notification
	 */
	private Button delAll;

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * <h3>DeleteNotifyAsyncTask</h3> The constructor for the class
	 * DeleteNotifyAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param main
	 *            The NotificationsActivity to set
	 * @param request
	 *            JsonRequestLoader to set
	 * @param pref
	 *            Preference to set
	 */
	public DeleteNotifyAsyncTask(Context c, NotificationsActivity main, FgPrefs pref) {
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
		notification = (LinearLayout) params[0];
		nid = (String) params[1];
		postid = (String) params[2];
		redirect = (Boolean) params[3];
		delAll = (Button) params[4];
		request = (JsonRequestLoader) params[5];
		boolean result;
		if (!nid.equals("0")) {
			result = request
					.getRequestStatus("http://fenstergang.com/notifications/android_notify_delete?key="
							+ pref.getPrefs("sessionKey", c)
							+ "&userId="
							+ pref.getPrefs("userId", c) + "&nid=" + nid);
		} else {
			result = request
					.getRequestStatus("http://fenstergang.com/notifications/android_notify_deleteAll?key="
							+ pref.getPrefs("sessionKey", c)
							+ "&userId="
							+ pref.getPrefs("userId", c));
		}

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
			if (notification != null) {
				notification.setVisibility(View.GONE);
			}
			main.notifyNum -= 1;
			if (redirect) {
				main.intentComment(postid);
			} else if (nid.equals("0")) {
		//dodan finish()
				main.finish();
				main.buksa(new View(c));
			} else if (main.notifyNum == 1) {
				if (delAll != null) {
					delAll.setVisibility(View.GONE);
				}
			} else if (main.notifyNum == 0) {
				//dodan finish()
				main.finish();
				main.buksa(new View(c));
			}
		} else {
			Toast.makeText(c, "Do�lo je do pogre�ke, poku�aj ponovo..",
					Toast.LENGTH_SHORT).show();
		}

	}
}
