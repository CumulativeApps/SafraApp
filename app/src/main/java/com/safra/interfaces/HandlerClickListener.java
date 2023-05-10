package com.safra.interfaces;

import com.safra.models.formElements.BaseFormElement;

public interface HandlerClickListener {

    void openProperties(BaseFormElement baseFormElement, int position, int childPosition);

    void duplicateItem(BaseFormElement baseFormElement, int position);

    void deleteItem(BaseFormElement baseFormElement, int position);

}
