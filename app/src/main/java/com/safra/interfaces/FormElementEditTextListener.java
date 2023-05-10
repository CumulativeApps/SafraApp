package com.safra.interfaces;

import android.text.Editable;
import android.text.TextWatcher;

import com.safra.adapters.FormAdapter;
import com.safra.models.formElements.BaseFormElement;

public class FormElementEditTextListener implements TextWatcher {

    private int position;
    private final FormAdapter adapter;
//    private boolean allowNumberRange = false;

    public FormElementEditTextListener(FormAdapter adapter) {
        this.adapter = adapter;
    }

//    public FormElementEditTextListener(FormAdapter adapter, boolean allowNumberRange) {
//        this.adapter = adapter;
//        this.allowNumberRange = allowNumberRange;
//    }

    public void updatePosition(int position) {
        this.position = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if(!allowNumberRange) {
        BaseFormElement baseFormElement = adapter.getValueAtIndex(position);
        String currentValue = baseFormElement.getFieldValue();
        String newValue = s.toString();

        if (currentValue != null) {
            if (!currentValue.equals(newValue)) {
                baseFormElement.setFieldValue(newValue);
                if (adapter.getValueChangedListener() != null) {
                    adapter.getValueChangedListener().onValueChanged(baseFormElement);
                }
            }
        }
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {
//        if (allowNumberRange) {
//            Log.e("Listener", "afterTextChanged: allowed");
//            if (!s.toString().isEmpty()) {
//                BaseFormElement baseFormElement = adapter.getValueAtIndex(position);
//                String currentValue = baseFormElement.getFieldValue();
//                String newValue = s.toString();
//                int mn, mx;
//                try {
//                    if (!baseFormElement.getMin().isEmpty()) {
//                        mn = Integer.parseInt(baseFormElement.getMin());
//
//                        if (Integer.parseInt(newValue) < mn) {
//                            baseFormElement.setFieldErrorEnabled(true);
//                            baseFormElement.setFieldError("Number should be >= " + mn);
//                            Log.e("Min Error", "afterTextChanged: ");
//                        }
//                    }
//
//                    if (!baseFormElement.getMax().isEmpty()) {
//                        mx = Integer.parseInt(baseFormElement.getMax());
//
//                        if (Integer.parseInt(newValue) > mx) {
//                            baseFormElement.setFieldErrorEnabled(true);
//                            baseFormElement.setFieldError("Number should be <= " + mx);
//                            Log.e("Max Error", "afterTextChanged: ");
//                        }
//                    }
//
//                } catch (NumberFormatException nfe) {
//                    nfe.printStackTrace();
//                    baseFormElement.setFieldErrorEnabled(true);
//                    baseFormElement.setFieldError("Invalid number format");
//                } finally {
//                    if (currentValue != null) {
//                        if (!currentValue.equals(newValue)) {
//                            baseFormElement.setFieldValue(newValue);
//                            if (adapter.getValueChangedListener() != null) {
//                                adapter.getValueChangedListener().onValueChanged(baseFormElement);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
