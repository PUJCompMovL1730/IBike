package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class CrearMarcador extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private EditText descrip;
    private LatLng locacionOrigen;
    private Button posicionA;
    private Spinner multi;
    private Button añadirM;
    private Marcador marca;
    public static final String PATH_MARKERS = "markers/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_marcador);
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        posicionA = (Button) findViewById(R.id.posicionActual);
        descrip = (EditText)findViewById(R.id.descripcion);
        multi = (Spinner) findViewById(R.id.Multiplicador);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        añadirM = (Button) findViewById(R.id.añadir);
        marca = new Marcador();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Multiplicadores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        multi.setAdapter(adapter);

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
        añadirM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String des = descrip.getText().toString();
                String idEmpresario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(validateDescripcion(des)){
                    String key = myRef.push().getKey();
                    marca.setIdMarcador(key);
                    marca.setIdEmpresario(idEmpresario);
                    marca.setLatitudMarcador(locacionOrigen.latitude);
                    marca.setLongitudMarcador(locacionOrigen.longitude);
                    marca.setMultiplicador((Integer) multi.getSelectedItem());
                    marca.setDescripcion(des);
                    marca.setVisitas(0);
                    myRef = database.getReference(PATH_MARKERS + key);
                    myRef.setValue(marca);
                    myRef = database.getReference("usersE/");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (idUsuario != null){
                                Empresario empresario = dataSnapshot.child(idUsuario).getValue(Empresario.class);
                                List<String> marcadoresDelEmpresario = new ArrayList<String>();
                                marcadoresDelEmpresario = empresario.getIdMarcadores();
                                marcadoresDelEmpresario.add(marca.getIdMarcador());
                                myRef = database.getReference("usersE/" + idUsuario);
                                myRef.setValue(empresario);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(getApplicationContext(),ActivityMapsE.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("LatitudO", locacionOrigen.latitude);
                    bundle.putDouble("LongitudO", locacionOrigen.longitude);
                    bundle.putString("Titulo", des);
                    bundle.putString("multi", "" + multi.getSelectedItem());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            }
        });
        posicionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permissions.checkSelfPermission(CrearMarcador.this, Permissions.FINE_LOCATION)){
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(CrearMarcador.this, new OnSuccessListener<Location>() {
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

    private  boolean validateDescripcion(String des){
        boolean validate = true;
        if(TextUtils.isEmpty(des)){
            descrip.setError("Required.");
            validate = false;
        }else {
            descrip.setError(null);
        }
        return validate;
    }
}
