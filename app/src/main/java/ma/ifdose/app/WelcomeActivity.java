package ma.ifdose.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import timber.log.Timber;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_welcome);

//        Log.i(TAG, "Started " + TAG);
        Timber.i("App started");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(ma.ifdose.app.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == ma.ifdose.app.R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick_1(View v) {
        Intent i = new Intent(this, CalculActivity.class);
        startActivity(i);
    }

    public void onClick_2(View v) {
        Intent i = new Intent(this, dailyInfosActivity.class);
        startActivity(i);
    }

    public void onClick_3(View v) {
        Intent i = new Intent(this, AddMealActivity.class);
        startActivity(i);
    }

    public void onClick_4(View v) {
        Intent i = new Intent(this, GenerateRapportActivity.class);
        startActivity(i);
    }

}


