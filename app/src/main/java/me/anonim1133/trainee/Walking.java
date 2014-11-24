package me.anonim1133.trainee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Walking extends Fragment{
	View rootView;
	GpsHelper gps;

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

		return rootView;
	}

	@Override
	public void onStart(){
		super.onStart();

	}

	public void onBtnStart(){
		gps.requestUpdates();
	}

	public void onBtnStop(){
		gps.stopPeriodicUpdates();
	}

	public void setTime(String time){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_time);
		tv.setText(time);
	}

	public void setTimeActive(String time_active){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_time_active);
		tv.setText(time_active);
	}
	public void setSpeed(String speed){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_speed);
		tv.setText(speed);
	}
	public void setSpeedAVG(String speed_avg){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_avg_speed);
		tv.setText(speed_avg);
	}
	public void setDistance(String distance){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_distance);
		tv.setText(distance);
	}
	public void setGoal(String goal){
		TextView tv = (TextView) rootView.findViewById(R.id.txt_goal);
		tv.setText(goal);
	}
}