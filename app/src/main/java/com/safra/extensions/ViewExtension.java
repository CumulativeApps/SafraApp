package com.safra.extensions;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewExtension {

    /**
     * Add loader at end of the recyclerview list
     * @param list - list that is passed in recyclerview adapter
     * @param adapter - recyclerview's adapter
     * @param <T> - type of list
     * @return - position at loader is added in list
     */
    public static <T> int addLoadingAnimation(List<T> list, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter){
        list.add(null);
        int pPosition = list.size() - 1;
        Log.e("addLoadingAnimation", "position: " + pPosition);
        adapter.notifyItemInserted(pPosition);
        adapter.notifyItemChanged(pPosition-1);
        return pPosition;
    }

    /**
     * Set view's visibility as per its condition
     * @param v - Visibility of which to change
     * @param condition - Condition for view to show/hide
     */
    public static void makeVisible(View v, boolean condition){
        if(condition){
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * Toggle arrow view with animation
     * @param view - Arrow view
     * @param isExpanded - condition toggle arrow
     * @return - Arrow is toggled to 180 degree or not
     */
    public static boolean toggleArrow(View view, boolean isExpanded) {
        if (isExpanded) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    /**
     * Expand/Collapse view with animation
     * @param view - Layout view
     * @param isExpanded - condition show/hide layout
     */
    public static void expandLayout(View view, boolean isExpanded){
        if(isExpanded){
            view.setVisibility(View.VISIBLE);
            view.animate().setDuration(200).alpha(1f);
        } else {
            view.setVisibility(View.GONE);
            view.animate().setDuration(200).alpha(0f);
        }
    }
}
