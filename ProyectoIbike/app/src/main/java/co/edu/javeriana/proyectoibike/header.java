package co.edu.javeriana.proyectoibike;

import android.app.Service;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class header extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private TextView nombreUsuario;
    private TextView correoUsuario;

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        correoUsuario = (TextView) findViewById(R.id.correoUsuario);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(String.valueOf(dataSnapshot.child("Nombre").getValue()), "onAuthStateChanged:signed_in:" );
                            nombreUsuario.setText(String.valueOf(dataSnapshot.child("Nombre").getValue())+" "+String.valueOf(dataSnapshot.child("Apellido").getValue()));
                            correoUsuario.setText(String.valueOf(dataSnapshot.child("Correo").getValue()));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {

                    Toast.makeText(header.this, "Error al autenticar", Toast.LENGTH_SHORT).show();

                }
            }
        };

    }


}
