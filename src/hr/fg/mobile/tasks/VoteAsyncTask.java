package hr.fg.mobile.tasks;

import hr.fg.mobile.R;
import hr.fg.mobile.plugins.JsonRequestLoader;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

/**
 * <h2>VoteAsyncTask</h2> AsyncTask for voting a comment or post
 * 
 * @author Fenster Gang
 * 
 */
public class VoteAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the button for voting
	 */
	private Button vote;

	/**
	 * Used to define the button which displays who has voted
	 */
	private Button votes;

	/**
	 * Used to define which action is requested. False for delete vote, and true
	 * for add a vote
	 */
	private Integer action;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * <h3>VoteAsyncTask</h3> The constructor of the class VoteAsyncTask
	 * 
	 * @param context
	 *            The context to set
	 */
	public VoteAsyncTask(Context context) {
		this.context = context;
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
		action = (Integer) params[2];
		vote = (Button) params[3];
		votes = (Button) params[4];
		Boolean result;

		if (action == 1) {
			result = request.postRequestStatus(
					"http://fenstergang.com/votes/android_vote_add", pairs);
		} else
			result = request.postRequestStatus(
					"http://fenstergang.com/votes/android_vote_delete", pairs);
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
			if (action == 1) {
				Toast.makeText(context, "Hracnuo si!", Toast.LENGTH_LONG)
						.show();
				vote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nospit,
						0, 0, 0);
				vote.setId(0);
			} else {
				Toast.makeText(context, "Maknuo si hracak!", Toast.LENGTH_LONG)
						.show();
				vote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spit,
						0, 0, 0);
				vote.setId(1);
			}

			JSONObject o = request.postRequestData();
			System.out.println(o.optInt("votesCount"));
			votes.setText(" | " + o.optInt("votesCount"));
		} else {
			Toast.makeText(context, "Došlo je do pogreške, pokušaj ponovo..",
					Toast.LENGTH_LONG).show();
		}
		vote.setEnabled(true);
	}

}
