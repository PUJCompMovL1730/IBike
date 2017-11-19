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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private EditText mUser;
    private EditText mPassword;
    private EditText mNombreUsuario;
    private EditText mApellidoUsuario;
    private Button btnRegistro;
    private EditText empresa;
    private RadioGroup rGroup;
    private RadioButton checkedRadioButton;
    private TextView mRegistro;
    private ImageButton imagenPerfil;

    public static final String PATH_USERS="users/";

    private ProgressDialog progressDialog;

    private static final int CAMERA_INTENT = 0;
    private static final int GALLERY_INTENT = 1;
    public static final int MY_PERMISSIONS_REQUEST_IMAGE_PICKER = 2;
    private Intent cameraIntent;
    private Intent galleryIntent;
    private Usuarios usuario;
    private Empresario usuarioEmpresario;
    private String nombre, apellido, correoE, contra, nombreEmpresa;
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
        usuarioEmpresario = new Empresario();

        mUser = (EditText) findViewById(R.id.mUser);
        mPassword = (EditText) findViewById(R.id.mPassword);
        mNombreUsuario = (EditText) findViewById(R.id.mNombreUsuario);
        mApellidoUsuario = (EditText) findViewById(R.id.mApellidoUsuario);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        mRegistro = (TextView) findViewById(R.id.mRegistro);
        imagenPerfil = (ImageButton) findViewById(R.id.imagenPerfil);
        rGroup = (RadioGroup)findViewById(R.id.radioGroup);
        checkedRadioButton = (RadioButton)rGroup.findViewById(R.id.nosi);
        empresa = (EditText) findViewById(R.id.empresa);


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


        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                boolean isChecked = checkedRadioButton.isChecked();
                if (!isChecked){
                    empresa.setVisibility(View.VISIBLE);
                }else{
                    empresa.setVisibility(View.INVISIBLE);
                }
            }
        });

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

    private boolean validateForm(String nombre, String apellido, String correoE, String contra,  String nombreEmpresa) {
        boolean valid = true;
        if (TextUtils.isEmpty(nombre)) {
            mNombreUsuario.setError("Required.");
            valid = false;
        } else {
            mNombreUsuario.setError(null);
        }
        if (TextUtils.isEmpty(apellido)) {
            mApellidoUsuario.setError("Required.");
            valid = false;
        } else {
            mApellidoUsuario.setError(null);
        }
        if (TextUtils.isEmpty(correoE)) {
            mUser.setError("Required.");
            valid = false;
        } else {
            mUser.setError(null);
        }
        if (TextUtils.isEmpty(contra)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        if(TextUtils.isEmpty(nombreEmpresa) && !checkedRadioButton.isChecked()){
            empresa.setError("Required.");
            valid = false;
        }else {
            empresa.setError(null);
        }
        return valid;

    }


    private void registerUser() {
        nombre = mNombreUsuario.getText().toString();
        apellido = mApellidoUsuario.getText().toString();
        correoE = mUser.getText().toString();
        contra = mPassword.getText().toString();
        nombreEmpresa = empresa.getText().toString();
        if (validateForm(nombre, apellido, correoE, contra, nombreEmpresa)) {
            if(isEmailValid(correoE)){
                progressDialog.setMessage("Registrando, Un momento por favor");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(correoE, contra).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            myRef = FirebaseDatabase.getInstance().getReference();
                            if(checkedRadioButton.isChecked()){
                                List<String> listaAmigos = new ArrayList<>();
                                List<String> rutas = new ArrayList<>();
                                String key=mAuth.getCurrentUser().getUid();
                                usuario = new Usuarios();
                                usuario.setNombre(nombre);
                                usuario.setApellido(apellido);
                                usuario.setCorreo(correoE);
                                usuario.setListaAmigos(listaAmigos);
                                usuario.setRutas(rutas);
                                usuario.setId(key);
                                usuario.setEmpresario(false);
                                usuario.setEquipo("rojo");
                                usuario.setMultiplicador(1);
                                usuario.setPuntuacion(0);
                                filePath = mStorage.child("Fotos").child(mAuth.getCurrentUser().getUid());
                                filePath.putFile(imageURI);
                                myRef=database.getReference("users/"+key);
                                myRef.setValue(usuario);
                                startActivity(new Intent(ActivitySignup.this, ActivityMaps.class));
                            }else {
                                List<String> idEventos = new ArrayList<String>();
                                List<String> idMarcadores = new ArrayList<String>();
                                String key = mAuth.getCurrentUser().getUid();
                                usuarioEmpresario.setId(key);
                                usuarioEmpresario.setNombre(nombre);
                                usuarioEmpresario.setApellido(apellido);
                                usuarioEmpresario.setEmpresario(true);
                                usuarioEmpresario.setIdEventos(idEventos);
                                usuarioEmpresario.setIdMarcadores(idMarcadores);
                                myRef=database.getReference("usersE/"+key);
                                myRef.setValue(usuarioEmpresario);
                                Intent intent = new Intent(ActivitySignup.this, ActivityMapsE.class);
                                startActivity(intent);
                            }


                        } else {
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
