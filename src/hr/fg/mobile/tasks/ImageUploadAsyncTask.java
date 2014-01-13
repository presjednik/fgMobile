package hr.fg.mobile.tasks;

import hr.fg.mobile.CommentActivity;
import hr.fg.mobile.PostsActivity;
import hr.fg.mobile.ProfileActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * <h2>ImageUploadAsyncTask</h2> AsyncTask for uploading a image
 * 
 * @author Fenster Gang
 * 
 */
public class ImageUploadAsyncTask extends AsyncTask<Object, Void, String> {

	/**
	 * Used to define the path of the picture on the device
	 */
	private String pathToOurFile;

	/**
	 * Used to define the URL of the server
	 */
	private String urlServer = "http://fenstergang.com/posts/android_upload";

	/**
	 * Used to define the HttpURLConnection we use
	 */
	private HttpURLConnection connection = null;

	/**
	 * Used to define the DataOutputStream which writes to the connection
	 */
	private DataOutputStream outputStream = null;

	/**
	 * Used to define the DataInputStream which reeds from the connection
	 */
	private DataInputStream inputStream = null;

	/**
	 * Used to define the end of the line
	 */
	private String lineEnd = "\r\n";

	/**
	 * Used to define hyphens
	 */
	private String twoHyphens = "--";

	/**
	 * Used to define the boundary
	 */
	private String boundary = "*****";

	/**
	 * Used to define the number of bytes
	 */
	private int bytesRead, bytesAvailable, bufferSize;

	/**
	 * Used to define the buffer
	 */
	private byte[] buffer;

	/**
	 * Used to define the maximum buffer size
	 */
	private int maxBufferSize = 1 * 1024 * 1024;

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define the String that returns from the doInBackground method
	 */
	private String str;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the Activity
	 */
	private Activity main;

	/**
	 * Used to define which Activity to call
	 */
	private Integer flag;

	/**
	 * Used to define the ProgressDialog which shows when uploading a image
	 */
	private ProgressDialog dialog;

	/**
	 * <h3>ImageUploadAsyncTask</h3> The constructor for the class
	 * ImageUploadAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param main
	 *            The Activity to set
	 */
	public ImageUploadAsyncTask(Context c, Activity main) {
		this.c = c;
		this.main = main;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected String doInBackground(Object... params) {

		pathToOurFile = (String) params[0];
		pairs = (List<NameValuePair>) params[1];
		flag = (Integer) params[2];
		dialog = (ProgressDialog) params[3];

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"data[Post][image-loc]\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			inputStream = new DataInputStream(connection.getInputStream());
			str = inputStream.readLine();

			inputStream.close();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			// Exception handling
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (!result.equals("false")) {

			if (flag == 1) {
				pairs.add(new BasicNameValuePair("data[Post][image][0]", result));
				((PostsActivity) main).postAsyncTaskDo(pairs);
			} else if (flag == 2) {
				pairs.add(new BasicNameValuePair("data[Post][image][0]", result));
				((CommentActivity) main).commentAsyncTaskDo(
						new JsonRequestLoader(), pairs, dialog);
			} else if (flag == 3) {
				pairs.add(new BasicNameValuePair("data[User][profile_picture]",
						result));
				((ProfileActivity) main).editField(null, null,
						"profile_picture", pairs, true);
			}
		} else {
			Toast.makeText(c, "Gre�ka u spremanju slike..", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
