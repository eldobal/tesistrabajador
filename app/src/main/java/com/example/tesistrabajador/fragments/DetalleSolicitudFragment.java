package com.example.tesistrabajador.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.interfaces.tesisAPI;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

public class DetalleSolicitudFragment extends Fragment {
    SweetAlertDialog dp;
    Solicitud solicitud = new Solicitud();
    private TextView numerosolicitud,fechasolicitud,fechadetallesolicitud,cliente,trabajador,rubro,precio,estadosolicitud,descripciondetallesolicitud,diagnosticodetallesolicitud,soluciondetallesolicitud;
    private ImageView imgperfiltrabajador,imgclientesacada;
    SharedPreferences prefs;
    private Button btncomollegar, btnfinalizar,btnconfirmarpago;
    private int idsolicitud=0;
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    private String rutperfil ="",contrasenaperfil="";
    double latitud=0.0,longitud=0.0;
    int pago = 0;
    CardView carddiagnostico;

    public DetalleSolicitudFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        numerosolicitud = (TextView) getActivity().findViewById(R.id.txtnumerosolicitud);
        fechasolicitud = (TextView)getActivity().findViewById(R.id.txtfechasolicitud);
        fechadetallesolicitud = (TextView)getActivity().findViewById(R.id.txtfechadetallesolicitud);
        trabajador = (TextView)getActivity().findViewById(R.id.txttrabajadorsolicituddetalle);
        rubro = (TextView)getActivity().findViewById(R.id.txtrubrosolicituddetalle);
        precio = (TextView)getActivity().findViewById(R.id.txtpreciosolicitud);
        estadosolicitud =(TextView)getActivity().findViewById(R.id.txtestadosolicitud);
        descripciondetallesolicitud =(TextView)getActivity().findViewById(R.id.txtdescripciondetallesolicitud);
        diagnosticodetallesolicitud =(TextView)getActivity().findViewById(R.id.txtdiagnosticodetallesolicitud);
        soluciondetallesolicitud =(TextView)getActivity().findViewById(R.id.txtsoluciondetallesolicitud);
        imgperfiltrabajador =(ImageView)getActivity().findViewById(R.id.imgperfilfilasolicitud);
        imgclientesacada =(ImageView)getActivity().findViewById(R.id.imgclientesacada);
        carddiagnostico =(CardView) getActivity().findViewById(R.id.carddiagnostico);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detalle_solicitud, container, false);
        btnfinalizar = (Button) v.findViewById(R.id.btnfinalizarsolicitud);
        btnconfirmarpago = (Button) v.findViewById(R.id.confirmarpagoefectivo);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setcredentiasexist();
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null) {
            idsolicitud = datosRecuperados.getInt("idsolicitud");
        }

        btncomollegar = (Button) v.findViewById(R.id.btncomollegar);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<Solicitud> call = tesisAPI.getSolicitudTrabajador(idsolicitud,rutperfil,contrasenaperfil);
        call.enqueue(new Callback<Solicitud>() {
            @Override
            public void onResponse(Call<Solicitud> call, Response<Solicitud> response) {
                if(!response.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de traer la solicitud. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    //falta alert dialog para avisar del error
                    Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                }
                else {
                    Solicitud solicituds = response.body();
                    if(solicituds.getEstado().equals("FINALIZADO")){
                        btncomollegar.setVisibility(View.GONE);
                        btnfinalizar.setVisibility(View.GONE);
                        btnconfirmarpago.setVisibility(View.VISIBLE);
                    }
                    if(solicituds.getEstado().equals("FINALIZANDO")){
                        btncomollegar.setVisibility(View.GONE);
                        btnfinalizar.setVisibility(View.GONE);
                        btnconfirmarpago.setVisibility(View.GONE);
                    }
                    if(solicituds.getEstado().equals("COMPLETADA Y PAGADA") || solicituds.getEstado().equals("COMPLETADA Y NO PAGADA")){
                        btncomollegar.setVisibility(View.GONE);
                        btnfinalizar.setVisibility(View.GONE);
                        btnconfirmarpago.setVisibility(View.GONE);
                        //invisible por mientras
                        carddiagnostico.setVisibility(View.GONE);
                       // diagnosticodetallesolicitud.setText(solicituds.getDiagnostico());
                    }if (solicituds.getEstado().equals("PENDIENTE") || solicituds.getEstado().equals("ATENDIENDO") ){
                        btncomollegar.setVisibility(View.VISIBLE);
                        btnfinalizar.setVisibility(View.VISIBLE);
                    }
                    numerosolicitud.setText("N Solicitud: "+solicituds.getIdSolicitud());
                    fechasolicitud.setText("Creada: "+solicituds.getFechaS());
                    fechadetallesolicitud.setText("Atendida: "+solicituds.getFechaA());
                    trabajador.setText("Rut Cliente: "+solicituds.getRUT());
                    rubro.setText("Rubro: "+solicituds.getRubro());
                    if(solicituds.getEstado().equals("COMPLETADA Y PAGADA") || solicituds.getEstado().equals("COMPLETADA Y NO PAGADA") || solicituds.getEstado().equals("FINALIZADO")  ){
                        precio.setText("Precio final: "+solicituds.getPrecio());
                    }else {
                        precio.setText("Precio aprox: " + solicituds.getPrecio());
                    }
                    estadosolicitud.setText("Estado : "+solicituds.getEstado());
                    descripciondetallesolicitud.setText(solicituds.getDescripcionP());
                    soluciondetallesolicitud.setText(solicituds.getSolucion());
                    latitud=(solicituds.getLatitud());
                    longitud=solicituds.getLongitud();
                    //carga de la foto del trabajor
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+solicituds.getFotoT())).into(imgperfiltrabajador);
                    //carga de foto cargada por el usuario
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+solicituds.getIdFoto())).into(imgclientesacada);
                }
            }
            @Override
            public void onFailure(Call<Solicitud> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                builder.setView(viewsync);
                AlertDialog dialog2 = builder.create();
                dialog2.setCancelable(false);
                dialog2.show();
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


            btncomollegar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //se envia la latitud y longitud para poder generar la ruta
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitudcliente", latitud);
                    bundle.putDouble("longitudcliente", longitud);
                    comollegarFragment comollegarFragment = new comollegarFragment();
                    comollegarFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.container,comollegarFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();
                }
            });




        btnfinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se finalizará la solicitud desde la parte del trabajador
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alertdialogfinalizarsolicitud,null);
                builder.setView(viewsync);
                AlertDialog dialog3 = builder.create();
                dialog3.setCancelable(false);
                dialog3.show();
                dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText preciofinal = (EditText) viewsync.findViewById(R.id.preciofinalizarsolicitud);
                EditText solucion = (EditText) viewsync.findViewById(R.id.solucionfinalizarsolicitud);
                Button btndismiss = viewsync.findViewById(R.id.btncerraralert);
                Button btnfinalizar = viewsync.findViewById(R.id.btnfinalizarsolicitud);

                btndismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog3.dismiss();
                    }
                });

                //metodo el cual finaliza la solicitud por parte del trabajador
                btnfinalizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int precio = Integer.parseInt(preciofinal.getText().toString());
                        String solucionopcional = solucion.getText().toString();
                        if(precio != 0){
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://proyectotesis.ddns.net/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                            Call<String> call = tesisAPI.finalizarSolicitud(rutperfil,contrasenaperfil,idsolicitud,precio,solucionopcional);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse( Call<String>call, Response<String> response) {
                                    //si esta malo se ejecuta este trozo
                                    if(!response.isSuccessful()){
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                        LayoutInflater inflater = getLayoutInflater();
                                        View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                                        builder2.setView(viewsync);
                                        AlertDialog dialog4 = builder2.create();
                                        dialog4.setCancelable(false);
                                        dialog4.show();
                                        dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                        texto.setText("Ha ocurrido un error con la respuesta al tratar de finalizar la solicitud. intente en un momento nuevamente.");
                                        Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                                        btncerrar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog3.dismiss();
                                                dialog4.dismiss();
                                                //enviar a la lista de solicitudes
                                                homeFragment homeFragment = new homeFragment();
                                                FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.replace(R.id.container, homeFragment);
                                                ft.commit();
                                            }
                                        });
                                        Toast.makeText(getContext(), "error/detalle/finalizar/onresponse :"+response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    //de lo contrario se ejecuta esta parte
                                    else {
                                        //respuesta del request
                                        String respusta = response.body();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            View viewsync2 = inflater.inflate(R.layout.alertdialogperfilactualizado,null);
                                            builder.setView(viewsync2);
                                            AlertDialog dialog5 = builder.create();
                                            dialog5.show();
                                            dialog5.setCancelable(false);
                                            dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            Button btncerraralert = viewsync2.findViewById(R.id.btnalertperfilexito);
                                            TextView texto2  = (TextView) viewsync2.findViewById(R.id.txtalertnotificacion);
                                            texto2.setText("Ha Iniciado la finalizacion de la solicitud Satisfactoriamente.");
                                            btncerraralert.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //enviar a la lista de solicitudes
                                                    homeFragment home = new homeFragment();
                                                    FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                                                    FragmentTransaction ft = fm.beginTransaction();
                                                    ft.replace(R.id.container, home,"hometag");
                                                    ft.commit();
                                                    dialog5.dismiss();
                                                    dialog3.dismiss();
                                                }
                                            });


                                    }
                                }

                                //si falla el request a la pagina mostrara este error
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    LayoutInflater inflater = getLayoutInflater();
                                    View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                                    builder.setView(viewsync);
                                    AlertDialog dialog6 = builder.create();
                                    dialog6.setCancelable(false);
                                    dialog6.show();
                                    dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                                    texto.setText("Ha ocurrido un error con la coneccion del servidor. Estamos trabajando para solucionarlo.");
                                    Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                                    btncerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog3.dismiss();
                                            dialog6.dismiss();
                                            //enviar a la lista de solicitudes
                                            solicitudesFragment solicitudesFragment = new solicitudesFragment();
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, solicitudesFragment, "solicitudtag")
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    //permite regresar hacia atras entre los fragments
                                                    //.addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    Toast.makeText(getContext(), "error/detalle/finalizar/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }else{
                            Toast.makeText(v.getContext(), "Ingrese un numero valido", Toast.LENGTH_LONG).show();
                        }
                    }
                });





            }
        });



        btnconfirmarpago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alert para saber si el pago estuvo weno
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
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
                        if(r1.isChecked()==true){
                            pago=1;
                            Toast.makeText(v.getContext(), "1", Toast.LENGTH_LONG).show();
                        }
                        if(r2.isChecked()==true){
                            pago=0;
                            Toast.makeText(v.getContext(), "0", Toast.LENGTH_LONG).show();
                        }
                        if(r1.isChecked()==false && r2.isChecked()==false){
                            Toast.makeText(v.getContext(), "seleccione una opcion por favor.", Toast.LENGTH_LONG).show();
                        }else{
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://proyectotesis.ddns.net/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                            //metodo para llamar a la funcion que queramos
                            //llamar a la funcion de get usuario la cual se le envia los datos (rut y contraseña )
                            Call<String> call = tesisAPI.ConfirmarPago(rutperfil,contrasenaperfil,idsolicitud,pago);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse( Call<String>call, Response<String> response) {
                                    //si esta malo se ejecuta este trozo
                                    if(!response.isSuccessful()){
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                        LayoutInflater inflater = getLayoutInflater();
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
                                                //enviar a la lista de solicitudes

                                            }
                                        });

                                        Toast.makeText(getContext(), "error/detalle/finalizar/onresponse :"+response.code(), Toast.LENGTH_LONG).show();
                                    }
                                    //de lo contrario se ejecuta esta parte
                                    else {
                                        //respuesta del request

                                        String respusta = response.body();
                                        btnfinalizar.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, solicitudesFragment, "solicitudtag")
                                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                        //permite regresar hacia atras entre los fragments
                                                        //.addToBackStack(null)
                                                        .commit();
                                              }
                                        });

                                    }
                                }
                                //si falla el request a la pagina mostrara este error
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    LayoutInflater inflater = getLayoutInflater();
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

                                    Toast.makeText(getContext(), "error/detalle/finalizar/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });


        return v;
    }


    //metodo que permite elejir un fragment
    private void showSelectedFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                //permite regresar hacia atras entre los fragments
                //.addToBackStack(null)
                .commit();
    }

    private void setcredentiasexist() {
        String rutq = getuserrutprefs();
        String contrasena = getusercontraseñaprefs();
        if (!TextUtils.isEmpty(rutq)&& (!TextUtils.isEmpty(contrasena)) ) {
            rutperfil=rutq.toString();
            contrasenaperfil=contrasena.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }

}
