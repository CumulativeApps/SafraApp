package com.safra.interfaces;

import android.app.Activity;

import com.safra.models.formElements.BaseFormElement;

public interface BaseViewHolderInterface {

    FormElementEditTextListener getListener();

    void bind(Activity context, BaseFormElement baseFormElement);

}
