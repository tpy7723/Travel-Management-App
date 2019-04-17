package com.example.pks01.bottom_navi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class First_Fragment extends Fragment {
    static ListView listview;
    static ListViewAdapter adapter;
    static List<DB_Item> db_itemData;

    Context context_;

    public First_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context_ = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        db_itemData = MainActivity.dbHelper.getAllDB_ItemData();
        adapter = new ListViewAdapter(db_itemData, context_);
        listview.setAdapter(adapter);

        Button button = (Button) view.findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.dbHelper.removeAllDB_Item();
                First_Fragment.db_itemData = MainActivity.dbHelper.getAllDB_ItemData();
                First_Fragment.adapter = new ListViewAdapter(First_Fragment.db_itemData, context_);
                First_Fragment.listview.setAdapter(First_Fragment.adapter);
                // do something
            }
        });
        return view;
    }

}
