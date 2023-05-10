package com.safra.interfaces;

import android.util.Log;

public interface CascadeOptionChanges {

    void onOptionEnableDisable(boolean isEnable);

    default void onOptionRemove(int position){
        Log.e("CascadeOptionChanges", "onOptionRemove: " + position);
    }

}
