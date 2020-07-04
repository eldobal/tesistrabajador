package com.example.tesistrabajador.clases;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
        }if(listasolicitudes.get(i).getEstado().equals("PENDIENTE")) {
            detalle.setText("Decidir");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));



            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(vista.getContext());
                    View viewsync = inflater.inflate(R.layout.alertconfirmacionnotificacion,null);
                    builder.setView(viewsync);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView textoalertnotificacion= (TextView) viewsync.findViewById(R.id.txtalertnotificacion);

                    Button btnconfirmar = viewsync.findViewById(R.id.btnconfirmarnotificacion);
                    Button btncancelar = viewsync.findViewById(R.id.btncancelarnotificacion);
                    EditText preciotrabajador = viewsync.findViewById(R.id.preciotrabajadornotificacion);
                    Button dismiss = viewsync.findViewById(R.id.btnocultaralert);

                    textoalertnotificacion.setText("Si Apreta el boton confirmar devera especificar un precio aprox y luego se le notificara al cliente" +
                            ". Si selecciona cancelar se eliminara esta solicitud y se le notificara de igual manera al cliente.(si no desea realizar ninguna accion seleeccione fuerta de este recuadro)");

                    dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btnconfirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!preciotrabajador.getText().toString().isEmpty()){

                                int precio = Integer.parseInt(preciotrabajador.getText().toString());
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                final String Fechasolicitud = sdf.format(calendar.getTime());
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://proyectotesis.ddns.net/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<String> call = tesisAPI.TrabajadorConfirmar(idsolicitud, Fechasolicitud, precio);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                        } else {
                                            listasolicitudes.remove(i);
                                            refresh(listasolicitudes);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
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
                            Call<String> call = tesisAPI.CancelarSolicitudt(idsolicitud);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(!response.isSuccessful()){
                                        Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        listasolicitudes.remove(i);
                                        refresh(listasolicitudes);
                                        dialog.dismiss();
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });


                }
            });
        }if(listasolicitudes.get(i).getEstado().equals("FINALIZANDO")){

            detalle.setText("finalizando");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));

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

        }if(listasolicitudes.get(i).getEstado().equals("COMPLETADA Y PAGADA")){
            detalle.setText("DETALLE");
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
        }


        return vista;
    }
}
