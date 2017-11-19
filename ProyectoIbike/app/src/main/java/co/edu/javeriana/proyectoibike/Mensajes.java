package co.edu.javeriana.proyectoibike;

import android.content.Context;
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

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Mensajes extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{


    private EditText m;
    private TextView t;
    private ListView ls;
    private Button b;
    public ArrayAdapter adapter;

    public List<Mensaje> mens;
    public ValueEventListener recibidor;
    public Bundle bundle;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_MESSAGES="messages/";
    public int cont=0;
    private FirebaseAuth mAuth;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        mens=new ArrayList<Mensaje>();
        ls=(ListView)findViewById(R.id.list_of_messages);
        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("messages/");
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


        loadMessages();





        //ls.setAdapter(adapter);

        t=(TextView)findViewById(R.id.nom);
        bundle=getIntent().getBundleExtra("bundle");
        String s=bundle.get("nombre").toString();
        t.setText(s);

        b=(Button)findViewById(R.id.enviar);
        m=(EditText)findViewById(R.id.men);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mens.add(m.getText().toString());


                myRef.push().setValue(new Mensaje(FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail(), m.getText().toString(), bundle.get("correo").toString())

                        );

                m.setText("");


            }

        });
    }

    public void loadMessages() {
        myRef = database.getReference(PATH_MESSAGES);
        recibidor=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Mensaje men = singleSnapshot.getValue(Mensaje.class);
                    Log.i("zzz", "Encontró mensaje: " + men.getTexto());


                    if(!valido(men)&&men.getDestino()!=null&&men.getRemitente()!=null) {

                        if(((men.getDestino().equals(bundle.get("correo").toString()))&&(men.getRemitente().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())))||((men.getRemitente().equals(bundle.get("correo").toString()))&&(men.getDestino().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())))) {
                            mens.add(men);
                        }
                    }

                    adapter = new ArrayAdapter<Mensaje>(Mensajes.this,android.R.layout.simple_list_item_1,mens);

                    ls.setAdapter(adapter);
                    ls.setSelection(adapter.getCount() - 1);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("", "error en la consulta", databaseError.toException());
            }
        };
        myRef. addValueEventListener(recibidor);
    }

    @Override
    protected void onStop(){
        super.onStop();
        myRef.removeEventListener(recibidor);
    }

    public boolean valido(Mensaje men){
        for (Mensaje m:mens) {
            if(men.getFecha()==m.getFecha()&&m.getTexto().equals(men.getTexto())){
                //Log.i("Tag",men.getDestino()+"=="+getIntent().getStringExtra("nombre"));
              return true;
            }
        }
        return false;
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
                startActivity(new Intent(Mensajes.this, ActivityPerfil.class));
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
                startActivity(new Intent(Mensajes.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(Mensajes.this, Conversaciones.class));
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
        Intent intent = new Intent(Mensajes.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
