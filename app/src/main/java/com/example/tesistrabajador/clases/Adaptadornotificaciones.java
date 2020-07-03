package com.example.tesistrabajador.clases;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

                    String textocomparar = "Solicitud "+listanotificaciones.get(i).getIdSolicitud()+" fue cancelada";



                    String textocompararconfirmada = "Solicitud "+listanotificaciones.get(i).getIdSolicitud()+" fue confirmada";




                    if (listanotificaciones.get(i).getMensaje().equals(textocomparar)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View viewsync = inflater.inflate(R.layout.alernotificacioncancelada, null);
                        builder.setView(viewsync);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView textoalertnotificacion = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        Button dismiss2 = viewsync.findViewById(R.id.btnocultaralert2);
                        textoalertnotificacion.setText("La notificacion con el id: " + notificacion.getIdSolicitud() + " ha sido cancelada por el cliente" +
                                "lo cual significa que la solitud se ha eliminado ");

                        dismiss2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://proyectotesis.ddns.net/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<String> call = tesisAPI.EliminarSoliPermanente(listanotificaciones.get(i).getIdSolicitud());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                        } else {
                                            listanotificaciones.remove(i);
                                            refresh(listanotificaciones);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.dismiss();

                            }
                        });

                    } if(listanotificaciones.get(i).getMensaje().equals(textocompararconfirmada)){

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        View viewsync = inflater.inflate(R.layout.alertconfirmacionnotificacion, null);
                        builder.setView(viewsync);
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView textoalertnotificacion = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);

                        Button btnconfirmar = viewsync.findViewById(R.id.btnconfirmarnotificacion);
                        Button btncancelar = viewsync.findViewById(R.id.btncancelarnotificacion);
                        EditText preciotrabajador = viewsync.findViewById(R.id.preciotrabajadornotificacion);
                        Button dismiss = viewsync.findViewById(R.id.btnocultaralert);


                        textoalertnotificacion.setText("Si Apreta el boton confirmar devera especificar un precio aprox y luego se le notificara al cliente" +
                                ". Si selecciona cancelar se eliminara esta solicitud y se le notificara de igual manera al cliente.(si no desea realizar ninguna accion seleeccione fuerta de este recuadro)");

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });

                        btnconfirmar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!preciotrabajador.getText().toString().isEmpty()) {

                                    int precio = Integer.parseInt(preciotrabajador.getText().toString());
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    final String Fechasolicitud = sdf.format(calendar.getTime());
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("http://proyectotesis.ddns.net/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                    Call<String> call = tesisAPI.TrabajadorConfirmar(listanotificaciones.get(i).getIdSolicitud(), Fechasolicitud, precio);
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (!response.isSuccessful()) {
                                                Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                            } else {
                                                listanotificaciones.remove(i);
                                                refresh(listanotificaciones);
                                                dialog2.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(v.getContext(), "Si desea confirmar agregue un valor aproximado", Toast.LENGTH_LONG).show();
                                }

                            }
                        });


                        btncancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://proyectotesis.ddns.net/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<String> call = tesisAPI.CancelarSolicitudt(listanotificaciones.get(i).getIdSolicitud());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                        } else {
                                            listanotificaciones.remove(i);
                                            refresh(listanotificaciones);
                                            dialog2.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });


                    }
                }
            });


        return vista;
    }


}
