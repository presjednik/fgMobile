package hr.fg.mobile.tasks;

import hr.fg.mobile.CommentActivity;
import hr.fg.mobile.PostsActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h2>EditPostAsyncTask</h2> AsyncTask for editing a post
 * 
 * @author Fenster Gang
 * 
 */
public class EditPostAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * Used to define the Activity
	 */
	private Activity main;

	/**
	 * Used to define if the LoginActivity or MainActivity is used
	 */
	private Boolean postOrComment;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the Dialog which shows when editing a post
	 */
	private Dialog dialog;

	/**
	 * Used to define the TextView which holds the text of the post
	 */
	private TextView editTextView;

	/**
	 * <h2>EditAsyncTask</h2> The constructor for the class EditPostAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param a
	 *            The Activity to set
	 */
	public EditPostAsyncTask(Context c, Activity a) {
		this.context = c;
		this.main = a;
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
				"http://fenstergang.com/posts/android_post_edit", pairs);
		postOrComment = (Boolean) params[2];
		editTextView = (TextView) params[3];
		dialog = (Dialog) params[4];
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

			String text = data.optString("edit");
			editTextView.setText(text);

			if (postOrComment) {
				((PostsActivity) main).setEditContentText(editTextView);
			} else
				((CommentActivity) main).setEditContentText(editTextView);

			dialog.dismiss();

		} else {
			Toast.makeText(context, "Došlo je do pogreške, pokušaj ponovo..",
					Toast.LENGTH_LONG).show();

		}
	}

}
