package me.anonim1133.trainee.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import me.anonim1133.trainee.utils.Average;

public class PedometerHelper implements SensorEventListener {
	private static String TAG = "PEDO";

	int number_of_steps = 0;
	long last_time = 0;
	short inactive_steps = 0;
	double threshold = 11.91;

	short time_between_steps = 300;
	float activity = 0;

	SensorManager sensorManager;
	Context c;
	Average avg;

	public PedometerHelper(Context context, double threshold) {
		c = context;
		this.threshold = threshold;

		avg = new Average((short)10);

		sensorManager = (SensorManager) c.getSystemService(c.SENSOR_SERVICE);
	}

	public void registerSensor(){
		if(!registerPedometer())
			registerAcclerometer();
	}

	private boolean registerAcclerometer(){
		Log.d(TAG, "Accelerometer starts");
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		return sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	}

	private boolean registerPedometer(){
		Log.d(TAG, "Step_Detector starts");
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
		return sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
	}

	public void unregisterSensor(){
		sensorManager.unregisterListener(this);
	}

	public int getStepCount(){
		return number_of_steps;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double v = Math.abs(
					Math.sqrt(
							Math.pow(x, 2)
									+
									Math.pow(y, 2)
									+
									Math.pow(z, 2)
					));

			long actualTime = System.currentTimeMillis();
			long difference = actualTime - last_time;

			if ((difference > time_between_steps) && (v > threshold)) {
				//11.91 = chodzenie, przy ~16 wykrywa tylko krok nogi przy której jest telefon
				//11.91 = przysiady wykrywa mniej więcej. Najlepiej gdy telefon jest w ręku.
				//20.01 = skoki

				//Zabezpieczenie przed pierwszymi krokami ( pierwsze 7 kroków, takze po 5s nieaktywności )
				//które mogą być ruchem telefonu chowanego do kieszeni.

				if( (difference) > 5000 || (inactive_steps < 5 && inactive_steps != 0) || number_of_steps == 0) {
					inactive_steps++;

					if(inactive_steps >= 5){
						inactive_steps = 0;
						number_of_steps++;


					}

				}else{
					//zliczanie kroków
					number_of_steps++;

					//zliczanie "aktywności" podczas chodu
					float average = avg.add((short)difference);
					//1000f bo chcemy aby jeden krok trwał mniej niż 1s
					if(average != 0 && average < 1000.0f){
						float tmp = 1000.0f - (average/1000);
						activity += tmp/1000;
					}
				}

				//utrzymywanie zmiennej czasowej możliwie blisko częstotliwości kroków.
				//utrzymujemy zmienną w zakresie 356-1024
				if((difference - time_between_steps) > 100 && time_between_steps < 1024){
					//tempo spada
					time_between_steps += 10;
				}else if((difference - time_between_steps) < 100 && time_between_steps > 356){
					//temporosnie
					time_between_steps -= 10;
				}

				last_time = System.currentTimeMillis();
			}
		}


		//Pedometr
		if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
			number_of_steps++;// step counter - Float.floatToIntBits(event.values[0]);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}
}
