package com.example.tesistrabajador.clases;

import com.google.gson.annotations.SerializedName;

public class Usuario {


    private String RUT;
    private String Nombre;
    private String Apellido;
    private String Correo;
    private String Contrasena;
    private String Fono;
    private String Foto;


    @SerializedName("id_idCiudad")
    private int idCiudad;
    @SerializedName("id_EstadoUsuario")
    private int idEstado;
    @SerializedName("id_TipoUsuario")
    private int idTipo;


    public Usuario() {
        this.RUT = RUT;
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this. Correo = Correo;
        this.Contrasena = Contrasena;
        this.Fono = Fono;
        this.idCiudad = idCiudad;
        this.idEstado = idEstado;
        this.idTipo = idTipo;
        this.Foto=Foto;
    }

    public String getRut() {
        return RUT;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getContrasena() {
        return Contrasena;
    }

    public String getFono() {
        return Fono;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }
}
