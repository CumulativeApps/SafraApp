package com.safra.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.RecyclerCascadingOptionBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.CascadeOptionChanges;
import com.safra.models.CascadeOptionItem;

import java.util.ArrayList;
import java.util.List;

public class CascadingOptionRecyclerAdapter
        extends RecyclerView.Adapter<CascadingOptionRecyclerAdapter.OptionViewHolder> {

    public static final String TAG = "cascading_option_adptr";

    private Context context;
    private List<CascadeOptionItem> optionList;
    private final CascadeOptionChanges listener;

    public CascadingOptionRecyclerAdapter(Context context, CascadeOptionChanges listener) {
        this.context = context;
        optionList = new ArrayList<>();
        this.listener = listener;
    }

    public CascadingOptionRecyclerAdapter(Context context, List<CascadeOptionItem> optionList, CascadeOptionChanges listener) {
        this.context = context;
        this.optionList = optionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCascadingOptionBinding binding = RecyclerCascadingOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OptionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CascadingOptionRecyclerAdapter.OptionViewHolder holder, int position) {
        holder.bindView(optionList.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull CascadingOptionRecyclerAdapter.OptionViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.size() > 0) {
            holder.refreshOptionList((CascadeOptionItem) payloads.get(0));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public void addOption(String option, int level) {
        CascadeOptionItem optionItem = new CascadeOptionItem();
        optionItem.setOption(option);
        optionItem.setLevel(level);
        optionItem.setChildOptionList(new ArrayList<>());
        optionList.add(optionItem);
//        notifyItemChanged(optionList.size() - 2);
        notifyItemInserted(optionList.size() - 1);
    }

    public void setOptions(List<CascadeOptionItem> optionList) {
        this.optionList = optionList;
        notifyDataSetChanged();
    }

    public List<CascadeOptionItem> getOptionList() {
        return optionList;
    }

    public boolean getIsAnyEnabled(){
        boolean isEnable = false;
        for(CascadeOptionItem coi : optionList){
            if(coi.isEnable())
                isEnable = coi.isEnable();
        }
        return isEnable;
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        RecyclerCascadingOptionBinding binding;
        CascadingOptionRecyclerAdapter adapter;

        public OptionViewHolder(@NonNull RecyclerCascadingOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(CascadeOptionItem optionItem) {
            binding.tvAddChild.setText(LanguageExtension.setText("add_child", context.getString(R.string.add_child)));
            binding.ivEdit.setText(LanguageExtension.setText("edit", context.getString(R.string.edit)));
            binding.ivSave.setText(LanguageExtension.setText("save", context.getString(R.string.save)));

            binding.etCascadingOption.setText(optionItem.getOption());

            binding.rvChildOption.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            adapter = new CascadingOptionRecyclerAdapter(context, optionItem.getChildOptionList(),
                    new CascadeOptionChanges() {
                @Override
                public void onOptionEnableDisable(boolean isEnable) {
                    optionItem.setEnable(adapter.getIsAnyEnabled());
                    listener.onOptionEnableDisable(isEnable);
                }

                @Override
                public void onOptionRemove(int position) {
                    optionItem.setOption(binding.etCascadingOption.getText() != null ? binding.etCascadingOption.getText().toString() : "");
                    binding.etCascadingOption.setEnabled(false);
                    optionItem.setChildOptionList(adapter.getOptionList());
                    binding.ivEdit.setVisibility(View.VISIBLE);
                    binding.ivSave.setVisibility(View.GONE);
                    notifyItemChanged(getAbsoluteAdapterPosition(), optionItem);
                }
            });
            binding.rvChildOption.setAdapter(adapter);

            binding.etCascadingOption.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null)
                        optionItem.setOption(s.toString());
                }
            });

            binding.viewHorizontalLine.setVisibility(optionItem.getLevel() == 1 ? View.INVISIBLE : View.VISIBLE);

            binding.viewVerticalLine.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);

            binding.ivEdit.setOnClickListener(v -> {
                binding.etCascadingOption.setEnabled(true);
                binding.etCascadingOption.requestFocus();
                binding.etCascadingOption.setSelection(binding.etCascadingOption.getText().length());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                listener.onOptionEnableDisable(true);
                binding.ivEdit.setVisibility(View.GONE);
                binding.ivSave.setVisibility(View.VISIBLE);
            });

            binding.ivSave.setOnClickListener(v -> {
                if(binding.etCascadingOption.getText() == null
                        || binding.etCascadingOption.getText().toString().isEmpty()) {
                    binding.etCascadingOption.setError("Enter option");
                } else {
                    binding.etCascadingOption.clearFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    binding.etCascadingOption.setEnabled(false);
                    binding.ivEdit.setVisibility(View.VISIBLE);
                    binding.ivSave.setVisibility(View.GONE);
                    listener.onOptionEnableDisable(false);
                    optionItem.setOption(binding.etCascadingOption.getText().toString());
                }
//                notifyItemChanged(getAbsoluteAdapterPosition(), optionItem);
            });

            binding.ivRemove.setOnClickListener(v -> {
                Log.e(TAG, "bindView: " + getBindingAdapterPosition());
                optionList.remove(getBindingAdapterPosition());
                notifyItemRemoved(getAbsoluteAdapterPosition());
                listener.onOptionRemove(getBindingAdapterPosition());
            });

            binding.tvAddChild.setOnClickListener(v -> {
                Log.e(TAG, "bindView: " + getAbsoluteAdapterPosition());
                for (CascadeOptionItem coi : optionItem.getChildOptionList()) {
                    Log.e(TAG, "bindView: " + coi);
                }
                List<CascadeOptionItem> options = optionItem.getChildOptionList();
                options.add(new CascadeOptionItem("New Option", optionItem.getLevel() + 1, new ArrayList<>()));
                optionItem.setOption(binding.etCascadingOption.getText() != null ? binding.etCascadingOption.getText().toString() : "");
                optionItem.setChildOptionList(options);
                binding.etCascadingOption.setEnabled(false);
                binding.ivEdit.setVisibility(View.VISIBLE);
                binding.ivSave.setVisibility(View.GONE);
                notifyItemChanged(getAbsoluteAdapterPosition(), optionItem);
            });
        }

        public void refreshOptionList(CascadeOptionItem newOptionItem) {
            adapter.setOptions(newOptionItem.getChildOptionList());
            binding.etCascadingOption.setText(newOptionItem.getOption());
            binding.viewVerticalLine.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
