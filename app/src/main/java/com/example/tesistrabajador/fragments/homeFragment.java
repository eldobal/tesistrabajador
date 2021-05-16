package com.example.tesistrabajador.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.clases.Adaptador;
import com.example.tesistrabajador.clases.GananciasAPI;
import com.example.tesistrabajador.clases.GlobalInfo;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.clases.UsuarioTrabajadorhome;
import com.example.tesistrabajador.interfaces.tesisAPI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class homeFragment extends Fragment {
    TextView notfound;
    LottieAnimationView loadinglista,loadingperfil;
    private ListView listaactivas;
    private SharedPreferences prefs,asycprefs,prefsganancias;
    private String rutusuario="";
    int azynctiempo =0,dia1=0,mes1=0,año1=0,dia2=0,mes2=0,año2=0,Gananciaperiodo=0,Porpagar=0,porpagarselccionado=0,porpagarpref=0;
    ArrayList<Solicitud> listasolicitudesterminadas,listasolicitudactivas,listasolicitudactivasinterna,solicitudinterna,Solicitudescomparar;
    ArrayList<Solicitud> Solicitudactual = new ArrayList<Solicitud>();
    Adaptador ads2;
    final static String rutaservidor= GlobalInfo.Rutaservidor;
    String estadotrabajador = "",contrasena="",fechainicio="",fechafin="",    Fechainicio="",Fechafin="",Fechaactual="";
    ImageView fotoperfil;
    TextView nombretrabajdor,txtperiodoganancias,txtganancasobtenidasel;
    EditText edittextgananciasperiodo,edittextprecioporpagar;
    Button btncambiodeestado,btncalcularganancias,btnpagarporpagar;
    NetworkInfo activeNetwork;
    ConnectivityManager cm ;
    CardView carganancias;
    AlertDialog dialog6;

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
        asycprefs = this.getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        settiempoasyncexist();
        setcredentiasexist();

        homeFragment test = (homeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("hometag");

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        activeNetwork = cm.getActiveNetworkInfo();
                        if (activeNetwork != null) {
                            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE )  {
                                if(test != null && test.isVisible() ) {
                                    // reiniciarfragment(rutusuario);
                                    reiniciarfragmentterminadas(rutusuario);
                                }
                            }else{
                                //manejar dialog
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 100, azynctiempo);  //ejecutar en intervalo definido por el programador
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
       // pieChart = (PieChart) v.findViewById(R.id.piechart);
        notfound = (TextView) v.findViewById(R.id.txtnotfoundhome);
        nombretrabajdor =(TextView) v.findViewById(R.id.txthomenombre);
        btncambiodeestado =(Button) v.findViewById(R.id.btncambiodeestadotrabajador);
        fotoperfil = (ImageView) v.findViewById(R.id.idimagenperfiltrabajador);
        loadinglista = (LottieAnimationView) v.findViewById(R.id.idanimacionlistasolicitud);
        loadingperfil = (LottieAnimationView) v.findViewById(R.id.loadinglistaimgperfilhome);
        btncalcularganancias =(Button) v.findViewById(R.id.btncalcularganancias);
        txtperiodoganancias = (TextView) v.findViewById(R.id.txtperiodoganancias);
        txtganancasobtenidasel = (TextView) v.findViewById(R.id.txtganancasobtenidasel);
        edittextgananciasperiodo = (EditText) v.findViewById(R.id.edittextgananciasperiodo);
        edittextprecioporpagar =(EditText) v.findViewById(R.id.edittextprecioporpagar);
        carganancias = (CardView) v.findViewById(R.id.cardganancias);
        btnpagarporpagar =(Button) v.findViewById(R.id.btnpagarporpagar);
        btnpagarporpagar.setVisibility(View.GONE);

        //se buscan el usuario y el tiempo de sync de la app
        prefsganancias = this.getActivity().getSharedPreferences("Preferencesganancias", Context.MODE_PRIVATE);

        //se comprueba si esque existe datos guardados
        setgananciasexist();

        if(!Fechainicio.equals("") &&!Fechafin.equals("")  && !Fechaactual.equals("")  && Gananciaperiodo!=0 ){
            carganancias.setVisibility(View.VISIBLE);
            if(fechainicio.equals("vacio") && fechafin.equals("vacio")){
                txtperiodoganancias.setText("Ganancias totales del perfil.");
            }else{
                txtperiodoganancias.setText("Ganancias desde: "+fechainicio+" hasta: "+fechafin+"");
            }
            edittextgananciasperiodo.setText(""+Gananciaperiodo);
            edittextprecioporpagar.setText(""+Porpagar);
            txtganancasobtenidasel.setText("Ganancias obtenidas:"+Fechaactual+"");
        }else{
            carganancias.setVisibility(View.GONE);
        }

            if(rutusuario.equals("")){
                //enviar al usuario hacia alguna pantalla de home y mostrar el error en forma de mensaje
                Intent intent = new Intent(getContext(), loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //linea que termina la ejecucion y no permite hacer onback
                getActivity().finish();
                Toast.makeText(getContext(), "el Usuario no es valido ", Toast.LENGTH_LONG).show();
            }else{
                cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        //se cargan los datos del trabajador
                        cargardatostrabajador();
                        ads2 = new Adaptador(getContext(), solicitudinterna);
                        reiniciarfragmentterminadas(rutusuario);
                        listaactivas = (ListView) v.findViewById(R.id.solicitudactual);
                        //declaracion de los swiperefresh para intanciarlos
                        // refreshLayoutterminadas = v.findViewById(R.id.refreshterminadas);
                        notfound.setText("");
                    } else {

                    }
                }

                btncambiodeestado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        activeNetwork = cm.getActiveNetworkInfo();
                        if (activeNetwork != null) {
                            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                //se carga la solicitud
                                cambiarestadotrabajador();
                            } else {

                            }
                        }
                    }
                });

                btncalcularganancias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fechainicio="";
                        fechafin="";
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View viewsync = inflater.inflate(R.layout.alertdialogganancias,null);
                        builder.setView(viewsync);
                        dialog6 = builder.create();
                        dialog6.setCancelable(false);
                        dialog6.show();
                        dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button btnfechainicio =(Button) viewsync.findViewById(R.id.btnfechainicio);
                        Button btnfechafin =(Button) viewsync.findViewById(R.id.btnfechafin);
                        Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                        Button btncalcularganancias =(Button) viewsync.findViewById(R.id.btnconsultarganancias);
                        btnfechainicio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                LayoutInflater inflater = getLayoutInflater();
                                View viewsync = inflater.inflate(R.layout.alerdialogganannciascalendar ,null);
                                builder.setView(viewsync);
                                AlertDialog dialog5 = builder.create();
                                dialog5.setCancelable(false);
                                dialog5.show();
                                dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                TextView textoinformativo =(TextView) viewsync.findViewById(R.id.txtdatepicker);
                                DatePicker datePicker =(DatePicker) viewsync.findViewById(R.id.datepickerGanancias);
                                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                Button btncalcularganancias =(Button) viewsync.findViewById(R.id.btnseleccionar);
                                textoinformativo.setText("Seleccione la fecha de INICIO del rango de tiempo del cual usted desea obtener las ganancias.");
                                btncerrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog5.dismiss();
                                    }
                                });

                                btncalcularganancias.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog5.dismiss();
                                        dia1= datePicker.getDayOfMonth();
                                        mes1 = datePicker.getMonth()+1;
                                        año1 = datePicker.getYear();
                                        fechainicio = dia1+"-"+mes1+"-"+año1;
                                    }
                                });
                            }
                        });
                        btnfechafin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                LayoutInflater inflater = getLayoutInflater();
                                View viewsync = inflater.inflate(R.layout.alerdialogganannciascalendar ,null);
                                builder.setView(viewsync);
                                AlertDialog dialog6 = builder.create();
                                dialog6.setCancelable(false);
                                dialog6.show();
                                dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                TextView textoinformativo =(TextView) viewsync.findViewById(R.id.txtdatepicker);
                                DatePicker datePicker =(DatePicker) viewsync.findViewById(R.id.datepickerGanancias);
                                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                                Button btncalcularganancias =(Button) viewsync.findViewById(R.id.btnseleccionar);
                                textoinformativo.setText("Seleccione la fecha de FIN del rango de tiempo del cual usted desea obtener las ganancias.");
                                btncerrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog6.dismiss();
                                    }
                                });

                                btncalcularganancias.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog6.dismiss();
                                        dia2= datePicker.getDayOfMonth();
                                        mes2 = datePicker.getMonth()+1;
                                        año2 = datePicker.getYear();
                                        fechafin = dia2+"-"+mes2+"-"+año2;
                                    }
                                });
                            }
                        });

                        btncerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog6.dismiss();
                            }
                        });

                        btncalcularganancias.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //se verifica que las fechas esten seleccionadas de forma correcta
                                //formato del calendario el cual toma la fecha actual.
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
                                String fechaactual = sdf.format(calendar.getTime());

                                if(fechainicio.equals("")&& fechafin.equals("")){
                                    fechainicio ="vacio";
                                    fechafin="vacio";
                                    calcularganancias(fechainicio, fechafin, fechaactual);
                                }
                                else {
                                    if (año1 <= año2) {
                                        if (mes1 <= mes2) {
                                            if (dia1 <= dia2) {
                                                if (fechafin.compareTo(fechaactual) == 0 || fechafin.compareTo(fechaactual) < 0) {
                                                    if (!fechainicio.equals("") || !fechafin.equals("")) {
                                                        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                        activeNetwork = cm.getActiveNetworkInfo();
                                                        if (activeNetwork != null) {
                                                            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                                                //se carga la solicitud
                                                                calcularganancias(fechainicio, fechafin, fechaactual);
                                                            } else { Toast.makeText(getContext(), "No se encuentra una coneccion.", Toast.LENGTH_LONG).show(); } }
                                                    } else { Toast.makeText(getContext(), "Seleccione ambas fechas para poder continuar.", Toast.LENGTH_LONG).show(); }
                                                } else { Toast.makeText(getContext(), "Seleccione una fecha de fin de periodo que no sea mayor al dia de hoy.", Toast.LENGTH_LONG).show(); }
                                            } else { Toast.makeText(getContext(), "el dia de inicio debe ser menor o igual al dia fin.", Toast.LENGTH_LONG).show(); }
                                        } else { Toast.makeText(getContext(), "el mes de inicio debe ser menor o igual al mes fin.", Toast.LENGTH_LONG).show(); }
                                    } else { Toast.makeText(getContext(), "el año de inicio debe ser menor o igual al año fin.", Toast.LENGTH_LONG).show(); }
                                }
                            }
                        });
                    }
                });

                if(Porpagar !=0){
                    btnpagarporpagar.setVisibility(View.VISIBLE);
                    btnpagarporpagar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            porpagarselccionado=0;
                            //alert para saber si el pago estuvo weno
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            LayoutInflater inflater = getLayoutInflater();
                            View viewsync = inflater.inflate(R.layout.alertdialogporpagar, null);
                            builder.setView(viewsync);
                            AlertDialog dialog8 = builder.create();
                            dialog8.setCancelable(false);
                            dialog8.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button dismiss = (Button) viewsync.findViewById(R.id.btncerraralert);
                            Button confirmacionpago = (Button) viewsync.findViewById(R.id.btnfinalizarsolicitud);
                            RadioButton r1 = (RadioButton) viewsync.findViewById(R.id.radioButton);
                            RadioButton r2 = (RadioButton) viewsync.findViewById(R.id.radioButton2);
                            if(Porpagar<=10000){
                                //SE OCULTA EL RADIOBUTTON CUANDO EL LA DEUDA ES INFERIOR A 10.000
                                r1.setVisibility(View.GONE);
                            }else{
                                int resto=Porpagar-10000;
                                r1.setText("Pagar la cantidad minima.("+resto+")");
                            }
                            r2.setText("Pagar la deuda en su totalidad.("+Porpagar+")");
                            dialog8.show();

                            dismiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog8.dismiss();
                                }
                            });

                            confirmacionpago.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(r1.isChecked()==true){
                                        //PAGO MINIMO EL CUAL DEJARA EL MARGEN
                                      porpagarselccionado= Porpagar-10000;
                                        porpagarpref = Porpagar-porpagarselccionado;
                                  //      Toast.makeText(v.getContext(), "1", Toast.LENGTH_LONG).show();
                                    }
                                    if(r2.isChecked()==true){
                                        //PAGO COMPLETO CON EL POR PAGAR ENTERO
                                        porpagarselccionado=Porpagar;
                                        porpagarpref=0;
                                    }
                                    if(r1.isChecked()==false && r2.isChecked()==false){
                                        Toast.makeText(v.getContext(), "seleccione una opcion por favor.", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(v.getContext(), "valor que se pagara"+porpagarselccionado, Toast.LENGTH_LONG).show();
                                        if(r1.isChecked()==true){
                                            //todo el codigo que se ejecuta cuando se pagara con webpay
                                                Bundle bundle = new Bundle();
                                                bundle.putString("rutusuario",rutusuario);
                                                bundle.putInt("monto",porpagarselccionado);
                                                bundle.putInt("montopref",porpagarpref);
                                                onepayFragment onepayFragment = new onepayFragment();
                                                onepayFragment.setArguments(bundle);
                                                getFragmentManager().beginTransaction().replace(R.id.container,onepayFragment)
                                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                        .commit();
                                                dialog8.dismiss();
                                        }
                                        if(r2.isChecked()==true){
                                            Bundle bundle = new Bundle();
                                            //id de la solicitud para que se pueda buscar en el detalle
                                            bundle.putString("rutusuario",rutusuario);
                                            bundle.putInt("monto",porpagarselccionado);
                                            bundle.putInt("montopref",porpagarpref);
                                            onepayFragment onepayFragment = new onepayFragment();
                                            onepayFragment.setArguments(bundle);
                                            getFragmentManager().beginTransaction().replace(R.id.container,onepayFragment)
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    .commit();
                                            dialog8.dismiss();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
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
            }
        return v;
    }

    private void calcularganancias(String fechainicio,String fechafin,String fechaactual) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<GananciasAPI> call = tesisAPI.DatosTrabajador(rutusuario,contrasena,fechainicio,fechafin);
        call.enqueue(new Callback<GananciasAPI>() {
            @Override
            public void onResponse(Call<GananciasAPI> call, Response<GananciasAPI> response) {
                if (!response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog4 = builder.create();
                    dialog4.setCancelable(false);
                    dialog4.show();
                    dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de traer el resultados de las ganancias. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog4.dismiss();
                        }
                    });
               //     Toast.makeText(getContext(), "error/homedatos/ganancias/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    GananciasAPI ganancias = response.body();
                    carganancias.setVisibility(View.VISIBLE);
                    if(fechainicio.equals("vacio") && fechafin.equals("vacio")){
                        txtperiodoganancias.setText("Ganancias totales del perfil.");
                    }else{
                        txtperiodoganancias.setText("Ganancias desde: "+fechainicio+" hasta: "+fechafin+"");
                    }
                    edittextgananciasperiodo.setText(""+ganancias.getGananciasTrabajador());
                    edittextprecioporpagar.setText(""+ganancias.getGananciasPorPagar());
                    txtganancasobtenidasel.setText("Ganancias obtenidas:"+fechaactual+"");
                    int gananciastrabajadaor=ganancias.getGananciasTrabajador();
                    int gananaciasporpagar = ganancias.getGananciasPorPagar();
                    //se guarda los valores para poder mostrarlos sin estar rcargando esta llamada
                    saveOnPreferencesganancias(fechainicio,fechafin,fechaactual,gananciastrabajadaor,gananaciasporpagar);
                    setgananciasexist();
                    dialog6.dismiss();
                //    Toast.makeText(getContext(), ""+ganancias.getGananciasPorPagar() +" / "+ganancias.getGananciasTrabajador(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GananciasAPI> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                builder.setView(viewsync);
                AlertDialog dialog5 = builder.create();
                dialog5.setCancelable(false);
                dialog5.show();
                dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog5.dismiss();
                    }
                });
           //     Toast.makeText(getContext(), "error/homedatos/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cambiarestadotrabajador() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<String> call = tesisAPI.CambiarEstadoTrabajador(rutusuario,contrasena);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de cambiar el estado de su perfil. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);
                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                   // Toast.makeText(getContext(), "error/homedatosESTADO/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    String msgestado = response.body();
                    if(msgestado.equals("Disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeactivo);
                        btncambiodeestado.setText("Disponible");
                    }
                    if(msgestado.equals("No Disponible")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeinactivo);
                        btncambiodeestado.setText("No Disponible");
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alertdialoghomebtncambioestado,null);
                    builder.setView(viewsync);
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Button btnaceptar = viewsync.findViewById(R.id.btnhomecambioestadoexito);

                    btnaceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                builder.setView(viewsync);
                AlertDialog dialog3 = builder.create();
                dialog3.setCancelable(false);
                dialog3.show();
                dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });
             //   Toast.makeText(getContext(), "error/homedatosESTADO/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void cargardatostrabajador() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<UsuarioTrabajadorhome> call = tesisAPI.TrabajadorHome(rutusuario,contrasena);
        call.enqueue(new Callback<UsuarioTrabajadorhome>() {
            @Override
            public void onResponse(Call<UsuarioTrabajadorhome> call, Response<UsuarioTrabajadorhome> response) {
                if (!response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog4 = builder.create();
                    dialog4.setCancelable(false);
                    dialog4.show();
                    dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de cambiar el estado de su perfil. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog4.dismiss();
                        }
                    });

                  //  Toast.makeText(getContext(), "error/homedatos/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    UsuarioTrabajadorhome usuarioTrabajador = response.body();
                    String rutaurl=usuarioTrabajador.getFoto();
                    nombretrabajdor.setText(usuarioTrabajador.getNombre()+" "+usuarioTrabajador.getApellido());
                    Glide.with(getContext()).load(String.valueOf(rutaurl)).into(fotoperfil);
                    estadotrabajador=usuarioTrabajador.getEstado();
                    loadingperfil.setVisibility(View.INVISIBLE);
                    loadingperfil.pauseAnimation();
                    if(estadotrabajador.equals("DISPONIBLE")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeactivo);
                        btncambiodeestado.setText("DISPONIBLE");
                    }
                    if(estadotrabajador.equals("NO DISPONIBLE")){
                        btncambiodeestado.setBackgroundResource(R.drawable.btn_homeinactivo);
                        btncambiodeestado.setText("NO DISPONIBLE");
                    }
                }
            }
            @Override
            public void onFailure(Call<UsuarioTrabajadorhome> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                builder.setView(viewsync);
                AlertDialog dialog5 = builder.create();
                dialog5.setCancelable(false);
                dialog5.show();
                dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog5.dismiss();
                    }
                });
              //  Toast.makeText(getContext(), "error/homedatos/onfailure :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void reiniciarfragmentterminadas(String rut) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Solicitud>> call = tesisAPI.TrabajadorSolicitudes(rut,contrasena);
        call.enqueue(new Callback<List<Solicitud>>() {
            @Override
            public void onResponse(Call<List<Solicitud>> call, Response<List<Solicitud>> response) {
                if (!response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog6 = builder.create();
                    dialog6.setCancelable(false);
                    dialog6.show();
                    dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de cambiar el estado de su perfil. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog6.dismiss();
                        }
                    });
              //      Toast.makeText(getContext(), "error/soli/onresponse :" + response.code(), Toast.LENGTH_LONG).show();
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
                        Solicitud1.setFotoT(solicitud.getFotoT());
                        Solicitudactual.add(Solicitud1);
                    }
                    for (int i = 0; i < Solicitudactual.size(); i++) {
                        Solicitud soli = new Solicitud();
                        soli = Solicitudactual.get(i);
                        if ( soli.getEstado().equals("ATENDIENDO") || soli.getEstado().equals("FINALIZADO") || soli.getEstado().equals("FINALIZANDO")  || soli.getEstado().equals("COMFIRMADA") ) {
                            solicitudinterna.add(soli);
                        }
                    }
                    if (solicitudinterna.size() != 0) {
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
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {

                loadinglista.setVisibility(View.INVISIBLE);
                loadinglista.pauseAnimation();
                notfound.setText("No Posee Solicitudes");
            }
        });

    }

    private void setgananciasexist() {
        String fechai = getFechainicio();
        String fechaf = getFechafin();
        String fechaa = getfechaactual();
        int gananciap = getgananciaperiodo();
        int porp = getporpagar();
        if (!TextUtils.isEmpty(fechai)&& (!TextUtils.isEmpty(fechaf)) &&(!TextUtils.isEmpty(fechaa)) && (gananciap !=0) ) {
            Fechainicio=fechai.toString();
            Fechafin=fechaf.toString();
            Fechaactual =fechaa.toString();
            Gananciaperiodo=gananciap;
            Porpagar = porp;
        }
    }
    //metodo para guardar los datos que se rescaten de la llamada
    private void saveOnPreferencesganancias(String fechainicio, String fechafin,String fechaactual,int gananciaperiodo,int porpagar) {
        SharedPreferences.Editor editor = prefsganancias.edit();
        editor.putString("fechainicio", fechainicio);
        editor.putString("fechafin", fechafin);
        editor.putString("fechaactual", fechaactual);
        editor.putInt("gananciaperiodo", gananciaperiodo);
        editor.putInt("porpagar", porpagar);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
    }

    private String getFechainicio() {
        return prefsganancias.getString("fechainicio", "");
    }

    private String getFechafin() {
        return prefsganancias.getString("fechafin", "");
    }

    private String getfechaactual() {
        return prefsganancias.getString("fechaactual", "");
    }

    private int getgananciaperiodo() {
        return prefsganancias.getInt("gananciaperiodo", 0);
    }

    private int getporpagar() {
        return prefsganancias.getInt("porpagar", 0);
    }

    //metodo para traer el rut del usuario hacia la variable local
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