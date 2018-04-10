package ma.ifdose.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import ma.ifdose.app.Models.Aliment;
import ma.ifdose.app.diabet.calculator.Calculator;
import timber.log.Timber;

public class ShowCalculActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private final static String TAG = "ShowCalculActivity";
    protected Calculator c;
    protected double resultat = 0.0;
    private SharedPreferences spGlycemies;
    private TextView tutoCalc, uniteInject;
    private EditText tConfirm;
    private SharedPreferences sp;
    private int idx;
    private double glucideAvantRepas;
    private double unitInject;
    private Context context;
    private SharedPreferences.Editor editGlyc;
    private String alimentsDuJour = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_show_calcul);
        tConfirm = (EditText) findViewById(ma.ifdose.app.R.id.confirm1);
        context = getApplicationContext();
        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        editGlyc = spGlycemies.edit();
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        float rp = sp.getFloat("rp", 0);
        float rd = sp.getFloat("rd", 0);
        float rc = sp.getFloat("rc", 0);
        float rdi = sp.getFloat("rdi", 0);
        float obj = sp.getFloat("obj", 0);
        float is = sp.getFloat("is", 0);
        Timber.i("rm : " + String.valueOf(rp));
        Intent i = getIntent();
        glucideAvantRepas = i.getDoubleExtra("glucoAvantRepas", 0.0);
        idx = i.getIntExtra("idx", 0);
        Timber.i("idx : " + String.valueOf(idx));
        alimentsDuJour = i.getStringExtra("alimentsDuJour");
        Timber.i(TAG, "alimentsDuJour : " + alimentsDuJour);
        String activitePhysique = i.getStringExtra("activitePhysique");
        Timber.i(" activitePhysique : " + activitePhysique);
        Bundle args = i.getBundleExtra("Aliments");
        String livret = i.getStringExtra("livret");
        if (livret != null) {
            if (livret.equals("avec") && args != null) {
                ArrayList<Aliment> aliments = (ArrayList<Aliment>) args.getSerializable("Array");
                c = new Calculator(aliments, rp, rd, rc, rdi, is, obj, glucideAvantRepas, idx);
            } else if (livret.equals("sans")) {
                double glucoTotal = i.getDoubleExtra("glucoTotal", 0);
                c = new Calculator(glucoTotal, rp, rd, rc, rdi, is, obj, glucideAvantRepas, idx);
            }
            Timber.i("R1 : " + String.valueOf(c.uc()));
            Timber.i("R2 : " + String.valueOf(c.ur()));
            Timber.i("ratio : " + String.valueOf(c.getRatio()));
            resultat = c.totalGluco();
            unitInject = c.uniteInjecter();
            Timber.i(" unitInject : " + unitInject);
            String[] activitesPhysique = getResources().getStringArray(ma.ifdose.app.R.array.activite_physique);
//            activitePhysique
            if (activitePhysique != null) {
                if (activitePhysique.equals(activitesPhysique[0])) {
                    Timber.i(" aucune activite ");
                } else if (activitePhysique.equals(activitesPhysique[1])) {
                    unitInject = unitInject - (20 * unitInject) / 100;
                    Timber.i(" activite faible (-20%) : " + unitInject);
                } else if (activitePhysique.equals(activitesPhysique[2])) {
                    unitInject = unitInject - (30 * unitInject) / 100;
                    Timber.i(" aucune modere (-30%) : " + unitInject);
                } else if (activitePhysique.equals(activitesPhysique[3])) {
                    unitInject = unitInject - (50 * unitInject) / 100;
                    Timber.i(" aucune intensive (-50%) : " + unitInject);
                }
            }

            String dx = "1", dx2 = "1";
            try {

                DecimalFormat df = new DecimalFormat("#.##");
                DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
                sym.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(sym);
                dx = df.format(resultat);
//            Log.i(TAG, "dx : "+dx);
//            dx.replaceAll(",",".");
                resultat = Double.parseDouble(dx);
                dx2 = df.format(unitInject);
//            dx2.replaceAll(",",".");
//            Log.i(TAG, "dx2 : "+dx2);

            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
            unitInject = Double.parseDouble(dx2);
            tutoCalc = (TextView) findViewById(ma.ifdose.app.R.id.totalGlucoValue);
            uniteInject = (TextView) findViewById(ma.ifdose.app.R.id.uniteInsulineValue);
            tutoCalc.setText(String.valueOf(resultat));
            uniteInject.setText(String.valueOf(unitInject));

        }
    }

    public void onClickConfirm(View v) {
        if ((tConfirm.getText().toString().trim().equals(""))) {
            Toast.makeText(getApplicationContext(), "المرجو ادخال نسبة الأنسولين المؤكد حقنها ",
                    Toast.LENGTH_SHORT).show();
        } else {
            double confirmDose = Double.parseDouble(String.valueOf(tConfirm.getText()));

            switch (idx) {
                case 0:
                    editGlyc.putFloat("glucoAvantRepas0", (float) glucideAvantRepas);
                    editGlyc.putFloat("unitInject0", (float) unitInject);
                    editGlyc.putFloat("confirmDose0", (float) confirmDose);
                    editGlyc.putString("aliments0", alimentsDuJour);
                    editGlyc.putFloat("gluco0", ((float) resultat));
                    break;
                case 1:
                    editGlyc.putFloat("glucoAvantRepas1", (float) glucideAvantRepas);
                    editGlyc.putFloat("unitInject1", (float) unitInject);
                    editGlyc.putFloat("confirmDose1", (float) confirmDose);
                    editGlyc.putString("aliments1", alimentsDuJour);
                    editGlyc.putFloat("gluco1", ((float) resultat));
                    break;
                case 2:
                    editGlyc.putFloat("glucoAvantRepas2", (float) glucideAvantRepas);
                    editGlyc.putFloat("unitInject2", (float) unitInject);
                    editGlyc.putFloat("confirmDose2", (float) confirmDose);
                    editGlyc.putString("aliments2", alimentsDuJour);
                    editGlyc.putFloat("gluco2", ((float) resultat));
                    break;
                case 3:
                    editGlyc.putFloat("glucoAvantRepas3", (float) glucideAvantRepas);
                    editGlyc.putFloat("unitInject3", (float) unitInject);
                    editGlyc.putFloat("confirmDose3", (float) confirmDose);
                    editGlyc.putString("aliments3", alimentsDuJour);
                    editGlyc.putFloat("gluco3", ((float) resultat));
                    break;
            }
            editGlyc.commit();

            String res = "تم تأكيد نتائجك";
            Toast.makeText(ShowCalculActivity.this, res, Toast.LENGTH_LONG).show();

            int page = idx;
            Intent intent = new Intent(context, GlycemiesActivity3.class);
            intent.putExtra("fragment", page);// One is your argument
            startNotificationAfter4Hours(page);
            startActivity(intent);
            finish();
        }
    }

    public void startNotificationAfter4Hours(int page) {
        Timber.i("Start notification after 4 Hours");
        final int REQUEST_CODE = 123;
        long alarmTime = System.currentTimeMillis() + (4 * 60 * 60 * 1000);

        Intent intent = new Intent(context, OnetimeAlarmReceiver.class);
        intent.putExtra("fragment", page);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

        Timber.i("alarmTime : " + alarmTime);
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
