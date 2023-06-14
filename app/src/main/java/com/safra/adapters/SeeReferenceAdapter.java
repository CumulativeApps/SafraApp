package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.safra.R;
import com.safra.fragments.FormSettingsFragment;

import java.util.ArrayList;

public class SeeReferenceAdapter extends ArrayAdapter<FormSettingsFragment.ShipmentStatus> {

    public SeeReferenceAdapter(Context context, ArrayList<FormSettingsFragment.ShipmentStatus> shipmentStatusList) {
        super(context, 0, shipmentStatusList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_form_type, parent, false);
        }

        TextView statusTextView = view.findViewById(R.id.tvFormType);
        FormSettingsFragment.ShipmentStatus status = getItem(position);
        statusTextView.setText(status.getStatusName());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_form_type, parent, false);
        }

        TextView statusTextView = view.findViewById(R.id.tvFormType);
        FormSettingsFragment.ShipmentStatus status = getItem(position);
        statusTextView.setText(status.getStatusName());

        return view;
    }
}

