package com.example.tesistrabajador.clases;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.fragments.DetalleSolicitudFragment;
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

public class Adaptador extends BaseAdapter implements Serializable {

    private static LayoutInflater inflater = null;
    SweetAlertDialog dp;
    Context contexto;
    ArrayList<Solicitud> listasolicitudes;
    ArrayList<Solicitud> lista;

    Solicitud soli = new Solicitud();


    public Adaptador(Context contexto, ArrayList<Solicitud> listasolicitudes) {
        this.contexto = contexto;
        this.listasolicitudes = listasolicitudes;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    //metodo el cual refresca el listview
    public void refresh(ArrayList<Solicitud> listasolicitudes){
        this.listasolicitudes = listasolicitudes;
        this.notifyDataSetChanged();
    }

    public void clearData() {  listasolicitudes.clear(); }

    @Override
    public int getCount() {
        return listasolicitudes.size();
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
    public View getView(int i, View convertView, ViewGroup parent) {

        //declaracion de la vista de cada item de la solicitud
        final View vista = inflater.inflate(R.layout.elemento_solicitud, null);
        TextView numerosolicitud = (TextView) vista.findViewById(R.id.txtfilanumerosolicitud);
        TextView fechasolicitud = (TextView) vista.findViewById(R.id.txtfilafechasolicitud);
        TextView estadosolicitud = (TextView) vista.findViewById(R.id.txtfilaestadosolicitudelemento);
        TextView nombretrabajador = (TextView) vista.findViewById(R.id.txtfilanombretrabajador);
       // TextView descripcion = (TextView) vista.findViewById(R.id.txtdescripciondetallesolicitud);
        ImageView fototrabajador = (ImageView) vista.findViewById(R.id.imgperfilfilasolicitud);
        final Button detalle = (Button) vista.findViewById(R.id.btndetallesolicitud);



        int idsolicitud = listasolicitudes.get(i).getIdSolicitud();
        numerosolicitud.setText("N Solicitud: "+String.valueOf(listasolicitudes.get(i).getIdSolicitud()));
        fechasolicitud.setText("Fecha: "+listasolicitudes.get(i).getFechaS());
        estadosolicitud.setText(listasolicitudes.get(i).getEstado());
        nombretrabajador.setText(listasolicitudes.get(i).getNombre()+" "+listasolicitudes.get(i).getApellido());
        //icono.setImageResource(imagenes[0]);

        soli.setIdSolicitud(listasolicitudes.get(i).getIdSolicitud());
        soli.setFechaS(listasolicitudes.get(i).getFechaS());
        soli.setEstado(listasolicitudes.get(i).getEstado());
        soli.setNombre(listasolicitudes.get(i).getNombre()+" "+listasolicitudes.get(i).getApellido());
        //se carga la imagen del trabajor en la lista de los trabajadores desde la lista de solicitudes
        Glide.with(vista.getContext()).load(String.valueOf(listasolicitudes.get(i).getFotoT())).into(fototrabajador);

        final int posicion = i;
        detalle.setTag(i);

        if(listasolicitudes.get(i).getEstado().equals("ATENDIENDO")) {

            detalle.setText("Detalle");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));
            //boton sobre el detalle de una solicitud individual
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Solicitud ut;
                    ut = listasolicitudes.get(posicion);
                    Bundle bundle = new Bundle();
                    //id de la solicitud para que se pueda buscar en el detalle
                    bundle.putInt("idsolicitud", idsolicitud);
                    DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                    detalleSolicitudFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container, detalleSolicitudFragment);
                    ft.commit();
                }
            });
        }if(listasolicitudes.get(i).getEstado().equals("CONFIRMADA")){

            detalle.setText("Confirmar");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
                            Call<Solicitud> call = tesisAPI.EstadoAtendiendo(listasolicitudes.get(i).getIdSolicitud(), Fechasolicitud);
                            call.enqueue(new Callback<Solicitud>() {
                                @Override
                                public void onResponse(Call<Solicitud> call, Response<Solicitud> response) {
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(vista.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                    } else {
                                        listasolicitudes.remove(i);
                                        refresh(listasolicitudes);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Solicitud> call, Throwable t) {
                                    Toast.makeText(vista.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
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
                            Call<String> call2 = tesisAPI.CancelarSolicitud(listasolicitudes.get(i).getIdSolicitud());
                            call2.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call2, Response<String> response) {
                                    if(!response.isSuccessful()){
                                        Toast.makeText(vista.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        listasolicitudes.remove(i);
                                        refresh(listasolicitudes);
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call2, Throwable t) {
                                    Toast.makeText(vista.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            sDialog.dismissWithAnimation();
                        }
                    })
                            .show();
                }
            });
        }if(listasolicitudes.get(i).getEstado().equals("PENDIENTE")) {
            detalle.setText("Cancelar");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dp=   new SweetAlertDialog(vista.getContext(), SweetAlertDialog.WARNING_TYPE);
                    dp.setTitleText("Desea Eliminar la Solicitud?");
                    dp.setContentText("Esta Solicitud sera eliminada de tu lista");
                    dp.setConfirmText("Si!");
                    dp.setCancelText("No!");
                    dp.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://proyectotesis.ddns.net/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                            Call<String> call3 = tesisAPI.CancelarSolicitud(listasolicitudes.get(i).getIdSolicitud());
                            call3.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call3, Response<String> response) {
                                    if(!response.isSuccessful()){
                                        Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    else {

                                        listasolicitudes.remove(i);
                                        refresh(listasolicitudes);
                                      /* solicitudeFragment solicitudeFragment = new solicitudeFragment();
                                        FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.container, solicitudeFragment);
                                        ft.commit();*/
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call3, Throwable t) {
                                    Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            sDialog.dismissWithAnimation();
                        }
                    })    .show();
                 dp.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                            .show();
                }
            });
        }
        return vista;
    }
}
