package me.anonim1133.trainee.sensors;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.sql.SQLException;

import me.anonim1133.trainee.db.DataBaseHelper;
import me.anonim1133.trainee.utils.Average;
import me.anonim1133.trainee.utils.GpxBuilder;


public class GpsHelper extends Activity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = "GpsHelper";

	//Number of samples for counting average
	private static final short AVERAGE_SAMPLE_COUNT = 100;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	private int UPDATE_INTERVAL_IN_SECONDS = 5;
	private int FAST_CEILING_IN_SECONDS = 1;
	private long UPDATE_INTERVAL_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;
	private boolean is_counting_steps = false;

	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private GpxBuilder gpx;
	private DataBaseHelper db;

	private Context c;

	private Average avg_speed;
	private Average avg_tempo;

	private Location last_location;

	private String user_name = "Unnamed";
	private String activity_name = "Unknown";

	private boolean active = false;

	private long time = 0;
	private long time_active = 0;
	private float total_distance = 0;
	private float speed = 0;

	private float speed_max = 0;
	private float speed_avg = 0;
	private float tempo = 0;
	private float tempo_avg = 0;
	private float tempo_min = 50.0f;

	private float altitude = 0;
	private float altitude_min = 10000;
	private float altitude_diff = 0;
	private float altitude_max = -1000;
	private float upward = 0;
	private float downward = 0;
	private int step_count = 0;

	private boolean updates_requested = false;

	public GpsHelper(Context context, int min_interval, int max_interval) {
		this.c = context;

		UPDATE_INTERVAL_IN_SECONDS = max_interval;
		FAST_CEILING_IN_SECONDS = min_interval;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");

		//Setting speed
		setSpeed(location.getSpeed()*3.6f);

		//Setting distance
		if(last_location != null){
			setDistance(last_location.distanceTo(location) / 1000);

			//Setting active
			if(!last_location.hasSpeed() && !location.hasSpeed())
				setActive(false);
			else
				setActive(true);

			//Setting altitude
			if(location.hasAltitude() && (location.getAccuracy() <= 10.0)){
				setAltitude((float)location.getAltitude(), location.getAltitude()-last_location.getAltitude());
			}
		}

		//Saving point to gpx
		if(!is_counting_steps)
			gpx.addPoint(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed()*3.6f,	location.getTime());
		else
			gpx.addPoint(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed()*3.6f, step_count,	location.getTime());

		last_location = location;
	}

	/* SETTERS */

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setTime_active(long time_active) {
		this.time_active = time_active;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setUserName(String name){
		user_name = name;
	}

	public void setActivityName(String name){
		activity_name = name;
	}

	public void setStepCounting(boolean bool){
		is_counting_steps = bool;
	}

	public void setStepCount(int steps){
		step_count = steps;
	}

	public void setSpeed(float speed){
		this.speed = speed;

		if(speed > 0) {
			setAverageSpeed(speed);
			setMaxSpeed(speed);

			tempo(speed);
		}
	}

	public void setMaxSpeed(float speed){
		if(speed > speed_max)
			speed_max = speed;
	}

	public void setAverageSpeed(float speed){
		if(speed > 0)
			speed_avg = avg_speed.add(speed);
	}

	public void tempo(float speed){
		if(speed != 0){
			 tempo = 60/speed;
		}else {
			tempo = 0;
		}

		setAverageTempo(tempo);
		setTempoMin(tempo_min);
	}

	public void setTempoMin(float tempo){
		if(tempo < tempo_min && tempo != 0)
			tempo_min = tempo;
	}

	public void setAverageTempo(float tempo){
		if(tempo > 0)
			avg_tempo.add(tempo);
	}

	public void setDistance(float distance){
		total_distance += distance;
	}

	public void setAltitude(double alt, double difference){
		float altitude_difference = (float)difference;

		altitude = (float)alt;
		altitude_diff = (altitude_max - altitude_min);

		if(alt > altitude_max)
			altitude_max = (float)alt;

		if(alt < altitude_min)
			altitude_min = (float)alt;

		if(altitude_difference < 0) downward += -altitude_difference;
		else if(altitude_difference > 0) upward += altitude_difference;

	}


	/* GETTERS */
	public float getSpeed_avg() {
		return speed_avg;
	}

	public float getSpeed_max() {
		return speed_max;
	}

	public float getSpeed() {
		return speed;
	}

	public float getTotal_distance() {
		return total_distance;
	}

	public float getTempo() {
		return tempo;
	}

	public float getTempo_avg() {
		return tempo_avg;
	}

	public float getTempo_min() {
		return tempo_min;
	}

	public float getAltitude() {
		return altitude;
	}

	public float getAltitude_min() {
		return altitude_min;
	}

	public float getAltitude_diff() {
		return altitude_diff;
	}

	public float getAltitude_max() {
		return altitude_max;
	}

	public float getUpward() {
		return upward;
	}

	public float getDownward() {
		return downward;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected");
		if (updates_requested) {
			startPeriodicUpdates();
		}
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "onDisconnected");
		// Display the connection status
	}

	@Override
	public void onStop(){
		Log.d(TAG, "onStop");
		stopPeriodicUpdates();
	}

	public void requestUpdates(){
		locationRequest = LocationRequest.create();
		locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		locationClient = new LocationClient(c, this, this);

		locationClient.connect();
		updates_requested = true;

		gpx = new GpxBuilder(c, activity_name, user_name);

		try {
			db = new DataBaseHelper(c);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void startPeriodicUpdates() {
		Log.d(TAG, "startPeriodicUpdates");
		locationClient.requestLocationUpdates(locationRequest, this);

		avg_speed = new Average(AVERAGE_SAMPLE_COUNT);
		avg_tempo = new Average(AVERAGE_SAMPLE_COUNT);
	}

	public void stopPeriodicUpdates() {
		Log.d(TAG, "stopPeriodicUpdates");

		String filename = gpx.close();

		db.addTraining(filename, activity_name, time, time_active, speed_max, avg_speed.get(), tempo_min, avg_tempo.get(), total_distance, (int)altitude_min, (int)altitude_max, (int)upward, (int)downward);



		locationClient.removeLocationUpdates(this);
	}

	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
				errorCode,
				this,
				CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), TAG);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed");
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
	        /*
	         * If no resolution is available, display a dialog to the
	         * user with the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}
}
