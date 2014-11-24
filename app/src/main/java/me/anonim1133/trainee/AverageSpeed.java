package me.anonim1133.trainee;

public class AverageSpeed {

	private short count = 0;
	private short limit = 0;
	private float sum = 0;

	public AverageSpeed(short limit) {
		this.limit = limit;
	}

	public float add(float value){
		count++;

		this.sum += value;

		if(this.count == this.limit){
			float ret = (sum/count);

			reset(ret);

			return ret;
		}else return 0;
	}

	private void reset(float average){
		this.count = 1;
		this.sum = average;
	}

}
