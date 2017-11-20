package co.edu.javeriana.proyectoibike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoEvento extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Evento evento;
    private Empresario empresario;
    private String PATH_EVENTS = "events/";
    private String PATH_USERSE = "usersE/";
    private TextView datos;
    private List<String> array = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_evento);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        evento = new Evento();
        empresario = new Empresario();
        //datos = (TextView)findViewById(R.id.textoEventos);
        //obtenerInfo();
        myRef = database.getReference(PATH_EVENTS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String idEmpresario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String info;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(InfoEvento.this,
                        android.R.layout.simple_list_item_1, array);
                for(DataSnapshot eventoI : dataSnapshot.getChildren()){
                    evento = eventoI.getValue(Evento.class);
                    if(evento.getIdEmpresario().equalsIgnoreCase(idEmpresario)){
                        info = "Evento: " + evento.getDescripcion() + "\n" +
                                "Fecha: " + evento.getFecha() + "\n" +
                                "Participantes: " + evento.getIdUsuarios().size();
                        array.add(info);
                    }
                }
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void obtenerInfo() {
        final String idEmpresario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = database.getReference(PATH_USERSE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresario = dataSnapshot.child(idEmpresario).getValue(Empresario.class);
                List<String> infoEventos = new ArrayList<String>();
                infoEventos = empresario.getIdEventos();

                for(int i = 0; i<infoEventos.size(); i++){
                    final String idEvento = infoEventos.get(i).trim();
                    myRef = database.getReference(PATH_EVENTS);
                    final int numEventos = i;
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            evento = dataSnapshot.child(idEvento).getValue(Evento.class);
                            if(evento.isEstado()){
                                String info = "Evento: " + evento.getDescripcion() + "\n" +
                                              "Fecha: " + evento.getFecha() + "\n" +
                                              "Participantes: " + evento.getIdUsuarios().size();
                                //array[numEventos] += info;
                                //datos.setText(datos.getText().toString() + info + "\n" );
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
