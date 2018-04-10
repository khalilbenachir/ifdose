package ma.ifdose.app.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import ma.ifdose.app.R;

public class OneFragment extends Fragment{
    private static final String TAG = "OneFragment";
    private TextView t1 , t2 , t3 ;
    private EditText t4 ,t5;
    private Button btnSave,btnSendData;
    private String t ,tt;
    private Context context ;
    private SharedPreferences spGlycemies ;
    private String url ;
    private RequestQueue queue ;
    private SharedPreferences.Editor edit;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity() ;
        spGlycemies = context.getSharedPreferences("glycemies", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        btnSave = (Button) rootView.findViewById(R.id.sauvegard);
        t1 = (TextView) rootView.findViewById(R.id.t1);
        t4 = (EditText) rootView.findViewById(R.id.t2);
        t2 = (TextView) rootView.findViewById(R.id.t3);
        t3 = (TextView) rootView.findViewById(R.id.t4);
        t5 = (EditText) rootView.findViewById(R.id.tc);
        edit = spGlycemies.edit();

        Float r1 = spGlycemies.getFloat("GlycAp0", 0);
        t = String.valueOf(r1);
        t4.setText(t);

        Float r2 = spGlycemies.getFloat("unitInject0", 0);
        t2.setText(String.valueOf(r2));

        Float r3 = spGlycemies.getFloat("glucoAvantRepas0", 0);
        t1.setText(String.valueOf(r3));

        Float r4 = spGlycemies.getFloat("confirmDose0", 0);
        t3.setText(String.valueOf(r4));

        String r5 = spGlycemies.getString("comment1", "");
        t5.setText(r5);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res;
                String t = String.valueOf(t4.getText());
                if (t.trim().equals("")) {
                    res = " المرجو ادخال الجلوكوز بعد 4 ساعات عن الوجبة : ";
                    Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                } else {
                    res = " حفظ ناجح ";
                    Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                    float GlycAp = Float.valueOf(t);
                    edit.putFloat("GlycAp0", GlycAp);
                    edit.apply();

                    edit.putString("comment1",t5.getText().toString());
                    edit.apply();

                }
            }
        });

        return rootView ;

    }


}