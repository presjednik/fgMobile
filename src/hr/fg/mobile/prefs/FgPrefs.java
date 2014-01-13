package hr.fg.mobile.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * <h2>FgPrefs</h2> Gets the shared preference
 * 
 * @author Fenster Gang
 * 
 */
public class FgPrefs {

	/**
	 * <h3>setPref</h3> Sets the value for shared preference
	 * 
	 * @param key
	 *            The key of the preference to be set
	 * @param value
	 *            The value of the preference to be set
	 * @param c
	 *            Context
	 */
	public void setPref(String key, String value, Context c) {

		SharedPreferences userData = c.getSharedPreferences("UserData", 0);
		Editor editor = userData.edit();
		editor.putString(key, value);
		editor.commit();

	}

	/**
	 * <h3>getPrefs</h3> Gets the value from shared preference
	 * 
	 * @param key
	 *            The key of the preference
	 * @param c
	 *            Context
	 * @return The value of the preference
	 */
	public String getPrefs(String key, Context c) {
		SharedPreferences userData = c.getSharedPreferences("UserData", 0);
		String pref = userData.getString(key, "");
		return pref;
	};

	/**
	 * <h3>removePrefs</h3> Removes a value from shared preference
	 * 
	 * @param key
	 *            The key of the preference to be removed
	 * @param c
	 *            Context
	 */
	public void removePrefs(String key, Context c) {
		SharedPreferences userData = c.getSharedPreferences("UserData", 0);
		Editor editor = userData.edit();
		editor.remove(key);
		editor.commit();
	}

}
