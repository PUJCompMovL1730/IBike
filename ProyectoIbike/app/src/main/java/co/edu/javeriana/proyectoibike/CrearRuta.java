package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CrearRuta extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText fecha;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Button guardar;
    private ImageButton posicionA;
    private Button iniciarR;
    private Usuarios usuario;
    private Rutas recorrido;
    private LatLng locacionOrigen;
    private LatLng locacionDestino;
    FBAuth autenticador;
    final	static	double	RADIUS_OF_EARTH_KM	=	6371;
    public static final String PATH_USERS = "users/";
    public static final String PATH_RUTES = "rutes/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ruta);
        fecha = (EditText) findViewById(R.id.fecha);
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        guardar = (Button) findViewById(R.id.guardarRecorrido);
        database = FirebaseDatabase.getInstance();
        posicionA = (ImageButton) findViewById(R.id.posicionActual);
        iniciarR = (Button) findViewById(R.id.iniciar);
        recorrido = new Rutas();
        final PlaceAutocompleteFragment fragmentOrigen = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.placeOrigen);
        fragmentOrigen.setHint("Origen");
        fragmentOrigen.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                locacionOrigen = place.getLatLng();
            }

            @Override
            public void onError(Status status) {

            }
        });
        PlaceAutocompleteFragment fragmentDestino = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.placeDestino);
        fragmentDestino.setHint("Destino");
        fragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                locacionDestino = place.getLatLng();
            }

            @Override
            public void onError(Status status) {

            }
        });

        autenticador = new FBAuth(this) {
            @Override
            public void onSuccess() {

            }
        };
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String idUser = autenticador.getUser().getUid();
                myRef = database.getReference(PATH_USERS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!fecha.getText().toString().isEmpty()){
                            Usuarios myUser = dataSnapshot.child(autenticador.getUser().getUid()).getValue(Usuarios.class);

                            String nombre = myUser.getNombre();
                            String apellido = myUser.getApellido();
                            final String key = myUser.getId();
                            String correo = myUser.getCorreo();
                            String equipo = myUser.getEquipo();
                            List<String> listaAmigos = myUser.getListaAmigos();
                            List<String> rutas = myUser.getRutas();

                            final Usuarios usuario = new Usuarios(nombre, apellido, key, correo, listaAmigos, equipo, rutas);
                            List<String> usuariosRuta = new ArrayList<String>();
                            usuariosRuta.add(idUser);
                            List<String> rutasDelUsuario = new ArrayList<String>();
                            rutasDelUsuario = usuario.getRutas();
                            recorrido.setOrigen(locacionOrigen);
                            recorrido.setDestino(locacionDestino);
                            double distancia = distance(locacionOrigen.latitude, locacionOrigen.longitude, locacionDestino.latitude, locacionDestino.longitude);
                            recorrido.setKilometros(distancia);
                            recorrido.setRealizado(false);
                            recorrido.setClima("Calido");
                            recorrido.setFecha(fecha.getText().toString());
                            String key2 = myRef.push().getKey();
                            recorrido.setIdRuta(key2);
                            rutasDelUsuario.add(recorrido.getIdRuta());
                            recorrido.setUsuariosRuta(usuariosRuta);
                            myRef = database.getReference(PATH_RUTES + key2);
                            myRef.setValue(recorrido);
                            usuario.setRutas(rutasDelUsuario);
                            myRef = database.getReference(PATH_USERS + idUser);
                            myRef.setValue(usuario);
                            Intent intent = new Intent(getApplicationContext(),ActivityMaps.class);
                            startActivity(intent);
                        }else {
                            String email = fecha.getText().toString();
                            if (TextUtils.isEmpty(email)) {
                                fecha.setError("Requerido.");
                            } else {
                                fecha.setError(null);
                            }
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        iniciarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String idUser = autenticador.getUser().getUid();
                myRef = database.getReference(PATH_USERS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuarios myUser = dataSnapshot.child(autenticador.getUser().getUid()).getValue(Usuarios.class);

                        String nombre = myUser.getNombre();
                        String apellido = myUser.getApellido();
                        final String key = myUser.getId();
                        String correo = myUser.getCorreo();
                        String equipo = myUser.getEquipo();
                        List<String> listaAmigos = myUser.getListaAmigos();
                        List<String> rutas = myUser.getRutas();

                        final Usuarios usuario = new Usuarios(nombre, apellido, key, correo, listaAmigos, equipo, rutas);


                        List<String> usuariosRuta = new ArrayList<String>();
                        usuariosRuta.add(idUser);
                        List<String> rutasDelUsuario = new ArrayList<String>();
                        rutasDelUsuario = usuario.getRutas();
                        recorrido.setOrigen(locacionOrigen);
                        recorrido.setDestino(locacionDestino);
                        double distancia = distance(locacionOrigen.latitude, locacionOrigen.longitude, locacionDestino.latitude, locacionDestino.longitude);
                        recorrido.setKilometros(distancia);
                        recorrido.setRealizado(false);
                        recorrido.setClima("Calido");
                        Calendar calendar = new GregorianCalendar();
                        Date fechaActual = calendar.getTime();
                        recorrido.setFecha(fechaActual.toString());
                        String key2 = myRef.push().getKey();
                        recorrido.setIdRuta(key2);
                        rutasDelUsuario.add(recorrido.getIdRuta());
                        recorrido.setUsuariosRuta(usuariosRuta);
                        myRef = database.getReference(PATH_RUTES + key2);
                        myRef.setValue(recorrido);
                        usuario.setRutas(rutasDelUsuario);
                        myRef = database.getReference(PATH_USERS + idUser);
                        myRef.setValue(usuario);
                        Intent intent = new Intent(getApplicationContext(),ActivityMaps.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("LatitudO", locacionOrigen.latitude);
                        bundle.putDouble("LongitudO", locacionOrigen.longitude);
                        bundle.putDouble("LatitudD", locacionDestino.latitude);
                        bundle.putDouble("LongitudD", locacionDestino.longitude);
                        bundle.putDouble("Distancia", distancia);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        posicionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permissions.checkSelfPermission(CrearRuta.this, Permissions.FINE_LOCATION)){
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(CrearRuta.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                locacionOrigen = new LatLng(location.getLatitude(), location.getLongitude());
                                fragmentOrigen.setText("Ubicación actual");
                            }
                        }
                    });
                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        autenticador.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        autenticador.stop();
    }
    public	double	distance(double	lat1,	double	long1,	double	lat2,	double	long2)	 {
        double	latDistance =	Math.toRadians(lat1	- lat2);
        double	lngDistance =	Math.toRadians(long1	 - long2);
        double	a	=	Math.sin(latDistance /	2)	*	Math.sin(latDistance /	2)
                +	Math.cos(Math.toRadians(lat1))	 *	Math.cos(Math.toRadians(lat2))
                *	Math.sin(lngDistance /	2)	*	Math.sin(lngDistance /	2);
        double	c	=	2	*	Math.atan2(Math.sqrt(a),	 Math.sqrt(1	- a));
        double	result	=	RADIUS_OF_EARTH_KM	*	c;
        return	Math.round(result*100.0)/100.0;
    }

}
