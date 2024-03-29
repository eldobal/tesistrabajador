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
import com.example.tesistrabajador.clases.GlobalInfo;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.interfaces.tesisAPI;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class passperfilFragment extends Fragment {
    SharedPreferences prefs;
    SweetAlertDialog dp;
    private String rutperfil ="",contrasenaperfil="",contraseñaactualcomparar="";
    private EditText contraseña1, contraseña2,contraseñaactual;
    private Button btncambiopass;
    private boolean validado = false;
    Usuario usuario = new Usuario();
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    NetworkInfo activeNetwork;
    ConnectivityManager cm ;

    public passperfilFragment() { }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //validacion contraseñas con alto nivel de dificultad
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        mAwesomeValidation.addValidation(getActivity(), R.id.cambiocontraseñaperfil, regexPassword, R.string.err_contraseña);
        mAwesomeValidation.addValidation(getActivity(), R.id.cambiocontraseña2perfil, regexPassword, R.string.err_contraseña2);
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
        View v= inflater.inflate(R.layout.fragment_passperfil, container, false);
        contraseñaactual = (EditText) v.findViewById(R.id.passactual);
        contraseña1 = (EditText) v.findViewById(R.id.cambiocontraseñaperfil);
        contraseña2 = (EditText) v.findViewById(R.id.cambiocontraseña2perfil);
        btncambiopass = (Button) v.findViewById(R.id.btncambiocontrasena);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //se comprueba que exita el rut y la contraseña
        setcredentiasexist();

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null || !rutperfil.isEmpty()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
            cargardatosperfil();
            }else{

            }
        }

        btncambiopass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
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
                    } else {

                    }
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
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<Usuario> call = tesisAPI.getUsuario(rutperfil,contrasenaperfil);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse( Call<Usuario>call, Response<Usuario> response) {
                //si esta malo se ejecuta este trozo
                if(!response.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                    builder.setView(viewsync);
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                    texto.setText("Ha ocurrido un error con la respuesta al tratar de cargar el perfil. intente en un momento nuevamente.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    Toast.makeText(getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                }
                else {
                    //respuesta del request
                    Usuario usuarios = response.body();
                    contraseñaactualcomparar = usuarios.getContrasena();
                }
            }
            //si falla el request a la pagina mostrara este error
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View viewsync = inflater.inflate(R.layout.alerdialogerrorservidor,null);
                builder.setView(viewsync);
                AlertDialog dialog2 = builder.create();
                dialog2.setCancelable(false);
                dialog2.show();
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                btncerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                Toast.makeText(getContext(), "errorc :"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarperfil() {
        String RUT = rutperfil;
        String Contrasena = contraseña2.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalInfo.Rutaservidor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
            Call<Usuario> call = tesisAPI.UsuarioPass(RUT,Contrasena,contrasenaperfil);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    //si esta malo se ejecuta este trozo
                    if (!response.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View viewsync = inflater.inflate(R.layout.alerdialogerrorresponce,null);
                        builder.setView(viewsync);
                        AlertDialog dialog3 = builder.create();
                        dialog3.setCancelable(false);
                        dialog3.show();
                        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        texto.setText("Ha ocurrido un error con la respuesta al tratar de actualizar el perfil. intente en un momento nuevamente.");
                        Button btncerrar =(Button) viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog3.dismiss();
                            }
                        });

                        Toast.makeText(getContext(), "error :" + response.code(), Toast.LENGTH_LONG).show();
                    }
                    //de lo contrario se ejecuta esta parte
                    else {
                        //respuesta del request
                        Usuario usuarios = response.body();
                        saveOnPreferences(Contrasena);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View viewsync = inflater.inflate(R.layout.alertdialogperfilactualizado, null);
                        builder.setView(viewsync);
                        AlertDialog dialog4 = builder.create();
                        dialog4.setCancelable(false);
                        dialog4.show();
                        dialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                        texto.setText("Felicitaciones Ha podido actualizar su contraseña satisfactoriamente!");
                        Button btncerraralert = viewsync.findViewById(R.id.btnalertperfilexito);

                        btncerraralert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), menuActivity.class);
                                startActivity(intent);
                                dialog4.dismiss();
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
                    AlertDialog dialog2 = builder.create();
                    dialog2.setCancelable(false);
                    dialog2.show();
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView texto = (TextView) viewsync.findViewById(R.id.txterrorservidor);
                    texto.setText("Ha ocurrido un error con la coneccion del servidor, Estamos trabajando para solucionarlo.");
                    Button btncerrar =(Button) viewsync.findViewById(R.id.btncerraralert);

                    btncerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });

                    Toast.makeText(getContext(), "errorb :" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
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
