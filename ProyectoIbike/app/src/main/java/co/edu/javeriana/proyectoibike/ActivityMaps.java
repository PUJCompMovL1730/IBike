package co.edu.javeriana.proyectoibike;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class ActivityMaps extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {





    //Variables GoogleAPI
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    //Variables de la Interfaz.
    private EditText texto;
    private Button ruta;

    // Variables Menu Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //Variables FireBase Autenticación
    private FirebaseAuth mAuth;
    //Atributos FireBase Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    // Limits for the geocoder search (Colombia)
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = -78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = -71.869905;


    //Variables Distancia Recorrida.
    double distanciaPropia;
    boolean inicio = false;

    //Coordenadas Finales
    double latitud;
    double longitud;

    //Variable polylines(Ruta pintada)
    private List<Polyline> polylines;

    //Coordenadas Iniciales
    double longitudOrigen;
    double latitudOrigen;

    //Validar Dominio Zona
    boolean validaDominio = false;
    boolean realizado = false;

    //Key Id Ruta Actual
    private String idRutaActual;

    //inicio
    boolean primeravez=true;

    //Circulo Actual
    String circuloActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        polylines = new ArrayList<>();
        texto = (EditText) findViewById(R.id.texto);
        ruta=(Button) findViewById(R.id.recorrido);
        //------------------------------------------------------------------------------------------
        /*
        * Declaracion Variables Menu Drawer
        */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        //------------------------------------------------------------------------------------------

        texto.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {

                        case KeyEvent.KEYCODE_DPAD_CENTER:

                            String direccion2 = texto.getText().toString();

                            //Toast.makeText(MapsActivity.this,"Enter Prro",Toast.LENGTH_LONG).show();
                            try {
                                irLugar(direccion2);
                                texto.setText("");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        case KeyEvent.KEYCODE_ENTER:


                            String direccion = texto.getText().toString();

                            //Toast.makeText(MapsActivity.this,"Enter Prro",Toast.LENGTH_LONG).show();
                            try {
                                irLugar(direccion);
                                texto.setText("");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        default:

                            break;
                    }
                }
                return false;
            }
        });

        //------------------------------------------------------------------------------------------

        ruta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMaps.this, CrearRuta.class));
            }

        });

    }//Fin onCreate


    //----------------------------------------------------------------------------------------------
    /*
    * Metodos Menu Drawer
    */
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------
    /*
    * Metodos Menu Drawer
    */
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.perfil: {
                startActivity(new Intent(ActivityMaps.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(ActivityMaps.this, ActivityNoticias.class));
                break;
            }
            case R.id.estadisticas: {
                //logout();
                break;
            }
            case R.id.mapa: {

            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityMaps.this, Conversaciones.class));
                break;
            }
            case R.id.logout: {
                logout();
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    /*
    * Metodos Menu Drawer
    */
    //----------------------------------------------------------------------------------------------
    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //----------------------------------------------------------------------------------------------
    /*
    * Metodos Menu Drawer
    */
    //----------------------------------------------------------------------------------------------
    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(ActivityMaps.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);


                cargarCirculos();


            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);


            cargarCirculos();

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(primeravez){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            primeravez=false;
        }



        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + userID);

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));


        if (inicio) {
            Log.d("INICIO", "INICIOALV");
            double distance = distance(location.getLatitude(), location.getLongitude(), latitud, longitud);
            if (distance < 20) {
                realizado=true;
                Log.d("Termino", "Termino");
                Log.d("Dist", String.valueOf(distanciaPropia));
                final int puntos = (int) (distanciaPropia / 1000);
                Log.d("Puntos Obtenidos: ", String.valueOf(puntos));
                Snackbar.make(mDrawerLayout, "Completaste la ruta, obtuviste " + puntos + " punto(s).", Snackbar.LENGTH_LONG).show();
                //Actualiza Base de Datos.
                //Actualizar Lista de rutas de Usuario

                myRef = database.getReference("rutas/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idRuta = idRutaActual;
                        if (idRuta != null){
                            Rutas rutas = dataSnapshot.child(idRuta).getValue(Rutas.class);
                            rutas.setRealizado(realizado);
                            myRef = database.getReference("rutas/" + idRuta);
                            myRef.setValue(rutas);
                            validaDominio = false;
                            realizado = false;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                myRef = database.getReference("users/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idUsuario = mAuth.getCurrentUser().getUid();
                        if (idUsuario != null){
                            Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                            Double punt = usuario.getPuntuacion();
                            usuario.setPuntuacion(punt+puntos);
                            myRef = database.getReference("users/" + idUsuario);
                            myRef.setValue(usuario);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Actualizar Puntaje Zonas
                myRef = database.getReference("circulos/");
                if(circuloActual.equals("Afuera")){

                } else if(circuloActual.equals("Amarillo")){

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String color = circuloActual;
                            if (color != null){
                                Circulos circulo = dataSnapshot.child(color).getValue(Circulos.class);

                                myRef = database.getReference("users/" + color);
                                myRef.setValue(circulo);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else if(circuloActual.equals("Azul")){

                } else if(circuloActual.equals("Rojo")){

                }











                inicio = false;
                mMap.clear();
                cargarCirculos();
            } else {
                Log.d("No Termino", "HPTA VIDA :'v");
            }

        }
    }


    public void cargarCirculos() {


        CircleOptions circleOptions = new CircleOptions() //Amarillo
                .center(new LatLng(4.626895, -74.064181)) //Cambiar Lat Lng
                .strokeWidth(10)
                .fillColor(Color.argb(128, 102, 216, 249))
                .strokeColor(Color.argb(128, 102, 216, 249))
                .radius(5000); //Cambiar Radio
        mMap.addCircle(circleOptions);

        CircleOptions circleOptions2 = new CircleOptions() //Azul
                .center(new LatLng(4.728902, -74.113546))
                .strokeWidth(10)
                .fillColor(Color.argb(128, 248,243,053))
                .strokeColor(Color.argb(128, 248,243,053))
                .radius(5000);
        mMap.addCircle(circleOptions2);

        CircleOptions circleOptions3 = new CircleOptions() //Rojo
                .center(new LatLng(4.616416, -74.153490))
                .strokeWidth(10)
                .fillColor(Color.argb(128, 255, 0, 0))
                .strokeColor(Color.argb(128, 255, 0, 0))
                .radius(5000);
        mMap.addCircle(circleOptions3);

    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    public void onProviderEnabled(String s) {

    }


    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
//Error OnStop
    @Override
    protected void onStop() {
        super.onStop();


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + userID);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userID);


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ActivityMaps.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            distanciaPropia = route.get(i).getDistanceValue();
            inicio = true;
            Log.d("Distancia", String.valueOf(route.get(i).getDistanceValue()));


        }
    }

    @Override
    public void onRoutingCancelled() {
    }


    public void irLugar(String direccion) throws IOException {

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(direccion, 1,
                lowerLeftLatitude,
                lowerLeftLongitude,
                upperRightLatitude,
                upperRigthLongitude);
        Address add = list.get(0);
        String locality = add.getLocality();
        Toast.makeText(ActivityMaps.this, locality, Toast.LENGTH_LONG).show();


        double lat = add.getLatitude();
        double lng = add.getLongitude();

        gotoLocation(lat, lng, 15, direccion);

    }

    private void gotoLocation(final double lat, final double lng, float zoom, final String direccion) {


        double dist = distance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), lat, lng);
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");

        dialogo1.setMessage(" Su destino se encuentra a : " + (int) dist + " metros de distancia, ¿Desea realizar la ruta? ");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                validarDistancia(lat, lng, direccion);

            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.show();


    }

    private void getRouteToMarker(LatLng destino) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), destino)
                .build();
        routing.execute();

    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(long1);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);

        double distance = startPoint.distanceTo(endPoint);
        Log.d("Distancia2", String.valueOf(distance));
        return distance;
        //Toast.makeText(this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();
    }

    private boolean validarDistancia(final double lat, final double lng, final String direccion) {

        //Validar en que circulo esta
        final LatLng ll = new LatLng(lat, lng);

        latitud = lat;
        longitud = lng;

        double circulo1 = distance(4.626895, -74.064181, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        double circulo2 = distance(4.728902, -74.113546, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        double circulo3 = distance(4.616416, -74.153490, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        final double dist = distance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), lat, lng);
        latitudOrigen = mLastLocation.getLatitude();
        longitudOrigen = mLastLocation.getLongitude();



        Log.d("CALCULOS", "c1: " + String.valueOf((int) circulo1) + " c2: " + String.valueOf((int) circulo2) + " c3: " + String.valueOf((int) circulo3));

        AlertDialog.Builder dialogo11 = new AlertDialog.Builder(this);
        dialogo11.setTitle("Importante");
        dialogo11.setMessage("El destino final de su ruta no se encuentra dentro de la zona de conquista, ¿Desea realizarla de todos modos? ");
        dialogo11.setCancelable(false);
        dialogo11.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mMap.clear();
                cargarCirculos();
                mMap.addMarker(new MarkerOptions().position(ll).title(direccion));
                myRef=database.getReference("rutas/");
                String key = myRef.push().getKey();
                idRutaActual = key;
                Calendar calendar = new GregorianCalendar();
                Date fechaActual = calendar.getTime();
                final Rutas ruta = new Rutas(key,latitud,longitud,longitudOrigen,latitudOrigen,fechaActual.toString(),dist,realizado,validaDominio,direccion);
                //Envio Datos a Base de Datos FireBase
                myRef=database.getReference("rutas/"+key);
                myRef.setValue(ruta);
                //Actualizar Lista de rutas de Usuario

                myRef = database.getReference("users/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idUsuario = mAuth.getCurrentUser().getUid();
                        if (idUsuario != null){
                            Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                            List<String> rutasDelUsuario = new ArrayList<String>();
                            rutasDelUsuario = usuario.getRutas();
                            rutasDelUsuario.add(ruta.getIdRuta());
                            myRef = database.getReference("users/" + idUsuario);
                            myRef.setValue(usuario);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                getRouteToMarker(ll);

            }
        });
        dialogo11.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //erasePolylines();
            }
        });


        if ((int) circulo1 < 5000) {
            circuloActual="Amarillo";
            Log.d("Circulo1", "Estoy en Universidad");
            double circulo11 = distance(4.626895, -74.064181, lat, lng);
            if (circulo11 < 5000) {
                Log.d("Circulo1", "DENTRO DEL CIRCULO");
                mMap.clear();
                cargarCirculos();
                mMap.addMarker(new MarkerOptions().position(ll).title(direccion));
                validaDominio=true;
                //Meter Datos  DB
                myRef=database.getReference("rutas/");
                String key = myRef.push().getKey();
                idRutaActual = key;
                Calendar calendar = new GregorianCalendar();
                Date fechaActual = calendar.getTime();
                final Rutas ruta = new Rutas(key,latitud,longitud,longitudOrigen,latitudOrigen,fechaActual.toString(),dist,realizado,validaDominio,direccion);
                //Envio Datos a Base de Datos FireBase
                myRef=database.getReference("rutas/"+key);
                myRef.setValue(ruta);
                //Actualizar Lista de rutas de Usuario

                myRef = database.getReference("users/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idUsuario = mAuth.getCurrentUser().getUid();
                        if (idUsuario != null){
                            Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                            List<String> rutasDelUsuario = new ArrayList<String>();
                            rutasDelUsuario = usuario.getRutas();
                            rutasDelUsuario.add(ruta.getIdRuta());
                            myRef = database.getReference("users/" + idUsuario);
                            myRef.setValue(usuario);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                getRouteToMarker(ll);
            } else {
                Log.d("Circulo1", "FUERA DEL CIRCULO");
                dialogo11.show();
            }
        } else if ((int) circulo2 < 5000) {
            circuloActual="Azul";
            Log.d("Circulo2", "Casa Camilo");
            double circulo22 = distance(4.728902, -74.113546, lat, lng);
            if (circulo22 < 5000) {
                Log.d("Circulo2", "DENTRO DEL CIRCULO");
                mMap.clear();
                cargarCirculos();
                mMap.addMarker(new MarkerOptions().position(ll).title(direccion));
                validaDominio=true;
                //Meter Datos  DB
                myRef=database.getReference("rutas/");
                String key = myRef.push().getKey();
                idRutaActual = key;
                Calendar calendar = new GregorianCalendar();
                Date fechaActual = calendar.getTime();
                final Rutas ruta = new Rutas(key,latitud,longitud,longitudOrigen,latitudOrigen,fechaActual.toString(),dist,realizado,validaDominio,direccion);
                //Envio Datos a Base de Datos FireBase
                myRef=database.getReference("rutas/"+key);
                myRef.setValue(ruta);
                //Actualizar Lista de rutas de Usuario

                myRef = database.getReference("users/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idUsuario = mAuth.getCurrentUser().getUid();
                        if (idUsuario != null){
                            Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                            List<String> rutasDelUsuario = new ArrayList<String>();
                            rutasDelUsuario = usuario.getRutas();
                            rutasDelUsuario.add(ruta.getIdRuta());
                            myRef = database.getReference("users/" + idUsuario);
                            myRef.setValue(usuario);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                getRouteToMarker(ll);
            } else {
                Log.d("Circulo2", "FUERA DEL CIRCULO");
                dialogo11.show();
            }
        } else if ((int) circulo3 < 5000) {
            circuloActual="Rojo";
            Log.d("Circulo3", "Hospital Kenedy");
            double circulo33 = distance(4.616416, -74.153490, lat, lng);
            if (circulo33 < 5000) {
                Log.d("Circulo3", "DENTRO DEL CIRCULO");
                mMap.clear();
                cargarCirculos();
                mMap.addMarker(new MarkerOptions().position(ll).title(direccion));
                validaDominio=true;
                //Meter Datos  DB
                myRef=database.getReference("rutas/");
                String key = myRef.push().getKey();
                idRutaActual = key;
                Calendar calendar = new GregorianCalendar();
                Date fechaActual = calendar.getTime();
                final Rutas ruta = new Rutas(key,latitud,longitud,longitudOrigen,latitudOrigen,fechaActual.toString(),dist,realizado,validaDominio,direccion);
                //Envio Datos a Base de Datos FireBase
                myRef=database.getReference("rutas/"+key);
                myRef.setValue(ruta);
                //Actualizar Lista de rutas de Usuario

                myRef = database.getReference("users/");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String idUsuario = mAuth.getCurrentUser().getUid();
                        if (idUsuario != null){
                            Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                            List<String> rutasDelUsuario = new ArrayList<String>();
                            rutasDelUsuario = usuario.getRutas();
                            rutasDelUsuario.add(ruta.getIdRuta());
                            myRef = database.getReference("users/" + idUsuario);
                            myRef.setValue(usuario);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                getRouteToMarker(ll);
            } else {
                Log.d("Circulo3", "FUERA DEL CIRCULO");
                dialogo11.show();
            }
        } else {
            circuloActual="Afuera";
            Log.d("FUERA", "FUERA");

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage(" La ruta que desea realizar no se encuentra dentro de un circulo de juego, ¿Desea realizarla de todos modos? ");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    mMap.clear();
                    cargarCirculos();
                    mMap.addMarker(new MarkerOptions().position(ll).title(direccion));
                    //Meter Datos  DB
                    myRef=database.getReference("rutas/");
                    String key = myRef.push().getKey();
                    idRutaActual = key;
                    Calendar calendar = new GregorianCalendar();
                    Date fechaActual = calendar.getTime();
                    final Rutas ruta = new Rutas(key,latitud,longitud,longitudOrigen,latitudOrigen,fechaActual.toString(),dist,realizado,validaDominio,direccion);
                    //Envio Datos a Base de Datos FireBase
                    myRef=database.getReference("rutas/"+key);
                    myRef.setValue(ruta);
                    //Actualizar Lista de rutas de Usuario

                    myRef = database.getReference("users/");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String idUsuario = mAuth.getCurrentUser().getUid();
                            if (idUsuario != null){
                                Usuarios usuario = dataSnapshot.child(idUsuario).getValue(Usuarios.class);
                                List<String> rutasDelUsuario = new ArrayList<String>();
                                rutasDelUsuario = usuario.getRutas();
                                rutasDelUsuario.add(ruta.getIdRuta());
                                myRef = database.getReference("users/" + idUsuario);
                                myRef.setValue(usuario);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    getRouteToMarker(ll);
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //erasePolylines();
                }
            });
            dialogo1.show();

        }


        return true;
    }


}
