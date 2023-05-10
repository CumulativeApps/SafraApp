package com.safra.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;

public class LineItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int decorationHeight, lastDecorationHeight;
    private boolean lastSpace;

    public LineItemDecoration(Context context, int bottomOffset, boolean lastSpace) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.recycler_divider);
        decorationHeight = context.getResources().getDimensionPixelSize(bottomOffset);
        lastDecorationHeight = context.getResources().getDimensionPixelSize(R.dimen.recycler_bottom_offset);
        this.lastSpace = lastSpace;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        if (parent != null && view != null) {
            int itemPosition = parent.getChildAdapterPosition(view);
            int totalCount = parent.getAdapter().getItemCount();

            if(itemPosition >= 0){
//                if (itemPosition <= totalCount - 1) {
//                    outRect.bottom = decorationHeight;
//                }

                if (itemPosition == totalCount - 1) {
                    if(lastSpace) {
                        outRect.bottom = lastDecorationHeight;
                    }else{
                        outRect.bottom = decorationHeight;
                    }
                }
            }
//            switch (gridNo){
//                case 1:
//                    if (itemPosition >= 0 && itemPosition == totalCount - 1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    break;
//                case 2:
//                    if (itemPosition >= 0 && itemPosition == totalCount - 2 && ((itemPosition+1)%2)==1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    break;
//                case 3:
//                    if (itemPosition >= 0 && itemPosition == totalCount - 3 && ((itemPosition+1)%3)==1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 2 && ((itemPosition+1)%3)==2) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    break;
//                case 4:
//                    if (itemPosition >= 0 && itemPosition == totalCount - 4 && ((itemPosition+1)%4)==1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 3 && ((itemPosition+1)%4)==2) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 2 && ((itemPosition+1)%4)==3) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    if (itemPosition >= 0 && itemPosition == totalCount - 1) {
//                        outRect.top = decorationHeight;
//                        if(lastSpace) {
//                            outRect.bottom = lastDecorationHeight;
//                        }else{
//                            outRect.bottom = decorationHeight;
//                        }
//                    }
//                    break;
//            }


//        }
    }
}
