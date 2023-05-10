package com.safra.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.safra.R;
import com.safra.databinding.ChipSelectableBinding;
import com.safra.databinding.RecyclerModuleBinding;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;

import java.util.HashMap;
import java.util.List;

import static com.safra.utilities.LanguageManager.languageManager;

public class ModuleRecyclerAdapter extends RecyclerView.Adapter<ModuleRecyclerAdapter.ModuleViewHolder> {

    public static final String TAG = "module_recycler_adapter";

    private final Context context;
    private final List<ModuleItem> moduleList;
    private final boolean isSelectable;
    private LayoutInflater inflater;

    public ModuleRecyclerAdapter(Context context, List<ModuleItem> moduleList, boolean isSelectable) {
        this.context = context;
        this.moduleList = moduleList;
        this.isSelectable = isSelectable;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        RecyclerModuleBinding binding = RecyclerModuleBinding.inflate(inflater, parent, false);
        return new ModuleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        holder.bindView(moduleList.get(position));
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    class ModuleViewHolder extends RecyclerView.ViewHolder {
        RecyclerModuleBinding binding;

        public ModuleViewHolder(@NonNull RecyclerModuleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ModuleItem moduleItem) {

            binding.tvModuleName.setText(languageManager.getLanguage() == 2
                    ? moduleItem.getPtModuleName() : moduleItem.getModuleName());

            if (moduleItem.getPermissionList() != null) {
                for (PermissionItem pi : moduleItem.getPermissionList()) {
                    Chip chip;
                    if (isSelectable) {
                        chip = ChipSelectableBinding.inflate(inflater).getRoot();
//                        chip = (Chip) inflater.inflate(R.layout.chip_selectable, null, false);
                        Log.e(TAG, "bindView: pi -> " + pi.isSelected());
                        chip.setChecked(pi.isSelected());
                    } else {
                        chip = new Chip(context);
                    }

                    chip.setText(languageManager.getLanguage() == 2
                            ? pi.getPtPermissionName().toUpperCase() : pi.getPermissionName().toUpperCase());
                    chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body2);
                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        pi.setSelected(isChecked);
                        Log.e("ModuleRecyclerAdapter", "bindView: " + binding.cgPermissions.getCheckedChipIds().toString());
                    });

                    binding.cgPermissions.addView(chip);
                }
//                PermissionRecyclerAdapter adapter = new PermissionRecyclerAdapter(context, moduleItem.getPermissionList());
//                permissionRecycler.setAdapter(adapter);
            }
        }
    }

    public HashMap<String, String> getSelectedModulesAndProperties() {
        StringBuilder sbModule = new StringBuilder();
        StringBuilder sbPermission = new StringBuilder();
        for (ModuleItem mi : moduleList) {
            boolean selected = false;
            for (PermissionItem pi : mi.getPermissionList()) {
                if (pi.isSelected()) {
                    selected = true;
                    if (sbPermission.toString().isEmpty()) {
                        sbPermission.append(pi.getPermissionId());
                    } else {
                        sbPermission.append(",").append(pi.getPermissionId());
                    }
                }
            }
            if (selected) {
                if (sbModule.toString().isEmpty()) {
                    sbModule.append(mi.getModuleId());
                } else {
                    sbModule.append(",").append(mi.getModuleId());
                }
            }
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("module_ids", sbModule.toString());
        hashMap.put("permission_ids", sbPermission.toString());

        return hashMap;
    }

}
