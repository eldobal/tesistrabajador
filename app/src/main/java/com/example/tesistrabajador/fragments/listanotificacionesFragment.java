package com.example.tesistrabajador.fragments;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.activitys.menuActivity;
import com.example.tesistrabajador.clases.Adaptadornotificaciones;
import com.example.tesistrabajador.clases.Notificacion;
import com.example.tesistrabajador.interfaces.tesisAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class listanotificacionesFragment extends Fragment {
    private NotificationManagerCompat notificationManager;
    ListView listanotificaciones;
    NetworkInfo NetworkInfo;
    SwipeRefreshLayout refreshnotificaciones;
    SharedPreferences prefs,asycprefs;
    LottieAnimationView animationnotification,animationnotificationloadign ;
    ArrayList<Notificacion> arraylistanotificaciones= new ArrayList<Notificacion>();;
    ArrayList<Notificacion> listanotificacionescomparar= new ArrayList<Notificacion>();;
    Adaptadornotificaciones ads ,adsnoti;
    private String rutusuario="";
    int azynctiempo =0;
    TextView notfound;
    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private int NOTIFICACION_ID = 0;
    String GROUP_KEY_WORK_EMAIL = "com.android.example.Notifications";
    int SUMMARY_ID = 0;
    Button btnprueba;


    public listanotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        //lista de notificaciones en un array para recibirlas con el get arguments
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        asycprefs = this.getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
      //
        notificationManager = NotificationManagerCompat.from(getActivity());
        //refreshnotificaciones =(SwipeRefreshLayout) getActivity().findViewById(R.id.refreshnotificaciones);
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_listanotificaciones, container, false);

       // arraylistanotificaciones = (ArrayList<Notificacion>) getArguments().getSerializable("arraynotificaciones");

        if(NetworkInfo != null && NetworkInfo.isConnected()){
            //declaracion de la lista y la animacion
          listanotificaciones = (ListView) v.findViewById(R.id.listanotificaciones);
           animationnotificationloadign = (LottieAnimationView) v.findViewById(R.id.animationotificationloading);
            animationnotification = (LottieAnimationView) v.findViewById(R.id.animationotification);
           animationnotification.setVisibility(View.INVISIBLE);


            //prefs que contienen datos del usuario
            setcredentiasexist();
            //prefs del tiempo de sync de la app
            settiempoasyncexist();



            if (rutusuario.equals("")){
                //enviar al usuario hacia alguna pantalla de home y mostrar el error en forma de mensaje
                Intent intent = new Intent(getContext(), loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //linea que termina la ejecucion y no permite hacer onback
                getActivity().finish();

            }else{
                //llamada azyn la cual busca las notificaciones que tiene el trabajador
                final View vista = inflater.inflate(R.layout.elementonotificacion, null);
                adsnoti = new Adaptadornotificaciones(getContext(), arraylistanotificaciones);
              // reiniciarfragmentnotificacionesASYNC(rutusuario);



                        if (arraylistanotificaciones.size() != 0) {
                            animationnotification.setVisibility(View.INVISIBLE);
                            animationnotificationloadign.setVisibility(View.INVISIBLE);
                            //se instancia el adaptadador en el cual se instanciara la lista de trbajadres para setearlas en el apdaptador
                            adsnoti.refresh(arraylistanotificaciones);
                            //se setea el adaptador a la lista del fragments
                            listanotificaciones.setAdapter(adsnoti);
                        }



                final Handler handler = new Handler();
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                if (isAdded() && isVisible() && getUserVisibleHint()) {
                                    try {
                                        //Ejecuta tu AsyncTask!
                                       // adsnoti.refresh(arraylistanotificaciones);
                                        reiniciarfragmentnotificacionesASYNC(rutusuario);
                                    } catch (Exception e) {
                                        Log.e("error", e.getMessage());
                                    }
                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 0, azynctiempo);  //ejecutar en intervalo definido por el programador


        }


        }else{
            getActivity().finish();
            Toast.makeText(getContext(), "Error en la conecctacion del dispocitivo, asegurese de que tenga coneccion", Toast.LENGTH_LONG).show();
        }


       /* refreshnotificaciones.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(1500,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        reiniciarfragmentnotificaciones(rutusuario);
                    }
                }.start();
            }
        });
        */
        return v;
    }

    private void reiniciarfragmentnotificacionesASYNC(String rutusuario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Notificacion>> call = tesisAPI.getNotificacion(rutusuario);
        call.enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "error/noti/onresponse" + response.code(), Toast.LENGTH_LONG).show();
                } else {

                    arraylistanotificaciones.clear();
                    List<Notificacion> notificaciones = response.body();
                    for (Notificacion notificacion : notificaciones) {
                        Notificacion notificacion1 = new Notificacion();
                        //se setean los valores del trabajador
                        notificacion1.setId(notificacion.getId());
                        notificacion1.setIdSolicitud(notificacion.getIdSolicitud());
                        notificacion1.setMensaje(notificacion.getMensaje());
                        notificacion1.setRUT(notificacion.getRUT());
                        //se guarda la lista con las notificaciones del usuario conectado
                        arraylistanotificaciones.add(notificacion1);
                    }
                    if (arraylistanotificaciones.size() != 0) {


                        adsnoti.refresh(arraylistanotificaciones);
                        listanotificaciones.setAdapter(adsnoti);

                        animationnotification.setVisibility(View.INVISIBLE);
                        animationnotification.pauseAnimation();
                        animationnotificationloadign.setVisibility(View.INVISIBLE);
                        animationnotificationloadign.pauseAnimation();

                    }else {
                        animationnotification.setVisibility(View.VISIBLE);
                        animationnotification.playAnimation();
                        animationnotificationloadign.setVisibility(View.INVISIBLE);
                        animationnotificationloadign.pauseAnimation();

                    }
                }

            }
            @Override
            public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                Toast.makeText(getActivity(), "error con el servidor" , Toast.LENGTH_LONG).show();

            }
        });
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



    //metodo el cual verifica la version del so para crear el canal
    private void createNotificationChannel(){
        //se verifica que el SO sea igual o superior a oreo
        //si es superior crea el notification chanel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    //metodo para crear la notificacion personalizada
    private void crearnotificacion(int NOTIFICACION_ID) {
        //se instancia el builder para crear la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID);
        //se declaran las propiedades y atributos
        builder.setSmallIcon(R.drawable.ic_notificacionicon);
        builder.setContentTitle("Nuevas Notificaciones Encontradas ");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.CYAN, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setGroup(GROUP_KEY_WORK_EMAIL);
        builder.setAutoCancel(true);


        //texto para mostrar de forma exancible
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Se ha realizado una actualizacion en la solicitud: "));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());

        //se instancia la notificacion
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build() );



        Notification summaryNotification =
                new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                        .setContentTitle("Nuevas Notificaciones")
                        //set content text to support devices running API level < 24
                        .setContentText("tienes nuevas notificaciones")
                        .setSmallIcon(R.drawable.ic_notificacionicon)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()

                                .setSummaryText("Notificaciones"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(NOTIFICACION_ID, builder.build());

        notificationManager.notify(SUMMARY_ID, summaryNotification);



    NOTIFICACION_ID = NOTIFICACION_ID+1;



    }


    private void setPendingIntent(){

        Intent notificationIntent = new Intent(getContext(), homeFragment.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(menuActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

    }


}
