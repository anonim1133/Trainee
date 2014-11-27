package me.anonim1133.trainee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

public class Walking extends Fragment{

	View rootView;
	GpsHelper gps;

	Chronometer chrono;

	boolean active = false;
	int active_time = 0;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);

		gps = new GpsHelper(activity, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.walking, container, false);

		rootView.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				onBtnStart();
			}
		});
		rootView.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				onBtnStop();
			}
		});


		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if(active){
					if(active_time < (SystemClock.elapsedRealtime() - chronometer.getBase()))
						active_time++;

					setTimeActive(getTimeActive());
				}
			}
		});

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public void onBtnStart() {
		gps.requestUpdates();

		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.setBase(SystemClock.elapsedRealtime());
		chrono.start();

		rootView.findViewById(R.id.btn_start).setVisibility(View.GONE);
		rootView.findViewById(R.id.btn_stop).setVisibility(View.VISIBLE);
	}

	public void onBtnStop() {
		if (gps != null)
			gps.stopPeriodicUpdates();

		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.stop();

		rootView.findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.btn_stop).setVisibility(View.GONE);
	}

	public String getTime(){
		long elapsedMillis = SystemClock.elapsedRealtime() - chrono.getBase();
		short hours = (short) (elapsedMillis / 3600);
		short minutes = (short) ((elapsedMillis % 3600) / 60);
		short seconds = (short) (elapsedMillis % 60);

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public long getTimeMs(){
		return SystemClock.elapsedRealtime() - chrono.getBase();
	}

	public String getTimeActive(){
		short hours = (short) (active_time / 3600);
		short minutes = (short) ((active_time % 3600) / 60);
		short seconds = (short) (active_time % 60);

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public long getTimeActiveMs(){
		return active_time;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setTimeActive(String time_active) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_time_active);
		tv.setText(time_active);
	}

	public void setSpeed(String speed) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_speed);
		tv.setText(speed);
	}

	public void setSpeedMax(String speed) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_speed);
		tv.setText(speed);
	}

	public void setSpeedAVG(String speed_avg) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_avg_speed);
		tv.setText(speed_avg);
	}

	public void setTempo(String speed) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_tempo);
		tv.setText(speed);
	}

	public void setTempoAVG(String speed_avg) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_avg_tempo);
		tv.setText(speed_avg);
	}

	public void setDistance(String distance) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_distance);
		tv.setText(distance);
	}

	public void setGoal(String goal) {
		TextView tv = (TextView) rootView.findViewById(R.id.txt_goal);
		tv.setText(goal);
	}

	public void setAltitude(String min, String diff, String max, String upward, String downward){
		TextView txt_min = (TextView) rootView.findViewById(R.id.txt_altitude_min);
		txt_min.setText(min);

		TextView txt_max = (TextView) rootView.findViewById(R.id.txt_altitude_max);
		txt_max.setText(max);

		TextView txt_diff= (TextView) rootView.findViewById(R.id.txt_altitude_diff);
		txt_diff.setText(diff);

		TextView txt_upward= (TextView) rootView.findViewById(R.id.txt_altitude_upward);
		txt_upward.setText(upward);

		TextView txt_downward= (TextView) rootView.findViewById(R.id.txt_altitude_downward);
		txt_downward.setText(downward);
	}

}