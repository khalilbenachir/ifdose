package ma.ifdose.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ma.ifdose.app.Singleton.HttpSingleton;
import ma.ifdose.app.db.Aliment;
import ma.ifdose.app.db.AlimentDao;
import ma.ifdose.app.db.DaoSession;
import timber.log.Timber;

public class AddMealActivity extends AppCompatActivity {
    public static final String JSON_ARRAY = "categories";
    public static final String KEY_NAME = "name";
    private final static String TAG = "AddMealActivity";
    private static final String KEY_ID ="id" ;
    HashMap<String, Integer> map;
    DaoSession daoSession;
    AlimentDao alimentDao;
    private Spinner categoriesSP;
    //    private Button btn ;
    private EditText q ;
    private EditText e ;
    private EditText glucoQtET;
    private JSONArray result;
    private String JsonCatURL ;
    private ArrayList<String> categories;
    private String category , name ;
    private double quantite, glucide;
    //    private String url ;
    private RequestQueue queue ;
    //    private String res ;
    private String host;
    private String port;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(ma.ifdose.app.R.layout.activity_add_meal);
            sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            host = sp.getString("host_url", getString(ma.ifdose.app.R.string.host_adr));
            port = sp.getString("host_port", getString(ma.ifdose.app.R.string.host_port));
            JsonCatURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlCategories);

            map = new HashMap<String, Integer>();
            categoriesSP = (Spinner) findViewById(ma.ifdose.app.R.id.foodCategory);
//        btn = (Button) findViewById(R.id.buttontCalcul);
            e = (EditText) findViewById(ma.ifdose.app.R.id.foodName);
            q = (EditText) findViewById(ma.ifdose.app.R.id.foodQuantity);
            glucoQtET = (EditText) findViewById(ma.ifdose.app.R.id.glucoQtET);
            categories = new ArrayList<String>();
            getCategories();
            queue = HttpSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
            queue.start();

            // get the note DAO
            daoSession = ((App) getApplication()).getDaoSession();
            alimentDao = daoSession.getAlimentDao();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    public void onClickAdd(View view){
        try {
            // retrieve input values
            category = categoriesSP.getSelectedItem().toString();
            quantite = Double.parseDouble(String.valueOf(q.getText()));
            glucide = Double.parseDouble(String.valueOf(glucoQtET.getText()));
            name = String.valueOf(e.getText()).trim();

            // verify if Aliment exist with the same name
            Aliment aliment = alimentDao.queryBuilder()
                    .where(AlimentDao.Properties.Name.eq(name))
                    .unique();

            if (aliment == null) {
                aliment = new Aliment();
            }

            aliment.setName(name);
            aliment.setGlucide(glucide);
            aliment.setQuantite(quantite);
            aliment.setCategory_id((long) map.get(category));
            alimentDao.insertOrReplace(aliment);
            Log.i(TAG, "Aliment inserted");
            addMealSuccess();

        } catch (Exception e) {
//            Log.e(TAG, "" + e.getMessage());
            Timber.e(e.getMessage());
            addMealFail();
        }
    }

    public void addMealSuccess() {
        Toast.makeText(getBaseContext(), getString(ma.ifdose.app.R.string.add_meal_success)
                , Toast.LENGTH_SHORT).show();

        // clear inputs
        e.setText("");
        q.setText("");
        glucoQtET.setText("");
    }

    public void addMealFail() {
        Toast.makeText(getBaseContext(), getString(ma.ifdose.app.R.string.add_meal_fail)
                , Toast.LENGTH_SHORT).show();
    }

    private void getCategories(){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(JsonCatURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray(JSON_ARRAY);

                            //Calling method getStudents to get the students from the JSON Array
                            getCategoriesList(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e(error.getMessage());
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getCategoriesList(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                map.put(json.getString( KEY_NAME), json.getInt(KEY_ID) );
                categories.add(json.getString( KEY_NAME));
            } catch (JSONException e) {
//                e.printStackTrace();
                Timber.e(e.getMessage());
            }
        }
        //Setting adapter to show the items in the spinner
        categoriesSP.setAdapter(new ArrayAdapter<String>(AddMealActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                categories));
    }
}