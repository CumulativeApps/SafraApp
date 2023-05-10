package com.safra.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OptionItem implements Parcelable {

    private String optionKey;
    private String optionValue;
    private boolean isSelected;

    public OptionItem() {
    }

    public OptionItem(String optionKey, String optionValue, boolean isSelected) {
        this.optionKey = optionKey;
        this.optionValue = optionValue;
        this.isSelected = isSelected;
    }

    protected OptionItem(Parcel in) {
        optionKey = in.readString();
        optionValue = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<OptionItem> CREATOR = new Creator<OptionItem>() {
        @Override
        public OptionItem createFromParcel(Parcel in) {
            return new OptionItem(in);
        }

        @Override
        public OptionItem[] newArray(int size) {
            return new OptionItem[size];
        }
    };

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(optionKey);
        dest.writeString(optionValue);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public String toString() {
        return "OptionItem{" +
                "optionKey='" + optionKey + '\'' +
                ", optionValue='" + optionValue + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
