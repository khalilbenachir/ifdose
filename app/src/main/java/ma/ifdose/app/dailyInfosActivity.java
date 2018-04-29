package ma.ifdose.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by youbi on 12-Apr-18.
 */

public class dailyInfosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_dailyinfos);

    }

    public void onClick_1(View v) {
        Intent i = new Intent(this, GlycemiesActivity3.class);
        startActivity(i);
    }

    public void onClick_2(View v) {
        Intent i = new Intent(this, ProfilActivity.class);
        startActivity(i);
    }


}
