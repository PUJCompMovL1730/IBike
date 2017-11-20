package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MostrarEventos extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Evento evento;
    private Vector<Evento> array = new Vector<Evento>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_eventos);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        evento = new Evento();
        myRef = database.getReference();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        evento();
    }

    private void evento() {
        myRef = database.getReference("events/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String info;

                for(DataSnapshot eventoI : dataSnapshot.getChildren()){
                    evento = eventoI.getValue(Evento.class);
                    /*info = "Evento: " + evento.getDescripcion() + "\n" +
                           "Fecha: " + evento.getFecha() + "\n" +
                           "Participantes: " + evento.getIdUsuarios().size();*/
                        array.add(evento);

                }
                ArrayAdapter adapter = new ArrayAdapter(MostrarEventos.this,
                        android.R.layout.simple_list_item_1, array);
                ListView listView = (ListView) findViewById(R.id.eventos);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(getBaseContext(), InscripcionEvento.class);
                        Bundle bundle= new Bundle();
                        bundle.putString("NombreE", array.get(position).getDescripcion());
                        bundle.putString("IdEvento", array.get(position).getIdEvento());
                        bundle.putString("FechaE", array.get(position).getFecha());
                        bundle.putString("Participantes", "" + array.get(position).getIdUsuarios().size());
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                startActivity(new Intent(MostrarEventos.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(MostrarEventos.this, ActivityNoticias.class));
                break;
            }
            case R.id.estadisticas: {
                //logout();
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(MostrarEventos.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(MostrarEventos.this, Conversaciones.class));
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
    private void logout(){
        mAuth.signOut();
        Intent intent = new Intent(MostrarEventos.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
