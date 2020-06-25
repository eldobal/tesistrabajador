package com.example.tesistrabajador.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.clases.Adaptador;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.clases.UsuarioTrabajador;
import com.example.tesistrabajador.clases.UsuarioTrabajadorhome;
import com.example.tesistrabajador.interfaces.tesisAPI;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class homeFragment extends Fragment {

    public static final String tag = "homefragment";

    private float[] ydata = {33.00f ,23.0f ,30.22f,33.44f,12.33f};
    private String[] xdata ={"sebastian ","daniel","cristobal","pablo","ricardo"};

    TextView notfound;
    LottieAnimationView loadinglista,loadingperfil;
    private ListView listaactivas;
    private SharedPreferences prefs,asycprefs;
    private String rutusuario="";
    int azynctiempo =0;
    ArrayList<Solicitud> listasolicitudesterminadas,listasolicitudactivas,listasolicitudactivasinterna,solicitudinterna,Solicitudescomparar;
    ArrayList<Solicitud> Solicitudes = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudactual = new ArrayList<Solicitud>();
    Adaptador ads,ads2;
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    String estadotrabajador = "";
    NetworkInfo NetworkInfo;
    ImageView fotoperfil;
    TextView nombretrabajdor;
    Button btncambiodeestado;




    PieChart pieChart;
    public homeFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Solicitudescomparar = new ArrayList<Solicitud>();
        listasolicitudesterminadas  = new ArrayList<Solicitud>();
        listasolicitudactivas  = new ArrayList<Solicitud>();
        listasolicitudactivasinterna   = new ArrayList<Solicitud>();
        solicitudinterna   = new ArrayList<Solicitud>();
        //  listasolicitudactivas = (ArrayList<Solicitud>) getArguments().getSerializable("arraylistaspendientes");
        // listasolicitudesterminadas = (ArrayList<Solicitud>) getArguments().getSerializable("arraylistasterminadas");
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);

        pieChart = (PieChart) v.findViewById(R.id.piechart);
        notfound = (TextView) v.findViewById(R.id.txtnotfoundhome);
        nombretrabajdor =(TextView) v.findViewById(R.id.txthomenombre);
        btncambiodeestado =(Button) v.findViewById(R.id.btncambiodeestadotrabajador);
        fotoperfil = (ImageView) v.findViewById(R.id.idimagenperfiltrabajador);

        asycprefs = this.getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        loadinglista = (LottieAnimationView) v.findViewById(R.id.idanimacionlistasolicitud);
        loadingperfil = (LottieAnimationView) v.findViewById(R.id.loadinglistaimgperfilhome);
       // loadingperfil.setVisibility(View.VISIBLE);
       // loadingperfil.playAnimation();

        //se buscan el usuario y el tiempo de sync de la app
        settiempoasyncexist();
        setcredentiasexist();

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
                //se cargan los datos del trabajador
                cargardatostrabajador();
                ads2 = new Adaptador(getContext(), solicitudinterna);
                reiniciarfragmentterminadas(rutusuario);
                listaactivas = (ListView) v.findViewById(R.id.solicitudactual);
                //declaracion de los swiperefresh para intanciarlos
               // refreshLayoutterminadas = v.findViewById(R.id.refreshterminadas);
                notfound.setText("");






                btncambiodeestado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cambiarestadotrabajador();
                    }
                });


                new CountDownTimer(1500,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        if (solicitudinterna.size() != 0) {
                            //se setea el adaptador a la lista del fragments
                            listaactivas.setAdapter(ads2);
                        }
                    }
                }.start();



                final Handler handler = new Handler();
                Timer timer = new Timer();

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                if (isAdded() && isVisible() && getUserVisibleHint()) {
                                    // ... do your thing
                                    try {
                                        //Ejecuta tu AsyncTask!
                                        // reiniciarfragment(rutusuario);
                                        reiniciarfragmentterminadas(rutusuario);
                                    } catch (Exception e) {
                                        Log.e("error", e.getMessage());
                                    }
                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 15000, azynctiempo);  //ejecutar en intervalo definido por el programador



              /*  refreshLayoutterminadas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
               */


            }


        }else{
            getActivity().finish();
            Toast.makeText(getContext(), "Error en la conecctacion del dispocitivo, asegurese de que tenga coneccion", Toast.LENGTH_LONG).show();
        }



        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("$");
        pieChart.setCenterTextSize(10);
        pieChart.setDrawEntryLabels(true);
        addDataSet();






        return v;
    }




    private void cambiarestadotrabajador() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<String> call = tesisAPI.CambiarEstadoTrabajador(rutusuario);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "error/homedatosESTADO/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {

                    String msgestado = response.body();

                    if(estadotrabajador.equals("Disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeinactivo);
                        btncambiodeestado.setText("No Disponible");
                        estadotrabajador.equals("No disponible");
                    }
                    if(estadotrabajador.equals("No disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeactivo);
                        btncambiodeestado.setText("Disponible");
                        estadotrabajador.equals("Disponible");
                    }

                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "error/homedatosESTADO/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }





    private void cargardatostrabajador() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<UsuarioTrabajadorhome> call = tesisAPI.TrabajadorHome(rutusuario);
        call.enqueue(new Callback<UsuarioTrabajadorhome>() {
            @Override
            public void onResponse(Call<UsuarioTrabajadorhome> call, Response<UsuarioTrabajadorhome> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "error/homedatos/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    UsuarioTrabajadorhome usuarioTrabajador = response.body();
                    String rutaurl=usuarioTrabajador.getFoto();
                    nombretrabajdor.setText(usuarioTrabajador.getNombre()+" "+usuarioTrabajador.getApellido());
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+rutaurl)).into(fotoperfil);
                    estadotrabajador=usuarioTrabajador.getEstado();

                    loadingperfil.setVisibility(View.INVISIBLE);
                    loadingperfil.pauseAnimation();


                    if(estadotrabajador.equals("Disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeactivo);
                        btncambiodeestado.setText("Disponible");
                    }
                    if(estadotrabajador.equals("No disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeinactivo);
                        btncambiodeestado.setText("No Disponible");
                    }
                }
            }
            @Override
            public void onFailure(Call<UsuarioTrabajadorhome> call, Throwable t) {
                Toast.makeText(getContext(), "error/homedatos/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }


    private void reiniciarfragmentterminadas(String rut) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Solicitud>> call = tesisAPI.TrabajadorSolicitudes(rut);
        call.enqueue(new Callback<List<Solicitud>>() {
            @Override
            public void onResponse(Call<List<Solicitud>> call, Response<List<Solicitud>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "error/soli/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    List<Solicitud> solicituds = response.body();
                    Solicitudactual.clear();
                    solicitudinterna.clear();
                    for (Solicitud solicitud : solicituds) {
                        Solicitud Solicitud1 = new Solicitud();
                        //se setean los valores del trabajador
                        Solicitud1.setIdSolicitud(solicitud.getIdSolicitud());
                        Solicitud1.setFechaS(solicitud.getFechaS());
                        Solicitud1.setNombre(solicitud.getNombre());
                        Solicitud1.setApellido(solicitud.getApellido());
                        Solicitud1.setEstado(solicitud.getEstado());
                        Solicitud1.setFotoT(rutaservidor+solicitud.getFotoT());
                        Solicitudactual.add(Solicitud1);
                    }



                    for (int i = 0; i < Solicitudactual.size(); i++) {
                        Solicitud soli = new Solicitud();
                        soli = Solicitudactual.get(i);
                        if ( soli.getEstado().equals("ATENDIENDO")  ) {
                            solicitudinterna.add(soli);
                        } else {

                        }
                    }
                    //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                    if (solicitudinterna.size() != 0) {
                        //se instancia la recarga de los items que se encuentan en la lista de aceptadas / finalisadas
                        ads2.refresh(solicitudinterna);
                        listaactivas.setAdapter(ads2);
                        loadinglista.setVisibility(View.INVISIBLE);
                        loadinglista.pauseAnimation();

                        notfound.setText("");

                    }else{
                        ads2.refresh(solicitudinterna);
                        listaactivas.setAdapter(ads2);
                        loadinglista.setVisibility(View.INVISIBLE);
                        loadinglista.pauseAnimation();



                        notfound.setText("No Posee Solicitudes");


                    }
                  //  refreshLayoutterminadas.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {
                Toast.makeText(getContext(), "error/soli/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();
                loadinglista.setVisibility(View.INVISIBLE);
                loadinglista.pauseAnimation();



                notfound.setText("No Posee Solicitudes");
            }
        });

    }

    private void addDataSet() {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < ydata.length; i++){
            yEntrys.add(new PieEntry(ydata[i] , i));
        }

        for(int i = 1; i < xdata.length; i++){
            xEntrys.add(xdata[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Employee Sales");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
       // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
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