package co.edu.javeriana.proyectoibike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivitySignup extends AppCompatActivity {

    //Variables Firebase
    //Variables FireBase Autenticación
    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private EditText mUser;
    private EditText mPassword;
    private EditText mNombreUsuario;
    private EditText mApellidoUsuario;
    private Button btnRegistro;
    private TextView mRegistro;
    private ImageButton imagenPerfil;

    public static final String PATH_USERS="users/";

    private ProgressDialog progressDialog;

    private static final int CAMERA_INTENT = 0;
    private static final int GALLERY_INTENT = 1;
    public static final int MY_PERMISSIONS_REQUEST_IMAGE_PICKER = 2;
    private Intent cameraIntent;
    private Intent galleryIntent;

    private Uri imageURI;
    private StorageReference mStorage;
            StorageReference filePath;
    private String PathFoto = "Default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        database= FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        mUser = (EditText) findViewById(R.id.mUser);
        mPassword = (EditText) findViewById(R.id.mPassword);
        mNombreUsuario = (EditText) findViewById(R.id.mNombreUsuario);
        mApellidoUsuario = (EditText) findViewById(R.id.mApellidoUsuario);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        mRegistro = (TextView) findViewById(R.id.mRegistro);
        imagenPerfil = (ImageButton) findViewById(R.id.imagenPerfil);

        progressDialog = new ProgressDialog(this);

        mRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySignup.this, ActivityLogin.class));
            }

        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarGaleria();
            }
        });

    }

    private String changeString(String s){
        return s ;
    }

    private void seleccionarGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_IMAGE_PICKER);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, MY_PERMISSIONS_REQUEST_IMAGE_PICKER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_IMAGE_PICKER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                seleccionarGaleria();
            } else {
                Toast.makeText(this, "Si no se da permiso no podemos acceder a la galeria.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSIONS_REQUEST_IMAGE_PICKER) {
            if (resultCode == RESULT_OK) {
                try {
                    imageURI = data.getData();


                    final InputStream imageStream = getContentResolver().openInputStream(imageURI);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imagenPerfil.setImageBitmap(selectedImage);







                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
        String Nombre = mNombreUsuario.getText().toString();
        if (TextUtils.isEmpty(Nombre)) {
            mNombreUsuario.setError("Requerido.");
            valid = false;
        } else {
            mNombreUsuario.setError(null);
        }
        String Apellido = mApellidoUsuario.getText().toString();
        if (TextUtils.isEmpty(Apellido)) {
            mApellidoUsuario.setError("Requerido.");
            valid = false;
        } else {
            mApellidoUsuario.setError(null);
        }
        return valid;
    }


    private void registerUser() {
        if (validateForm()) {
            //getting email and password from edit texts
            final String email = mUser.getText().toString().trim();
            if(isEmailValid(email)){
                String password = mPassword.getText().toString().trim();
                final String nombre = mNombreUsuario.getText().toString().trim();
                final String apellido = mApellidoUsuario.getText().toString().trim();


                //if the email and password are not empty
                //displaying a progress dialog

                progressDialog.setMessage("Registrando, Un momento por favor");
                progressDialog.show();

                //creating a new user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {

                            myRef = FirebaseDatabase.getInstance().getReference();

                            //Toast.makeText(ActivitySignup.this, "pglo"+PathFoto, Toast.LENGTH_SHORT).show();




                            List<String> listaAmigos = new ArrayList<>();
                            List<String> rutas = new ArrayList<>();
                            String key=mAuth.getCurrentUser().getUid();

                            Usuarios user = new Usuarios(nombre,apellido,key,email, listaAmigos,"Default",rutas);


                            filePath = mStorage.child("Fotos").child(mAuth.getCurrentUser().getUid());
                            filePath.putFile(imageURI);
                            myRef=database.getReference("users/"+key);
                            myRef.setValue(user);

                            //myRef.child("users").child(key).setValue(user);

                        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                        currentUserDB.child("Nombre").setValue(nombre);
                        currentUserDB.child("Apellido").setValue(apellido);
                        currentUserDB.child("Correo").setValue(email);
                        currentUserDB.child("Photo").setValue("Default");*/
                            //display some message here
                            //Toast.makeText(Main3Activity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ActivitySignup.this, ActivityMaps.class));
                        } else {
                            //display some message here
                            Toast.makeText(ActivitySignup.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }else {
                Toast.makeText(this,"Dirección de correo Invalidad", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private boolean isEmailValid(String email) {
        boolean isValid;
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

}
