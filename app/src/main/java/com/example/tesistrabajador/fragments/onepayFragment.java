package com.example.tesistrabajador.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tesistrabajador.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link onepayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class onepayFragment extends Fragment {

    WebView webView;
    String rutusuario="";
    int montoporpagar=0,montopagarpref=0;
    NetworkInfo activeNetwork;
    ConnectivityManager cm ;
    TimerTask task;
    SharedPreferences prefsganancias;

    public onepayFragment() {
        // Required empty public constructor
    }


    public static onepayFragment newInstance(String param1, String param2) {
        onepayFragment fragment = new onepayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_onepay, container, false);
        //se requieren recibir los datos claves(Monto,idsolicitud) para luego ser enviados al webview con la interfaz de webpay
        prefsganancias = this.getActivity().getSharedPreferences("Preferencesganancias", Context.MODE_PRIVATE);
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null) {
            montoporpagar = datosRecuperados.getInt("monto");
            rutusuario = datosRecuperados.getString("rutusuario");
            montopagarpref = datosRecuperados.getInt("montopref");
        }else{
            //falta validacion sobre el id
            homeFragment homeFragment  = new homeFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            getActivity().finish();
        }

        //configuracion del webview
        webView = (WebView) v.findViewById(R.id.webview);
        //este codigo habilia javascript
        WebSettings webSettings = webView.getSettings();
        webSettings = webView.getSettings(); webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true); webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);



        //url del backend webpay
        String url="http://proyectotesis.ddns.net/datos/WebPayPorPagar";
        String postData= null;
        String montoporpagarurl = Integer.toString(montoporpagar);

        try {
            postData = "RUT="+ URLEncoder.encode(rutusuario,"UTF8")+
                    "&Monto=" +URLEncoder.encode(montoporpagarurl,"UTF-8")
            ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                webView.postUrl(url, postData.getBytes());
            } else {

            }
        }

        //url la cual estara alojado el backend con la implementacion de transbank
        String urltrasaccionfinalizada ="http://proyectotesis.ddns.net/Datos/Final";
        String urltransaccionerror  ="http://proyectotesis.ddns.net/Datos/Error";

        //metodo en el cual el timer pregunta frecuentemente si la urlactual es igual a la urffinalizada
        final Handler handler = new Handler();
        Timer timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        activeNetwork = cm.getActiveNetworkInfo();
                        if (activeNetwork != null) {
                            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                //se captura la url donde se encuentra el usuario en el webview
                                String webUrlactual = webView.getUrl();
                                Toast.makeText(getContext(), webUrlactual, Toast.LENGTH_SHORT).show();
                                if (urltrasaccionfinalizada.equals(webUrlactual)) {
                                    task.cancel();
                                    //alert dialog con el mensaje de que se ha pagado correctamente
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    LayoutInflater inflater = getLayoutInflater();
                                    View viewsync = inflater.inflate(R.layout.alertdialogwebpaypago, null);
                                    builder.setView(viewsync);
                                    AlertDialog dialog7 = builder.create();
                                    dialog7.setCancelable(false);
                                    dialog7.show();
                                    dialog7.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    Button btncerrar = (Button) viewsync.findViewById(R.id.btnalertperfilexito);
                                    btncerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog7.dismiss();
                                            //salir de esta pantalla
                                            saveOnPreferencesgananciasporpagar(montopagarpref);
                                            homeFragment homeFragment = new homeFragment();
                                            getFragmentManager().beginTransaction().replace(R.id.container, homeFragment)
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    .commit();
                                        }
                                    });
                                }
                                if(urltransaccionerror.equals(webUrlactual)){
                                    task.cancel();
                                    //alert dialog con el mensaje de que se ha pagado correctamente
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    LayoutInflater inflater = getLayoutInflater();
                                    View viewsync = inflater.inflate(R.layout.alertdialogwebpaypago, null);
                                    builder.setView(viewsync);
                                    AlertDialog dialog8 = builder.create();
                                    dialog8.setCancelable(false);
                                    dialog8.show();
                                    dialog8.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    LottieAnimationView lottieAnimationView = (LottieAnimationView) viewsync.findViewById(R.id.idanimacionpagowebpay);
                                    lottieAnimationView.setAnimation(R.raw.pagoincorrecto);
                                    TextView texto = (TextView) viewsync.findViewById(R.id.txtalertnotificacion);
                                    texto.setText("Lamentablemente el pago ha sido rechazado. por fabor intentelo nuevamente!. en caso de que el problema persista por favor pagar en efectivo o comunicarse con la linea de atencion");
                                    Button btncerrar = (Button) viewsync.findViewById(R.id.btnalertperfilexito);
                                    btncerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog8.dismiss();
                                            homeFragment solicitudeFragment = new homeFragment();
                                            getFragmentManager().beginTransaction().replace(R.id.container, solicitudeFragment,"hometag")
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    .commit();
                                        }
                                    });
                                }
                            } else {
                                Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "No se ha encontrado una coneccion a Internet.", Snackbar.LENGTH_LONG);
                                snackBar.show();
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 500);  //ejecutar en intervalo definido por el programador






        return v;
    }




    //metodo para guardar los datos que se rescaten de la llamada
    private void saveOnPreferencesgananciasporpagar(int porpagar) {
        SharedPreferences.Editor editor = prefsganancias.edit();
        editor.putInt("porpagar", porpagar);
        //linea la cual guarda todos los valores en la pref antes de continuar
        editor.commit();
        editor.apply();
    }




}