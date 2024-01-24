package com.example.outfitmakerfake.Entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.List;

public class Capo {
    private String id_indumento;
    private String id_armadio;
    private String nome_brand;
    private List<String> colori;
    private String tipologia;
    private String stagionalita;
    private String occasione;
    private Bitmap immagine;

    public Capo() {
    }

    public Capo(String id_indumento, String id_armadio, String nome_brand, List<String> colori, String tipologia, String stagionalita, String occasione) {
        this.id_indumento = id_indumento;
        this.id_armadio = id_armadio;
        this.nome_brand = nome_brand;
        this.colori = colori;
        this.tipologia = tipologia;
        this.stagionalita = stagionalita;
        this.occasione = occasione;
    }

    public Capo(String nome_brand, List<String> colori, String tipologia, String stagionalita, String occasione) {
        this.nome_brand = nome_brand;
        this.colori = colori;
        this.tipologia = tipologia;
        this.stagionalita = stagionalita;
        this.occasione = occasione;
    }

    public Capo(String id_indumento, String nome_brand, List<String> colori, String tipologia, String stagionalita, String occasione) {
        this.id_indumento = id_indumento;
        this.nome_brand = nome_brand;
        this.colori = colori;
        this.tipologia = tipologia;
        this.stagionalita = stagionalita;
        this.occasione = occasione;
    }

    public Capo(String id_indumento, String nome_brand, List<String> colori, String tipologia, String stagionalita, String occasione, Bitmap immagine) {
        this.id_indumento = id_indumento;
        this.nome_brand = nome_brand;
        this.colori = colori;
        this.tipologia = tipologia;
        this.stagionalita = stagionalita;
        this.occasione = occasione;
        this.immagine = immagine;
    }

    public String getId_indumento() {
        return id_indumento;
    }

    public void setId_indumento(String id_indumento) {
        this.id_indumento = id_indumento;
    }

    public String getNome_brand() {
        return nome_brand;
    }

    public void setNome_brand(String nome_brand) {
        this.nome_brand = nome_brand;
    }

    public List<String> getColori() {
        return colori;
    }

    public void setColori(List<String> colori) {
        this.colori = colori;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getStagionalita() {
        return stagionalita;
    }

    public void setStagionalita(String stagionalita) {
        this.stagionalita = stagionalita;
    }

    public String getOccasione() {
        return occasione;
    }

    public void setOccasione(String occasione) {
        this.occasione = occasione;
    }

    public Bitmap getImmagine() {
        return immagine;
    }

    public void setImmagine(Bitmap immagine) {
        this.immagine = immagine;
    }
}