package com.example.tesistrabajador.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.clases.Adaptador;
import com.example.tesistrabajador.clases.Adaptadornotificaciones;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.interfaces.tesisAPI;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class solicitudesFragment extends Fragment  {

    SweetAlertDialog dp;
    LottieAnimationView loadinglista,listavacia,loadinglistaactiva,listaactivavacia;
    private static LayoutInflater inflater = null;
    private TextView tvNoRegistros;
    private ListView lista,listaactivas;
    private ImageButton btnVolver;
    private SharedPreferences prefs,asycprefs;
    private String rutusuario="",contrasenaperfil="";
    int azynctiempo =0,filtro=0,filtroterminada=0;
    ArrayList<Solicitud> listasolicitudesterminadas,listasolicitudactivas,listasolicitudactivasinterna,listasolicitudterminadasinterna,Solicitudescomparar,listasolicitudfinalizando;
    ArrayList<Solicitud> Solicitudes = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudesterminadas = new ArrayList<Solicitud>();
    SwipeRefreshLayout refreshLayout,refreshLayoutterminadas;
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    Adaptador ads,ads2;
    TextView notfound;
    NetworkInfo NetworkInfo;
    Spinner spinneractivas,spinnerterminadas;
    public solicitudesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_solicitudes, container, false);
        asycprefs = this.getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        loadinglista = (LottieAnimationView) v.findViewById(R.id.idanimacionlistasolicitud);
        listavacia = (LottieAnimationView) v.findViewById(R.id.idanimacionlistavacia);
        loadinglistaactiva = (LottieAnimationView) v.findViewById(R.id.idanimacionlistasolicitud2);
        listaactivavacia = (LottieAnimationView) v.findViewById(R.id.idanimacionlistavacia2);
        listavacia.setVisibility(View.INVISIBLE);
        listaactivavacia.setVisibility(View.INVISIBLE);
        //se buscan el usuario y el tiempo de sync de la app
        settiempoasyncexist();
        setcredentiasexist();

        Solicitudescomparar = new ArrayList<Solicitud>();
        listasolicitudesterminadas = new ArrayList<Solicitud>();
        listasolicitudactivas = new ArrayList<Solicitud>();
        listasolicitudterminadasinterna = new ArrayList<Solicitud>();
        listasolicitudactivasinterna = new ArrayList<Solicitud>();

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo = connectivityManager.getActiveNetworkInfo();


        spinneractivas =(Spinner) v.findViewById(R.id.spinnerordenar);
        spinnerterminadas =(Spinner) v.findViewById(R.id.spinnerordenarterminadas);

        String[] datos = new String[] {"Por Defecto","Pendiente","Confirmada"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, datos);

        String[] datos2 = new String[] {"Por Defecto","Completada y Pagada", "Completada y No Pagada"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, datos2);

       spinneractivas.setAdapter(adapter);
        spinnerterminadas.setAdapter(adapter2);


        if (NetworkInfo != null && NetworkInfo.isConnected()) {

        if(rutusuario.equals("")){
            //enviar al usuario hacia alguna pantalla de home y mostrar el error en forma de mensaje
            Intent intent = new Intent(getContext(), loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //linea que termina la ejecucion y no permite hacer onback
            getActivity().finish();
            Toast.makeText(getContext(), "el Usuario no es valido ", Toast.LENGTH_LONG).show();
        }else{
            ads = new Adaptador(getContext(), listasolicitudactivasinterna);
            ads2 = new Adaptador(getContext(), listasolicitudterminadasinterna);
          //  reiniciarfragmentterminadas(rutusuario);
            listaactivas = (ListView) v.findViewById(R.id.solicitudactual);
            lista = (ListView) v.findViewById(R.id.listadosolicitudescliente);
            //declaracion de los swiperefresh para intanciarlos
            refreshLayoutterminadas = v.findViewById(R.id.refreshterminadas);

            //notfound =(TextView) v.findViewById(R.id.txtnotfoundlistasolicitudes);
            //notfound.setText("");
            final View vista = inflater.inflate(R.layout.elemento_solicitud, null);

      /*      new CountDownTimer(1500,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if (listasolicitudterminadasinterna.size() != 0) {
                        //se setea el adaptador a la lista del fragments
                        lista.setAdapter(ads2);
                    }
                    if(listasolicitudactivasinterna.size()!=0){
                        listaactivas.setAdapter(ads);
                    }
                }
            }.start();
*/

            solicitudesFragment test = (solicitudesFragment) getActivity().getSupportFragmentManager().findFragmentByTag("solicitudtag");

            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(test != null && test.isVisible() && NetworkInfo.isConnected() && NetworkInfo !=null) {
                                    // reiniciarfragment(rutusuario);
                                    reiniciarfragmentterminadas(rutusuario);
                            }
                        }
                    });
                }
            };
            timer.schedule(task, 0, azynctiempo);  //ejecutar en intervalo definido por el programador


            refreshLayoutterminadas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new CountDownTimer(1500,1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                        @Override
                        public void onFinish() {
                            reiniciarfragmentterminadas(rutusuario);
                        }
                    }.start();
                }
            });



            spinneractivas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch(position) {
                        case 0:
                            filtro=0;
                            ordenarlista(0);
                            break;
                        case 1:
                            filtro=1;
                            ordenarlista(1);
                            break;
                        case 2:
                            filtro=2;
                            ordenarlista(2);
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            spinnerterminadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch(position) {
                        case 0:
                            filtroterminada=0;
                            ordenarlistaterminadas(0);
                            break;
                        case 1:
                            filtroterminada=1;
                            ordenarlistaterminadas(1);
                            break;
                        case 2:
                            filtroterminada=2;
                            ordenarlistaterminadas(2);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });






        }

        }else{
            getActivity().finish();
            Toast.makeText(getContext(), "Error en la conecctacion del dispocitivo, asegurese de que tenga coneccion", Toast.LENGTH_LONG).show();
        }

        return v;
    }



    private void ordenarlistaterminadas(int orden) {

        ArrayList<Solicitud> listasoliterminadas = new ArrayList<Solicitud>();
        if(orden ==0){
            Adaptador ad = new Adaptador(getContext(), listasolicitudterminadasinterna);
            lista.setAdapter(ad);
        }
        if(orden ==1){
            listasoliterminadas.clear();
            for (int i = 0; i <listasolicitudterminadasinterna.size() ; i++) {

                if(listasolicitudterminadasinterna.get(i).getEstado().equals("COMPLETADA Y PAGADA")){
                    listasoliterminadas.add(listasolicitudterminadasinterna.get(i));
                }
            }
            if(listasoliterminadas.size() != 0) {
                Adaptador ad = new Adaptador(getContext(), listasoliterminadas);
                ad.refresh(listasoliterminadas);
                lista.setAdapter(ad);
            }else{
                Adaptador ad= new Adaptador(getContext(), listasolicitudterminadasinterna);
                lista.setAdapter(ad);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alertdialogfiltroestrellas,null);
                builder.setView(viewsync);
                AlertDialog dialog7 = builder.create();
                dialog7.setCancelable(false);
                dialog7.show();
                dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                texto.setText("No se Han encontrado solicitudes con este estado. se mostrarà la lista por defecto");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog7.dismiss();
                    }
                });
            }
        }
        if(orden ==2){
            listasoliterminadas.clear();
            for (int i = 0; i <listasolicitudterminadasinterna.size() ; i++) {

                if(listasolicitudterminadasinterna.get(i).getEstado().equals("COMPLETADA Y NO PAGADA")){
                    listasoliterminadas.add(listasolicitudterminadasinterna.get(i));
                }
            }
            if(listasoliterminadas.size() != 0) {
                Adaptador ad = new Adaptador(getContext(), listasoliterminadas);
                ad.refresh(listasoliterminadas);
                lista.setAdapter(ad);
            }else{
                Adaptador ad= new Adaptador(getContext(), listasolicitudterminadasinterna);
                lista.setAdapter(ad);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alertdialogfiltroestrellas,null);
                builder.setView(viewsync);
                AlertDialog dialog7 = builder.create();
                dialog7.setCancelable(false);
                dialog7.show();
                dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                texto.setText("No se Han encontrado solicitudes con este estado. se mostrarà la lista por defecto");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog7.dismiss();
                    }
                });
            }
        }






    }

    private void ordenarlista(int orden) {
        ArrayList<Solicitud> listasoliactivas = new ArrayList<Solicitud>();
        if(orden ==0){
            Adaptador ad = new Adaptador(getContext(), listasolicitudactivasinterna);
            listaactivas.setAdapter(ad);
        }
        if(orden ==1){
            listasoliactivas.clear();
            for (int i = 0; i <listasolicitudactivasinterna.size() ; i++) {

                if(listasolicitudactivasinterna.get(i).getEstado().equals("PENDIENTE")){
                    listasoliactivas.add(listasolicitudactivasinterna.get(i));
                }
            }
            if(listasoliactivas.size() != 0) {
                Adaptador ad = new Adaptador(getContext(), listasoliactivas);
                ad.refresh(listasoliactivas);
                listaactivas.setAdapter(ad);
            }else{
                Adaptador ad= new Adaptador(getContext(), listasolicitudactivasinterna);
                listaactivas.setAdapter(ad);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alertdialogfiltroestrellas,null);
                builder.setView(viewsync);
                AlertDialog dialog7 = builder.create();
                dialog7.setCancelable(false);
                dialog7.show();
                dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                texto.setText("No se Han encontrado solicitudes con este estado. se mostrarà la lista por defecto");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog7.dismiss();
                    }
                });
            }
        }
        if(orden ==2){
            listasoliactivas.clear();
            for (int i = 0; i <listasolicitudactivasinterna.size() ; i++) {

                if(listasolicitudactivasinterna.get(i).getEstado().equals("CONFIRMADA")){
                    listasoliactivas.add(listasolicitudactivasinterna.get(i));
                }
            }
            if(listasoliactivas.size() != 0) {
                Adaptador ad = new Adaptador(getContext(), listasoliactivas);
                ad.refresh(listasoliactivas);
                listaactivas.setAdapter(ad);
            }else{
                Adaptador ad= new Adaptador(getContext(), listasolicitudactivasinterna);
                listaactivas.setAdapter(ad);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alertdialogfiltroestrellas,null);
                builder.setView(viewsync);
                AlertDialog dialog7 = builder.create();
                dialog7.setCancelable(false);
                dialog7.show();
                dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                texto.setText("No se Han encontrado solicitudes con este estado. se mostrarà la lista por defecto");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog7.dismiss();
                    }
                });
            }
        }

    }


    private void reiniciarfragmentterminadas(String rut) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Solicitud>> call = tesisAPI.TrabajadorSolicitudes(rut,contrasenaperfil);
        call.enqueue(new Callback<List<Solicitud>>() {
            @Override
            public void onResponse(Call<List<Solicitud>> call, Response<List<Solicitud>> response) {
                if (!response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog3 = builder.create();
                    dialog3.setCancelable(false);
                    dialog3.show();
                    dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de traer las listas de solicitudes. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog3.dismiss();
                        }
                    });

                    Toast.makeText(getContext(), "error/soli/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {

                    List<Solicitud> solicituds = response.body();
                    Solicitudesterminadas.clear();
                    listasolicitudterminadasinterna.clear();
                    listasolicitudactivasinterna.clear();
                 //  listasolicitudfinalizando.clear();
                    for (Solicitud solicitud : solicituds) {
                        Solicitud Solicitud1 = new Solicitud();
                        //se setean los valores del trabajador
                        Solicitud1.setIdSolicitud(solicitud.getIdSolicitud());
                        Solicitud1.setFechaS(solicitud.getFechaS());
                        Solicitud1.setNombre(solicitud.getNombre());
                        Solicitud1.setApellido(solicitud.getApellido());
                        Solicitud1.setEstado(solicitud.getEstado());
                        Solicitud1.setFotoT(rutaservidor+solicitud.getFotoT());
                        Solicitudesterminadas.add(Solicitud1);
                    }

                    for (int i = 0; i < Solicitudesterminadas.size(); i++) {
                        Solicitud soli = new Solicitud();
                        soli = Solicitudesterminadas.get(i);
                        if ( soli.getEstado().equals("PENDIENTE")  || soli.getEstado().equals("CONFIRMADA") ) {
                            listasolicitudactivasinterna.add(soli);
                        } if(soli.getEstado().equals("COMPLETADA Y PAGADA") || soli.getEstado().equals("COMPLETADA Y NO PAGADA")  ) {
                            listasolicitudterminadasinterna.add(soli);
                        }
                    }
                    //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                    if (listasolicitudterminadasinterna.size() != 0) {
                        //se instancia la recarga de los items que se encuentan en la lista de aceptadas / finalisadas
                        Adaptador adsnoti = new Adaptador(getContext(), listasolicitudterminadasinterna);
                        lista.setAdapter(adsnoti);

                        loadinglista.setVisibility(View.INVISIBLE);
                        loadinglista.pauseAnimation();

                        listavacia.setVisibility(View.INVISIBLE);
                        listavacia.pauseAnimation();
                      //  notfound.setText("");



                    }if (listasolicitudterminadasinterna.size()==0){

                        ordenarlista(0);
                        loadinglista.setVisibility(View.INVISIBLE);
                        loadinglista.pauseAnimation();

                        listavacia.setVisibility(View.VISIBLE);
                        listavacia.playAnimation();

                      //  notfound.setText("No Posee Solicitudes");

                    }

                    if(listasolicitudactivasinterna.size()!=0){
                        ordenarlistaterminadas(filtroterminada);
                        loadinglistaactiva.setVisibility(View.INVISIBLE);
                        loadinglistaactiva.pauseAnimation();

                        listaactivavacia.setVisibility(View.INVISIBLE);
                        listaactivavacia.pauseAnimation();

                    }
                    if(listasolicitudactivasinterna.size()==0){
                        ordenarlistaterminadas(0);
                        loadinglistaactiva.setVisibility(View.INVISIBLE);
                        loadinglistaactiva.pauseAnimation();

                        listaactivavacia.setVisibility(View.VISIBLE);
                        listaactivavacia.playAnimation();

                    }


                    spinneractivas.setVisibility(View.VISIBLE);
                    spinnerterminadas.setVisibility(View.VISIBLE);

                    refreshLayoutterminadas.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {
                Toast.makeText(getContext(), "error/soli/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
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
                        dialog4.dismiss();
                    }
                });
            }
        });

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
            rutusuario=rutq.toString();
            contrasenaperfil=contrasena.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }



    private void settiempoasyncexist() {
        int tiempoasync = gettiempoasync();
        if (tiempoasync!=0) {
            azynctiempo=tiempoasync;
        }
    }

    private int gettiempoasync() {
        return asycprefs.getInt("tiempo", 0);
    }



}