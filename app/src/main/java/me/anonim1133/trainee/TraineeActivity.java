package me.anonim1133.trainee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class TraineeActivity extends Activity {

	Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee);
        if (savedInstanceState == null) {
	        fragment = new SelectActivity();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

	/* Selecting proper fragments for activities */

	public void selectActivity(View view){
		fragment = new SelectActivity();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	public void selectBiking(View view){
		fragment = new Biking();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack(null)
				.commit();
	}

	public void selectWalking(View view){
		fragment = new Walking();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack(null)
				.commit();
	}

	public void selectJumping(View view){
		fragment =  new Jumping();
		getFragmentManager().beginTransaction()
				.replace(R.id.container,fragment)
				.addToBackStack(null)
				.commit();
	}

	public void selectSquats(View view){
		fragment = new Squats();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack(null)
				.commit();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trainee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
