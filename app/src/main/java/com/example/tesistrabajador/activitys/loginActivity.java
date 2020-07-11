package com.example.tesistrabajador.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Alertdialoglogin;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.interfaces.tesisAPI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class loginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    public static final int  Signincode = 777;
    private GoogleSignInClient googleSignInClient;
    SweetAlertDialog dp;
    SharedPreferences prefs,asycprefs;
    private EditText txtrut,txtpass;
    private Button btnlogin,btnregister;
    private String usuarioconectado="",contraseñausuarioconectado="";
    int idciudad=0;
    int azynctiempo =0;
    private SignInButton signInButton;

    public loginActivity() {
    }
    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Alertdialoglogin alertdialoglogin = new Alertdialoglogin(loginActivity.this);
        alertdialoglogin.startalertdialog();
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        asycprefs = getSharedPreferences("asycpreferences", Context.MODE_PRIVATE);

        setcredentiasexist();
        settiempoasyncexist();

        if(!usuarioconectado.isEmpty()&&(!contraseñausuarioconectado.isEmpty())){
            Intent intent = new Intent(loginActivity.this, menuActivity.class);
            saveOnPreferences(usuarioconectado,contraseñausuarioconectado,idciudad);
            startActivity(intent);
            alertdialoglogin.cancelalerdialog();
        }else{
            alertdialoglogin.cancelalerdialog();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton =(SignInButton) findViewById(R.id.Signbutton);
        txtrut = (EditText) findViewById(R.id.txtemail);
        txtpass = (EditText) findViewById(R.id.txtpassword);
        btnlogin = (Button) findViewById(R.id.btnlogin);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.Signbutton:
                        singIn();
                        break;
                }
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!TextUtils.isEmpty(txtrut.getText()) && !TextUtils.isEmpty(txtpass.getText())){
                    alertdialoglogin.startalertdialog();
                    String rut = txtrut.getText().toString();
                    String contrasena = txtpass.getText().toString();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://proyectotesis.ddns.net/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                    //metodo para llamar a la funcion que queramos
                    Call<Usuario> call = tesisAPI.getLoginTrabajador(rut,contrasena);
                    call.enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                            //si esta malo se ejecuta este trozo
                            if(!response.isSuccessful()){
                                Snackbar snackBar = Snackbar.make(loginActivity.this.findViewById(android.R.id.content),
                                        "Este Usuario no es un Trabajador/Contraseña erronea", Snackbar.LENGTH_LONG);
                                snackBar.show();

                               alertdialoglogin.cancelalerdialog();
                            }
                            //de lo contrario se ejecuta esta parte
                            else {
                                //respuesta del request
                                Usuario usuarios = response.body();
                                //declaracion de variables del response
                                String usuarioconectadopass = usuarios.getContrasena().toString();
                                String usuarioconectado = usuarios.getRut().toString();
                                idciudad = usuarios.getIdCiudad();
                                //if que compara los datos rescatados del response con los datos ingresados
                                if (usuarioconectado.equals(rut) && usuarioconectadopass.equals(contrasena)) {
                                    if(azynctiempo==0){
                                        azynctiempo =15000;
                                        saveOnazyncPreferences(azynctiempo);
                                    }
                                    Intent intent = new Intent(loginActivity.this, menuActivity.class);
                                    saveOnPreferences(rut,contrasena,idciudad);
                                    startActivity(intent);
                                    finish();
                                    alertdialoglogin.cancelalerdialog();
                                }
                            }
                        }
                        //si falla el request a la pagina mostrara este error
                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            Snackbar snackBar = Snackbar.make(loginActivity.this.findViewById(android.R.id.content),
                                    "Error al Iniciar Sesion", Snackbar.LENGTH_LONG);
                            alertdialoglogin.cancelalerdialog();
                        }
                    });
                }else{
                    Snackbar snackBar = Snackbar.make(loginActivity.this.findViewById(android.R.id.content),
                            "introdusca datos antes de logear", Snackbar.LENGTH_LONG);
                }
            }
        });



    }


    private void singIn(){
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,Signincode);
    }

    private void saveOnPreferences(String rut, String contrasena,int idciudad) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Rut", rut);
        editor.putString("ContraseNa", contrasena);
        editor.putInt("idCiudad", idciudad);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
    }

    private void setcredentiasexist() {
        String rut = getuserrutprefs();
        String contraseña = getusercontraseñaprefs();
        int ciudadid =getuseridciudadprefs();
        //string para asignar los valores del usuario si es que existe
        idciudad=ciudadid;
        if (!TextUtils.isEmpty(rut) && !TextUtils.isEmpty(contraseña)&& ciudadid !=0) {
            usuarioconectado=rut.toString();
            contraseñausuarioconectado=contraseña.toString();
        }
    }

    private String getuserrutprefs() {
        return prefs.getString("Rut", "");
    }

    private int getuseridciudadprefs() {
        return prefs.getInt("idCiudad", 0);
    }

    private String getusercontraseñaprefs() {
        return prefs.getString("ContraseNa", "");
    }


    //metodo el cual verifica al abrir la app si esque existe una cuenta de google
    @Override
    protected void onStart() {
        //busca la ultima cuenta logeada
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //si encuentra una cuenta acciona el metodo gomainscreen
        if (account!=null){
            goMainScreen();
        }
        super.onStart();
    }

    //metodo en el cual se verifica que los codigos coincidan
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        //si coinciden se ejecuta el metodo handlesigninresult
        if (requestCode == Signincode) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    //metodo el cual busca la cuenta que intenta iniciar si es correcto redirije menu
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account!=null){
                // Signed in successfully, show authenticated UI.
                goMainScreen();}
            //sino muestra el error
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //metodo para diririr al usuario hacia una activity que queramos
    private void goMainScreen() {
        Intent intent = new Intent(this,menuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }




    private void saveOnazyncPreferences(int tiempoasync) {
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




}