package hr.fg.mobile;

import hr.fg.mobile.plugins.ImageDownloader;
import hr.fg.mobile.prefs.FgPrefs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * <h2>ImageActivity</h2> Activity for showing a image
 * 
 * @author Fenster Gang
 * 
 */
public class ImageActivity extends Activity implements OnTouchListener {

	/**
	 * Used to define users finger action (touch, drag, ...)
	 */
	private static final String TAG = "Touch";

	/**
	 * Used to scale points of the image
	 */
	private Matrix matrix = new Matrix();

	/**
	 * Used to scale points of the image
	 */
	private Matrix savedMatrix = new Matrix();

	/**
	 * Used to define the state when user wants to perform nothing
	 */
	static final int NONE = 0;

	/**
	 * Used to define the state when user wants to perform drag
	 */
	static final int DRAG = 1;

	/**
	 * Used to define the state when user wants to perform zoom
	 */
	static final int ZOOM = 2;

	/**
	 * Used to define the mode that needs to be performed
	 */
	int mode = NONE;

	/**
	 * Used to define the point the user is touching
	 */
	private PointF start = new PointF();

	/**
	 * Used to define the point the user is touching
	 */
	private PointF mid = new PointF();

	/**
	 * Used to hold the spacing between fingers
	 */
	float oldDist = 1f;

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle bun = getIntent().getExtras();

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View fullImage = inflater.inflate(R.layout.full_img_view, null);
		LinearLayout imageLayout = (LinearLayout) fullImage
				.findViewById(R.id.fullImage_layout);
		ImageView imageView = (ImageView) fullImage
				.findViewById(R.id.fullImage);
		ImageDownloader image = new ImageDownloader(this, true);
		String imgName;
		if (bun.getBoolean("posts")) {
			imgName = PostsActivity.imageName;
		} else {
			imgName = CommentActivity.imageName;
		}
		image.download("http://fenstergang.com/img/" + imgName, imageView);

		imageView.setOnTouchListener(this);
		setContentView(imageLayout);
	}

	// HEADER METODE

	/**
	 * <h3>buksa</h3> Intents the activity PostsActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void buksa(View v) {
		Intent p = new Intent(c, PostsActivity.class);
		p.putExtra("pflag", "all");
		p.putExtra("userp", "0");
		startActivity(p);
	}

	/**
	 * <h3>banda</h3> Intents the activity UsersActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void banda(View v) {
		Intent p = new Intent(c, UsersActivity.class);
		startActivity(p);
	}

	/**
	 * <h3>profil</h3> Intents the activity ProfileActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void profil(View v) {
		Intent p = new Intent(c, ProfileActivity.class);
		p.putExtra("user", Integer.parseInt(pref.getPrefs("userId", c)));
		startActivity(p);
	}

	/**
	 * <h3>notify</h3> Intents the activity NotificationActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void notify(View v) {
		Intent p = new Intent(c, NotificationsActivity.class);
		startActivity(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		view.setScaleType(ImageView.ScaleType.MATRIX);
		float scale;

		// Handle touch events here...

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // first finger down only
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG"); // write to LogCat
			mode = DRAG;
			break;

		case MotionEvent.ACTION_UP: // first finger lifted

		case MotionEvent.ACTION_POINTER_UP: // second finger lifted

			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;

		case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 5f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y); // create the transformation in the matrix
									// of points
			} else if (mode == ZOOM) {
				// pinch zooming
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 5f) {
					matrix.set(savedMatrix);
					scale = newDist / oldDist; // setting the scaling of the
												// matrix...if scale > 1 means
												// zoom in...if scale < 1 means
												// zoom out
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix); // display the transformation on screen

		return true; // indicate event was handled
	}

	/**
	 * <h3>spacing</h3> Checks the spacing between the two fingers on touch
	 * 
	 * @param event
	 *            The event on which it checks the spacing
	 * @return The spacing between the two fingers
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * <h3>midPoint</h3> Calculates the midpoint between the two fingers
	 * 
	 * @param point
	 *            The midpoint to be set
	 * @param event
	 *            The event on which it checks the midpoint
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
