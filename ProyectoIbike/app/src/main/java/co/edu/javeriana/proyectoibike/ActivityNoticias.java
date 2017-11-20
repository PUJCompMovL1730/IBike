package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class ActivityNoticias extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    StorageReference filePath;
    Rutas ruta;
    private ListView lista;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;



    private Vector<Rutas> nombresFriend = new Vector<Rutas>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        ruta = new Rutas();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        lista = (ListView) findViewById(R.id.noticias);
        ruta();
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Noticia.class);
                Bundle bundle= new Bundle();
                bundle.putString("hasta", nombresFriend.get(position).getNombreDestino() );
                bundle.putString("desde", nombresFriend.get(position).getLatitudOrigen()+", "+nombresFriend.get(position).getLongitudOrigen() );
                bundle.putString("dia", nombresFriend.get(position).getFecha() );
                bundle.putString("id", nombresFriend.get(position).getIdRuta() );
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

    }


    public void ruta(){
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();


        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);





                userAlreadyAdd(myUser.getRutas());




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private boolean userAlreadyAdd(final List<String> friends){





            myRef2 = database.getReference("rutas/");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                         ruta = singleSnapshot.getValue(Rutas.class);


                        String fecha = ruta.getFecha();
                        LatLng origen = new LatLng(ruta.getLatitudOrigen(), ruta.getLongitudOrigen()) ;
                        LatLng destino = new LatLng(ruta.getLatitudDestino(), ruta.getLongitudDestino());
                        String clima = ruta.getIdReporte();
                        boolean realizado = ruta.isRealizado();



                        if(!realizado&&ruta.isProgramada()){
                            nombresFriend.add(ruta);
                        }


                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityNoticias.this, android.R.layout.simple_list_item_1,nombresFriend);
                    lista.setAdapter(arrayAdapter);





                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        return  true;
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

            case R.id.perfil: {
                startActivity(new Intent(ActivityNoticias.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(ActivityNoticias.this, ActivityNoticias.class));
                break;
            }
            case R.id.estadisticas: {
                //logout();
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(ActivityNoticias.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityNoticias.this, Conversaciones.class));
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
        Intent intent = new Intent(ActivityNoticias.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
