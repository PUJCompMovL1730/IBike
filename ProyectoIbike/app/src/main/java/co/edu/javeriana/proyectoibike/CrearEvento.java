package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CrearEvento extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText fecha;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Button guardar;
    private Button posicionA;
    private Evento evento;
    private EditText des;
    private String PATH_EVENT = "events/";
    private LatLng locacionOrigen;
    private double latitudO, longitudO, latitudD, longitudD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);
        fecha = (EditText) findViewById(R.id.fecha);
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        guardar = (Button) findViewById(R.id.guardarEvento);
        des = (EditText) findViewById(R.id.editText);
        database = FirebaseDatabase.getInstance();
        posicionA = (Button) findViewById(R.id.posicionActual);
        myRef = database.getReference();
        evento = new Evento();
        final PlaceAutocompleteFragment fragmentOrigen = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.placeOrigen);
        fragmentOrigen.setHint("Punto de partida");
        fragmentOrigen.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latitudO = place.getLatLng().latitude;
                longitudO = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {

            }
        });
        PlaceAutocompleteFragment fragmentDestino = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.placeDestino);
        fragmentDestino.setHint("Punto de llegada");
        fragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latitudD = place.getLatLng().latitude;
                longitudD = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {

            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descrip = des.getText().toString();
                String idEmpresario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String fechaI = fecha.getText().toString();
                if(validateDescripcion(descrip)){
                    String key = myRef.push().getKey();
                    evento.setIdEvento(key);
                    evento.setIdEmpresario(idEmpresario);
                    evento.setLatitudOrigen(latitudO);
                    evento.setLongitudOrigen(longitudO);
                    evento.setLatitudDestino(latitudD);
                    evento.setLongitudDestino(longitudD);
                    evento.setFecha(fechaI);
                    evento.setEstado(true);
                    evento.setDescripcion(descrip);
                    myRef = database.getReference(PATH_EVENT + key);
                    myRef.setValue(evento);
                    myRef = database.getReference("usersE/");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (idUsuario != null){
                                Empresario empresario = dataSnapshot.child(idUsuario).getValue(Empresario.class);
                                List<String> eventosDelEmpresario = new ArrayList<String>();
                                eventosDelEmpresario = empresario.getIdEventos();
                                eventosDelEmpresario.add(evento.getIdEvento());
                                myRef = database.getReference("usersE/" + idUsuario);
                                myRef.setValue(empresario);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(getApplicationContext(),ActivityMapsE.class);
                    startActivity(intent);
                }

            }
        });
        posicionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permissions.checkSelfPermission(CrearEvento.this, Permissions.FINE_LOCATION)){
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(CrearEvento.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                latitudO = location.getLatitude();
                                longitudO = location.getLongitude();
                                fragmentOrigen.setText("Ubicación actual");
                            }
                        }
                    });
                }

            }
        });

    }
    private  boolean validateDescripcion(String descrip){
        boolean validate = true;
        if(TextUtils.isEmpty(descrip)){
            des.setError("Required.");
            validate = false;
        }else {
            des.setError(null);
        }
        return validate;
    }
}
