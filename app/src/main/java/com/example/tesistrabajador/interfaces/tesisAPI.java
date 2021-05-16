package com.example.tesistrabajador.interfaces;

import com.example.tesistrabajador.clases.Ciudad;
import com.example.tesistrabajador.clases.GananciasAPI;
import com.example.tesistrabajador.clases.Notificacion;
import com.example.tesistrabajador.clases.Solicitud;
import com.example.tesistrabajador.clases.Usuario;
import com.example.tesistrabajador.clases.UsuarioTrabajadorhome;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface tesisAPI {

    //llamada que se utiliza en el perfilfragment del usuario
    @GET("/login")
    Call<Usuario> getUsuario(@Query("RUTUSUARIO") String id,
                             @Query("Contrasena") String pass
    );





    //metodo para traer las notificaciones del cliente que esta ocupando la app /listanotificacion
    @GET("api/NotificacionAPI")
    Call<List<Notificacion>> getNotificacion(@Query("RUT") String rut,
                                             @Query("Contrasena") String contrasena
    );


    //api que se encuentra en uso en adaptador /pendiente
    @FormUrlEncoded
    @POST("/trabajadorconfirmar")
    Call<Object> TrabajadorConfirmar(@Field("idSolicitud") int idsolicitud,
                                     @Field("Fecha") String fechaconfirmacion,
                                      @Field("Precio") int precio,
                                     @Field("RUT") String rut,
                                     @Field("Contrasena") String contrasena
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
    @GET("/ciudades")
    Call<List<Ciudad>> getCiudades();










    /*   @GET("api/UsuarioAPI")
    Call<Usuario> getLoginTrabajador(@Query("RUT") String id,
                           @Query("pass") String pass
    );
*/
    @FormUrlEncoded
    @POST("/loginT")
    Call<Usuario> getLoginTrabajador(@Field("RUT") String id,
                           @Field("Contrasena") String pass
    );







    @GET("/trabajadorsolicitudes")
    Call<List<Solicitud>> TrabajadorSolicitudes(@Query("RUT") String rut,
                                                @Query("Contrasena") String Contrasena

    );



    @GET("/trabajadorsolicitud")
    Call<Solicitud> getSolicitudTrabajador(@Query("idSolicitud") int id,
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
    @FormUrlEncoded
    @POST("/trabajadorhome")
    Call<UsuarioTrabajadorhome> TrabajadorHome(@Field("RUT") String rut,
                                               @Field("Contrasena") String Contrasena
    );



    //metodo para finalizar la solicitud desde el cliente
    @FormUrlEncoded
    @POST("/finalizarsolicitud")
    Call<Object> finalizarSolicitud(@Field("RUT") String rut,
                                       @Field("Contrasena") String contrasena,
                                       @Field("idSolicitud") int idsolicitud,
                                       @Field("Precio") int preciofinal,
                                       @Field("Solucion") String solucion
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


    //metodo para calcular las ganancias
    @POST("api/SolicitudAPI")
    Call<GananciasAPI> DatosTrabajador(@Query("RUTU") String rutusuario,
                                       @Query("Contrasena") String contrasena,
                                       @Query("FirstDate") String fechainicio,
                                       @Query("SecondDate") String fechafin
    );



}
