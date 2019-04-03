package ma.ifdose.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.greenrobot.greendao.annotation.Id;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.ifdose.app.ListAdapter.selectedAdapter;
import ma.ifdose.app.Models.Aliment;
import ma.ifdose.app.Singleton.HttpSingleton;
import ma.ifdose.app.db.AlimentDao;
import ma.ifdose.app.db.DaoSession;
import timber.log.Timber;


public class CalculActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public int count;
    public static final String JSON_ARRAY = "categories";
    public static final String JSON_ARRAY_2 = "aliments";
    private final static String TAG = "CalculActivity";
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    boolean mItemPressed = false;
    boolean mSwiping = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    Network network;
    SwipeMenuListView listView;
    JSONObject jsonApi;
    SharedPreferences.Editor editGlyc, edit;
    private Context context;
    private Aliment food = null;
//    private String category_id;
    private Spinner UniteesSpinner,
            mealSP, calculTypeSP, activitePhysiqueSP,activitePhysiqueSP2;
    private AutoCompleteTextView alimentsAutoComp;
    private TextView categoryFoodTV, foodTV, quantiteGramTV, listFoodTV, tvGlucide;
    private Map<String, Integer> map;
    private Map<String, Integer> map1;
    private String JsonCatURL;
    private String JsonFoodsURL = "";
    private String JsonFoodURL;
    private ArrayList<String> foodsSelected;
    private ArrayList<String> categories;
    private ArrayList<String> aliments;
    private JSONArray result;
    private EditText qauntite, glucideET;
    private Button btnAddAliment, btnCalcul;
    private RadioGroup radioButtonGroup;
    private EditText glucideAvantRepas;
    private ArrayAdapter adapter;
    private RequestQueue queue;
    private SharedPreferences spGlycemies;
    private SharedPreferences sp;
    private String host, port;
    private int idx = 0;
    private StringBuilder alimentsDuJour;
    private DaoSession daoSession;
    private AlimentDao alimentDao;
    private String name, unite,q;
    private int id;

    private boolean quantiteBisNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_calcul);
        context = getBaseContext();
        network = new BasicNetwork(new HurlStack());
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        host = sp.getString("host_url", getString(ma.ifdose.app.R.string.host_adr));
        port = sp.getString("host_port", getString(ma.ifdose.app.R.string.host_port));
        map = new HashMap<String, Integer>();
        map1 = new HashMap<String, Integer>();
        foodsSelected = new ArrayList<>();
        categories = new ArrayList<String>();
        aliments = new ArrayList<String>();
        JsonCatURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlCategories);
        queue = HttpSingleton.getInstance(this.getBaseContext()).getRequestQueue();
        queue.start();
        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        edit = sp.edit();
        editGlyc = spGlycemies.edit();
        glucideAvantRepas = (EditText) findViewById(ma.ifdose.app.R.id.glucoAvantRepas);
        listView = (SwipeMenuListView) findViewById(ma.ifdose.app.R.id.listFood);
        btnAddAliment = (Button) findViewById(ma.ifdose.app.R.id.buttonAdd);
        btnCalcul = (Button) findViewById(ma.ifdose.app.R.id.buttontCalcul);
        //activitePhysiqueSP2 = (Spinner) findViewById(ma.ifdose.app.R.id.spActivitePhysique2);
        alimentsAutoComp = (AutoCompleteTextView) findViewById(ma.ifdose.app.R.id.food);
        UniteesSpinner = (Spinner) findViewById(ma.ifdose.app.R.id.unite);
        activitePhysiqueSP = (Spinner) findViewById(ma.ifdose.app.R.id.spActivitePhysique);
        mealSP = (Spinner) findViewById(ma.ifdose.app.R.id.mealSP);
        calculTypeSP = (Spinner) findViewById(ma.ifdose.app.R.id.calculTypeSP);
        qauntite = (EditText) findViewById(ma.ifdose.app.R.id.quantiteGram);
        glucideET = (EditText) findViewById(ma.ifdose.app.R.id.glucideET);
//        categoryFoodTV = (TextView) findViewById(ma.ifdose.app.R.id.categoryFoodTV);
        foodTV = (TextView) findViewById(ma.ifdose.app.R.id.foodTV);
        quantiteGramTV = (TextView) findViewById(ma.ifdose.app.R.id.quantiteGramTV);
        listFoodTV = (TextView) findViewById(ma.ifdose.app.R.id.listFoodTV);
        tvGlucide = (TextView) findViewById(ma.ifdose.app.R.id.tvGlucide);
        alimentsDuJour = new StringBuilder();
        final ImageView dropDownArrow2;
       dropDownArrow2 = (ImageView) findViewById(R.id.dropDownImage2);
        //getAliments();
        getCategories();
        alimentsAutoComp.setThreshold(1);
        count =sp.getInt("count",0);
        final PopupMenu popupMenu=new PopupMenu(this, dropDownArrow2);

        // get the note DAO
        daoSession = ((App) getApplication()).getDaoSession();
        alimentDao = daoSession.getAlimentDao();
        JsonFoodsURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlAlims);

       dropDownArrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0; i < categories.size(); i++)
                    popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, categories.get(i));


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        alimentsAutoComp.setText(item.getTitle());
                        return true;
                    }
                });


                popupMenu.show();
            }
        });


        /*categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JsonFoodsURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlAlims);
                switch (parent.getId()) {
                    case ma.ifdose.app.R.id.categoryFood:
                        String text = categoriesSpinner.getSelectedItem().toString();
                        category_id = String.valueOf(map.get(text));
                        JsonFoodsURL += category_id;
                        getAliments();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

        // affichage suivant le type de calcul
        calculTypeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] calculTypes = getResources().getStringArray(ma.ifdose.app.R.array.calcul_type);
                if (parent.getSelectedItem().toString().equals(calculTypes[0])) {

                    Timber.i("avec livret");
                    Toast.makeText(getBaseContext(), "avec livret", Toast.LENGTH_SHORT).show();

//                    categoryFoodTV.setVisibility(View.VISIBLE);
                    foodTV.setVisibility(View.VISIBLE);
                    quantiteGramTV.setVisibility(View.VISIBLE);
//                    categoriesSpinner.setVisibility(View.VISIBLE);
                    alimentsAutoComp.setVisibility(View.VISIBLE);
                    UniteesSpinner.setVisibility(View.VISIBLE);
                    qauntite.setVisibility(View.VISIBLE);
                    UniteesSpinner.setVisibility(View.VISIBLE);
                    btnAddAliment.setVisibility(View.VISIBLE);
                    listFoodTV.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);

                    tvGlucide.setVisibility(View.GONE);
                    glucideET.setVisibility(View.GONE);

                } else if (parent.getSelectedItem().toString().equals(calculTypes[1])) {

                    Timber.i("sans livret");
                    Toast.makeText(getBaseContext(), "sans livret", Toast.LENGTH_SHORT).show();

//                    categoryFoodTV.setVisibility(View.GONE);
                    foodTV.setVisibility(View.GONE);
                    quantiteGramTV.setVisibility(View.GONE);
//                    categoriesSpinner.setVisibility(View.GONE);
                    alimentsAutoComp.setVisibility(View.GONE);
                //    dropDownArrow2.setVisibility(View.GONE);
                    UniteesSpinner.setVisibility(View.GONE);
                    qauntite.setVisibility(View.GONE);
                    UniteesSpinner.setVisibility(View.GONE);
                    btnAddAliment.setVisibility(View.GONE);
                    listFoodTV.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);

                    tvGlucide.setVisibility(View.VISIBLE);
                    glucideET.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        getCategories();
        //adapter = new selectedAdapter(foodsSelected, context, mTouchListener);
        //foodsSelected.add(new Aliment("Khalil","Khalil",11,11));
        //foodsSelected.add(new Aliment("Khalil","Khalil",11,11));

        adapter=new ArrayAdapter(CalculActivity.this,android.R.layout.simple_list_item_1,foodsSelected);

        listView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0:
                        Log.i("Log","delete");
                        foodsSelected.remove(position);
                        adapter.notifyDataSetChanged();
                        break;

                }
                // true : close the menu; false : not close the menu
                return true;
            }
        });
        calculTypeSP.setSelection(0, true);
    }





    public void addAliment(View view) {
        //getAliments();
        if ((qauntite.getText().toString().trim().equals(""))) {
            Toast.makeText(context, "المرجو ادخال الكمية", Toast.LENGTH_SHORT).show();
        } else {
            id=-1;
            JsonFoodURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlAlim);
            food = new Aliment();
            name = alimentsAutoComp.getText().toString();
            unite = UniteesSpinner.getSelectedItem().toString();
            q = String.valueOf(qauntite.getText());
            System.out.println("***************ID*********"+"***********"+map.get(name));
            for(String index:map.keySet()){
                if(name.equals(index)){
                    id=map.get(index);
                }

            }
            System.out.println("***************ID*********"+ id);
            Log.i("Id",String.valueOf(id));

            food.setNom(name);
            food.setQuantiteA(q + " " + unite);
            if (id != -1) {
                JsonFoodURL += id;
                getSelectedItem(unite, name, q);
                System.out.println("DataBase**************");


//                foodsSelected.add(food);
//                if ( quantiteBisNull && (unite.equals("قطعة") || unite.equals("طبق")
//                        || unite.equals("كاس"))) {
//                    Toast.makeText(context, "المرجو إدخال الوحدة المناسبة", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Timber.i("QuantiteA : "+food.getQuantiteA());
//                Timber.i("QuantiteB : "+food.getQuantiteB());
            } else {
                ma.ifdose.app.db.Aliment aliment=new ma.ifdose.app.db.Aliment();
                aliment.setName(food.getNom());
                aliment.setGlucide(food.getGlucide());
                aliment.setQuantite(food.getQuantiteB());
                alimentDao.insert(aliment);
                System.out.println("DataBase*********DONE*****");
                //ma.ifdose.app.db.Aliment al1 = al.get(0);
                //food.setQuantiteB(al1.getQuantite());
                //food.setGlucide(al1.getGlucide());
                foodsSelected.add(food.getNom());
                System.out.println("******foods******"+foodsSelected+"**************");
                //adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listView);
                alimentsDuJour.append(name + " : " + q + " " + unite + ";");
                Toast.makeText(context, "لقد تمت الإضافة", Toast.LENGTH_SHORT).show();


            }
            alimentsAutoComp.setText("");
            qauntite.setText("");

        }
    }



    public void calcul(View view) {
        double g = 0;
        if ((glucideAvantRepas.getText().toString().trim().equals(""))) {
            Toast.makeText(context, "المرجو ادخال نسبة الجلوكوز قبل الوجبة ", Toast.LENGTH_SHORT)
                    .show();
        } else {
            count = count + 1;
            edit.putInt("count", count);
            edit.commit();
            Timber.i("nombre de visite :"+count);
            g = Double.parseDouble(String.valueOf(glucideAvantRepas.getText()));
            Intent i = new Intent(context, ShowCalculActivity.class);
            i.putExtra("glucoAvantRepas", g);
            idx = getIdx();
            i.putExtra("idx", idx);

            String activitePhysique = activitePhysiqueSP.getSelectedItem().toString();
            Timber.i("activitePhysique : " + activitePhysique);
            i.putExtra("activitePhysique", activitePhysique);

            String calculType = calculTypeSP.getSelectedItem().toString();
            String calculTypes[] = getResources().getStringArray(ma.ifdose.app.R.array.calcul_type);

            if (calculType.equals(calculTypes[0])) {
                i.putExtra("livret", "avec");
                Bundle args = new Bundle();
                args.putSerializable("Array", foodsSelected);
                i.putExtra("Aliments", args);
                i.putExtra("alimentsDuJour", alimentsDuJour.toString());
            } else if (calculType.equals(calculTypes[1])) {
                if ((glucideET.getText().toString().trim().equals(""))) {
                    Toast.makeText(context, "المرجو ادخال كمية الجلوكوز", Toast.LENGTH_SHORT).show();
                } else {
                    i.putExtra("livret", "sans");
                    String glucoTotal = glucideET.getText().toString();
                    i.putExtra("glucoTotal", Double.parseDouble(glucoTotal));
                }
            }
            startActivity(i);
            finish();
        }
    }

    private int getIdx() {

        String selectedMeal = mealSP.getSelectedItem().toString();
        String[] meals = getResources().getStringArray(ma.ifdose.app.R.array.meal_type);

        if (selectedMeal.equals(meals[0])) {
            return 0;
        } else if (selectedMeal.equals(meals[1])) {
            return 1;
        } else if (selectedMeal.equals(meals[2])) {
            return 2;
        } else if (selectedMeal.equals(meals[3])) {
            return 3;
        }

        return 0;
    }


    private void getCategories() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(JsonCatURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(JSON_ARRAY_2);
                            getCategoriesList(result);
                        } catch (JSONException e) {
                            Timber.e(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e(error.toString());
                    }
                });
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void getCategoriesList(JSONArray j) {
        JSONObject jsn = new JSONObject();
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                map.put(json.getString(KEY_NAME), json.getInt(KEY_ID));
                categories.add(json.getString(KEY_NAME));
                ma.ifdose.app.db.Aliment aliment =new ma.ifdose.app.db.Aliment();
                aliment.setName(json.getString(KEY_NAME));
                aliment.setGlucide(json.getDouble("glucide"));
                aliment.setQuantite(json.getDouble("quantite"));
                aliment.setCategory_id(json.getLong("category_id"));
                alimentDao.insert(aliment);

                System.out.println(categories);

            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }
        }
        //Setting adapter to show the items in the spinner

        //ArrayAdapter<String> adapterSpinner=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        //activitePhysiqueSP2.setAdapter(adapterSpinner);
        //System.out.println("Spinnner ------------*********");

    }


/*
    private void getAliments() {
        System.out.println("*******************TEST*****************"+map1);
        StringRequest stringRequest = new StringRequest(JsonFoodsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {
                    //Parsing the fetched Json String to JSON Object
                    j = new JSONObject(response);
                    //Storing the Array of JSON String to our JSON Array
                    result = j.getJSONArray(JSON_ARRAY_2);
                    System.out.println("*******************TEST*****************"+result);
                    getAlimentsList(result);
                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .putString("foodJson",j.toString()).apply();

                } catch (JSONException e) {
                    Timber.e(e.getMessage());

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e(error.toString());
                        String jsonString = PreferenceManager.
                                getDefaultSharedPreferences(context).getString("foodJson","");
                        try{
                            JSONObject jsonObject = new JSONObject(jsonString);
                            if(!jsonObject.getJSONArray(JSON_ARRAY_2).equals(null)){
                                result = jsonObject.getJSONArray(JSON_ARRAY_2);
                                getAlimentsList(result);
                            }
                        }
                        catch (JSONException f){
                            Timber.e(f.getMessage());
                        }
                    }
                });
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void getAlimentsList(JSONArray j) {
        System.out.println("----------------Test---------");

        try {
 //           aliments.clear();
 //           map1.clear();

            for (int i = 0; i < j.length(); i++) {
                JSONObject json = j.getJSONObject(i);
                System.out.println("----------------Test---------"+json.getString(KEY_NAME));
                map1.put(json.getString(KEY_NAME), json.getInt(KEY_ID));
                aliments.add(json.getString(KEY_NAME));
            }

            List<ma.ifdose.app.db.Aliment> alimentsDB = alimentDao
                    .queryBuilder()
                    */
/*.where(AlimentDao.Properties.Category_id.eq(category_id))*//*

                    .list();

            Timber.i("Local aliments : " + alimentsDB.size());
            for (ma.ifdose.app.db.Aliment al : alimentsDB) {
                Timber.i("Local " + al.getName() + " - " + al.getCategory_id());
                map1.put(al.getName(), -1);
                aliments.add(al.getName());
            }

            //Setting adapter to show the items in the spinner
            alimentsAutoComp.setAdapter(new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, aliments));
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }
*/

    private void getSelectedItem(final String unite, final String name, final String q) {
        final RequestQueue queue = Volley.newRequestQueue(context);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, JsonFoodURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray foodsApi = response.getJSONArray("aliments");
                            jsonApi = foodsApi.getJSONObject(0);
                            addAlimentInstructions(jsonApi);
                        } catch (JSONException e) {
                            Timber.i(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String jsonString = PreferenceManager.
                                getDefaultSharedPreferences(context).getString("foodJson","");
                        try{
                            JSONObject jsonObject = new JSONObject(jsonString);
                            if(!jsonObject.getJSONArray(JSON_ARRAY_2).equals(null)){
                                result = jsonObject.getJSONArray(JSON_ARRAY_2);

                                jsonApi = getJSONObjectByID(result);
                                addAlimentInstructions(jsonApi);
                            }
                        }
                        catch(JSONException f){
                            Timber.e(f.getMessage());
                        }
                        /*
                        Timber.e("Volly Error", error.toString());
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Timber.e("Status code : " + String.valueOf(networkResponse.statusCode));
                        }
                        /*String jsonString = PreferenceManager.
                                getDefaultSharedPreferences(context).getString("foodJson","");
                        try{
                            JSONObject jsonObject = new JSONObject(jsonString);
                            if(!jsonObject.getJSONArray(JSON_ARRAY_2).equals(null)){
                                result = jsonObject.getJSONArray(JSON_ARRAY_2);
                                int id= map1.get(name);
                            }
                        }
                        catch (JSONException f){
                            Timber.e(f.getMessage());
                        }*/

                    }
                });
        queue.add(jsObjRequest);
    }

//increase listview height based on children

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if(listItem != null){
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    /* avertissement en cliquant retour */
    @Override
    public void onBackPressed() {
        if(listView.getAdapter().getCount()!=0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.onBackPressed_title)
                    .setMessage(R.string.onBackPressed_message)
                    .setNegativeButton("إلغاء", null)
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivity(new Intent(CalculActivity.this, WelcomeActivity.class));

                        }
                    }).create().show();

        }
        else {
            startActivity(new Intent(CalculActivity.this, WelcomeActivity.class));
        }
    }

    JSONObject getJSONObjectByID(JSONArray j){

        for(int i=0;i<j.length();i++) {
                try {
                    JSONObject jobj = j.getJSONObject(i);
                    int id = jobj.getInt("id");
                    if (id == map1.get(alimentsAutoComp.getText().toString())) {
                        return jobj;
                    }
                }
                catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        }
        return null;
    }
    void addAlimentInstructions(JSONObject jsonApi) {
        try{
        food.setGlucide(jsonApi.getDouble("glucide"));
        if (!jsonApi.isNull("quantite")) {
            food.setQuantiteB(jsonApi.getDouble("quantite"));
            Timber.i("JSON quantite " + jsonApi.getDouble("quantite"));
        } else {
            food.setQuantiteB(1);
            Timber.i("JSON quantite Null " + food.getQuantiteB());
            if (!(unite.equals("قطعة") || unite.equals("طبق")
                    || unite.equals("كاس"))) {
                Toast.makeText(context, "المرجو إدخال الوحدة المناسبة",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
        ma.ifdose.app.db.Aliment aliment=new ma.ifdose.app.db.Aliment();
        aliment.setName(food.getNom());
        aliment.setQuantite(food.getQuantiteB());
        aliment.setGlucide(food.getGlucide());
        aliment.setCategory_id((long) id);
        alimentDao.insert(aliment);
        System.out.println("*********INSERTED*******"+alimentDao.count());
        foodsSelected.add(food.getNom());
        setListViewHeightBasedOnChildren(listView);
        alimentsDuJour.append(name + " : " + q + " " + unite + ";");
        Timber.i("QuantiteA : " + food.getQuantiteA());
        Timber.i("QuantiteB : " + food.getQuantiteB());}

        catch(JSONException g){
            g.printStackTrace();
        }

    }



}
