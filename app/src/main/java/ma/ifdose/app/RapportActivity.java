package ma.ifdose.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RapportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_rapport);
    }

   public void onClick_1(View v) {
        Intent i = new Intent(this, ProfilActivity.class);
        startActivity(i);
    }


    public void onGenerateRapportBtnClick(View v) {
        Intent i = new Intent(this, GenerateRapportActivity.class);
        startActivity(i);
    }

    public void onClick_3(View v) {
        Intent i = new Intent(this, GlycemiesActivity3.class);
        startActivity(i);
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
