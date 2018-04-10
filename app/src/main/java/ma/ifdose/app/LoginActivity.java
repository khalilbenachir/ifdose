


package ma.ifdose.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ma.ifdose.app.Singleton.HttpSingleton;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static double rd = 1, rp = 1, rc = 1, rdi = 1, is = 1, obj = 1;
    private static String firstName, lastName;
    private static String host, port;
    SharedPreferences.Editor edit;
    Intent intent;
    JSONArray patients;
    SharedPreferences sp;
    SharedPreferences spGlycemies;
    @Bind(ma.ifdose.app.R.id._passwordText)
    EditText _passwordText;
    @Bind(ma.ifdose.app.R.id.sign)
    Button _loginButton;
    private Context context;
    private Cache cache;
    private Network network;
    private String id;
    private String pass;
    private String url;
    private boolean previouslyConnected = false;
    private boolean connected = false;
    private boolean passwordError = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_login);

        context = getApplicationContext();
        cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        network = new BasicNetwork(new HurlStack());

        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        edit = sp.edit();
        ButterKnife.bind(this);

        host = sp.getString("host_url", getString(ma.ifdose.app.R.string.host_adr));
        port = sp.getString("host_port", getString(ma.ifdose.app.R.string.host_port));
        url = host + ":" + port + getString(ma.ifdose.app.R.string.urlLogin);

        if (!sp.contains(getString(ma.ifdose.app.R.string.pref_previously_started))) {
            edit.putBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), Boolean.FALSE);
            edit.commit();
        }

        previouslyConnected = sp.getBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), false);
        if (!previouslyConnected) {
            _loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
        }

        Log.i(TAG, "previouslyConnected : " + previouslyConnected);

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

    public void login() {
        Timber.i("تسجيل الدخول");
        passwordError = false;
        intent = new Intent(this, WelcomeActivity.class);
        pass = _passwordText.getText().toString();
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url + pass, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            patients = response.getJSONArray("patients");

                            if (patients != null) {
                                Timber.i("JSON patients object length : " + patients.length());
                            }

                            if (patients.length() >= 1 && patients.getJSONObject(0) != null) {
                                JSONObject patient = patients.getJSONObject(0);
                                connected = true;
                                edit.putBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), Boolean.TRUE);

                                id = patient.getString("id");
                                firstName = patient.getString("nom");
                                lastName = patient.getString("prenom");

                                if (!patient.isNull("ratioPetitDej"))
                                    rp = patient.getDouble("ratioPetitDej");
                                if (!patient.isNull("ratioDej"))
                                    rd = patient.getDouble("ratioDej");
                                if (!patient.isNull("ratioColl"))
                                    rc = patient.getDouble("ratioColl");
                                if (!patient.isNull("ratioDinnez"))
                                    rdi = patient.getDouble("ratioDinnez");
                                if (!patient.isNull("IndiceSensibilite"))
                                    is = patient.getDouble("IndiceSensibilite");
                                if (!patient.isNull("Objectif"))
                                    obj = patient.getDouble("Objectif");

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
                                edit.putString("pass", pass);

                                edit.commit();
                                startActivity(intent);
                            } else {
                                edit.putBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), Boolean.FALSE);
                                edit.commit();
                            }

                        } catch (JSONException e) {
                            Timber.e("JSON Error : " + e.getMessage());
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e("Volly Error : " + error.toString());

                        edit.putBoolean(getString(ma.ifdose.app.R.string.pref_previously_started), Boolean.FALSE);
                        edit.commit();

                        String message = null;

                        if (error instanceof NetworkError) {
                            message = "Pas de connexion internet...veuillez verifier votre connexion!";
                        } else if (error instanceof ServerError) {
                            message = "Serveur introuvable. veuillez reessayer après!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Erreur d'authentification!";
                        } else if (error instanceof ParseError) {
                            message = "Mot de passe incorrect!";
                            passwordError = true;
                        } else if (error instanceof NoConnectionError) {
                            message = "Pas de connexion internet...veuillez verifier votre connexion!";
                        } else if (error instanceof TimeoutError) {
                            message = "Délai de connection dépassé!...veuillez verifier votre connexion!";
                        }

//                        NetworkResponse networkResponse = error.networkResponse;
//                        if (networkResponse != null) {
//                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
//                            CharSequence msg = networkResponse.toString();

                        CharSequence msg = message + "" + error.getMessage();
//                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        showErrorAlert(LoginActivity.this, message);

                        Timber.e(msg.toString());
//                        }
                    }

                });

        HttpSingleton.getInstance(context).addToRequestQueue(jsObjRequest);

        if (!validate()) {
            onNotValidInput();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ma.ifdose.app.R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("مصادقة ...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (connected)
                            onLoginSuccess();
                        else
                            onLoginFail();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "تم التسجيل بنجاح", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public void onLoginFail() {
        _loginButton.setEnabled(true);
        _passwordText.setError("كلمة المرور غير صحيحة");
    }

    public void onNotValidInput() {
        Toast.makeText(getBaseContext(), "فشل تسجيل الدخول", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString();

        if (password.isEmpty() || password.length() != 8) {
            _passwordText.setError("8 أحرف أبجدية رقمية");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void showErrorAlert(Context con, String msg) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(con).create();

            alertDialog.setTitle(getString(ma.ifdose.app.R.string.alert_msg));
            alertDialog.setMessage(msg);
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton(Dialog.BUTTON_NEUTRAL, getString(ma.ifdose.app.R.string.alert_btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            if (!passwordError)
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(ma.ifdose.app.R.string.alert_btn_parametres),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                            }
                        });

            alertDialog.show();
        } catch (Exception e) {
            Timber.e("Show Dialog: " + e.getMessage());
        }
    }
}


