package ma.ifdose.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import ma.ifdose.app.Models.User;
import ma.ifdose.app.Singleton.HttpSingleton;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1000;
    private static String host, port;
    private final String TAG = "SplashActivity";
    User user;
    private String pass;
    private String url;
    private RequestQueue queue;
    private Context context;
    private Cache cache;
    private Network network;
    private JSONArray patients;
    private SharedPreferences sp;
    private SharedPreferences spGlycemies;
    private SharedPreferences.Editor edit;
    private boolean previouslyStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_splash);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        edit = sp.edit();

        queue = HttpSingleton.getInstance(this.getBaseContext()).getRequestQueue();
        queue.start();

        previouslyStarted = sp.getBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), false);

        host = sp.getString("host_url", getString(ma.ifdose.app.R.string.host_adr));
        port = sp.getString("host_port", getString(ma.ifdose.app.R.string.host_port));
        url = host + ":" + port + getString(ma.ifdose.app.R.string.urlLogin);
        pass = sp.getString("pass", "");

        new ATask().execute();
    }

    private class ATask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (previouslyStarted) {
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                            url + pass, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                patients = response.getJSONArray("patients");

                                if (patients != null) {
//                                    Log.i(TAG, "JSON patients object length : " + patients.length());
                                    Timber.i("JSON patients object length : " + patients.length());
                                }

                                if (patients.length() >= 1 && patients.getJSONObject(0) != null) {
                                    JSONObject patient = patients.getJSONObject(0);

                                    String id = patient.getString("id");
                                    String firstName = patient.getString("nom");
                                    String lastName = patient.getString("prenom");
                                    double rp = patient.getDouble("ratioPetitDej");
                                    double rd = patient.getDouble("ratioDej");
                                    double rc = patient.getDouble("ratioColl");
                                    double rdi = patient.getDouble("ratioDinnez");
                                    double is = patient.getDouble("IndiceSensibilite");
                                    double obj = patient.getDouble("Objectif");

                                    edit.putString("nom", firstName);
                                    edit.putString("pren", lastName);
                                    edit.putFloat("rp", (float) rp);
                                    edit.putFloat("rd", (float) rd);
                                    edit.putFloat("rc", (float) rc);
                                    edit.putFloat("rdi", (float) rdi);
                                    edit.putFloat("rp", (float) rp);
                                    edit.putFloat("is", (float) is);
                                    edit.putFloat("obj", (float) obj);
                                    edit.putString("id", id);
                                    edit.apply();

//                                    Log.i(TAG, "Updated patient infos");
                                    Timber.i("Updated patient infos");
                                }

                            } catch (Exception e) {
//                                Log.e(TAG, e.getMessage());
                                Timber.e(e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Timber.e(error.toString());
                        }
                    });

                    queue.add(jsObjRequest);
                }
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Timber.i("previouslyStarted : " + previouslyStarted);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!previouslyStarted) {
                        edit.putString("host_adr", getString(ma.ifdose.app.R.string.host_adr));
                        edit.putString("host_port", getString(ma.ifdose.app.R.string.host_port));
                        edit.apply();
                        Timber.i("not logged in ):");
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    } else {
                        Timber.i("already logged in (:");
                        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    }
                }
            }, SPLASH_DURATION);
        }
    }

}