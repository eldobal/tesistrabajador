package com.example.tesistrabajador.clases;

import com.google.gson.annotations.SerializedName;

public class UsuarioTrabajador {


    private String RUT;
    private String Nombre;
    private String Apellido;
    private String Correo;
    private String Contrasena;
    private String Fono;
    private int idRubro;
    private String Rubro;
    private String Solicitud;
    private String Calificacion;
    private String Foto;
    private String Estado;
    private String Ciudad;


    @SerializedName("idCiudad")
    private int idCiudad;
    @SerializedName("EstadoUsuario")
    private int idEstado;
    @SerializedName("TipoUsuario")
    private int idTipo;


    public UsuarioTrabajador() {
        this.RUT = RUT;
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Correo = Correo;
        this.Contrasena = Contrasena;
        this.Fono = Fono;
        this.idCiudad = idCiudad;
        this.Ciudad = Ciudad;
        this.idRubro = idRubro;
        this.idEstado = idEstado;
        this.idTipo = idTipo;
        this.Foto = Foto;
        this.Rubro = Rubro;
        this.Solicitud = Solicitud;
        this.Calificacion = Calificacion;
        this.Estado = Estado;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getRUT() {
        return RUT;
    }

    public void setRUT(String RUT) {
        this.RUT = RUT;
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

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {
        Contrasena = contrasena;
    }

    public String getFono() {
        return Fono;
    }

    public void setFono(String fono) {
        Fono = fono;
    }

    public String getRubro() {
        return Rubro;
    }

    public void setRubro(String rubro) {
        Rubro = rubro;
    }

    public String getSolicitud() {
        return Solicitud;
    }

    public void setSolicitud(String solicitud) {
        Solicitud = solicitud;
    }

    public String getCalificacion() {
        return Calificacion;
    }

    public void setCalificacion(String calificacion) {
        Calificacion = calificacion;
    }

    public String getCiudad() {
        return Ciudad;
    }

    public void setCiudad(String ciudad) {
        Ciudad = ciudad;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String Foto) {
        this.Foto = Foto;
    }
}
