package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.List;

public class ActivityDetailFriend extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;

    private TextView user_profile_name;
    private TextView user_profile_short_bio;
    private ImageButton fotoPerfil;
    private ImageButton addFriend;

    private StorageReference mStorage;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_friend);
        Intent inte = getIntent();
        final String aux = inte.getStringExtra("llave");

        Log.i("ss", "llave que se paso: " + aux);



        database= FirebaseDatabase.getInstance();
        fotoPerfil = (ImageButton) findViewById(R.id.user_profile_photo);
        user_profile_name = (TextView) findViewById(R.id.user_profile_name);
        user_profile_short_bio = (TextView) findViewById(R.id.user_profile_short_bio);
        addFriend = (ImageButton) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addAmigo(aux);
            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();

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



        loadUsers(aux);

    }

    public void addAmigo(final String llave){
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();


        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);

                String nombre = myUser.getNombre();
                String apellido = myUser.getApellido();
                final String key = myUser.getId();
                String correo = myUser.getCorreo();
                String equipo = myUser.getEquipo();
                List<String> listaAmigos = myUser.getListaAmigos();
                List<String> rutas = myUser.getRutas();

                listaAmigos.add(llave);
                final Usuarios user = new Usuarios(nombre, apellido, key, correo, listaAmigos, equipo, rutas);

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
                                agregarAmigo(llave);
                                //startActivity(new Intent(ActivityDetailFriend.this, ActivityPerfil.class));
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void agregarAmigo(final String llave){
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();


        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios myUser = dataSnapshot.child(llave).getValue(Usuarios.class);

                String nombre = myUser.getNombre();
                String apellido = myUser.getApellido();
                final String key = myUser.getId();
                String correo = myUser.getCorreo();
                String equipo = myUser.getEquipo();
                List<String> listaAmigos = myUser.getListaAmigos();
                List<String> rutas = myUser.getRutas();

                listaAmigos.add(currentFirebaseUser.getUid());
                final Usuarios user = new Usuarios(nombre, apellido, key, correo, listaAmigos, equipo, rutas);

                final File localFile;
                try {
                    localFile = File.createTempFile("images", "png");
                    StorageReference imagesStorage = mStorage.child("Fotos").child(llave);
                    imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Uri uri = Uri.fromFile(localFile);
                                filePath = mStorage.child("Fotos").child(llave);
                                filePath.putFile(uri);
                                myRef = database.getReference("users/" + key);
                                myRef.setValue(user);

                                startActivity(new Intent(ActivityDetailFriend.this, ActivityPerfil.class));
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadUsers(final String aux) {

        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;



        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuarios myUser = dataSnapshot.child(aux).getValue(Usuarios.class);

                Log.i("ss", "Encontró usuario: " + myUser.getNombre());
                String name = myUser.getNombre();
                String correo = myUser.getCorreo();
                String apellido = myUser.getApellido();
                user_profile_name.setText(name+" "+apellido);
                user_profile_short_bio.setText(correo);
                final File localFile;

                try {
                    localFile = File.createTempFile("images", "png");
                    StorageReference imagesStorage = mStorage.child("Fotos").child(aux);
                    imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Uri uri = Uri.fromFile(localFile);
                                Bitmap image = null;
                                try {
                                    image = (Bitmap) BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    fotoPerfil.setImageBitmap(image);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ss", "error en la consulta", databaseError.toException());
            }
        });
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
                startActivity(new Intent(ActivityDetailFriend.this, ActivityPerfil.class));
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
                startActivity(new Intent(ActivityDetailFriend.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                //logout();
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
        Intent intent = new Intent(ActivityDetailFriend.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
