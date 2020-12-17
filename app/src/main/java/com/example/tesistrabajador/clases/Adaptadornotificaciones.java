package com.example.tesistrabajador.clases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.fragments.DetalleSolicitudFragment;
import com.example.tesistrabajador.fragments.solicitudesFragment;
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
    private static LayoutInflater inflater = null;
    Context contexto;
    ArrayList<Notificacion> listanotificaciones;
    SharedPreferences prefs;
    String rutusuario="",contrasena="";
    int pago = 0;
    Notificacion notificacion = new Notificacion();

    public Adaptadornotificaciones(Context contexto, ArrayList<Notificacion> listanotificaciones) {
        this.contexto = contexto;
        this.listanotificaciones = listanotificaciones;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

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
        prefs = contexto.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setcredentiasexist();
        final View vista = inflater.inflate(R.layout.elementonotificacion, null);
        CardView card = (CardView) vista.findViewById(R.id.cardnotificacion);
        TextView mensaje = (TextView) vista.findViewById(R.id.mensajenotificacion);
        mensaje.setText(listanotificaciones.get(i).getMensaje());
        notificacion.setId(listanotificaciones.get(i).getId());
        notificacion.setMensaje(listanotificaciones.get(i).getMensaje());
        notificacion.setRUT(listanotificaciones.get(i).getRUT());
        notificacion.setIdSolicitud(listanotificaciones.get(i).getIdSolicitud());
        final int posicion = i;
        card.setTag(i);

        int idsolicitud = listanotificaciones.get(i).getIdSolicitud();

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textocomparar = "Solicitud "+idsolicitud+" fue cancelada";
                    String textocompararconfirmada = "Solicitud "+idsolicitud+" ha sido confirmada";
                    String textonuevasolicitud = "Solicitud "+idsolicitud+ " ha sido finalizada";
                    String textosolicitudpagada = "Solicitud "+idsolicitud+" ha sido pagada mediante WebPay.";

                    if (listanotificaciones.get(i).getMensaje().equals(textocomparar)) {
                         int idsolicitud = listanotificaciones.get(i).getIdSolicitud();
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
                                        .baseUrl(GlobalInfo.Rutaservidor)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<String> call = tesisAPI.EliminarSoliPermanente(idsolicitud,rutusuario,contrasena);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                            View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog3 = builder.create();
                                            dialog3.setCancelable(false);
                                            dialog3.show();
                                            dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                            texto.setText("Ha ocurrido un error con la respuesta al tratar de eliminar esta notificacion. intente en un momento nuevamente.");
                                            Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                                            btncerrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    dialog3.dismiss();
                                                }
                                            });
                                        //    Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                        } else {
                                            listanotificaciones.remove(i);
                                            refresh(listanotificaciones);
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                        View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                        builder.setView(viewsync);
                                        AlertDialog dialog4 = builder.create();
                                        dialog4.setCancelable(false);
                                        dialog4.show();
                                        dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                        texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                                        Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                        btncerrar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                dialog4.dismiss();
                                            }
                                        });
                                     //   Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    }
                    if(listanotificaciones.get(i).getMensaje().equals(textocompararconfirmada)){
                        Bundle bundle = new Bundle();
                        //id de la solicitud para que se pueda buscar en el detalle
                        bundle.putInt("idsolicitud", idsolicitud);
                        DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                        detalleSolicitudFragment.setArguments(bundle);
                        FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.container, detalleSolicitudFragment);
                        ft.commit();
                    }
                    if(listanotificaciones.get(i).getMensaje().equals(textonuevasolicitud)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        View viewsync = inflater.inflate(R.layout.alertdialogconfirmarpago, null);
                        builder.setView(viewsync);
                        AlertDialog dialog7 = builder.create();
                        dialog7.setCancelable(false);
                        dialog7.show();
                        dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button dismiss = (Button) viewsync.findViewById(R.id.btncerraralert);
                        Button confirmacionpago = (Button) viewsync.findViewById(R.id.btnfinalizarsolicitud);
                        RadioButton r1 = (RadioButton) viewsync.findViewById(R.id.radioButton);
                        RadioButton r2 = (RadioButton) viewsync.findViewById(R.id.radioButton2);

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog7.dismiss();
                            }
                        });

                        confirmacionpago.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(r1.isChecked()==true){ pago=1; }
                                if(r2.isChecked()==true){ pago=0; }
                                if(r1.isChecked()==false && r2.isChecked()==false){
                                    Toast.makeText(v.getContext(), "seleccione una opcion por favor.", Toast.LENGTH_LONG).show();
                                }else{
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(GlobalInfo.Rutaservidor)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                    Call<String> call = tesisAPI.ConfirmarPago(rutusuario,contrasena,idsolicitud,pago);
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse( Call<String>call, Response<String> response) {
                                            if(!response.isSuccessful()){
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder(contexto);
                                                View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                                builder2.setView(viewsync);
                                                AlertDialog dialog8 = builder2.create();
                                                dialog8.setCancelable(false);
                                                dialog8.show();
                                                dialog8.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                                texto.setText("Ha ocurrido un error con la respuesta al tratar de confirmar el pago. intente en un momento nuevamente.");
                                                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                                                btncerrar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog7.dismiss();
                                                        dialog8.dismiss();
                                                    }
                                                });
                                             //   Toast.makeText(contexto, "error/detalle/finalizar/onresponse :"+response.code(), Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                                View viewsync2 = inflater.inflate(R.layout.alertdialogperfilactualizado, null);
                                                builder.setView(viewsync2);
                                                AlertDialog dialog9 = builder.create();
                                                dialog9.show();
                                                dialog9.setCancelable(false);
                                                dialog9.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                Button btncerraralert = viewsync2.findViewById(R.id.btnalertperfilexito);
                                                TextView texto = (TextView) viewsync2.findViewById(R.id.txtalertnotificacion);
                                                texto.setText("Ha enviado su respuesta Satisfactoriamente.");
                                                btncerraralert.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog9.dismiss();
                                                        dialog7.dismiss();
                                                        solicitudesFragment solicitudesFragment = new solicitudesFragment();
                                                        FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                                                        FragmentTransaction ft = fm.beginTransaction();
                                                        ft.addToBackStack(null);
                                                        ft.replace(R.id.container, solicitudesFragment,"solicitudtag");
                                                        ft.commit();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                            View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog10 = builder.create();
                                            dialog10.setCancelable(false);
                                            dialog10.show();
                                            dialog10.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                            texto.setText("Ha ocurrido un error con la coneccion del servidor. Estamos trabajando para solucionarlo.");
                                            Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                            btncerrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog7.dismiss();
                                                    dialog10.dismiss(); }
                                            });
                                         //   Toast.makeText(contexto, "error/detalle/finalizar/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    if(listanotificaciones.get(i).getMensaje().equals(textosolicitudpagada)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View viewsync = inflater.inflate(R.layout.alernotificacioncancelada, null);
                        builder.setView(viewsync);
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        TextView textoalertnotificacion = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        Button dismiss = viewsync.findViewById(R.id.btnocultaralert2);
                        textoalertnotificacion.setText("La notificacion con el id: " + notificacion.getId() + " ha sido pagada por el cliente lo cual significa que la solitud ha sido completada en su totalidad");
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int idnotificacion = listanotificaciones.get(i).getId();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(GlobalInfo.Rutaservidor)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<String> call = tesisAPI.borrarNotificacion(rutusuario,contrasena,idnotificacion);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                            View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog8 = builder.create();
                                            dialog8.setCancelable(false);
                                            dialog8.show();
                                            dialog8.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                            texto.setText("Ha ocurrido un error con la respuesta al tratar de eliminar esta notificacion. intente en un momento nuevamente.");
                                            Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                                            btncerrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog2.dismiss();
                                                    dialog8.dismiss();
                                                }
                                            });
                                          //  Toast.makeText(v.getContext(), "error :" + response.code()+" "+idnotificacion, Toast.LENGTH_LONG).show();
                                        } else {
                                            listanotificaciones.remove(i);
                                            refresh(listanotificaciones);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(vista.getContext());
                                            View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizado,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog5 = builder.create();
                                            dialog5.setCancelable(false);
                                            dialog5.show();
                                            dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                            texto.setText("La Solicitud ha sigo completada en su totalidad!");
                                            Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);

                                            btncerraralert.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog5.dismiss();
                                                    dialog2.dismiss();
                                                }
                                            });
                                        }
                                    }@Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                        View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                        builder.setView(viewsync);
                                        AlertDialog dialog9 = builder.create();
                                        dialog9.setCancelable(false);
                                        dialog9.show();
                                        dialog9.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                        texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                                        Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                        btncerrar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog2.dismiss();
                                                dialog9.dismiss();
                                            }
                                        });
                                      //  Toast.makeText(v.getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                    if(!listanotificaciones.get(i).getMensaje().equals(textocomparar) && !listanotificaciones.get(i).getMensaje().equals(textosolicitudpagada) && !listanotificaciones.get(i).getMensaje().equals(textocompararconfirmada)  && !listanotificaciones.get(i).getMensaje().equals(textonuevasolicitud) ){
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
                        textoalertnotificacion.setText("Si Apreta el boton confirmar devera especificar un precio aprox.Si selecciona cancelar se eliminara esta solicitud");
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnconfirmar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!preciotrabajador.getText().toString().isEmpty() ){
                                    int precio = Integer.parseInt(preciotrabajador.getText().toString());
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    final String Fechasolicitud = sdf.format(calendar.getTime());
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(GlobalInfo.Rutaservidor)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();
                                    tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                    Call<String> call = tesisAPI.TrabajadorConfirmar(idsolicitud, Fechasolicitud, precio,rutusuario,contrasena);
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (!response.isSuccessful()) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                                View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                                builder.setView(viewsync);
                                                AlertDialog dialog4 = builder.create();
                                                dialog4.setCancelable(false);
                                                dialog4.show();
                                                dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                                texto.setText("Ha ocurrido un error con la respuesta al tratar de eliminar esta notificacion. intente en un momento nuevamente.");
                                                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                                                btncerrar.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        dialog4.dismiss();
                                                    }
                                                });
                                                Toast.makeText(v.getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                                            } else {
                                                solicitudesFragment soli = new solicitudesFragment();
                                                FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.replace(R.id.container, soli,"solicitudtag");
                                                ft.commit();
                                                listanotificaciones.remove(i);
                                                refresh(listanotificaciones);
                                                dialog.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(vista.getContext());
                                                View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizado,null);
                                                builder.setView(viewsync);
                                                AlertDialog dialog2 = builder.create();
                                                dialog2.show();
                                                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                                texto.setText("Felicitaciones Ha enviado su respuesta satisfactoriamente!");
                                                Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);
                                                btncerraralert.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog2.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                            View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog4 = builder.create();
                                            dialog4.setCancelable(false);
                                            dialog4.show();
                                            dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                            texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                                            Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                            btncerrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    dialog4.dismiss();
                                                }
                                            });
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
                                Call<String> call = tesisAPI.CancelarSolicitudt(idsolicitud,rutusuario,contrasena);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(!response.isSuccessful()){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                            View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog5 = builder.create();
                                            dialog5.setCancelable(false);
                                            dialog5.show();
                                            dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                            texto.setText("Ha ocurrido un error con la respuesta al tratar de eliminar esta notificacion. intente en un momento nuevamente.");
                                            Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);
                                            btncerrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    dialog5.dismiss();
                                                }
                                            });
                                            Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            solicitudesFragment soli = new solicitudesFragment();
                                            FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            ft.replace(R.id.container, soli,"solicitudtag");
                                            ft.commit();
                                            dialog.dismiss();
                                            listanotificaciones.remove(i);
                                            refresh(listanotificaciones);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(vista.getContext());
                                            View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizado,null);
                                            builder.setView(viewsync);
                                            AlertDialog dialog3 = builder.create();
                                            dialog3.setCancelable(false);
                                            dialog3.show();
                                            dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                            texto.setText("Felicitaciones Ha cancelado la solicitud satisfactoriamente!");
                                            Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);
                                            btncerraralert.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog3.dismiss();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                        View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                        builder.setView(viewsync);
                                        AlertDialog dialog6 = builder.create();
                                        dialog6.setCancelable(false);
                                        dialog6.show();
                                        dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                        texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                                        Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                        btncerrar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                dialog6.dismiss();
                                            }
                                        });
                                        Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        return vista;
    }

    private void setcredentiasexist() {
        String rutq = getuserrutprefs();
        String contrasena2 = getusercontraseñaprefs();
        if (!TextUtils.isEmpty(rutq)&& (!TextUtils.isEmpty(contrasena2)) ) {
            rutusuario=rutq.toString();
            contrasena=contrasena2.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }

}
