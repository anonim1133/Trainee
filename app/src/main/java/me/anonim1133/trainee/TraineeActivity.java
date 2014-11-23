package me.anonim1133.trainee;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;



public class TraineeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SelectActivity())
                    .commit();
        }
    }

	public void selectActivity(View view){
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new SelectActivity())
				.commit();
	}

	public void selectBiking(View view){
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new Biking())
				.addToBackStack(null)
				.commit();
	}

	public void selectWalking(View view){
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new Walking())
				.addToBackStack(null)
				.commit();
	}

	public void selectJumping(View view){
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new Jumping())
				.addToBackStack(null)
				.commit();
	}

	public void selectSquats(View view){
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new Squats())
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
