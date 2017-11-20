package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static co.edu.javeriana.proyectoibike.R.id.user_profile_name;

public class ActivityEditProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText mNombreUsuario;
    private EditText mApellidoUsuario;

    private Button btnGuardarCambios;

    public static final int MY_PERMISSIONS_REQUEST_IMAGE_PICKER = 2;


    private ImageButton imagenPerfil;

    private Uri imageURI;
    private StorageReference mStorage;
    StorageReference filePath;
    private boolean cambio = false;
    private boolean cambioNombre = false;
    private boolean cambioApellido = false;

    DatabaseReference myRef;
    FirebaseDatabase database;
     String nombre;
     String apellido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        mNombreUsuario = (EditText) findViewById(R.id.mNombreUsuario);
        mApellidoUsuario = (EditText) findViewById(R.id.mApellidoUsuario);
        btnGuardarCambios = (Button) findViewById(R.id.btnGuardarCambios);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        imagenPerfil = (ImageButton) findViewById(R.id.fotoPerfil);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                actualizar();


            }


        });

        imagenPerfil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                cambiarFoto();


            }

        });

        cargarImg();
        //mNombreUsuario =

            myRef = database.getReference("users/");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuarios myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);

                mNombreUsuario.setHint(myUser.getNombre());
                mApellidoUsuario.setHint(myUser.getApellido());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ss", "error en la consulta", databaseError.toException());
            }
        });


    }

    private void cambiarFoto() {

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
                cambiarFoto();
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
                    cambio = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarImg() {

        final File localFile;
        try {
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            localFile = File.createTempFile("images", "png");
            StorageReference imagesStorage = mStorage.child("Fotos").child(currentFirebaseUser.getUid());
            imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Uri uri = Uri.fromFile(localFile);
                        Bitmap image = null;
                        try {
                            image = (Bitmap) BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                            imagenPerfil.setImageBitmap(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void validateForm() {
        boolean valid = true;
        String Nombre = mNombreUsuario.getText().toString();
        if (!TextUtils.isEmpty(Nombre)) {
            cambioNombre = true;

        }
        String Apellido = mApellidoUsuario.getText().toString();
        if (!TextUtils.isEmpty(Apellido)) {
            cambioApellido = true;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.perfil: {
                startActivity(new Intent(ActivityEditProfile.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(ActivityEditProfile.this, ActivityNoticias.class));
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(ActivityEditProfile.this, ActivityMaps.class));;
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityEditProfile.this, Conversaciones.class));
                break;
            }
            case R.id.eventos: {
                startActivity(new Intent(ActivityEditProfile.this, MostrarEventos.class));
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


    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(ActivityEditProfile.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void actualizar() {


        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();


        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuarios myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);
                validateForm();
                if (cambioNombre){
                    nombre = mNombreUsuario.getText().toString().trim();
                } else {
                    nombre = myUser.getNombre();

                }

                if(cambioApellido){
                    apellido = mApellidoUsuario.getText().toString().trim();
                } else {
                    apellido = myUser.getApellido();
                }


                final String key = myUser.getId();
                String correo = myUser.getCorreo();
                String equipo = myUser.getEquipo();
                String nombre = myUser.getNombre();
                mNombreUsuario.setText(nombre);
                String apellido = myUser.getApellido();
                mApellidoUsuario.setText(apellido);
                List<String> listaAmigos = myUser.getListaAmigos();
                List<String> rutas = myUser.getRutas();



                final Usuarios user = new Usuarios(nombre, apellido, key, correo, listaAmigos, equipo, rutas);


                if (cambio) {

                    filePath = mStorage.child("Fotos").child(mAuth.getCurrentUser().getUid());
                    filePath.putFile(imageURI);

                    myRef = database.getReference("users/" + key);
                    myRef.setValue(user);

                    startActivity(new Intent(ActivityEditProfile.this, ActivityPerfil.class));

                } else {

                    final File localFile;
                    try {
                        localFile = File.createTempFile("images", "png");
                        StorageReference imagesStorage = mStorage.child("Fotos").child(currentFirebaseUser.getUid());
                        imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Uri uri = Uri.fromFile(localFile);
                                    filePath = mStorage.child("Fotos").child(mAuth.getCurrentUser().getUid());
                                    filePath.putFile(uri);
                                    myRef = database.getReference("users/" + key);
                                    myRef.setValue(user);

                                    startActivity(new Intent(ActivityEditProfile.this, ActivityPerfil.class));
                                } else {
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ss", "error en la consulta", databaseError.toException());
            }
        });


    }


}
