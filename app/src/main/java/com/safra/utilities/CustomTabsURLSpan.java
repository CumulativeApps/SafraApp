package com.safra.utilities;

import android.os.Parcel;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

public class CustomTabsURLSpan extends URLSpan {
    public CustomTabsURLSpan(String url) {
        super(url);
    }

    public CustomTabsURLSpan(@NonNull Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {
        String url = getURL();
        Log.e("TAG", "onClick: " + url);
        super.onClick(widget);
    }
}
