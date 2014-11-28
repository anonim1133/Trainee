package me.anonim1133.trainee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import me.anonim1133.trainee.R;
import me.anonim1133.trainee.sensors.AccelerometerHelper;

public class Jumping extends Fragment {
	View rootView;
	Chronometer chrono;

	AccelerometerHelper accelerometer;

	boolean active = false;
	int moves = 0;

	int succesion = 0;
	long time_succesion = 0;
	int moves_succesion = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.jumping, container, false);

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
		rootView.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				onBtnNext();
			}
		});

		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
			if (time_succesion < getTimeMs()-1)
				time_succesion++;

			int last_moves = moves;
			moves = accelerometer.getMoveCount();

			moves_succesion += moves - last_moves;

			updateUI();
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);

		accelerometer = new AccelerometerHelper(activity, 20.01);
	}

	public void onBtnStart() {
		accelerometer.registerSensor();

		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.setBase(SystemClock.elapsedRealtime());
		chrono.start();

		rootView.findViewById(R.id.btn_start).setVisibility(View.GONE);
		rootView.findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.btn_stop).setVisibility(View.VISIBLE);
	}

	public void onBtnStop() {
		if(accelerometer != null)
			accelerometer.unregisterSensor();

		chrono = (Chronometer) rootView.findViewById(R.id.chronometer);
		chrono.stop();

		rootView.findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.btn_next).setVisibility(View.GONE);
		rootView.findViewById(R.id.btn_stop).setVisibility(View.GONE);
	}

	public void onBtnNext() {
		nextSuccession();
	}

	public long getTimeMs(){
		return SystemClock.elapsedRealtime() - chrono.getBase();
	}

	public String getTimeSuccession(){
		short hours = (short) (time_succesion / 3600);
		short minutes = (short) ((time_succesion % 3600) / 60);
		short seconds = (short) (time_succesion % 60);

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	private void nextSuccession(){
		succesion++;

		time_succesion = 0;
		moves_succesion = 0;
	}

	/* UI values setting */

	private void updateUI(){
		updateMoves();
		updateSuccession();
		updateSuccessionMoves();
		updateSuccessionTime();
	}

	private void updateMoves(){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_jump_count);
		tv.setText(String.valueOf(moves));
	}

	private void updateSuccession(){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_succession);
		tv.setText(String.valueOf(succesion));
	}

	private void updateSuccessionTime(){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_succession_time);
		tv.setText(getTimeSuccession());
	}

	private void updateSuccessionMoves(){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_succession_jump_count);
		tv.setText(String.valueOf(moves_succesion));
	}
}
