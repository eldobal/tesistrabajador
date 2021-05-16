package com.example.tesistrabajador.clases;

import java.util.ArrayList;

public class Solicitud{

    private int idSolicitud;
    private String Rubro;
    private String Nombre;
    private String Apellido;
    private String FechaS;
    private String RUT;
    private int Precio;
    private String metodoPago;
    private String FechaA;
    private String EstadoSolicitud;
    private String Descripcion;
    private String Diagnostico;
    private String Solucion;
    private String idFoto;
    private String Foto;
    private double latitud;
    private double longitud;




    private ArrayList<Solicitud> lista = new ArrayList<Solicitud>();


    public Solicitud() {
        this.idSolicitud= idSolicitud;
        this.FechaS= FechaS;
        this.Rubro=Rubro;
        this.Descripcion= Descripcion;
        this.RUT= RUT;
        this.Precio= Precio;
        this.idFoto= idFoto;
        this.metodoPago=metodoPago;
        this.FechaA=FechaA;
        this.Diagnostico=Diagnostico;
        this.Solucion=Solucion;
        this.EstadoSolicitud= EstadoSolicitud;
        this.Nombre= Nombre;
        this.Apellido= Apellido;
        this.Foto=Foto;
        this.latitud=latitud;
        this.longitud=longitud;
    }


    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getRubro() {
        return Rubro;
    }

    public void setRubro(String rubro) {
        Rubro = rubro;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getFechaS() {
        return FechaS;
    }

    public void setFechaS(String fechaS) {
        FechaS = fechaS;
    }

    public String getRUT() {
        return RUT;
    }

    public void setRUT(String RUT) {
        this.RUT = RUT;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getFechaA() {
        return FechaA;
    }

    public void setFechaA(String fechaA) {
        FechaA = fechaA;
    }

    public String getEstado() {
        return EstadoSolicitud;
    }

    public void setEstado(String estado) {
        this.EstadoSolicitud = estado;
    }

    public String getDescripcionP() {
        return Descripcion;
    }

    public void setDescripcionP(String descripcion) {
        Descripcion = descripcion;
    }

    public String getDiagnostico() {
        return Diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        Diagnostico = diagnostico;
    }

    public String getSolucion() {
        return Solucion;
    }

    public void setSolucion(String solucion) {
        Solucion = solucion;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getFotoT() {
        return Foto;
    }

    public void setFotoT(String fotoT) {
        Foto = fotoT;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public ArrayList<Solicitud> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Solicitud> lista) {
        this.lista = lista;
    }
}

