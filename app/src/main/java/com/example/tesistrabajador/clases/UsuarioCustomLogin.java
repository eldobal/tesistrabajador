package com.example.tesistrabajador.clases;

public class UsuarioCustomLogin {

    private String RUT;
    private String Contrasena;
    private int idCiudad;


    public UsuarioCustomLogin() {
        this.RUT = RUT;
        this.Contrasena = Contrasena;
        this.idCiudad = idCiudad;
    }

    public String getRUT() {
        return RUT;
    }

    public void setRUT(String RUT) {
        this.RUT = RUT;
    }

    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {
        Contrasena = contrasena;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }
}
