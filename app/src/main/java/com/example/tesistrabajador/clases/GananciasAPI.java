package com.example.tesistrabajador.clases;

import java.util.ArrayList;

public class GananciasAPI {

    private int GananciasTrabajador;
    private int GananciaPorPagar;


    private ArrayList<GananciasAPI> lista = new ArrayList<GananciasAPI>();


    public GananciasAPI(){
        this.GananciasTrabajador= GananciasTrabajador;
        this.GananciaPorPagar=GananciaPorPagar;
    }



    public int getGananciasTrabajador() {
        return GananciasTrabajador;
    }

    public void setGananciasTrabajador(int gananciasTrabajador) {
        GananciasTrabajador = gananciasTrabajador;
    }

    public int getGananciasPorPagar() {
        return GananciaPorPagar;
    }

    public void setGananciasPorPagar(int gananciasPorPagar) {
        GananciaPorPagar = gananciasPorPagar;
    }

    public ArrayList<GananciasAPI> getLista() {
        return lista;
    }

    public void setLista(ArrayList<GananciasAPI> lista) {
        this.lista = lista;
    }
}
