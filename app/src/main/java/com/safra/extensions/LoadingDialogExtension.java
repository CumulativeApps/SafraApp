package com.safra.extensions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import com.safra.R;
import com.safra.databinding.DialogLoadingBinding;

public class LoadingDialogExtension {

    public static Dialog dialog = null;

    public static void showLoading(Context context, String message) {
        if (dialog == null) {
            dialog = new Dialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DialogLoadingBinding binding = DialogLoadingBinding.inflate(inflater);
            binding.tvLoadingMessage.setText(message != null ? message : LanguageExtension.setText("loading_progress", context.getString(R.string.loading_progress)));
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    public static void hideLoading() {
        if (dialog != null) {
            Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();
            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed())
                dialog.dismiss();

            dialog = null;
        }
    }

}
