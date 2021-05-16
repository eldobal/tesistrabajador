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

public class Adaptador extends BaseAdapter implements Serializable {

    private static LayoutInflater inflater = null;
    Context contexto;
    ArrayList<Solicitud> listasolicitudes;
    ArrayList<Solicitud> lista;
    SharedPreferences prefs;
    String rutusuario="",contrasena="";
    Solicitud soli = new Solicitud();

    public Adaptador(Context contexto, ArrayList<Solicitud> listasolicitudes) {
        this.contexto = contexto;
        this.listasolicitudes = listasolicitudes;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

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
        prefs = contexto.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setcredentiasexist();
        final View vista = inflater.inflate(R.layout.elemento_solicitud, null);
        TextView numerosolicitud = (TextView) vista.findViewById(R.id.txtfilanumerosolicitud);
        TextView fechasolicitud = (TextView) vista.findViewById(R.id.txtfilafechasolicitud);
        TextView estadosolicitud = (TextView) vista.findViewById(R.id.txtfilaestadosolicitudelemento);
        TextView nombretrabajador = (TextView) vista.findViewById(R.id.txtfilanombretrabajador);
        ImageView fototrabajador = (ImageView) vista.findViewById(R.id.imgperfilfilasolicitud);
        final Button detalle = (Button) vista.findViewById(R.id.btndetallesolicitud);
        int idsolicitud = listasolicitudes.get(i).getIdSolicitud();
        String descripcion = listasolicitudes.get(i).getDescripcionP();
        numerosolicitud.setText("N Solicitud: "+String.valueOf(listasolicitudes.get(i).getIdSolicitud()));
        fechasolicitud.setText("Fecha: "+listasolicitudes.get(i).getFechaS());
        estadosolicitud.setText(listasolicitudes.get(i).getEstado());
        nombretrabajador.setText(listasolicitudes.get(i).getNombre()+" "+listasolicitudes.get(i).getApellido());

        soli.setIdSolicitud(listasolicitudes.get(i).getIdSolicitud());
        soli.setFechaS(listasolicitudes.get(i).getFechaS());
        soli.setEstado(listasolicitudes.get(i).getEstado());
        soli.setNombre(listasolicitudes.get(i).getNombre()+" "+listasolicitudes.get(i).getApellido());
        Glide.with(vista.getContext()).load(String.valueOf(listasolicitudes.get(i).getFotoT())).into(fototrabajador);

        final int posicion = i;
        detalle.setTag(i);

        if(listasolicitudes.get(i).getEstado().equals("ATENDIENDO") ||listasolicitudes.get(i).getEstado().equals("FINALIZADO") ) {
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
                    Bundle bundle = new Bundle();
                    bundle.putInt("idsolicitud", idsolicitud);
                    DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                    detalleSolicitudFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, detalleSolicitudFragment);
                    ft.commit();
                }
            });
        }

        if(listasolicitudes.get(i).getEstado().equals("CONFIRMADA")  ) {
            detalle.setVisibility(View.GONE);
            detalle.setText("Detalle");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("idsolicitud", idsolicitud);
                    DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                    detalleSolicitudFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, detalleSolicitudFragment);
                    ft.commit();
                }
            });
        }

        if(listasolicitudes.get(i).getEstado().equals("PENDIENTE")) {
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
                    textoalertnotificacion.setText("Descripción de la solicitud: "+descripcion+"");
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
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                final String Fechasolicitud = sdf.format(calendar.getTime());
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(GlobalInfo.Rutaservidor)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                Call<Object> call = tesisAPI.TrabajadorConfirmar(idsolicitud, Fechasolicitud, precio,rutusuario,contrasena);
                                call.enqueue(new Callback<Object>() {
                                    @Override
                                    public void onResponse(Call<Object> call, Response<Object> response) {
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
                                            listasolicitudes.remove(i);
                                            refresh(listasolicitudes);
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
                                    public void onFailure(Call<Object> call, Throwable t) {
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
                                    .baseUrl(GlobalInfo.Rutaservidor)
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
                                        listasolicitudes.remove(i);
                                        refresh(listasolicitudes);

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
            });
        }
        if(listasolicitudes.get(i).getEstado().equals("FINALIZANDO")){
            detalle.setText("finalizando");
            detalle.setBackgroundDrawable(ContextCompat.getDrawable(vista.getContext(), R.drawable.bg_ripplecancelar) );
            numerosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            fechasolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            estadosolicitud.setTextColor(vista.getResources().getColor(R.color.colordark));
            nombretrabajador.setTextColor(vista.getResources().getColor(R.color.colordark));
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("idsolicitud", idsolicitud);
                    DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                    detalleSolicitudFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, detalleSolicitudFragment);
                    ft.commit();
                }
            });
        }

        if(listasolicitudes.get(i).getEstado().equals("COMPLETA Y PAGADA") ||listasolicitudes.get(i).getEstado().equals("COMPLETA Y NO PAGADA")  ){
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
                    Bundle bundle = new Bundle();
                    bundle.putInt("idsolicitud", idsolicitud);
                    DetalleSolicitudFragment detalleSolicitudFragment = new DetalleSolicitudFragment();
                    detalleSolicitudFragment.setArguments(bundle);
                    FragmentManager fm = ((AppCompatActivity) contexto).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, detalleSolicitudFragment);
                    ft.commit();
                }
            });
        }


        return vista;
    }

    private void setcredentiasexist() {
        String rut = getuserrutprefs();
        String contraseña = getusercontraseñaprefs();
        if (!TextUtils.isEmpty(rut) && !TextUtils.isEmpty(contraseña)) {
            rutusuario=rut.toString();
            contrasena=contraseña.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }

}
