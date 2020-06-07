package com.example.tesistrabajador.clases;

import androidx.annotation.NonNull;

public class Ciudad {

    private int idCiudad;
    private String Nombre;
    private int id_idComuna;

    public Ciudad() {

    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public String getNombre() {
        return Nombre;
    }

    public int getId_idComuna() {
        return id_idComuna;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setId_idComuna(int id_idComuna) {
        this.id_idComuna = id_idComuna;
    }

    @NonNull
    @Override
    public String toString() {
        return Nombre;
    }
}
