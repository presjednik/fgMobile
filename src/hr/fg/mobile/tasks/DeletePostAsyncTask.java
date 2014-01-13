package hr.fg.mobile.tasks;

import hr.fg.mobile.plugins.JsonRequestLoader;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <h2>DeletePostAsyncTask</h2> AsyncTask for deleting a post
 * 
 * @author Fenster Gang
 * 
 */
public class DeletePostAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the Dialog which shows when deleting a post
	 */
	private Dialog dialog;

	/**
	 * Used to define the LinearLayout which holds the post that needs to be
	 * deleted
	 */
	private LinearLayout deleteLayout;

	/**
	 * <h3>DeletePostAsyncTask</h3> The constructor for the class
	 * DeletePostAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 */
	public DeletePostAsyncTask(Context c) {
		this.context = c;
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
				"http://fenstergang.com/posts/android_post_delete", pairs);
		deleteLayout = (LinearLayout) params[2];
		dialog = (Dialog) params[3];
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

			deleteLayout.setVisibility(View.GONE);

			dialog.dismiss();

		} else {
			Toast.makeText(context, "Došlo je do pogreške, pokušaj ponovo..",
					Toast.LENGTH_LONG).show();

		}
	}

}
