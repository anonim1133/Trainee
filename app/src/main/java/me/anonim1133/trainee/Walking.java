package me.anonim1133.trainee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

public class Walking extends Fragment{

	View rootView;
	GpsHelper gps;

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


		Chronometer chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if(active){
					if(active_time < (SystemClock.elapsedRealtime() - chronometer.getBase()))
						active_time++;

					short hours = (short) (active_time / 3600);
					short minutes = (short) ((active_time % 3600) / 60);
					short seconds = (short) (active_time % 60);

					setTimeActive(String.format("%02d:%02d:%02d", hours, minutes, seconds));
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

				Chronometer chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
				chrono.start();
			}

			public void onBtnStop() {
				if (gps != null)
					gps.stopPeriodicUpdates();

				Chronometer chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
				chrono.stop();
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

			public void setSpeedAVG(String speed_avg) {
				TextView tv = (TextView) rootView.findViewById(R.id.txt_avg_speed);
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
				txt_diff.setText(upward);

				TextView txt_downward= (TextView) rootView.findViewById(R.id.txt_altitude_downward);
				txt_diff.setText(downward);
			}

		}