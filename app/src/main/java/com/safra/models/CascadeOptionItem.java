package com.safra.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CascadeOptionItem implements Parcelable {

    private int id;
    private String option;
    private int parentId;
    private int level;
    private List<CascadeOptionItem> childOptionList;

    private boolean isSelected;
    private boolean isEnable;

    public CascadeOptionItem() {
    }

    public CascadeOptionItem(String option, int level, List<CascadeOptionItem> childOptionList) {
        this.option = option;
        this.level = level;
        this.childOptionList = childOptionList;
    }

    public CascadeOptionItem(int id, String option, int parentId, int level,
                             List<CascadeOptionItem> childOptionList, boolean isSelected, boolean isEnable) {
        this.id = id;
        this.option = option;
        this.parentId = parentId;
        this.level = level;
        this.childOptionList = childOptionList;
        this.isSelected = isSelected;
        this.isEnable = isEnable;
    }

    protected CascadeOptionItem(Parcel in) {
        id = in.readInt();
        option = in.readString();
        parentId = in.readInt();
        level = in.readInt();
        childOptionList = in.createTypedArrayList(CascadeOptionItem.CREATOR);
        isSelected = in.readByte() != 0;
        isEnable = in.readByte() != 0;
    }

    public static final Creator<CascadeOptionItem> CREATOR = new Creator<CascadeOptionItem>() {
        @Override
        public CascadeOptionItem createFromParcel(Parcel in) {
            return new CascadeOptionItem(in);
        }

        @Override
        public CascadeOptionItem[] newArray(int size) {
            return new CascadeOptionItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<CascadeOptionItem> getChildOptionList() {
        return childOptionList;
    }

    public void setChildOptionList(List<CascadeOptionItem> childOptionList) {
        this.childOptionList = childOptionList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public String toString() {
        return "CascadeOptionItem{" +
                "id=" + id +
                ", option='" + option + '\'' +
                ", parentId=" + parentId +
                ", level=" + level +
                ", childOptionList=" + childOptionList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(option);
        dest.writeInt(parentId);
        dest.writeInt(level);
        dest.writeTypedList(childOptionList);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isEnable ? 1 : 0));
    }
}
