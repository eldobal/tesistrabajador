package com.example.tesistrabajador.clases;

import java.util.ArrayList;

public class GananciasAPI {



    private int GananciasTrabajador;
    private int GananciasPorPagar;


    private ArrayList<GananciasAPI> lista = new ArrayList<GananciasAPI>();


    public GananciasAPI(){

        this.GananciasTrabajador= GananciasTrabajador;
        this.GananciasPorPagar=GananciasPorPagar;
    }



    public int getGananciasTrabajador() {
        return GananciasTrabajador;
    }

    public void setGananciasTrabajador(int gananciasTrabajador) {
        GananciasTrabajador = gananciasTrabajador;
    }

    public int getGananciasPorPagar() {
        return GananciasPorPagar;
    }

    public void setGananciasPorPagar(int gananciasPorPagar) {
        GananciasPorPagar = gananciasPorPagar;
    }

    public ArrayList<GananciasAPI> getLista() {
        return lista;
    }

    public void setLista(ArrayList<GananciasAPI> lista) {
        this.lista = lista;
    }
}
