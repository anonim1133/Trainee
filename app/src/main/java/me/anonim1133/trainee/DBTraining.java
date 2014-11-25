package me.anonim1133.trainee;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBTraining {
	private static String TAG = "ZACJA_DB_TRAIN";
	private static String TABLE_NAME = "training";

	private SQLiteDatabase db;

	public DBTraining(SQLiteDatabase database){
		this.db = database;
	}

	public boolean add(String gpx, String training_type, String time, String time_active, float speed_avg, float tempo_avg, float distance, int altitude_min, int altitude_max, int altitude_upward, int altitude_downward){
		ContentValues values = new ContentValues();
		values.put("gpx", gpx);
		values.put("time", time);
		values.put("time_active", time_active);
		values.put("speed_avg", speed_avg);
		values.put("tempo_avg", tempo_avg);
		values.put("distance", distance);
		values.put("altitude_min", altitude_min);
		values.put("altitude_max", altitude_max);
		values.put("altitude_upward", altitude_upward);
		values.put("altitude_downward", altitude_downward);

		try{
			long value = db.insertOrThrow(TABLE_NAME, null, values);
			if(value > 0) return true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return false;
	}

	public Cursor getLast(int limit) {
		Cursor cursor;

		if(limit != 0)
			cursor = db.query(TABLE_NAME , new String[] {"id"}, null, null, null, null, "id DESC", String.valueOf(limit));
		else
			cursor = db.query(TABLE_NAME , new String[] {"id"}, null, null, null, null, "id DESC", null);

		return cursor;
	}
}