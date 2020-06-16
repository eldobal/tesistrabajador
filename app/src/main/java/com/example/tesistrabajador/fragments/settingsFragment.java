package com.example.tesistrabajador.fragments;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.loginActivity;
import com.example.tesistrabajador.fragments.preguntasFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class settingsFragment extends Fragment {
    private GoogleSignInClient googleSignInClient;
    SweetAlertDialog dp;
    SharedPreferences asycprefs;
    private SharedPreferences prefs;
    int azynctiempo =0;
    String tiempo= "";


    public settingsFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        asycprefs = getActivity().getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);
        settiempoasyncexist();

        //declaracion de botones
        final Button btnsalir = (Button) v.findViewById(R.id.btnsalir);
        final Button btnpreguntas = (Button) v.findViewById(R.id.btnpreguntas);
        final Button btntiemposync = (Button) v.findViewById(R.id.btncambiodetiempoactualizacion);
        final Button btnsobrenosotros = (Button) v.findViewById(R.id.btnsobrenosotros);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        prefs = this.getActivity().getSharedPreferences("Preferences",Context.MODE_PRIVATE);

        //trozo de codigo para rescatar parametros de la cuenta de usuario
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Toast.makeText(getContext(), "Nombre"+personFamilyName+" Correo: "+personEmail+ " id:" +personId+"", Toast.LENGTH_LONG).show();
        }


        //botton salir de la app
        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnsalir:
                        SweetAlertDialog dp3 =  new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                        dp3.setTitleText("Estas seguro de Querer Cerrar Tu Sesion?");
                        dp3.setContentText("Tendras que iniciar sesion para volver a ingrar!");
                        dp3.setConfirmText("Si!");
                        dp3.setCancelText("No!");
                        dp3.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                signOut();
                                sDialog.dismissWithAnimation();
                            }
                        })
                                .show();
                        break;
                }
            }
        });


        //boton para cambiar el tiempo de actualizacion de la app
        btntiemposync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrartiempoazyncactual(azynctiempo);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerttiemposync,null);
                builder.setView(viewsync);
                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView azyncactual= (TextView) viewsync.findViewById(R.id.txttiempoactualizacion);
                azyncactual.setText("Tiempo de Actualizacion Actual: "+tiempo);
                Button btn15segundos = viewsync.findViewById(R.id.alertbtn15);
                Button btn30segundos = viewsync.findViewById(R.id.alertbtn30);
                Button btn1minuto = viewsync.findViewById(R.id.alertbtn1m);
                Button btn5minutos = viewsync.findViewById(R.id.alertbtn5m);
                Button btnguardarcambios = viewsync.findViewById(R.id.alertbtnguardarcambios);
                Button btncancelar = viewsync.findViewById(R.id.alertbtncancelarcambios);

                btn15segundos.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { azynctiempo=15000;
                    btn15segundos.setBackgroundResource(R.drawable.bg_ripplepass);
                    btn30segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn1minuto.setBackgroundResource(R.drawable.bg_btnsync);
                    btn5minutos.setBackgroundResource(R.drawable.bg_btnsync);
                }});

                btn30segundos.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { azynctiempo=30000;
                    btn30segundos.setBackgroundResource(R.drawable.bg_ripplepass);
                    btn15segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn1minuto.setBackgroundResource(R.drawable.bg_btnsync);
                    btn5minutos.setBackgroundResource(R.drawable.bg_btnsync);

                }});

                btn1minuto.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { azynctiempo=60000;
                    btn1minuto.setBackgroundResource(R.drawable.bg_ripplepass);
                    btn15segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn30segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn5minutos.setBackgroundResource(R.drawable.bg_btnsync);

                }});

                btn5minutos.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { azynctiempo=300000;
                    btn5minutos.setBackgroundResource(R.drawable.bg_ripplepass);
                    btn15segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn30segundos.setBackgroundResource(R.drawable.bg_btnsync);
                    btn1minuto.setBackgroundResource(R.drawable.bg_btnsync);
                }});


                btnguardarcambios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(azynctiempo!=0){
                            saveOnPreferences(azynctiempo);
                        }
                        dialog.dismiss();
                    }
                });

                btncancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settiempoasyncexist();
                        dialog.dismiss();
                    }
                });


            }
        });


        //boton que te redirije al fragment de preguntas
        btnpreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedFragment(new preguntasFragment());
            }
        });


        btnsobrenosotros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectedFragment(new sobrenosotrosFragment());
            }
        });

        return v;
    }

    private void mostrartiempoazyncactual(int azynctiempo){

        if(azynctiempo ==0){
            tiempo= "No definido";
        }
        if(azynctiempo ==15000){
            tiempo= "15 Segundos";
        }
        if(azynctiempo ==30000){
            tiempo= "30 Segundos";
        }
        if(azynctiempo ==60000){
            tiempo= "1 Minuto";
        }
        if(azynctiempo ==300000){
            tiempo= "5 Minutos";
        }
    }


    private void saveOnPreferences(int tiempoasync) {
        SharedPreferences.Editor editor = asycprefs.edit();
        editor.putInt("tiempo", tiempoasync);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
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


    //metodo el cual se llama cuando se apreta cerrar sesion
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), task -> {
                    Toast.makeText(getContext(), "Salido", Toast.LENGTH_LONG).show();
                    revokeAccess();
                });
    }



    //metodo que te dirije al login /no te deja volver con el boton
    public void logout(){
        Intent intent= new Intent(getView().getContext(), loginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(intent);
    }


    //metodo para poder pasar de un fragmen a otro /(en caso de que no funcione proba getchild fragment)
    private void showSelectedFragment(Fragment fragment){
        getFragmentManager().beginTransaction().replace(R.id.container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();

    }


    private void revokeAccess() {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), task -> {
                    //envio hacia el login
                    Intent intent = new Intent(getView().getContext(), loginActivity.class);
                    //flags para que no pueda volver hacia atras
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //borra al usuario cargado en prefs
                    removesharedpreferenced();
                    startActivity(intent);
                });

    }


    //metodo para borrar las credenciales guardadas
    public  void removesharedpreferenced(){
        prefs.edit().clear().apply();
    }



}
