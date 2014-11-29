package me.anonim1133.trainee.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	private static String DATABASE_NAME = "zacja.db";
	private static final int DATABASE_VERSION = 5;

	private SQLiteDatabase db;
	private DBTraining training;

	public DataBaseHelper(Context context) throws SQLException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		Log.d(TAG, "konstruktor");
		this.open();

		training = new DBTraining(this.db);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String CREATE_CONQUERED_TABLE = "CREATE TABLE `conquered` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `points` INTEGER DEFAULT '0', `date` INTEGER DEFAULT '0', `longitude` REAL DEFAULT '0', `latitude` REAL DEFAULT '0'); CREATE INDEX `long_index` ON `conquered` (`longitude` ASC);CREATE INDEX `lat_index` ON `conquered` (`latitude` ASC);CREATE INDEX `date_index` ON `conquered` (`date` DESC);";
		String CREATE_WIFI_TABLE = "CREATE TABLE `wifi` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `ssid` TEXT, `bssid` TEXT, `signal` INTEGER DEFAULT '0', `security` INTEGER DEFAULT '0', `longitude` REAL DEFAULT '0', `latitude` REAL DEFAULT '0')";
		String CREATE_TRAINING_TABLE = "CREATE TABLE `training` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `gpx` TEXT, training_type TEXT, `time` INTEGER, `time_active` INTEGER, `moves` INTEGER, `speed_max` REAL, `speed_avg` REAL, `tempo_min` REAL, `tempo_avg` REAL, `distance` REAL, `altitude_min` INTEGER, `altitude_max` INTEGER, `altitude_upward` INTEGER, `altitude_downward` INTEGER);";

		Log.d(TAG, "onCreate");
		database.execSQL(CREATE_CONQUERED_TABLE);
		database.execSQL(CREATE_WIFI_TABLE);
		database.execSQL(CREATE_TRAINING_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int i, int i2) {
		Log.d(TAG, "OnUpgrade");
		database.execSQL("DROP TABLE IF EXISTS wifi;");
		database.execSQL("DROP TABLE IF EXISTS conquered;");
		database.execSQL("DROP TABLE IF EXISTS training;");
		onCreate(database);
	}

	public void open() throws SQLException {
		Log.d(TAG, "open");
		this.db = this.getWritableDatabase();
	}

	public boolean addTraining(String gpx, String training_type, long time, long time_active, float speed_max, float speed_avg, float tempo_min, float tempo_avg, float distance, int altitude_min, int altitude_max, int altitude_upward, int altitude_downward){
		return training.add(gpx, training_type, time, time_active, speed_max, speed_avg, tempo_min, tempo_avg, distance, altitude_min, altitude_max, altitude_upward, altitude_downward);
	}

	public boolean addTraining(String gpx, String training_type, long time, long time_active, int moves, float speed_max, float speed_avg, float tempo_min, float tempo_avg, float distance, int altitude_min, int altitude_max, int altitude_upward, int altitude_downward){
		return training.add(gpx, training_type, time, time_active, moves, speed_max, speed_avg, tempo_min, tempo_avg, distance, altitude_min, altitude_max, altitude_upward, altitude_downward);
	}
}


