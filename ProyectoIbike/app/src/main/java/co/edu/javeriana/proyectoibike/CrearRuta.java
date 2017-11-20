package co.edu.javeriana.proyectoibike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class CrearRuta extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText fecha;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Button guardar;
    private ImageButton posicionA;
    private Usuarios usuario;
    private Rutas recorrido;
    private LatLng locacionOrigen;
    private LatLng locacionDestino;
    FBAuth autenticador;
    final	static	double	RADIUS_OF_EARTH_KM	=	6371;
    public static final String PATH_USERS = "users/";
    public static final String PATH_RUTES = "rutes/";
    ProgressDialog mProgressDialog;
    public Clima clima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ruta);
        fecha = (EditText) findViewById(R.id.fecha);
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        guardar = (Button) findViewById(R.id.guardarRecorrido);
        database = FirebaseDatabase.getInstance();
        posicionA = (ImageButton) findViewById(R.id.posicionActual);
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
                            recorrido.setLatitudOrigen(locacionOrigen.latitude);
                            recorrido.setLongitudOrigen(locacionOrigen.longitude);
                            recorrido.setLatitudDestino(locacionDestino.latitude);
                            recorrido.setLongitudDestino(locacionDestino.longitude);
                            recorrido.setIdReporte("clima");
                            double distancia = distance(locacionOrigen.latitude, locacionOrigen.longitude, locacionDestino.latitude, locacionDestino.longitude);
                            recorrido.setKilometros(distancia);
                            recorrido.setRealizado(false);
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
                            String fechaR = fecha.getText().toString();
                            if (TextUtils.isEmpty(fechaR)) {
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

        new DownloadJSON().execute();
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

    public static JSONObject getJSONfromURL(String url){
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        // Download JSON data from URL
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
        }

        // Convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }

        try{

            jArray = new JSONObject(result);
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
        }

        return jArray;
    }
    //Esto obtiene el clima.
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(CrearRuta.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Obteniendo Información");
            // Set progressdialog message
            mProgressDialog.setMessage("Cargando...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            clima = new Clima();
            // YQL JSON URL
            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20%3D%20368148%20%20and%20u%3D'c'&format=json&diagnostics=true&callback=";

            try {
                // Retrive JSON Objects from the given URL in JSONfunctions.class
                JSONObject json_data = getJSONfromURL(url);
                JSONObject json_query = json_data.getJSONObject("query");
                JSONObject json_results = json_query.getJSONObject("results");
                JSONObject chan = json_results.getJSONObject("channel");
                JSONObject wind = chan.getJSONObject("wind");
                JSONObject atmosphere = chan.getJSONObject("atmosphere");
                JSONObject condition = chan.getJSONObject("item").getJSONObject("condition");
                clima.setDir_viento(wind.optLong("direction"));
                clima.setVel_viento(wind.optLong("speed"));
                clima.setHumedad(atmosphere.optLong("humidity"));
                clima.setTemperatura(condition.optInt("temp"));
                clima.setTexto(condition.optString("text"));

            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //Toast.makeText(CrearRuta.this, clima.toString(), Toast.LENGTH_SHORT).show();
            Log.i("Clima obtenido", clima.toString());
            mProgressDialog.dismiss();
        }
    }
}
