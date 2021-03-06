package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.ArrayList;
import java.util.List;

public class ActivityPerfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef1;

    private TextView user_profile_name;
    private TextView user_profile_short_bio;
    private TextView puntajeTotal;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private ImageButton editardatos;
    private ImageButton fotoPerfil;
    private ImageButton add_friend, friendsButt;
    private List<Rutas> array = new ArrayList<Rutas>();
    private Rutas ruta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        database = FirebaseDatabase.getInstance();
        fotoPerfil = (ImageButton) findViewById(R.id.user_profile_photo);
        puntajeTotal = (TextView) findViewById(R.id.puntajeTotal);
        user_profile_name = (TextView) findViewById(R.id.user_profile_name);
        user_profile_short_bio = (TextView) findViewById(R.id.user_profile_short_bio);
        editardatos = (ImageButton) findViewById(R.id.editardatos);
        friendsButt = (ImageButton) findViewById(R.id.imageButtonFriends);
        mStorage = FirebaseStorage.getInstance().getReference();
        editardatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(getBaseContext(), ActivityEditProfile.class);
                startActivity(inte);
            }
        });


        add_friend = (ImageButton) findViewById(R.id.add_friend);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(getBaseContext(), ActivityAmigos.class);
                startActivity(inte);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }


        friendsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(getBaseContext(), UserFriends.class);
                startActivity(inte);
            }
        });

        loadUsers();


    }

    public void loadUsers() {

        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        myRef = database.getReference("users/");
       // myRef1 = database.getReference("rutas/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuarios myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);

                Log.i("ss", "Encontró usuario: " + myUser.getNombre());
                String name = myUser.getNombre();
                String correo = myUser.getCorreo();
                String apellido = myUser.getApellido();
                double punto = myUser.getPuntuacion();
                puntajeTotal.setText(Integer.toString((int) punto));
                user_profile_name.setText(name + " " + apellido);
                user_profile_short_bio.setText(correo);
                final File localFile;
                List<String> rutas = myUser.getRutas();

                try {
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
                                    fotoPerfil.setImageBitmap(image);
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
        @Override
        public void onCancelled (DatabaseError databaseError){
            Log.w("ss", "error en la consulta", databaseError.toException());
        }
    });

        //obtenerInfo();
        myRef1 = database.getReference("rutas/");
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String informacion;
                ArrayAdapter<Rutas> adapter = new ArrayAdapter<Rutas>(ActivityPerfil.this,
                        android.R.layout.simple_list_item_1, array);
                for (DataSnapshot rutaI : dataSnapshot.getChildren()) {
                     ruta = rutaI.getValue(Rutas.class);
                    if (ruta.getUsuariosRuta().get(0).toString().equalsIgnoreCase(idUsuario) && ruta.isRealizado() == true) {
                        informacion = "Destino: " + ruta.getNombreDestino() + "\n" +
                                "Fecha: " + ruta.getFecha() + "\n" +
                                "Metros recorridos: " + Integer.toString((int)ruta.getKilometros()) + "\n";
                        array.add(ruta);
                    }
                }
                ListView listView = (ListView) findViewById(R.id.listaRutas);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), RutaCompartible.class);
                        Bundle bundle= new Bundle();
                        bundle.putString("hasta", array.get(position).getNombreDestino() );
                        bundle.putString("desde", array.get(position).getLatitudOrigen()+", "+array.get(position).getLongitudOrigen() );
                        bundle.putString("dia", array.get(position).getFecha() );
                        bundle.putString("id", array.get(position).getIdRuta() );
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ss", "error en la consulta", databaseError.toException());
            }
        });
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

                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(ActivityPerfil.this, ActivityNoticias.class));
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(ActivityPerfil.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityPerfil.this, Conversaciones.class));
                break;
            }
            case R.id.eventos: {
                startActivity(new Intent(ActivityPerfil.this, MostrarEventos.class));
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
        Intent intent = new Intent(ActivityPerfil.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
