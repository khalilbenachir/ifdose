package ma.ifdose.app.Models;

import android.os.Parcel;
import android.os.Parcelable;


public class Aliment implements Parcelable {
    public static final Parcelable.Creator<Aliment> CREATOR = new Parcelable.Creator<Aliment>() {

        public Aliment createFromParcel(Parcel in) {
            return new Aliment(in);
        }

        public Aliment[] newArray(int size) {
            return new Aliment[size];
        }
    };
    private String nom;
    private String quantiteA ;
    private double quantiteB ;
    private double glucide ;

    public Aliment(){}

    public Aliment(String nom , String quantiteA , double quantiteB , double glucide){
        this.nom=nom ;
        this.quantiteA=quantiteA ;
        this.quantiteB=quantiteB;
        this.glucide=glucide ;

    }

    public Aliment(Parcel in){
        this.nom = in.readString();
        this.quantiteA = in.readString();
        this.quantiteB = in.readDouble() ;
        this.glucide = in.readDouble() ;

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getQuantiteA() {
        return quantiteA;
    }

    public void setQuantiteA(String quantiteA) {
        this.quantiteA = quantiteA;
    }

    public double getQuantiteB() {
        return quantiteB;
    }

    public void setQuantiteB(double quantiteB) {
        this.quantiteB = quantiteB;
    }

    public double getGlucide() {
        return glucide;
    }

    public void setGlucide(double glucide) {
        this.glucide = glucide;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(quantiteA);
        dest.writeDouble(quantiteB);
        dest.writeDouble(glucide);
    }
}
