package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityMapsE extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener{

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    // Variables Menu Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //Variables FireBase Autenticación
    private FirebaseAuth mAuth;
    private String PATH_MARKERS = "markers/";
    private int ZOOM_STREET = 15;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Marcador marcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_e);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        database = FirebaseDatabase.getInstance();
        marcador = new Marcador();
        //variables menu
        mAuth = FirebaseAuth.getInstance();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                if (Permissions.checkSelfPermission(ActivityMapsE.this, Permissions.FINE_LOCATION)){
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(ActivityMapsE.this, new
                            OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng destino = place.getLatLng();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, ZOOM_STREET));
                                    }
                                }
                            });


                }
                else {
                    Toast.makeText(ActivityMapsE.this,"Es posible que no este encendido la ubicación del dispositivo",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(Permissions.askPermission(ActivityMapsE.this, Permissions.FINE_LOCATION, "Debe habilitar la ubicación del dispositivo")){
            location();
            if(bundle != null){
                LatLng origen = new LatLng(bundle.getDouble("LatitudO"), bundle.getDouble("LongitudO"));
                mMap.addMarker(new MarkerOptions().position(origen).title(bundle.getString("Titulo")).snippet("Multiplicador por visitar este punto: x" +
                        bundle.getString("multi")));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, ZOOM_STREET));
            }else {
                obtenerUbicacionActual();
            }
            cargarMarcadores();
        }
        else {
            Toast.makeText(ActivityMapsE.this,"Es posible que no este encendido la ubicación del dispositivo",Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera

    }

    private void cargarMarcadores() {
        myRef = database.getReference(PATH_MARKERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot marca : dataSnapshot.getChildren()){
                    marcador = marca.getValue(Marcador.class);
                    LatLng posicion = new LatLng(marcador.getLatitudMarcador(),marcador.getLongitudMarcador());
                    mMap.addMarker(new MarkerOptions().position(posicion)
                            .title(marcador.getDescripcion()).snippet("Multiplicador por visitar este punto: x" + marcador.getMultiplicador()));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void obtenerUbicacionActual() {
        if (Permissions.checkSelfPermission(ActivityMapsE.this, Permissions.FINE_LOCATION)){
            mFusedLocationClient.getLastLocation().addOnSuccessListener(ActivityMapsE.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        LatLng origen = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, ZOOM_STREET));
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!Permissions.permissionGranted(requestCode, permissions, grantResults)) {
            return;
        }
        switch (requestCode) {
            case Permissions.FINE_LOCATION:
                location();
                break;
        }
    }

    private void location() {
        if(Permissions.checkSelfPermission(this, Permissions.FINE_LOCATION)) {
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.addMarcador: {
                Intent intent = new Intent(getBaseContext(),CrearMarcador.class);
                startActivity(intent);
                break;
            }
            case R.id.cEvento: {
                Intent intent = new Intent(getBaseContext(),CrearEvento.class);
                startActivity(intent);
                break;
            }
            case R.id.infoEvento: {
                Intent intent = new Intent(getBaseContext(),InfoEvento.class);
                startActivity(intent);
                break;
            }
            case R.id.delEvento: {

                break;
            }
            case R.id.infoMarcador: {

                //logout();
                break;
            }
            case R.id.delMarcador: {

                //logout();
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

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

    }


    private void logout(){
        mAuth.signOut();
        Intent intent = new Intent(ActivityMapsE.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
