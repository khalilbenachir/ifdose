package ma.ifdose.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.RadioGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String JSON_ARRAY = "categories";
    public static final String JSON_ARRAY_2 = "aliments";
    private final static String TAG = "CalculActivity";
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    boolean mItemPressed = false;
    boolean mSwiping = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    Network network;
    ListView listView;
    JSONObject jsonApi;
    SharedPreferences.Editor editGlyc;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private Aliment food = null;
    private String category_id;
    private Spinner categoriesSpinner, UniteesSpinner,
            mealSP, calculTypeSP, activitePhysiqueSP;
    private AutoCompleteTextView alimentsAutoComp;
    private TextView categoryFoodTV, foodTV, quantiteGramTV, listFoodTV, tvGlucide;
    private Map<String, Integer> map;
    private Map<String, Integer> map1;
    private String JsonCatURL;
    private String JsonFoodsURL = "";
    private String JsonFoodURL;
    private ArrayList<Aliment> foodsSelected;
    private ArrayList<String> categories;
    private ArrayList<String> aliments;
    private JSONArray result;
    private EditText qauntite, glucideET;
    private Button btnAddAliment, btnCalcul;
    private RadioGroup radioButtonGroup;
    private EditText glucideAvantRepas;
    private selectedAdapter adapter;
    private RequestQueue queue;
    private SharedPreferences spGlycemies;
    private SharedPreferences sp;
    private String host, port;
    private int idx = 0;
    private StringBuilder alimentsDuJour;
    private DaoSession daoSession;
    private AlimentDao alimentDao;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            listView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        listView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(listView, v);
                                        } else {
                                            mSwiping = false;
                                            listView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };
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
        foodsSelected = new ArrayList<Aliment>();
        categories = new ArrayList<String>();
        aliments = new ArrayList<String>();
        JsonCatURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlCategories);
        queue = HttpSingleton.getInstance(this.getBaseContext()).getRequestQueue();
        queue.start();
        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        editGlyc = spGlycemies.edit();
        glucideAvantRepas = (EditText) findViewById(ma.ifdose.app.R.id.glucoAvantRepas);
        listView = (ListView) findViewById(ma.ifdose.app.R.id.listFood);
        btnAddAliment = (Button) findViewById(ma.ifdose.app.R.id.buttonAdd);
        btnCalcul = (Button) findViewById(ma.ifdose.app.R.id.buttontCalcul);
        categoriesSpinner = (Spinner) findViewById(ma.ifdose.app.R.id.categoryFood);
        alimentsAutoComp = (AutoCompleteTextView) findViewById(ma.ifdose.app.R.id.food);
        UniteesSpinner = (Spinner) findViewById(ma.ifdose.app.R.id.unite);
        activitePhysiqueSP = (Spinner) findViewById(ma.ifdose.app.R.id.spActivitePhysique);
        mealSP = (Spinner) findViewById(ma.ifdose.app.R.id.mealSP);
        calculTypeSP = (Spinner) findViewById(ma.ifdose.app.R.id.calculTypeSP);
        qauntite = (EditText) findViewById(ma.ifdose.app.R.id.quantiteGram);
        glucideET = (EditText) findViewById(ma.ifdose.app.R.id.glucideET);
        categoryFoodTV = (TextView) findViewById(ma.ifdose.app.R.id.categoryFoodTV);
        foodTV = (TextView) findViewById(ma.ifdose.app.R.id.foodTV);
        quantiteGramTV = (TextView) findViewById(ma.ifdose.app.R.id.quantiteGramTV);
        listFoodTV = (TextView) findViewById(ma.ifdose.app.R.id.listFoodTV);
        tvGlucide = (TextView) findViewById(ma.ifdose.app.R.id.tvGlucide);
        alimentsDuJour = new StringBuilder();
        ImageView dropDownArrow2;
        dropDownArrow2 = (ImageView) findViewById(R.id.dropDownImage2);
        alimentsAutoComp.setThreshold(1);



        // get the note DAO
        daoSession = ((App) getApplication()).getDaoSession();
        alimentDao = daoSession.getAlimentDao();

        dropDownArrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alimentsAutoComp.showDropDown();
            }
        });

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });

        // affichage suivant le type de calcul
        calculTypeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] calculTypes = getResources().getStringArray(ma.ifdose.app.R.array.calcul_type);
                if (parent.getSelectedItem().toString().equals(calculTypes[0])) {

                    Timber.i("avec livret");
                    Toast.makeText(getBaseContext(), "avec livret", Toast.LENGTH_SHORT).show();

                    categoryFoodTV.setVisibility(View.VISIBLE);
                    foodTV.setVisibility(View.VISIBLE);
                    quantiteGramTV.setVisibility(View.VISIBLE);
                    categoriesSpinner.setVisibility(View.VISIBLE);
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

                    categoryFoodTV.setVisibility(View.GONE);
                    foodTV.setVisibility(View.GONE);
                    quantiteGramTV.setVisibility(View.GONE);
                    categoriesSpinner.setVisibility(View.GONE);
                    alimentsAutoComp.setVisibility(View.GONE);
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
        adapter = new selectedAdapter(foodsSelected, context, mTouchListener);
        listView.setAdapter(adapter);
        calculTypeSP.setSelection(0, true);
    }

    public void addAliment(View view) {
        if ((qauntite.getText().toString().trim().equals(""))) {
            Toast.makeText(context, "المرجو ادخال الكمية", Toast.LENGTH_SHORT).show();
        } else {
            JsonFoodURL = host + ":" + port + getString(ma.ifdose.app.R.string.urlAlim);
            food = new Aliment();
            String name = alimentsAutoComp.getText().toString();
            String unite = UniteesSpinner.getSelectedItem().toString();
            String q = String.valueOf(qauntite.getText());
            int id = map1.get(name);
            food.setNom(name);
            food.setQuantiteA(q + " " + unite);
            if (id != -1) {
                JsonFoodURL += id;
                getSelectedItem(unite, name, q);
//                foodsSelected.add(food);
//                if ( quantiteBisNull && (unite.equals("قطعة") || unite.equals("طبق")
//                        || unite.equals("كاس"))) {
//                    Toast.makeText(context, "المرجو إدخال الوحدة المناسبة", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Timber.i("QuantiteA : "+food.getQuantiteA());
//                Timber.i("QuantiteB : "+food.getQuantiteB());
            } else {
                List<ma.ifdose.app.db.Aliment> al = alimentDao
                        .queryBuilder().where(AlimentDao.Properties.Name.eq(name)).list();
                ma.ifdose.app.db.Aliment al1 = al.get(0);
                food.setQuantiteB(al1.getQuantite());
                food.setGlucide(al1.getGlucide());
                foodsSelected.add(food);
                setListViewHeightBasedOnChildren(listView);
                alimentsDuJour.append(name + " : " + q + " " + unite + ";");
            }

        }
    }

    public void calcul(View view) {
        double g = 0;
        if ((glucideAvantRepas.getText().toString().trim().equals(""))) {
            Toast.makeText(context, "المرجو ادخال نسبة الجلوكوز قبل الوجبة ", Toast.LENGTH_SHORT)
                    .show();
        } else {
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
                            result = j.getJSONArray(JSON_ARRAY);
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
            } catch (JSONException e) {
                Timber.e(e.getMessage());
            }
        }
        //Setting adapter to show the items in the spinner
        categoriesSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categories));
    }

    private void getAliments() {
        StringRequest stringRequest = new StringRequest(JsonFoodsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {
                    //Parsing the fetched Json String to JSON Object
                    j = new JSONObject(response);
                    //Storing the Array of JSON String to our JSON Array
                    result = j.getJSONArray(JSON_ARRAY_2);
                    getAlimentsList(result);
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

    private void getAlimentsList(JSONArray j) {
        try {
            aliments.clear();
            map1.clear();

            for (int i = 0; i < j.length(); i++) {
                JSONObject json = j.getJSONObject(i);
                map1.put(json.getString(KEY_NAME), json.getInt(KEY_ID));
                aliments.add(json.getString(KEY_NAME));
            }

            List<ma.ifdose.app.db.Aliment> alimentsDB = alimentDao
                    .queryBuilder()
                    .where(AlimentDao.Properties.Category_id.eq(category_id))
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

    private void getSelectedItem(final String unite, final String name, final String q) {
        final RequestQueue queue = Volley.newRequestQueue(context);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, JsonFoodURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray foodsApi = response.getJSONArray("aliments");
                            jsonApi = foodsApi.getJSONObject(0);
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
                            foodsSelected.add(food);
                            setListViewHeightBasedOnChildren(listView);
                            alimentsDuJour.append(name + " : " + q + " " + unite + ";");
                            Timber.i("QuantiteA : " + food.getQuantiteA());
                            Timber.i("QuantiteB : " + food.getQuantiteB());
                        } catch (JSONException e) {
                            Timber.i(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e("Volly Error", error.toString());
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Timber.e("Status code : " + String.valueOf(networkResponse.statusCode));
                        }
                    }
                });
        queue.add(jsObjRequest);
    }

    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i <= listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = adapter.getItemId(position);
                if (child != null) mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = listView.getPositionForView(viewToRemove);
        Log.i("pos", String.valueOf(position));
        adapter.remove(adapter.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        listView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {

                                    mSwiping = false;
                                    listView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        selectedAdapter listAdapter = (selectedAdapter) this.listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
