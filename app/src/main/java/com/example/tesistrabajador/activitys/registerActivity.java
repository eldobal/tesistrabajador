package com.example.tesistrabajador.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Ciudad;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.interfaces.tesisAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class registerActivity extends AppCompatActivity {
    SweetAlertDialog dp;
    private SharedPreferences prefs;
    private ArrayAdapter<Ciudad> adapter;

    private EditText txtrut,txtemail,txtcontraseña,txtcontraseña2,txtnombre,txtapellidos,txttelefono,txtdireccion;
    private Spinner spinnerciudades;
    private Button btnregistrar;
    private int posicion = 0;
    private List<Ciudad> listaciudades;
    NetworkInfo networkInfo;
    private int idCiudad =0;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        //listausuarios = new ArrayList<Usuario>();
        txtrut = (EditText) findViewById(R.id.rut) ;
        txtemail = (EditText) findViewById(R.id.email);
        txtcontraseña = (EditText) findViewById(R.id.password);
        txtcontraseña2 = (EditText) findViewById(R.id.password2);
        txtnombre = (EditText) findViewById(R.id.nombre);
        txtapellidos = (EditText) findViewById(R.id.apellido);
        txttelefono = (EditText) findViewById(R.id.telefono);
        spinnerciudades = (Spinner) findViewById(R.id.spinner);
        btnregistrar=(Button)findViewById(R.id.registrarse);


        if (networkInfo != null && networkInfo.isConnected()) {
            //carga las ciudades en el spinner
            cargarspiner();
        }else{
            //manejar exepcion
        }





        spinnerciudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ciudad ciudad = (Ciudad) parent.getSelectedItem();
                displayciudaddata(ciudad);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //validaciones hechas con awesome validation
        // mAwesomeValidation.addValidation(this, R.id.rut, , R.string.err_rut);
        //se valida como +569 y el numero de 8 digitos del usuario
        mAwesomeValidation.addValidation(this, R.id.telefono, "^[+]?[0-9]{10,13}$", R.string.err_fono);
        mAwesomeValidation.addValidation(this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_correo);
        mAwesomeValidation.addValidation(this, R.id.nombre, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(this, R.id.apellido, "[a-zA-Z\\s]+", R.string.err_apellido);
        //validacion contraseñas con alto nivel de dificultad
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        mAwesomeValidation.addValidation(this, R.id.password, regexPassword, R.string.err_contraseña);
        mAwesomeValidation.addValidation(this, R.id.password2, regexPassword, R.string.err_contraseña);



        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    if(validaRut(txtrut.getText().toString())) {
                        if ((txtcontraseña.getText().toString().equals(txtcontraseña2.getText().toString()))) {

                            if (networkInfo != null && networkInfo.isConnected()) {
                                String RUT = txtrut.getText().toString();
                                String Correo = txtemail.getText().toString();
                                String Nombre = txtnombre.getText().toString();
                                String Apellido = txtapellidos.getText().toString();
                                String Fono = txttelefono.getText().toString();
                                String Contrasena = txtcontraseña2.getText().toString();
                                Integer id_idCiudad = idCiudad;
                                Integer id_EstadoUsuario = 2;
                                Integer id_TipoUsuario = 2;
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://proyectotesis.ddns.net/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
                                //metodo para llamar a la funcion que queramos
                                Call<Usuario> call = tesisAPI.PostUsuario(RUT, Nombre, Apellido, Correo, Contrasena, Fono, id_idCiudad, id_EstadoUsuario, id_TipoUsuario);
                                try {
                                    call.enqueue(new Callback<Usuario>() {
                                        @Override
                                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                            if (!response.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "error code " + response.code(), Toast.LENGTH_LONG).show();
                                            } else {
                                                Usuario usuarioresponce = response.body();
                                                //usar sweetalert dialog para genera aviso
                                                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Usuario> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), "error code " + t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else{
                                //manejar excepcion

                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Complete los campos Correctamente", Toast.LENGTH_LONG).show();
                        }




                    }else{
                        Toast.makeText(getApplicationContext(), "El Rut No es valido ", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "error validate", Toast.LENGTH_LONG).show();
                }



            }
        });



    }




    private String getuserusuairoprefs(){

        return prefs.getString("usuario","");
    }


    private boolean validaciontelefono(String telefono){
        if(telefono.length()==9){

        }
        return true;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(registerActivity.this, loginActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }



    //request para traer los datos desde la pag u cargarlos en un spinner
    private void cargarspiner(){
        try {
            ArrayList<Ciudad> listaciudades = new ArrayList<Ciudad>();
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
                public void onResponse( Call<List<Ciudad>>call, Response<List<Ciudad>> response) {

                    //si esta malo se ejecuta este trozo
                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
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

                            //problema con cargar los datos a la lista
                            listaciudades.add(ciudadl);
                        }

                        //cargar la lista de ciudades rescatadas en el spinner
                        ArrayAdapter<Ciudad> a = new ArrayAdapter<Ciudad>(registerActivity.this,android.R.layout.simple_spinner_item,listaciudades);
                        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerciudades.setAdapter(a);


                    }
                }

                //si falla el request a la pagina mostrara este error
                @Override
                public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    public void getSelectedCiudad(View v){
        Ciudad ciudad = (Ciudad) spinnerciudades.getSelectedItem();
        displayciudaddata(ciudad);
    }



    //metodo para validar el rut de la persona
    public static Boolean validaRut ( String rut ) {
        Pattern pattern = Pattern.compile("^[0-9]+-[0-9kK]{1}$");
        Matcher matcher = pattern.matcher(rut);
        if ( matcher.matches() == false ) return false;
        String[] stringRut = rut.split("-");
        return stringRut[1].toLowerCase().equals(registerActivity.dv(stringRut[0]));
    }

    public static String dv ( String rut ) {
        Integer M=0,S=1,T=Integer.parseInt(rut);
        for (;T!=0;T=(int) Math.floor(T/=10))
            S=(S+T%10*(9-M++%6))%11;
        return ( S > 0 ) ? String.valueOf(S-1) : "k";
    }

    //muestra los datos de la ciudad especifica selelcionada
    private void displayciudaddata(Ciudad ciudad){
        idCiudad = ciudad.getIdCiudad();
        String Nombre = ciudad.getNombre();
        int id_idComuna= ciudad.getId_idComuna();
        String ciudaddata ="ID ciudad :"+ idCiudad + " nombre ciudad"+Nombre + " id comuna: "+id_idComuna;
    }




}