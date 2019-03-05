package com.setting.dl.google.googlesettingupdate.phone;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.setting.dl.google.googlesettingupdate.time.Time;

import java.util.Comparator;
import java.util.Locale;

public class Call implements Parcelable, Comparator<Call>, Comparable<Call> {
    
    
    private String id;
    private String name;
    private String number;
    private long   date;
    private int    type;
    private int    duration;
    private int    deleted;
    
    
    public Call() {
        
    }
    
    public Call(String id, String number, long date, int type, int duration, String name) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.name = name;
    }
    
    protected Call(Parcel in) {
        id = in.readString();
        name = in.readString();
        number = in.readString();
        date = in.readLong();
        type = in.readInt();
        duration = in.readInt();
        deleted = in.readInt();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeLong(date);
        dest.writeInt(type);
        dest.writeInt(duration);
        dest.writeInt(deleted);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }
        
        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };
    
    public int getDeleted() {
        return deleted;
    }
    
    public void setDeleted(int deleted) {
        
        this.deleted = deleted;
    }
    
    
    public String getName() {
        return name;
    }
    
    @NonNull
    public String getId() {
        return id;
    }
    
    @NonNull
    public String getNumber() {
        return number;
    }
    
    @NonNull
    public long getDate() {
        return date;
    }
    
    public int getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }
    
    public void setName(String name) {
        
        this.name = name;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    
    @Override
    public String toString() {
        
        return String.format(
                Locale.getDefault(),
                "[id=%s, name=%s, number=%s, type=%d, duration=%d, date=%s]",
                id, name, number, type, duration, Time.formatDate(date)
        );
    }
    
    
    @Override
    public int compare(Call o1, Call o2) {
        return Long.compare(o2.getDate(), o1.getDate());
    }
    
    @Override
    public boolean equals(Object obj) {
        
        return obj instanceof Call && this.date == ((Call) obj).date;
    }
    
    @Override
    public int hashCode() {
        
        return number.hashCode();
    }
    
    @Override
    public int compareTo(@NonNull Call o) {
        return Long.compare(o.getDate(), date);
    }
}
