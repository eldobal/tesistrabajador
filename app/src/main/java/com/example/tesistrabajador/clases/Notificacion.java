package com.example.tesistrabajador.clases;

import java.util.ArrayList;

public class Notificacion {

    private int Id;
    private String Mensaje;
    private String RUT;
    private int idSolicitud;

    private ArrayList<Notificacion> lista = new ArrayList<Notificacion>();


    public Notificacion() {
        this.Id= Id;
        this.Mensaje= Mensaje;
        this.RUT=RUT;
        this.idSolicitud= idSolicitud;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getRUT() {
        return RUT;
    }

    public void setRUT(String RUT) {
        this.RUT = RUT;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public ArrayList<Notificacion> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Notificacion> lista) {
        this.lista = lista;
    }
}
