package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.ChildCategoryDbModel;

import java.util.ArrayList;
import java.util.HashMap;

public class CategorySpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> categories;
    TextView categoryName;
    View view;

    public CategorySpinnerAdapter(ArrayList<HashMap<String, String>> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.item_expandablerecycler_childcategory, null);
        categoryName = view.findViewById(R.id.spinner_item_txtViewCategoryName);
        categoryName.setText(categories.get(position).get(ChildCategoryDbModel.NAME));

        return view;
    }
}
