package co.edu.javeriana.proyectoibike;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Conversaciones extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private ListView ls;
    public ValueEventListener recibidor;
    public ArrayAdapter adapter;
    public List<String> mens;
    public List<Usuarios> mens2;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_USERS="users/";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);


        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("users/");
        ls=(ListView)findViewById(R.id.msjs);
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
        loadConvers();

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Mensajes.class);
                Bundle bundle= new Bundle();
                bundle.putString("nombre", mens2.get(position).getNombre() +" "+ mens2.get(position).getApellido() );
                bundle.putString("correo",mens2.get(position).getCorreo());
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

    }


    public void loadConvers() {
        myRef = database.getReference(PATH_USERS);

        recibidor=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Usuarios men = dataSnapshot.child(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).getValue(Usuarios.class);

                    Log.i("zzz", "Encontró conversación: " + men);

                    mens=new ArrayList<String>();
                    mens2=new ArrayList<Usuarios>();

                    for (String a:men.getListaAmigos()) {
                        mens.add(a);
                    }

                    for (String s:mens) {
                        Usuarios m = dataSnapshot.child(s).getValue(Usuarios.class);
                        mens2.add(m);
                    }

                    adapter = new ArrayAdapter<Usuarios>(getApplicationContext(),android.R.layout.simple_list_item_1,mens2);


                    ls.setAdapter(adapter);
                    ls.setSelection(adapter.getCount() - 1);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("", "error en la consulta", databaseError.toException());
            }
        };
        myRef. addListenerForSingleValueEvent(recibidor);
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
                startActivity(new Intent(Conversaciones.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                //logout();
                break;
            }
            case R.id.estadisticas: {
                //logout();
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(Conversaciones.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(Conversaciones.this, Conversaciones.class));
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
        Intent intent = new Intent(Conversaciones.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
