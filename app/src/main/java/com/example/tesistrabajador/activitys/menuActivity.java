package com.example.tesistrabajador.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Notificacion;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.fragments.listanotificacionesFragment;
import com.example.tesistrabajador.fragments.perfilFragment;
import com.example.tesistrabajador.fragments.settingsFragment;
import com.example.tesistrabajador.fragments.sobrenosotrosFragment;
import com.example.tesistrabajador.fragments.solicitudesFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menuActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    TextView nombre,email,id;
    ImageView fotoperfil;
    BottomNavigationView mbottomNavigationView;
    SweetAlertDialog dp;
    private SharedPreferences prefs;
    int contador=0;

    SwipeRefreshLayout refreshLayout;

    String rut="";
    ArrayList<Solicitud> listasolicitudesterminadas = new ArrayList<Solicitud>();
    ArrayList<Solicitud> listasolicitudactivas = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudescomparar = new ArrayList<Solicitud>();
    ArrayList<Solicitud> Solicitudes = new ArrayList<Solicitud>();

    ArrayList<Notificacion> listanotificaciones = new ArrayList<Notificacion>();
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setContentView(R.layout.activity_menu);
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setcredentiasexist();

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

        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //se muestra el fragment de peril
                if(menuItem.getItemId()== R.id.menu_profile){
                    showSelectedFragment(new perfilFragment());
                }
                //se muestra el fragment de rubros
                if(menuItem.getItemId()== R.id.menu_home){

                }
                //se muestra el fragment de la lista de solicitudes
                if(menuItem.getItemId()==R.id.menu_solicitud){
                       showSelectedFragment(new solicitudesFragment());

                }
                //se muestra el fragment de configuracion y setting
                if(menuItem.getItemId()== R.id.menu_settings){
                    showSelectedFragment(new settingsFragment());
                }
                if(menuItem.getItemId()== R.id.menu_notificaciones){
                    showSelectedFragment(new listanotificacionesFragment());
                }

                return true;
            }
        });




    }


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
                .addToBackStack(null)
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

    private void setcredentiasexist() {
        String rutc = getuserrutprefs();

        if (!TextUtils.isEmpty(rutc)) {
            rut=rutc;
            //  txtpass.setText(contraseña);
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }



}