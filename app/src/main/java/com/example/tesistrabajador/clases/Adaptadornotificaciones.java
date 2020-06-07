package com.example.tesistrabajador.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.interfaces.tesisAPI;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adaptadornotificaciones  extends BaseAdapter implements Serializable {

    SweetAlertDialog dp;
    private static LayoutInflater inflater = null;
    Context contexto;
    ArrayList<Notificacion> listanotificaciones;


    Notificacion notificacion = new Notificacion();


    public Adaptadornotificaciones(Context contexto, ArrayList<Notificacion> listanotificaciones) {
        this.contexto = contexto;
        this.listanotificaciones = listanotificaciones;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    //metodo el cual se utiliza para actualizar la lista con los cambios
    public void refresh(ArrayList<Notificacion> listanotificaciones) {
        this.listanotificaciones = listanotificaciones;
        this.notifyDataSetChanged();
    }

    //metodo el cual limpia la lista con los elementos que tenga dentro
    public void clearData() {
        listanotificaciones.clear();
    }

    @Override
    public int getCount() {
        return listanotificaciones.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View vista = inflater.inflate(R.layout.elementonotificacion, null);

        //datos del elemento en el cual se cargaran los trabajadores
        //  TextView cliente = (TextView) vista.findViewById(R.id.txtclientesolicituddetalle);
        CardView card = (CardView) vista.findViewById(R.id.cardnotificacion);
        TextView mensaje = (TextView) vista.findViewById(R.id.mensajenotificacion);

        mensaje.setText(listanotificaciones.get(i).getMensaje());

        notificacion.setId(listanotificaciones.get(i).getId());
        notificacion.setMensaje(listanotificaciones.get(i).getMensaje());
        notificacion.setRUT(listanotificaciones.get(i).getRUT());
        notificacion.setIdSolicitud(listanotificaciones.get(i).getIdSolicitud());
        final int posicion = i;
        card.setTag(i);


        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dp = new SweetAlertDialog(vista.getContext(), SweetAlertDialog.WARNING_TYPE);
                dp.setTitleText("Confirmar La solicitud?");
                dp.setContentText("si confirma esta solicitud el trbajador realizara el trabajo. so la cancela se eliminara esta solicitud");
                dp.setConfirmText("Confirmar!");
                dp.setCancelText("Cancelar!");
                dp.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        final String Fechasolicitud = sdf.format(calendar.getTime());
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://proyectotesis.ddns.net/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                        Call<Solicitud> call = tesisAPI.EstadoAtendiendo(listanotificaciones.get(i).getIdSolicitud(), Fechasolicitud);
                        call.enqueue(new Callback<Solicitud>() {
                            @Override
                            public void onResponse(Call<Solicitud> call, Response<Solicitud> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                } else {
                                    listanotificaciones.remove(i);
                                    refresh(listanotificaciones);

                                }
                            }

                            @Override
                            public void onFailure(Call<Solicitud> call, Throwable t) {
                                Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        sDialog.dismissWithAnimation();
                    }
                }).show();
                dp.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://proyectotesis.ddns.net/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                        Call<String> call = tesisAPI.CancelarSolicitud(listanotificaciones.get(i).getIdSolicitud());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(!response.isSuccessful()){
                                    Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    listanotificaciones.remove(i);
                                    refresh(listanotificaciones);
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        sDialog.dismissWithAnimation();
                    }
                })
                        .show();

            }
        });


        return vista;
    }


}
