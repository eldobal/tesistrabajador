package com.example.tesistrabajador.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Adaptadornotificaciones;
import com.example.tesistrabajador.clases.Notificacion;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.fragments.homeFragment;
import com.example.tesistrabajador.fragments.listanotificacionesFragment;
import com.example.tesistrabajador.fragments.perfilFragment;
import com.example.tesistrabajador.fragments.settingsFragment;
import com.example.tesistrabajador.fragments.solicitudesFragment;
import com.example.tesistrabajador.mapclasses.TaskLoadedCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menuActivity extends AppCompatActivity implements TaskLoadedCallback {
    private GoogleSignInClient googleSignInClient;
    private NotificationManagerCompat notificationManager;
    TextView nombre,email,id;
    ImageView fotoperfil;
    BottomNavigationView mbottomNavigationView;
    SweetAlertDialog dp;
    private SharedPreferences prefs,prefsnotificacion;
    int contador=0;
    ListView listanotificacion;
    Adaptadornotificaciones adsnoti;
    SwipeRefreshLayout refreshLayout;
    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;
    String latorigen="",longorigen="";
    String rut="",contrasenaperfil;
    ArrayList<Solicitud> listasolicitudesterminadas = new ArrayList<Solicitud>();
    ArrayList<Solicitud> listasolicitudactivas = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudescomparar = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudes = new ArrayList<Solicitud>();
    ArrayList<Notificacion> arraylistanotificaciones= new ArrayList<Notificacion>();;
    ArrayList<Notificacion> listanotificacionescomparar= new ArrayList<Notificacion>();;
    ArrayList<Notificacion> listanotificaciones = new ArrayList<Notificacion>();
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        solicitudesFragment solicitudeFragment = new solicitudesFragment();
        listanotificacionesFragment listanotificacionesFragment = new listanotificacionesFragment();
        homeFragment homeFragment  = new homeFragment();
        notificationManager = NotificationManagerCompat.from(this);
        setContentView(R.layout.activity_menu);
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        prefsnotificacion = getSharedPreferences("PreferencesNotificacion", Context.MODE_PRIVATE);
        setcredentiasexist();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

        //al momento de crear el home en el onCreate cargar con el metodo sin backtostack
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // trae el cliende de google
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //trozo de codigo para rescatar parametros de la cuenta de usuario
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Toast.makeText(menuActivity.this, "Nombre"+personName+" Correo: "+personEmail+ " id:" +personId+"", Toast.LENGTH_LONG).show();
        }

        mbottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomnavigation);
        //se carga como primer fragment la lista de Notificaciones
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new listanotificacionesFragment()).commit();
        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //se muestra el fragment de peril
                if(menuItem.getItemId()== R.id.menu_profile){
                   // showSelectedFragment(new perfilFragment());
                        showSelectedFragment(new perfilFragment());
                }
                //se muestra el fragment de rubros
                if(menuItem.getItemId()== R.id.menu_home){
                    //showSelectedFragment(new homeFragment());
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment, "hometag")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //permite regresar hacia atras entre los fragments
                            .addToBackStack(null)
                            .commit();

                }
                //se muestra el fragment de la lista de solicitudes
                if(menuItem.getItemId()==R.id.menu_solicitud){

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, solicitudeFragment, "solicitudtag")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //permite regresar hacia atras entre los fragments
                            .addToBackStack(null)
                            .commit();


                }
                //se muestra el fragment de configuracion y setting
                if(menuItem.getItemId()== R.id.menu_settings){
                    showSelectedFragment(new settingsFragment());
                }
                if(menuItem.getItemId()== R.id.menu_notificaciones){

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, listanotificacionesFragment, "notificacionestag")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //permite regresar hacia atras entre los fragments
                            .addToBackStack(null)
                            .commit();

                }

                return true;
            }
        });


    }


  /*  private void reiniciarfragmentnotificacionesASYNC(String rutusuario) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<List<Notificacion>> call = tesisAPI.GetNotificacion(rutusuario,contrasenaperfil);
        call.enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "error/noti/onresponse" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    listanotificaciones.clear();
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

                        for (int i =0 ; i<arraylistanotificaciones.size();i++){
                            listanotificaciones.add(arraylistanotificaciones.get(i));
                        }

                       if(listanotificacionescomparar.size() != arraylistanotificaciones.size()){

                          setPendingIntent();
                           createNotificationChannel();
                           crearnotificacion();
                       }else{

                       }


                       listanotificacionescomparar = arraylistanotificaciones;

                    }else {

                    }
                }

            }
            @Override
            public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error con el servidor" , Toast.LENGTH_LONG).show();

            }
        });
    } */


   /* private void iniciarfragmentsolitudes() {
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
                    Toast.makeText(menuActivity.this, "error :" + response.code(), Toast.LENGTH_LONG).show();
                } else {
                    listasolicitudactivas.clear();
                    listasolicitudesterminadas.clear();
                    List<Solicitud> solicituds = response.body();
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
                    if(Solicitudescomparar !=Solicitudes) {

                        if (Solicitudes.size() > 0) {

                            Solicitudescomparar = Solicitudes;
                            for (int i = 0; i < Solicitudes.size(); i++) {
                                Solicitud soli = new Solicitud();
                                soli = Solicitudes.get(i);
                                if (soli.getEstado().equals("PENDIENTE") || soli.getEstado().equals("ATENDIENDO") || soli.getEstado().equals("CONFIRMADA")  ) {
                                    listasolicitudesterminadas.add(soli);
                                } else {
                                  // listasolicitudesterminadas.add(soli);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Solicitud>> call, Throwable t) {
                Toast.makeText(menuActivity.this, "error :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }  */


    //metodo que permite elejir un fragment
    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                //permite regresar hacia atras entre los fragments
                .addToBackStack(null)
                .commit();
    }

    //metodo que permite elejir un fragment y no volver hacia atras
    private void cargarfragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                //permite regresar hacia atras entre los fragments
                //.addToBackStack(null)
                .commit();
    }

    private void saveOnPreferences(String rut, String contrasena) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Rut", rut);
        editor.putString("ContraseNa", contrasena);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
    }




    //metodo el cual verifica la version del so para crear el canal
    private void createNotificationChannel(){
        //se verifica que el SO sea igual o superior a oreo
        //si es superior crea el notification chanel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    //metodo para crear la notificacion personalizada
    private void crearnotificacion() {
        //se instancia el builder para crear la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        //se declaran las propiedades y atributos
        builder.setSmallIcon(R.drawable.userprofile);
        builder.setContentTitle("Nueva Notificacion Encontrada");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.CYAN, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        //texto para mostrar de forma exancible
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Usted tiene una nueva notificacion, si desea visualizar su lista de notificaciones" +
                "selcciones el icono notificaciones en la barra de opciones / si desea ir directamente aprete esta notificacion"));
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        //se instancia la notificacion
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }


    private void setPendingIntent(){

        Intent notificationIntent = new Intent(this, listanotificacionesFragment.class);
        notificationIntent.putExtra("menuFragment", "favoritesMenuItem");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(menuActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        pendingIntent = stackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void setcredentiasexist() {
        String rutq = getuserrutprefs();
        String contrasena = getusercontraseñaprefs();
        if (!TextUtils.isEmpty(rutq)&& (!TextUtils.isEmpty(contrasena)) ) {
            rut=rutq.toString();
            contrasenaperfil=contrasena.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }


    @Override
    public void onTaskDone(Object... values) {

    }
}