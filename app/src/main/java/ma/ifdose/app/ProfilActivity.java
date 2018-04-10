package ma.ifdose.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity {
    private TextView t1, t2, t3, t4, t5, t6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_profil);
        t2 = (TextView) findViewById(ma.ifdose.app.R.id.txt1);
        t3 = (TextView) findViewById(ma.ifdose.app.R.id.txt2);
        t4 = (TextView) findViewById(ma.ifdose.app.R.id.txt3);
        t5 = (TextView) findViewById(ma.ifdose.app.R.id.txt4);
        t6 = (TextView) findViewById(ma.ifdose.app.R.id.txt5);

        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        float rp = sp.getFloat("rp", 0);
        float rd = sp.getFloat("rd", 0);
        float rc = sp.getFloat("rc", 0);
        float rdi = sp.getFloat("rdi", 0);
        float is = sp.getFloat("is", 0);

        t2.setText(String.valueOf(rp));
        t3.setText(String.valueOf(rd));
        t4.setText(String.valueOf(rc));
        t5.setText(String.valueOf(rdi));
        t6.setText(String.valueOf(is + " غرام/لتر "));
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

}
