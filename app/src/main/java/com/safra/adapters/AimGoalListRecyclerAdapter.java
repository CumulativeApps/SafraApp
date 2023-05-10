package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.safra.R;

import java.util.List;

public class AimGoalListRecyclerAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    public AimGoalListRecyclerAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        TextInputEditText editText = convertView.findViewById(R.id.edit_text);
        editText.setText(getItem(position));

        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            data.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
