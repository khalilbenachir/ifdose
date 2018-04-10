package ma.ifdose.app.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ma.ifdose.app.Models.Aliment;
import ma.ifdose.app.R;

public class selectedAdapter extends ArrayAdapter<Aliment> implements ListAdapter {
    View.OnTouchListener mTouchListener;
    private ArrayList<Aliment> list = new ArrayList<Aliment>();
    private Context context;

    public selectedAdapter(ArrayList<Aliment> list, Context context , View.OnTouchListener listener) {
        super(context, R.layout.list_item, list);
        this.list = list;
        this.context = context;
        this.mTouchListener = listener ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View v = convertView;
        if(v!=null)v.setOnTouchListener(mTouchListener);
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        Aliment a = getItem(position);
        if (a != null) {
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.list_item_string);
            viewHolder.txtQuantite = (TextView) convertView.findViewById(R.id.quantite);
            //Handle TextView and display string from your list
            viewHolder.txtName.setText(a.getNom());
            viewHolder.txtQuantite.setText(a.getQuantiteA());
            // Return the completed view to render on screen
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtQuantite;
    }
}