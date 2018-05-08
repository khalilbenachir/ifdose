package ma.ifdose.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ma.ifdose.app.GenerateRapportActivity;
import ma.ifdose.app.GlycemiesActivity3;
import ma.ifdose.app.R;
import ma.ifdose.app.Singleton.HttpSingleton;


public class Fragment4 extends Fragment{

    private static final String TAG = "Fragment4";
    private String t ,tt;
    private TextView t1 , t2 , t3 ;
    private Button btnSave, btnSendData;
    private EditText t4 ,t5;
    private Context context;
    private SharedPreferences spGlycemies;
    private SharedPreferences.Editor edit;

    private String url;
    private RequestQueue queue;
    private SharedPreferences sp;

    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        queue = HttpSingleton.getInstance(context).getRequestQueue();
        queue.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_4, container, false) ;
        btnSave = (Button) rootView.findViewById(R.id.sauvegard);
        btnSendData = (Button) rootView.findViewById(R.id.printData);
        t1 = (TextView) rootView.findViewById(R.id.t1);
        t4=(EditText) rootView.findViewById(R.id.t2);
        t2= (TextView) rootView.findViewById(R.id.t3) ;
        t3= (TextView) rootView.findViewById(R.id.t4) ;
        t5=(EditText) rootView.findViewById(R.id.tc);
        edit=spGlycemies.edit();


        Float r1 = spGlycemies.getFloat("GlycAp3",0);
        t = String.valueOf(r1);
        t4.setText(t);

        Float r2 = spGlycemies.getFloat("unitInject3",0);
        t2.setText(String.valueOf(r2));

        Float r3 = spGlycemies.getFloat("glucoAvantRepas3",0);
        t1.setText(String.valueOf(r3));

        Float r4 = spGlycemies.getFloat("confirmDose3",0);
        t3.setText(String.valueOf(r4));

        String r5 = spGlycemies.getString("comment4", "");
        t5.setText(r5);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res ;
                String t = String.valueOf(t4.getText()) ;
                if(t.trim().equals("")){
                    res = " المرجو ادخال الجلوكوز بعد 4 ساعات عن الوجبة : " ;
                    Toast.makeText(context,res,Toast.LENGTH_LONG).show(); }
                else {
                    res = " حفظ ناجح ";
                    Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                    float GlycAp = Float.valueOf(t);
                    edit.putFloat("GlycAp3", GlycAp);
                    edit.apply();

                    edit.putString("comment4",t5.getText().toString());
                    edit.apply();
                }

            }});

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent i = new Intent(getActivity(), GenerateRapportActivity.class);
                 startActivity(i);
            }
        });
        return rootView ;

    }




    public void resetSPValues() {
        edit.remove("GlycAp3");
        edit.remove("unitInject3");
        edit.remove("glucoAvantRepas3");
        edit.remove("confirmDose3");
        edit.remove("GlycAp2");
        edit.remove("unitInject2");
        edit.remove("glucoAvantRepas2");
        edit.remove("confirmDose2");
        edit.remove("GlycAp1");
        edit.remove("unitInject1");
        edit.remove("glucoAvantRepas1");
        edit.remove("confirmDose1");
        edit.remove("GlycAp0");
        edit.remove("unitInject0");
        edit.remove("glucoAvantRepas0");
        edit.remove("confirmDose0");
        edit.apply();
    }

    private void postRapport() {
        Float r1 = spGlycemies.getFloat("GlycAp0", 0);
        Float r2 = spGlycemies.getFloat("unitInject0", 0);
        Float r3 = spGlycemies.getFloat("glucoAvantRepas0", 0);
        Float r4 = spGlycemies.getFloat("confirmDose0", 0);

        Float r5 = spGlycemies.getFloat("GlycAp1", 0);
        Float r6 = spGlycemies.getFloat("unitInject1", 0);
        Float r7 = spGlycemies.getFloat("glucoAvantRepas1", 0);
        Float r8 = spGlycemies.getFloat("confirmDose1", 0);

        Float r9 = spGlycemies.getFloat("GlycAp2", 0);
        Float r10 = spGlycemies.getFloat("unitInject2", 0);
        Float r11 = spGlycemies.getFloat("glucoAvantRepas2", 0);
        Float r12 = spGlycemies.getFloat("confirmDose2", 0);

        Float r13 = spGlycemies.getFloat("GlycAp3", 0);
        Float r14 = spGlycemies.getFloat("unitInject3", 0);
        Float r15 = spGlycemies.getFloat("glucoAvantRepas3", 0);
        Float r16 = spGlycemies.getFloat("confirmDose3", 0);

        String id = sp.getString("id", "0");
        String host = sp.getString("host_url", getString(R.string.host_adr));
        String port = sp.getString("host_port", getString(R.string.host_port));

        url = host + ":" + port
                + getString(R.string.urlPostRapport) + id + "&r1=" + r1 + "&r2=" + r2 + "&r3=" + r3
                + "&r4=" + r4 + "&r5=" + r5 + "&r6=" + r6 + "&r7=" + r7 + "&r8=" + r8 + "&r9=" + r9 + "&r10=" + r10
                + "&r11=" + r11 + "&r12=" + r12 + "&r13=" + r13 + "&r14=" + r14 + "&r15=" + r15 + "&r16=" + r16;

        Log.i(TAG, "Visiting : " + url);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String res = getString(R.string.report_info_sent_success);
                        Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                        resetSPValues();
                        Intent intent = new Intent(context, GlycemiesActivity3.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (error != null && error.getMessage() != null) {
                            String response = error.getMessage();
                            Log.e("Error.Response", response);
                            String res = getString(R.string.report_info_sent_fail);
                            Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        queue.add(postRequest);
    }




}
