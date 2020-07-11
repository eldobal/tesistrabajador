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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.activitys.menuActivity;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.interfaces.tesisAPI;

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
public class passperfilFragment extends Fragment {
    SharedPreferences prefs;
    SweetAlertDialog dp;
    private String rutperfil ="",contrasenaperfil="",contraseñaactualcomparar="";
    private EditText contraseña1, contraseña2,contraseñaactual;
    private Button btncambiopass;
    private boolean validado = false;
    Usuario usuario = new Usuario();
    NetworkInfo networkInfo;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    public passperfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //validacion contraseñas con alto nivel de dificultad
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        mAwesomeValidation.addValidation(getActivity(), R.id.cambiocontraseñaperfil, regexPassword, R.string.err_contraseña);
        mAwesomeValidation.addValidation(getActivity(), R.id.cambiocontraseña2perfil, regexPassword, R.string.err_contraseña2);
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        btncambiopass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()){
                    validado=true;
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_passperfil, container, false);
        contraseñaactual = (EditText) v.findViewById(R.id.passactual);
        contraseña1 = (EditText) v.findViewById(R.id.cambiocontraseñaperfil);
        contraseña2 = (EditText) v.findViewById(R.id.cambiocontraseña2perfil);
        btncambiopass = (Button) v.findViewById(R.id.btncambiocontrasena);
        Usuario usuario = new Usuario();
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);



        //se comprueba que exita el rut y la contraseña
        setcredentiasexist();

        if (networkInfo != null && networkInfo.isConnected()) {
            //metodo el cual traera los datos del usuario y se comparara la contraseña acutal y la que se trae
            cargardatosperfil();
        }else{
            //no hay coneccion manejar excepcion

        }


        btncambiopass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (mAwesomeValidation.validate()) {
                        String contraseñaactual1 = contraseñaactual.getText().toString();
                        //String  usuariocontraseña = usuario.getContrasena().toString();
                        String contraseñanueva = contraseña1.getText().toString();
                        String contraseñanueva2 = contraseña2.getText().toString();
                        if (contraseñaactual1.equals(contraseñaactualcomparar) && (contraseñanueva.equals(contraseñanueva2))) {
                            actualizarperfil();

                        }
                    }else{
                        Toast.makeText(getContext(), "error al validar la contraseña", Toast.LENGTH_LONG).show();
                    }
                }else{
                    //no hay coneccion manejar excepcion

                }

            }
        });

        return v;
    }


    private void saveOnPreferences(String contrasena) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ContraseNa", contrasena);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
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
                    contraseñaactualcomparar = usuarios.getContrasena();
                }
            }
            //si falla el request a la pagina mostrara este error
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "errorc :"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void actualizarperfil() {
        String RUT = rutperfil;
        String Contrasena = contraseña2.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        try {
            //metodo para llamar a la funcion que queramos
            //llamar a la funcion de get usuario la cual se le envia los datos (rut y contraseña )
            Call<Usuario> call = tesisAPI.UsuarioPass(RUT,Contrasena,contrasenaperfil);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    //si esta malo se ejecuta este trozo
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                    }
                    //de lo contrario se ejecuta esta parte
                    else {
                        //respuesta del request
                        Usuario usuarios = response.body();
                        saveOnPreferences(Contrasena);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizado,null);
                        builder.setView(viewsync);
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        texto.setText("Felicitaciones Ha podido actualizar su contraseña satisfactoriamente!");
                        Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerraralert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), menuActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                }
                //si falla el request a la pagina mostrara este error
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(getContext(), "errorb :" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
        }
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
}
