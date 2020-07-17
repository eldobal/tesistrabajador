package com.example.tesistrabajador.interfaces;

import com.example.tesistrabajador.clases.Ciudad;
import com.example.tesistrabajador.clases.Notificacion;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.clases.SolicitudDb;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.clases.UsuarioTrabajador;
import com.example.tesistrabajador.clases.UsuarioTrabajadorhome;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface tesisAPI {

    //llamada que se utiliza en el perfilfragment del usuario
    @GET("api/UsuarioAPI")
    Call<Usuario> getUsuario(@Query("RUTUSUARIO") String id,
                             @Query("Contrasena") String pass
    );


    //metodo para traer las notificaciones del cliente que esta ocupando la app /listanotificacion
    @GET("api/NotificacionAPI")
    Call<List<Notificacion>> getNotificacion(@Query("RUT") String rut,
                                             @Query("Contrasena") String contrasena
    );


    //api que se encuentra en uso en adaptador /pendiente
    @POST("api/SolicitudAPI")
    Call<String> TrabajadorConfirmar(@Query("idSolicitud") int idsolicitud,
                                     @Query("FechaD") String fechaconfirmacion,
                                      @Query("Precio") int precio,
                                     @Query("RUT") String rut,
                                     @Query("Contrasena") String contrasena
    );

    //api que se usa en la llamada actualizarperfil de perfilfragment
    @POST("api/UsuarioAPI")
    Call<Usuario> ActualizarUsuario(@Query("RUT") String RUT,
                                    @Query("Nombre") String Nombre,
                                    @Query("Apellido") String Apellido,
                                    @Query("Correo") String Correo,
                                    @Query("Fono") String Fono,
                                    @Query("id_idCiudad") int id_idCiudad,
                                    @Query("Contrasena") String contrasena
    );


    @POST("api/UsuarioAPI")
    Call<Usuario> UsuarioPass(@Query("RUT") String RUT,
                              @Query("Contrasena") String Contrasena,
                              @Query("Contrasenaantigua") String contrasenaantigua
    );

    //metodo el cual trae el listado de ciudades / usando acutalmente para cargar el spiner de ciudades en el registrar usaurio
    @GET("api/CiudadAPI/")
    Call<List<Ciudad>> getCiudades();


    @GET("api/UsuarioAPI")
    Call<Usuario> getLoginTrabajador(@Query("RUT") String id,
                           @Query("pass") String pass
    );


    @GET("api/SolicitudAPI")
    Call<List<Solicitud>> TrabajadorSolicitudes(@Query("trabajadorRUT") String rut,
                                                @Query("Contrasena") String Contrasena

    );



    @GET("api/SolicitudAPI")
    Call<Solicitud> getSolicitudTrabajador(@Query("idSolicitudT") int id,
                                           @Query("RUT") String rut,
                                           @Query("Contrasena") String Contrasena
                                           );



    //metodo para cancelar la solicitud del trabajador
    @POST("api/SolicitudAPI")
    Call<String> CancelarSolicitudt(@Query("idcancelarT") int idSolicitud,
                                    @Query("RUTU") String rut,
                                    @Query("Contrasena") String Contrasena
    );

    //metodo para cancelar la solicitud del cliente
    @POST("api/SolicitudAPI")
    Call<String> EliminarSoliPermanente(@Query("Delete") int idSolicitud,
                                        @Query("RUTU") String rut,
                                        @Query("Contrasena") String Contrasena
    );


    //metodo para cancelar la solicitud del cliente
    @POST("api/UsuarioAPI")
    Call<String> CambiarEstadoTrabajador(@Query("RUT") String rut,
                                         @Query("Contrasena") String Contrasena
    );


    //metodo para cancelar la solicitud del cliente
    @GET("api/UsuarioAPI")
    Call<UsuarioTrabajadorhome> TrabajadorHome(@Query("TRUT") String rut,
                                               @Query("Contrasena") String Contrasena
    );



    //metodo para finalizar la solicitud desde el cliente
    @POST("api/SolicitudAPI")
    Call<String> finalizarSolicitud(@Query("RUT") String rut,
                                       @Query("Contrasena") String contrasena,
                                       @Query("SolicitudFinalizar") int idsolicitud,
                                       @Query("PrecioFinal") int preciofinal,
                                       @Query("Solucion") String solucion
    );

    //metodo para confirmar si el pago esta correcto
    @POST("api/SolicitudAPI")
    Call<String> ConfirmarPago(@Query("RUT") String rut,
                                    @Query("Contrasena") String contrasena,
                                    @Query("SolicitudConfirmar") int idsolicitud,
                                       @Query("Pago") int pago
    );


    //metodo para cancelar la solicitud del cliente
    @POST("api/SolicitudAPI")
    Call<String> borrarNotificacion(@Query("RUT") String rutusuario,
                                    @Query("Contrasena") String contrasena,
                                    @Query("Notificacion") int idnotificacion

    );




}
