package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RutaCompartible extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private TextView d;
    private TextView h;
    private TextView di;
    private TextView sp;
    private TextView dir;
    private TextView hu;
    private TextView es;
    private TextView tem;
    private Button b;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;

    public Bundle bundle;
    public String id;
    private Rutas rout;
    private Clima clima;
    private String idClima;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta_compartible);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        d=(TextView)findViewById(R.id.desde) ;
        h=(TextView)findViewById(R.id.hasta) ;
        di=(TextView)findViewById(R.id.dia) ;
        sp=(TextView)findViewById(R.id.velviento) ;
        dir=(TextView)findViewById(R.id.dirviento) ;
        hu=(TextView)findViewById(R.id.humedad) ;
        es=(TextView)findViewById(R.id.estado) ;
        tem=(TextView)findViewById(R.id.temperatura) ;
        b=(Button)findViewById(R.id.unirse) ;

        bundle=getIntent().getBundleExtra("bundle");
        d.setText("Ruta planeada desde: "+bundle.get("desde").toString());
        h.setText("Destino: "+bundle.get("hasta").toString());
        di.setText("Fecha: "+bundle.get("dia").toString());
        id=bundle.get("id").toString();

        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("rutas/");
        myRef2=database.getReference("climas/");


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                rout = dataSnapshot.child(id).getValue(Rutas.class);
                idClima=rout.getClima();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clima = dataSnapshot.child(idClima).getValue(Clima.class);
                sp.setText("Velocidad del viento: "+clima.getVel_viento()+" Km/h.");
                dir.setText("Dirección del viento: "+clima.getDir_viento()+"\u00b0");
                hu.setText("Humedad: "+clima.getHumedad()+"%");
                es.setText("Estado: "+clima.getTexto());
                tem.setText("Temperatura: "+clima.getTemperatura()+" \u00b0C");



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //compartir con facebook
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Mira la ruta que estoy compartiendo! "+"\n "+"Destino: "+ rout.getNombreDestino()+ "\n"+
                        "Metros recorridos: " + Integer.toString((int)rout.getKilometros())+"\n"+"#ibike");
                //intent.setPackage("com.facebook.katana");
                //startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share with"));


            }

        });

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
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

            case R.id.perfil: {
                startActivity(new Intent(RutaCompartible.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(RutaCompartible.this, ActivityNoticias.class));
                break;
            }
            case R.id.estadisticas: {
                //logout();
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(RutaCompartible.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(RutaCompartible.this, Conversaciones.class));
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
        Intent intent = new Intent(RutaCompartible.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
