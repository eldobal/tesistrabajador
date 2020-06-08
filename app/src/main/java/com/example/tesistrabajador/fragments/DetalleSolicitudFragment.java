package com.example.tesistrabajador.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tesistrabajador.R;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.interfaces.tesisAPI;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleSolicitudFragment extends Fragment {
    SweetAlertDialog dp;
    Solicitud solicitud = new Solicitud();
    private TextView numerosolicitud,fechasolicitud,fechadetallesolicitud,cliente,trabajador,rubro,precio,estadosolicitud,descripciondetallesolicitud,diagnosticodetallesolicitud,soluciondetallesolicitud;
    private ImageView imgperfiltrabajador,imgclientesacada;
    SharedPreferences prefs;
    private Button btnvolver;
    private int idsolicitud=0;
    final static String rutaservidor= "http://proyectotesis.ddns.net";
    public DetalleSolicitudFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        numerosolicitud = (TextView) getActivity().findViewById(R.id.txtnumerosolicitud);
        fechasolicitud = (TextView)getActivity().findViewById(R.id.txtfechasolicitud);
        fechadetallesolicitud = (TextView)getActivity().findViewById(R.id.txtfechadetallesolicitud);
        trabajador = (TextView)getActivity().findViewById(R.id.txttrabajadorsolicituddetalle);
        rubro = (TextView)getActivity().findViewById(R.id.txtrubrosolicituddetalle);
        precio = (TextView)getActivity().findViewById(R.id.txtpreciosolicitud);
        estadosolicitud =(TextView)getActivity().findViewById(R.id.txtestadosolicitud);
        descripciondetallesolicitud =(TextView)getActivity().findViewById(R.id.txtdescripciondetallesolicitud);
        diagnosticodetallesolicitud =(TextView)getActivity().findViewById(R.id.txtdiagnosticodetallesolicitud1);
        soluciondetallesolicitud =(TextView)getActivity().findViewById(R.id.txtsoluciondetallesolicitud);
        imgperfiltrabajador =(ImageView)getActivity().findViewById(R.id.imgperfilfilasolicitud);
        imgclientesacada =(ImageView)getActivity().findViewById(R.id.imgclientesacada);
        btnvolver = (Button)getActivity().findViewById(R.id.btnvolver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detalle_solicitud, container, false);
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null) {
            idsolicitud = datosRecuperados.getInt("idsolicitud");
        }



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://proyectotesis.ddns.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tesisAPI tesisAPI = retrofit.create(com.example.tesistrabajador.interfaces.tesisAPI.class);
        Call<Solicitud> call = tesisAPI.getSolicitudCliente(idsolicitud);
        call.enqueue(new Callback<Solicitud>() {
            @Override
            public void onResponse(Call<Solicitud> call, Response<Solicitud> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(v.getContext(), "error :"+response.code(), Toast.LENGTH_LONG).show();
                }
                else {
                    Solicitud solicituds = response.body();
                    numerosolicitud.setText("N Solicitud: "+solicituds.getIdSolicitud());
                    fechasolicitud.setText("Creada: "+solicituds.getFechaS());
                    fechadetallesolicitud.setText("Atendida: "+solicituds.getFechaA());
                    trabajador.setText("Rut trabajador: "+solicituds.getRUT());
                    rubro.setText("Rubro: "+solicituds.getRubro());
                    precio.setText("Precio aprox: "+solicituds.getPrecio());
                    estadosolicitud.setText("Estado : "+solicituds.getEstado());
                    descripciondetallesolicitud.setText(solicituds.getDescripcionP());
                    diagnosticodetallesolicitud.setText(solicituds.getDiagnostico());
                    soluciondetallesolicitud.setText(solicituds.getSolucion());
                    //carga de la foto del trabajor
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+solicituds.getFotoT())).into(imgperfiltrabajador);
                    //carga de foto cargada por el usuario
                    Glide.with(getContext()).load(String.valueOf(rutaservidor+solicituds.getIdFoto())).into(imgclientesacada);
                }
            }
            @Override
            public void onFailure(Call<Solicitud> call, Throwable t) {
                Toast.makeText(v.getContext(), "error :"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }
}
