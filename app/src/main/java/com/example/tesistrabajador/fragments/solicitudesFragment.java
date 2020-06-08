package com.example.tesistrabajador.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.clases.Adaptador;
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
    private static LayoutInflater inflater = null;
    private TextView tvNoRegistros;
    private ListView lista,listaactivas;
    private ImageButton btnVolver;
    private SharedPreferences prefs,asycprefs;
    private String rutusuario;
    int azynctiempo =0;
    ArrayList<Solicitud> listasolicitudesterminadas,listasolicitudactivas,listasolicitudactivasinterna,listasolicitudterminadasinterna,Solicitudescomparar;
    ArrayList<Solicitud> Solicitudes = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudesterminadas = new ArrayList<Solicitud>();
    SwipeRefreshLayout refreshLayout,refreshLayoutterminadas;
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    Adaptador ads,ads2;
    NetworkInfo NetworkInfo;
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

        Solicitudescomparar = new ArrayList<Solicitud>();
        listasolicitudesterminadas  = new ArrayList<Solicitud>();
        listasolicitudactivas  = new ArrayList<Solicitud>();
        listasolicitudactivasinterna   = new ArrayList<Solicitud>();
        listasolicitudterminadasinterna   = new ArrayList<Solicitud>();
      //  listasolicitudactivas = (ArrayList<Solicitud>) getArguments().getSerializable("arraylistaspendientes");
      //  listasolicitudesterminadas = (ArrayList<Solicitud>) getArguments().getSerializable("arraylistasterminadas");

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_solicitudes, container, false);
        asycprefs = this.getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rutusuario="fdsfsds";
        settiempoasyncexist();
        setcredentiasexist();
        reiniciarfragment(rutusuario);
        reiniciarfragmentterminadas(rutusuario);
        lista = (ListView) v.findViewById(R.id.listadosolicitudescliente);
        //declaracion de los swiperefresh para intanciarlos
        refreshLayoutterminadas = v.findViewById(R.id.refreshterminadas);

        //comprueba si es que existe coneccion
        if (NetworkInfo != null && NetworkInfo.isConnected()) {

            if (rutusuario.isEmpty()){
                //enviar al usuario hacia alguna pantalla de home y mostrar el error en forma de mensaje
                Intent intent = new Intent(getContext(), loginActivity.class);
                startActivity(intent);
            }else {
                //if (Solicitudes.size() > 0) {
                final View vista = inflater.inflate(R.layout.elemento_solicitud, null);
                //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                if (listasolicitudactivas.size() != 0) {
                    ads = new Adaptador(getContext(), listasolicitudactivas);
                    //se setea el adaptador a la lista del fragments
                    listaactivas.setAdapter(ads);
                }
                if (listasolicitudesterminadas.size() != 0) {
                    ads2 = new Adaptador(getContext(), listasolicitudesterminadas);
                    //se setea el adaptador a la lista del fragments
                    lista.setAdapter(ads2);
                }

            }


            final Handler handler = new Handler();
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                //Ejecuta tu AsyncTask!
                                reiniciarfragment(rutusuario);
                                reiniciarfragmentterminadas(rutusuario);
                            } catch (Exception e) {
                                Log.e("error", e.getMessage());
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

            /*
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new CountDownTimer(1000,1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                        @Override
                        public void onFinish() {
                            reiniciarfragment(rutusuario);
                        }
                    }.start();
                }
            });
            */
        }else{
            //manejar excepcion

        }

        return v;
    }

    private void reiniciarfragment(String rut) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Solicitud>> call = tesisAPI.getSolicitudes(rut);
        call.enqueue(new Callback<List<Solicitud>>() {
            @Override
            public void onResponse(Call<List<Solicitud>> call, Response<List<Solicitud>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    List<Solicitud> solicituds = response.body();
                    Solicitudes.clear();
                    listasolicitudactivas.clear();
                    for (Solicitud solicitud : solicituds) {
                        Solicitud Solicitud1 = new Solicitud();
                        //se setean los valores del trabajador
                        Solicitud1.setIdSolicitud(solicitud.getIdSolicitud());
                        Solicitud1.setFechaS(solicitud.getFechaS());
                        Solicitud1.setNombre(solicitud.getNombre());
                        Solicitud1.setApellido(solicitud.getApellido());
                        Solicitud1.setEstado(solicitud.getEstado());
                        Solicitud1.setFotoT(rutaservidor+solicitud.getFotoT());
                        Solicitudes.add(Solicitud1);
                    }
                    listasolicitudactivas.clear();
                    for (int i = 0; i < Solicitudes.size(); i++) {
                        Solicitud soli = new Solicitud();
                        soli = Solicitudes.get(i);

                        if (soli.getEstado().equals("PENDIENTE") || soli.getEstado().equals("ATENDIENDO") || soli.getEstado().equals("CONFIRMADA")  ) {
                            listasolicitudactivas.add(soli);
                        }else{

                        }
                    }
                    //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                    if (listasolicitudactivas.size() != 0) {
                        //se instancia la recarga de los items que se encuentan en la lista de activas / pendientes
                        ads.refresh(listasolicitudactivas);
                    }
               //  refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {
                Toast.makeText(getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void reiniciarfragmentterminadas(String rut) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Solicitud>> call = tesisAPI.getSolicitudes(rut);
        call.enqueue(new Callback<List<Solicitud>>() {
            @Override
            public void onResponse(Call<List<Solicitud>> call, Response<List<Solicitud>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    List<Solicitud> solicituds = response.body();
                    Solicitudesterminadas.clear();
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
                    listasolicitudterminadasinterna.clear();
                    for (int i = 0; i < Solicitudesterminadas.size(); i++) {
                        Solicitud soli = new Solicitud();
                        soli = Solicitudesterminadas.get(i);
                        if (soli.getEstado().equals("COMPLETADA Y PAGADA") || soli.getEstado().equals("COMPLETADA Y NO PAGADA") ) {
                            listasolicitudterminadasinterna.add(soli);
                        } else {

                        }
                    }
                    //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                    if (listasolicitudterminadasinterna.size() != 0) {
                        //se instancia la recarga de los items que se encuentan en la lista de aceptadas / finalisadas
                        ads2.refresh(listasolicitudterminadasinterna);
                    }
                    refreshLayoutterminadas.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {
                Toast.makeText(getContext(), "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
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
    //metodo para traer el rut del usuario hacia la variable local
    private void setcredentiasexist() {
        String rut = getuserrutprefs();
        if (!TextUtils.isEmpty(rut)) {
            rutusuario=rut.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
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