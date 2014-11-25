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

		float ret = (sum/count);

		if(this.count == this.limit)
			reset(ret);

		return ret;
	}

	public float get(){
		return sum/count;
	}

	private void reset(float average){
		this.count = 1;
		this.sum = average;
	}

}
