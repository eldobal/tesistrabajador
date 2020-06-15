package com.example.tesistrabajador.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.menuActivity;
import com.example.tesistrabajador.clases.Ciudad;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.fragments.passperfilFragment;
import com.example.tesistrabajador.interfaces.tesisAPI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

/**
 * A simple {@link Fragment} subclass.
 */
public class perfilFragment extends Fragment {

    private ImageView fotoperfil;
    SharedPreferences prefs;
    SweetAlertDialog dp;
    LottieAnimationView loadingdots2;
    private GoogleSignInClient googleSignInClient;
    private String rutperfil ="",contrasenaperfil="",rutaurl="";
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    private EditText rut,nombre,apellido,correo,telefono;
    private Spinner ciudad;
    private int idCiudad =0;
    private boolean validado=false,validacion1=false;
    private ArrayList<Ciudad> listaciudades = new ArrayList<Ciudad>();
    private Button editardatos,editarpass;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    NetworkInfo networkInfo;
    public perfilFragment() {
        // Required empty public constructor
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAwesomeValidation.addValidation(getActivity(), R.id.telefonoperfil, "^[+]?[0-9]{10,13}$", R.string.err_fono);
        mAwesomeValidation.addValidation(getActivity(), R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_correo);
        mAwesomeValidation.addValidation(getActivity(), R.id.nombre, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(getActivity(), R.id.apellido, "[a-zA-Z\\s]+", R.string.err_apellido);
        //realizar validacion
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_perfil, container, false);
        rut= (EditText) v.findViewById(R.id.rut);
        nombre = (EditText) v.findViewById(R.id.nombre);
        apellido = (EditText) v.findViewById(R.id.apellido);
        correo = (EditText) v.findViewById(R.id.email);
        telefono = (EditText) v.findViewById(R.id.telefonoperfil);
        ciudad = (Spinner) v.findViewById(R.id.spinner);
        fotoperfil = (ImageView) v.findViewById(R.id.usericon);
        editardatos = (Button) v.findViewById(R.id.actualizarperfil);
        editarpass = (Button) v.findViewById(R.id.actualizarcontraseña);
        loadingdots2 =(LottieAnimationView) v.findViewById(R.id.loadindots2);
        prefs = this.getActivity().getSharedPreferences("Preferences",Context.MODE_PRIVATE);

        if (networkInfo != null && networkInfo.isConnected()) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
            //trozo de codigo para rescatar parametros de la cuenta de usuario
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                nombre.setText(personGivenName);
                apellido.setText(personFamilyName);
                correo.setText(personEmail);
                //glide es una libreria con la cual se pueden cargar y descargar imagenespara pode utilizar en androidstudio
                //glide transforma la ruta que llega y la tranforma en una foto
                Glide.with(this).load(String.valueOf(personPhoto)).into(fotoperfil);
                Toast.makeText(getContext(), "Nombre"+personFamilyName+" Correo: "+personEmail+ " id:" +personId+"", Toast.LENGTH_LONG).show();
            }




        ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ciudad ciudad = (Ciudad) parent.getSelectedItem();
                displayciudaddata(ciudad);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //se comprueban que exista el rut y la contraseña
        setcredentiasexist();


        if(rutperfil.equals("")){

        }else{
            //se carga el spiner con las ciudades que hay en la base de datos
            cargarspiner();
            //se carga los datos del perfil para setearlos en los campos
            cargardatosperfil();
            //seccion de codigo en el cual se debera traer el json con los datos del usuario
            //donde se setearan los datos a los edittext

            ciudad.setEnabled(false);
            ciudad.setClickable(false);

            //cuando se apriete el boton se preguntara si desea editar
            //lo cual hara los edittext editables otra vez
            //si el usuario vuevle a apretar los datos devera guardar y volver a poner no editable los edittext
            editardatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dp = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE);
                    dp.setTitleText("Estas Segur@ De Querer Cambiar Los Datos?");
                    dp.setContentText("Podras Cambiar Tus Datos Personales!");
                    dp.setConfirmText("Si,Deseo Actualizar!");
                    dp.setCancelText("No,No Quiero");
                    //si preciona el boton si se podran editar los edittext
                    dp.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            //si vuelve a precionar el boton no podra editar los edittext y saldra un mensaje
                            editardatos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //metodo para hacer request de cambio de datos por parte del usuario
                                    if (mAwesomeValidation.validate()) {

                                        if (networkInfo != null && networkInfo.isConnected()) {
                                            actualizarperfil();
                                            dp = new SweetAlertDialog(v.getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                            dp.setTitleText("Has Actualizado tu perfil !");
                                            dp.setContentText("para volver a editar recargue el perfil!");
                                            dp.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    //metodo para cambiar de activity
                                                    updateDetail();
                                                }
                                            })
                                                    .show();
                                            //parametros false
                                            {
                                                rut.setEnabled(false);
                                                rut.setFocusable(false);
                                                rut.setFocusableInTouchMode(false);

                                                correo.setEnabled(false);
                                                correo.setFocusable(false);
                                                correo.setFocusableInTouchMode(false);

                                                nombre.setEnabled(false);
                                                nombre.setFocusable(false);
                                                nombre.setFocusableInTouchMode(false);

                                                apellido.setEnabled(false);
                                                apellido.setFocusable(false);
                                                apellido.setFocusableInTouchMode(false);

                                                telefono.setEnabled(false);
                                                telefono.setFocusable(false);
                                                telefono.setFocusableInTouchMode(false);

                                                ciudad.setEnabled(false);
                                                ciudad.setFocusable(false);
                                                ciudad.setFocusableInTouchMode(false);
                                            }
                                        }else{
                                            //no hay coneccion manejar excepcion

                                        }


                                    }
                                }
                            });
                            sDialog.dismissWithAnimation();
                            {
                                rut.setText(rut.getText());

                                correo.setEnabled(true);
                                correo.setFocusable(true);
                                correo.setFocusableInTouchMode(true);
                                correo.setText(correo.getText());

                                nombre.setEnabled(true);
                                nombre.setFocusable(true);
                                nombre.setFocusableInTouchMode(true);
                                nombre.setText(nombre.getText());

                                apellido.setEnabled(true);
                                apellido.setFocusable(true);
                                apellido.setFocusableInTouchMode(true);
                                apellido.setText(apellido.getText());

                                telefono.setEnabled(true);
                                telefono.setFocusable(true);
                                telefono.setFocusableInTouchMode(true);
                                telefono.setText(telefono.getText());

                                ciudad.setEnabled(true);
                                ciudad.setFocusable(true);
                                ciudad.setFocusableInTouchMode(true);
                            }
                        }
                    })
                            .show();

                }
            });

            //boton el cual redirije hacia la pantalla de cambio de contraseña del perfil del usuario
            editarpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SweetAlertDialog dp2 =new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE);
                    dp2.setTitleText("Estas Segur@ De Querer Cambiar Tu Contraseña?");
                    dp2.setContentText("Ten Cuidado!");
                    dp2.setConfirmText("Si,Deseo Actualizar!");
                    dp2.setCancelText("No,No Quiero");
                    //si preciona el boton si se podran editar los edittext
                    dp2.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            showSelectedFragment(new passperfilFragment());
                            sDialog.dismissWithAnimation();
                        }
                    })
                            .show();
                }
            });


        }





        }else{
            //no hay coneccion manejar excepcion
            getActivity().finish();
            Toast.makeText(getContext(), "Error en la conecctacion del dispocitivo, asegurese de que tenga coneccion", Toast.LENGTH_LONG).show();
        }




        return v;
    }

    //ir desde un fragment hacia una actividad
    public void updateDetail() {
        Intent intent = new Intent(getActivity(), menuActivity.class);
        startActivity(intent);
    }

    private void cargardatosperfil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        //metodo para llamar a la funcion que queramos
        //llamar a la funcion de get usuario la cual se le envia los datos (rut y contraseña )
        Call<Usuario> call = tesisAPI.getUsuario(rutperfil,contrasenaperfil);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse( Call<Usuario>call, Response<Usuario> response) {
                //si esta malo se ejecuta este trozo
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                }
                //de lo contrario se ejecuta esta parte
                else {
                    //respuesta del request
                    Usuario usuarios = response.body();
                    rut.setText(usuarios.getRut().toString());
                    nombre.setText(usuarios.getNombre().toString());
                    apellido.setText(usuarios.getApellido().toString());
                    correo.setText(usuarios.getCorreo().toString());
                    telefono.setText(usuarios.getFono().toString());
                    idCiudad = usuarios.getIdCiudad();
                    rutaurl = usuarios.getFoto();
                    loadingdots2.setVisibility(View.INVISIBLE);
                    loadingdots2.cancelAnimation();
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+rutaurl)).into(fotoperfil);

                    boolean encontrado = false;
                    for(int i=1; i<listaciudades.size()&&encontrado==false;i++){
                        if(listaciudades.get(i).getIdCiudad() ==idCiudad){
                            ciudad.setSelection(i);
                            encontrado= true;
                        }
                    }
                }
            }

            //si falla el request a la pagina mostrara este error
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarspiner(){
        try {

            ArrayList<String> listanombres = new ArrayList<String>();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://proyectotesis.ddns.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);

            //metodo para llamar a la funcion que queramos
            Call<List<Ciudad>> call = tesisAPI.getCiudades();
            call.enqueue(new Callback<List<Ciudad>>() {
                @Override
                public void onResponse( Call<List<Ciudad>>call, Response<List<Ciudad>>response) {
                    //si esta malo se ejecuta este trozo
                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                    }
                    //de lo contrario se ejecuta esta parte
                    else {
                        //respuesta del request
                        List<Ciudad> ciudades = response.body();
                        //declaracion de variables del response
                        for(Ciudad ciudad: ciudades){
                            //falta poder cargar la lista del response hacia un spinner
                            Ciudad ciudadl = new Ciudad();
                            ciudadl.setNombre(ciudad.getNombre()) ;
                            ciudadl.setIdCiudad(ciudad.getIdCiudad());
                            ciudadl.setId_idComuna(ciudad.getId_idComuna());
                            listaciudades.add(ciudadl);
                        }
                        //cargar la lista de ciudades rescatadas en el spinner
                        ArrayAdapter<Ciudad> a = new ArrayAdapter<Ciudad>(getContext(),android.R.layout.simple_spinner_item,listaciudades);
                        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ciudad.setAdapter(a);


                    }
                }

                //si falla el request a la pagina mostrara este error
                @Override
                public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                    Toast.makeText(getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void actualizarperfil() {
        try {
            String RUT = rut.getText().toString();
            String Correo = correo.getText().toString();
            String Nombre = nombre.getText().toString();
            String Apellido = apellido.getText().toString();
            String Fono = telefono.getText().toString();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://proyectotesis.ddns.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
            //metodo para llamar a la funcion que queramos
            //llamar a la funcion de get usuario la cual se le envia los datos (rut y contraseña )
            Call<Usuario> call = tesisAPI.ActualizarUsuario(RUT,Nombre,Apellido,Correo,Fono,idCiudad);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse( Call<Usuario>call, Response<Usuario> response) {
                    //si esta malo se ejecuta este trozo
                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                    }
                    //de lo contrario se ejecuta esta parte
                    else {
                        //respuesta del request
                        Usuario usuarios = response.body();
                    }
                }
                //si falla el request a la pagina mostrara este error
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void getSelectedCiudad(View v){
        Ciudad ciudad1 = (Ciudad) ciudad.getSelectedItem();
        displayciudaddata(ciudad1);
    }

    //muestra los datos de la ciudad especifica selelcionada
    private void displayciudaddata(Ciudad ciudad){
        idCiudad = ciudad.getIdCiudad();
        String Nombre = ciudad.getNombre();
        int id_idComuna= ciudad.getId_idComuna();
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

    private void showSelectedFragment(Fragment fragment){
        getFragmentManager().beginTransaction().replace(R.id.container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}
