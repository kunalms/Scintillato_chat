package com.scintillato.scintillatochat;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    private int image;
    private String text;

    public Item(int image, String text){
        this.image = image;
        this.text = text;
    }

    public int getImage(){
        return( this.image);
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.image);
        dest.writeString(this.text);
    }

    protected Item(Parcel in) {
        this.image = in.readInt();
        this.text = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
