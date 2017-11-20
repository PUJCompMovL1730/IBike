package co.edu.javeriana.proyectoibike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityLogin extends AppCompatActivity {

    //Variables FireBase Autenticación
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Campos de texto Correo y Password
    private EditText mUser;
    private EditText mPassword;
    private TextView mRegistro;
    private Button btnIngresar;
    private Usuarios usuario;
    private Empresario empresa;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRefE;
    private String PATH_USERS = "users/";
    private String PATH_USERSE = "usersE/";
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Autenticando...");
        mProgress.setMessage("Por Favor Espere...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        usuario = new Usuarios();
        empresa = new Empresario();
        mAuth = FirebaseAuth.getInstance();


        mUser = (EditText) findViewById(R.id.mUser);
        mPassword = (EditText) findViewById(R.id.mPassword);
        mRegistro = (TextView) findViewById(R.id.mRegistro);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    final String idUser = user.getUid();
                    myRef = database.getReference(PATH_USERS);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usuario =  dataSnapshot.child(idUser).getValue(Usuarios.class);
                            Log.d("ss", "onAuthStateChanged:signed_in:" + idUser);
                            if(usuario != null){
                                startActivity(new Intent(ActivityLogin.this, ActivityMaps.class));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    final String idUserE = user.getUid();
                    myRefE = database.getReference(PATH_USERSE);
                    myRefE.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            empresa = dataSnapshot.child(idUserE).getValue(Empresario.class);
                            Log.d("ss", "onAuthStateChanged:signed_in:" + idUser);
                            if(empresa != null){
                                startActivity(new Intent(ActivityLogin.this, ActivityMapsE.class));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out
                    Log.d("ss", "onAuthStateChanged:signed_out");
                }
            }
        };


        mRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivitySignup.class));
            }

        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = mUser.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mUser.setError("Requerido.");
            valid = false;
        } else {
            mUser.setError(null);
        }
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Requerido.");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    protected void iniciarSesion() {
        if (validateForm()) {
            mProgress.show();

            String email = mUser.getText().toString();
            String password = mPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.d("ss", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        //Error al autenticar, el usuario que esta ingresando no existe en nuestra base  de datos.
                        Toast.makeText(ActivityLogin.this, "Error al autenticar", Toast.LENGTH_SHORT).show();
                        mUser.setText("");
                        mPassword.setText("");
                        mProgress.dismiss();
                    }
                }
            });
        }
    }
}
