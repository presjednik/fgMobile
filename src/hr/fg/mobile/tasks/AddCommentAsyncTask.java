package hr.fg.mobile.tasks;

import hr.fg.mobile.CommentActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * <h2>AddCommentAsyncTask</h2> AsyncTask for adding a comment to a single post
 * 
 * @author Fenster Gang
 * 
 */
public class AddCommentAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the dialog which shows when adding the comment
	 */
	private ProgressDialog dialog;

	/**
	 * Used to define the CommentActivity from which we use a method
	 */
	private CommentActivity activity;

	/**
	 * <h3>AddCommentAsyncTask</h3> The constructor for the class
	 * AddCommentAsyncTask
	 * 
	 * @param activity
	 *            The CommentActivity to set
	 */
	public AddCommentAsyncTask(CommentActivity activity) {
		this.activity = activity;
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
				"http://fenstergang.com/posts/android_comment_add", pairs);

		this.dialog = (ProgressDialog) params[2];
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {

		dialog.cancel();
		CommentActivity c = activity;
		if (result) {
			c.reset(c);
		} else {
			Toast.makeText(c, "Greska kod spremanja komentara!",
					Toast.LENGTH_LONG).show();
		}
	}

}
