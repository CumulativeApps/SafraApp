package com.safra.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FileItem implements Parcelable {

    private long fileId;
    private String fileUrl;
    private String parentFieldName;

    public FileItem() {
    }

    protected FileItem(Parcel in) {
        fileId = in.readLong();
        fileUrl = in.readString();
        parentFieldName = in.readString();
    }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getParentFieldName() {
        return parentFieldName;
    }

    public void setParentFieldName(String parentFieldName) {
        this.parentFieldName = parentFieldName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(fileId);
        dest.writeString(fileUrl);
        dest.writeString(parentFieldName);
    }
}
