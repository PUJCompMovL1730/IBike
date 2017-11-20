package co.edu.javeriana.proyectoibike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class InforMarcador extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Marcador marcador;
    private Empresario empresario;
    private String PATH_MARKERS = "markers/";
    private List<String> array = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_marcador);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        marcador = new Marcador();
        empresario = new Empresario();
        myRef = database.getReference(PATH_MARKERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String idEmpresario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String info;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(InforMarcador.this,
                        android.R.layout.simple_list_item_1, array);
                for(DataSnapshot marcadorI : dataSnapshot.getChildren()){
                    marcador = marcadorI.getValue(Marcador.class);
                    if(marcador.getIdEmpresario().equalsIgnoreCase(idEmpresario)){
                        info = "Punto: " + marcador.getDescripcion() + "\n" +
                                "Multiplicador: x" + marcador.getMultiplicador() + "\n" +
                                "Visitas a este punto: " + marcador.getVisitas();
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
}

