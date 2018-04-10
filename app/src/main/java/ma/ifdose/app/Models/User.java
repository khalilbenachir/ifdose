package ma.ifdose.app.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String name ;
    private String prenom ;
    private double ratioPetitDej ;
    private double ratioDej ;
    private double ratioColl ;
    private double ratioDinnez ;
    private double indiceSens;
    private double obj ;

    public User(String name, String prenom, double ratioPetitDej, double ratioDej, double ratioColl, double ratioDinnez, double indiceSens, double obj) {
        this.name = name;
        this.prenom = prenom;
        this.ratioPetitDej = ratioPetitDej;
        this.ratioDej = ratioDej;
        this.ratioColl = ratioColl;
        this.ratioDinnez = ratioDinnez;
        this.indiceSens = indiceSens;
        this.obj = obj;
    }

    public User(Parcel in) {
        this.name = in.readString();
        this.prenom = in.readString();
        this.ratioPetitDej = in.readDouble();
        this.ratioDej = in.readDouble();
        this.ratioColl = in.readDouble();
        this.ratioDinnez = in.readDouble();
        this.indiceSens = in.readDouble();
        this.obj = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(prenom);
        dest.writeDouble(ratioPetitDej);
        dest.writeDouble(ratioDej);
        dest.writeDouble(ratioColl);
        dest.writeDouble(ratioDinnez);
        dest.writeDouble(indiceSens);
        dest.writeDouble(obj);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public double getRatioPetitDej() {
        return ratioPetitDej;
    }

    public void setRatioPetitDej(double ratioPetitDej) {
        this.ratioPetitDej = ratioPetitDej;
    }

    public double getRatioDej() {
        return ratioDej;
    }

    public void setRatioDej(double ratioDej) {
        this.ratioDej = ratioDej;
    }

    public double getRatioColl() {
        return ratioColl;
    }

    public void setRatioColl(double ratioColl) {
        this.ratioColl = ratioColl;
    }

    public double getRatioDinnez() {
        return ratioDinnez;
    }

    public void setRatioDinnez(double ratioDinnez) {
        this.ratioDinnez = ratioDinnez;
    }

    public double getIndiceSens() {
        return indiceSens;
    }

    public void setIndiceSens(double indiceSens) {
        this.indiceSens = indiceSens;
    }

    public double getObj() {
        return obj;
    }

    public void setObj(double obj) {
        this.obj = obj;
    }

}
