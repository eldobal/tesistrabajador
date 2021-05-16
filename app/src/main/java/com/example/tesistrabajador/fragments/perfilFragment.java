package com.example.tesistrabajador.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Ciudad;
import com.example.tesistrabajador.clases.GlobalInfo;
import com.example.tesistrabajador.clases.Usuario;
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

public class perfilFragment extends Fragment {

    private ImageView fotoperfil;
    SharedPreferences prefs;
    SweetAlertDialog dp;
    LottieAnimationView loadingdots2;
    private GoogleSignInClient googleSignInClient;
    private String rutperfil ="",contrasenaperfil="",rutaurl="";
    final static String rutaservidor= GlobalInfo.Rutaservidor;
    private EditText rut,nombre,apellido,correo,telefono;
    private Spinner ciudad;
    private int idCiudad =0;
    private boolean validado=false,validacion1=false;
    private ArrayList<Ciudad> listaciudades = new ArrayList<Ciudad>();
    private Button editardatos,editarpass;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    NetworkInfo activeNetwork;
    ConnectivityManager cm ;


    public perfilFragment() {
        // Required empty public constructor
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAwesomeValidation.addValidation(getActivity(), R.id.telefonoperfil, "^[+]?[0-9]{10,13}$", R.string.err_fono);
        mAwesomeValidation.addValidation(getActivity(), R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_correo);
        mAwesomeValidation.addValidation(getActivity(), R.id.nombre, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(getActivity(), R.id.apellido, "[a-zA-Z\\s]+", R.string.err_apellido);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_perfil, container, false);
       // ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
       // NetworkInfo = connectivityManager.getActiveNetworkInfo();
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
          //si no encuentra el rut enviar al usuario a la pantalla de inicio
        }else{
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    cargarspiner();
                    cargardatosperfil();
                } else {
                    //manejar alert
                }
            }


            ciudad.setEnabled(false);
            ciudad.setClickable(false);
            //cuando se apriete el boton se preguntara si desea editar
            //lo cual hara los edittext editables otra vez
            //si el usuario vuevle a apretar los datos devera guardar y volver a poner no editable los edittext
            editardatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizar, null);
                    builder.setView(viewsync);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Button btnconfirmar = viewsync.findViewById(R.id.btnalertactualizar);
                    Button btncancelar = viewsync.findViewById(R.id.btnalertcancelarperfil);
                    btnconfirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editardatos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //metodo para hacer request de cambio de datos por parte del usuario
                                    if (mAwesomeValidation.validate()) {
                                        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                        activeNetwork = cm.getActiveNetworkInfo();
                                        if (activeNetwork != null) {
                                            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                                actualizarperfil();
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
                                            } else {
                                                //manejar alert
                                            }
                                        }
                                    }
                                }
                            });

                            dialog.dismiss();
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
                    });
                    btncancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        dialog.dismiss();
                        }
                    });
                }
            });

            //boton el cual redirije hacia la pantalla de cambio de contraseña del perfil del usuario
            editarpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    View viewsync = inflater.inflate(R.layout.alertdialogperfilacutalizarpass, null);
                    builder.setView(viewsync);
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Button btnconfirmar = viewsync.findViewById(R.id.btnalertactualizarpass);
                    Button btncancelar = viewsync.findViewById(R.id.btnalertcancelarperfilpass);

                    btnconfirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showSelectedFragment(new passperfilFragment());
                            dialog2.dismiss();
                        }
                    });
                    btncancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog2.dismiss();
                        }
                    });
                }
            });
        }
        return v;
    }

    //ir desde un fragment hacia una actividad
    public void updateDetail() {
        Intent intent = new Intent(getActivity(), perfilFragment.class);
        startActivity(intent);
    }

    private void cargardatosperfil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<Usuario> call = tesisAPI.getLoginTrabajador(rutperfil,contrasenaperfil);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse( Call<Usuario>call, Response<Usuario> response) {
                if(!response.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog3 = builder.create();
                    dialog3.setCancelable(false);
                    dialog3.show();
                    dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de cargar los datos del perfil. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog3.dismiss();
                        }
                    });
                  //  Toast.makeText(getContext(), "error/perfil/cargar/onresponse :"+response.code(), Toast.LENGTH_LONG).show();
                }
                //de lo contrario se ejecuta esta parte
                else {
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
                    Glide.with(getContext()).load(String.valueOf(rutaurl)).into(fotoperfil);
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
              //  Toast.makeText(getContext(), "error/perfil/cargar/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarspiner(){
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalInfo.Rutaservidor)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
            Call<List<Ciudad>> call = tesisAPI.getCiudades();
            call.enqueue(new Callback<List<Ciudad>>() {
                @Override
                public void onResponse( Call<List<Ciudad>>call, Response<List<Ciudad>>response) {
                    if(!response.isSuccessful()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                        builder.setView(viewsync);
                        AlertDialog dialog5 = builder.create();
                        dialog5.setCancelable(false);
                        dialog5.show();
                        dialog5.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        texto.setText("Ha ocurrido un error con la respuesta al tratar de cargar el spinner. intente en un momento nuevamente.");
                        Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog5.dismiss();
                            }
                        });

                      //  Toast.makeText(getContext(), "error/perfil/cargarspinner/onresponse:"+response.code(), Toast.LENGTH_LONG).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                    builder.setView(viewsync);
                    AlertDialog dialog6 = builder.create();
                    dialog6.setCancelable(false);
                    dialog6.show();
                    dialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                    texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog6.dismiss();
                        }
                    });
                  //  Toast.makeText(getContext(), "error/perfil/cargarspinner/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
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
                    .baseUrl(GlobalInfo.Rutaservidor)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
            Call<Usuario> call = tesisAPI.ActualizarUsuario(RUT,Nombre,Apellido,Correo,Fono,idCiudad,contrasenaperfil);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse( Call<Usuario>call, Response<Usuario> response) {
                    //si esta malo se ejecuta este trozo
                    if(!response.isSuccessful()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        View viewsync = getActivity().getLayoutInflater().inflate(R.layout.alertdialogusuarioexistente, null);
                        builder.setView(viewsync);
                        AlertDialog dialog7 = builder.create();
                        dialog7.setCancelable(false);
                        dialog7.show();
                        dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        TextView texto =(TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        texto.setText("Ha ocurrido un error al actualizar su perfil.");
                        Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerraralert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSelectedFragment(new perfilFragment());
                                dialog7.dismiss();
                            }
                        });
                     //   Toast.makeText(getContext(), "error/perfil/actualizar perfil/onfailure:"+response.code(), Toast.LENGTH_LONG).show();
                    }
                    //de lo contrario se ejecuta esta parte
                    else {
                        //respuesta del request
                         Usuario usuarios = response.body();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        View viewsync = getActivity().getLayoutInflater().inflate(R.layout.alertdialogperfilactualizado, null);
                        builder.setView(viewsync);
                        AlertDialog dialog3 = builder.create();
                        dialog3.setCancelable(false);
                        dialog3.show();
                        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerraralert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSelectedFragment(new perfilFragment());
                                dialog3.dismiss();
                                //metodo para cambiar de activity
                            }
                        });
                    }
                }
                //si falla el request a la pagina mostrara este error
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                    builder.setView(viewsync);
                    AlertDialog dialog8 = builder.create();
                    dialog8.setCancelable(false);
                    dialog8.show();
                    dialog8.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                    texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);
                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog8.dismiss();
                        }
                    });
                 //   Toast.makeText(getContext(), "error/perfil/actualizar perfil/onfailure:"+t.getMessage(), Toast.LENGTH_LONG).show();
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
