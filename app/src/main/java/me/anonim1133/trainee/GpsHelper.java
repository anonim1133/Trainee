package me.anonim1133.trainee;

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


public class GpsHelper extends Activity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String APPTAG = "GpsHelper";

	//Number of samples for counting average
	private static final short AVERAGE_SAMPLE_COUNT = 100;

	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	public static final int FAST_CEILING_IN_SECONDS = 1;
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

	private LocationClient locationClient;
	private LocationRequest locationRequest;

	private Context c;
	private Biking biking;
	private Walking walking;
	private AverageSpeed avg;

	private Location last_location;
	private float total_distance = 0;
	private float altitude_min = 10000;
	private float altitude_max = -1000;
	private float upward = 0;
	private float downward = 0;

	private boolean updates_requested = false;


	public GpsHelper(Context context){
		this.c = context;

		locationRequest = LocationRequest.create();
		locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		locationClient = new LocationClient(c, this, this);

		requestUpdates();
	}

	public GpsHelper(Context context, Biking biking) {
		this.c = context;
		this.biking = biking;
	}

	public GpsHelper(Context context, Walking walking) {
		this.c = context;
		this.walking = walking;
	}

	public void setActive(boolean active){
		if(biking != null)
			biking.setActive(active);

		if(walking != null)
			walking.setActive(active);
	}

	public void setSpeed(float speed){
		if(biking != null)
			biking.setSpeed(String.valueOf(speed));

		if(walking != null) {
			if (speed == 0f)
				walking.setSpeed("0:00");
			else
				walking.setSpeed(String.format("%.2f", 60 / speed).replace(",", ":"));
		}

		setAverageSpeed(speed);
	}

	public void setDistance(float distance){
		if(biking != null)
			biking.setDistance(String.format("%.2f", distance));

		if(walking != null)
			walking.setDistance(String.format("%.2f", distance));
	}

	public void setAverageSpeed(float speed){
		if(biking != null)
			biking.setSpeedAVG(String.format("%.2f", avg.add(speed)));

		if(walking != null)
			walking.setSpeedAVG(String.format("%.2f", avg.add(speed)).replace(",", ":"));
	}

	public void setAltitude(float min, float diff, float max, float upward, float downward){
		if(biking != null)
			biking.setAltitude(String.valueOf(min), String.valueOf(diff), String.valueOf(max), String.valueOf(upward), String.valueOf(downward));

		if(walking != null)
			walking.setAltitude(String.valueOf(min), String.valueOf(diff), String.valueOf(max), String.valueOf(upward), String.valueOf(downward));
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(APPTAG, "onLocationChanged");

		//Setting speed
		setSpeed(location.getSpeed()*3.6f);

		//Setting altitude
		if(location.hasAltitude()){
			if(((float) location.getAltitude()) > altitude_max)
				altitude_max = (float) location.getAltitude();

			if(((float) location.getAltitude()) < altitude_min)
				altitude_min = (float) location.getAltitude();

			if(last_location != null && location.hasAltitude() && last_location.hasAltitude()){
				float altitude_difference = (float)(location.getAltitude() - last_location.getAltitude());

				if(altitude_difference < 0) downward += -altitude_difference;
				else upward += altitude_difference;
			}

			if(altitude_max < 10000 & altitude_min > -1000)
				setAltitude(altitude_min, altitude_max-altitude_min, altitude_max, upward, downward);
		}

		//Setting distance
		if(last_location != null){
			total_distance += last_location.distanceTo(location);
			setDistance(total_distance / 1000);

			//Setting active
			if(!last_location.hasSpeed() && !location.hasSpeed())
				setActive(false);
			else
				setActive(true);
		}

		last_location = location;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(APPTAG, "onConnected");
		if (updates_requested) {
			startPeriodicUpdates();
		}
	}

	@Override
	public void onDisconnected() {
		Log.d(APPTAG, "onDisconnected");
		// Display the connection status
	}

	@Override
	public void onStop(){
		Log.d(APPTAG, "onStop");
		stopPeriodicUpdates();
	}

	public void requestUpdates(){
		locationRequest = LocationRequest.create();
		locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		locationClient = new LocationClient(c, this, this);

		locationClient.connect();
		updates_requested = true;
	}


	private void startPeriodicUpdates() {
		Log.d(APPTAG, "startPeriodicUpdates");
		locationClient.requestLocationUpdates(locationRequest, this);

		avg = new AverageSpeed(AVERAGE_SAMPLE_COUNT);
	}

	public void stopPeriodicUpdates() {
		Log.d(APPTAG, "stopPeriodicUpdates");
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
			errorFragment.show(getFragmentManager(), APPTAG);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(APPTAG, "onConnectionFailed");
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
