package com.safra.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.safra.R;
import com.safra.databinding.DialogDeleteBinding;
import com.safra.extensions.LanguageExtension;

public class DeleteDialog extends DialogFragment {

    public static final String TAG = "delete_dialog";

    private FragmentActivity mActivity = null;

    private String type;
    private long id, onlineId;
    private int position;

    private String requestKey;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogDeleteBinding binding = DialogDeleteBinding.inflate(inflater, container, false);

        binding.tvDialogTitle.setText(LanguageExtension.setText("delete", getString(R.string.delete)));
        binding.btnCancel.setText(LanguageExtension.setText("cancel", getString(R.string.cancel)));
        binding.btnDelete.setText(LanguageExtension.setText("delete", getString(R.string.delete)));

        if (getArguments() != null) {
            requestKey = getArguments().getString("request_key");
            binding.tvDialogMessage.setText(getArguments().getString("message"));
            type = getArguments().getString("type");
            id = getArguments().getLong("id");
            onlineId = getArguments().getLong("online_id");
            position = getArguments().getInt("position");
        }

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnDelete.setOnClickListener(v -> {
            delete();
//            switch (type) {
//                case "user":
//                    deleteUser();
//                    break;
//                case "task":
//                    deleteTask();
//                    break;
//            }
//            dismiss();
//            Extension.signOutUser(mActivity);
        });

        return binding.getRoot();
    }

    private void delete(){
        dismiss();
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putLong("online_id", onlineId);
        bundle.putInt("position", position);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }

//    private void deleteUser() {
//        if (getTargetFragment() != null)
//            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_SUCCESS_DELETE_USER, i);
//    }

//    private void deleteTask() {
//        dismiss();
//        Bundle bundle = new Bundle();
//        bundle.putLong("id", id);
//        bundle.putLong("online_id", onlineId);
//        bundle.putInt("position", position);
//        if (getTargetFragment() != null)
//            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_SUCCESS_DELETE_TASK, i);
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }
}
