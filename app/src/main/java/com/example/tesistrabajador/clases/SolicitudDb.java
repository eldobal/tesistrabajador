package com.example.tesistrabajador.clases;

import java.util.ArrayList;

public class SolicitudDb {


    private int idSolicitud;
    private String Fecha;
    private String Descripcion;
    private String idFoto;
    private String RUT_Cliente;
    private String RUT_Trabajador;
    private int idEstadoSolicitud;
    private int idRubro;


    private ArrayList<SolicitudDb> lista = new ArrayList<SolicitudDb>();


    public SolicitudDb() {
        this.idSolicitud= idSolicitud;
        this.Fecha= Fecha;
        this.Descripcion= Descripcion;
        this.idFoto= idFoto;
        this.RUT_Cliente=RUT_Cliente;
        this.RUT_Trabajador=RUT_Trabajador;
        this.idEstadoSolicitud=idEstadoSolicitud;
        this.idRubro=idRubro;
    }


    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getRUT_Cliente() {
        return RUT_Cliente;
    }

    public void setRUT_Cliente(String RUT_Cliente) {
        this.RUT_Cliente = RUT_Cliente;
    }

    public String getRUT_Trabajador() {
        return RUT_Trabajador;
    }

    public void setRUT_Trabajador(String RUT_Trabajador) {
        this.RUT_Trabajador = RUT_Trabajador;
    }

    public int getIdEstadoSolicitud() {
        return idEstadoSolicitud;
    }

    public void setIdEstadoSolicitud(int idEstadoSolicitud) {
        this.idEstadoSolicitud = idEstadoSolicitud;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public ArrayList<SolicitudDb> getLista() {
        return lista;
    }

    public void setLista(ArrayList<SolicitudDb> lista) {
        this.lista = lista;
    }
}
