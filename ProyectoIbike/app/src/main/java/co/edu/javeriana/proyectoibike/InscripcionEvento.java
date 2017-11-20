package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class InscripcionEvento extends AppCompatActivity {
    private TextView info;
    private Button inscribir;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Evento event;
    public Bundle bundle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscripcion_evento);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        event = new Evento();
        info = (TextView) findViewById(R.id.infoEvento);
        inscribir = (Button) findViewById(R.id.unirse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        bundle=getIntent().getBundleExtra("bundle");
        info.setText("Evento elegido: " + bundle.getString("NombreE").toString() + "\n" +
                     "Fecha del evento: " + bundle.getString("FechaE").toString() + "\n" +
                     "Participantes inscritos: " + bundle.getString("Participantes").toString());
        final String idEvento = bundle.getString("IdEvento");
        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("events/");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.child(idEvento).getValue(Evento.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        inscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean registrado = false;
                int i = 0;
                List<String> usuariosEvento = event.getIdUsuarios();
                if(!usuariosEvento.isEmpty()){
                    while (registrado==false&&i<usuariosEvento.size()){
                        if(usuariosEvento.get(i).equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                            registrado = true;
                        }
                        i++;
                    }
                    if (registrado == false){
                        usuariosEvento.add(mAuth.getCurrentUser().getUid());
                        event.setIdUsuarios(usuariosEvento);
                        myRef = database.getReference("events/" + idEvento);
                        myRef.setValue(event);
                        Intent intent = new Intent(getApplicationContext(),ActivityMaps.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(),"Usted ya se ha registrado en este evento", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    usuariosEvento.add(mAuth.getCurrentUser().getUid());
                    event.setIdUsuarios(usuariosEvento);
                    myRef = database.getReference("events/" + idEvento);
                    myRef.setValue(event);
                    Intent intent = new Intent(getApplicationContext(),ActivityMaps.class);
                    startActivity(intent);
                }

            }
        });


    }
}
