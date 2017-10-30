package co.edu.javeriana.proyectoibike;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserFriends extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference myRef;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;

    Usuarios myUser = new Usuarios();
    List<String> auxNames = new ArrayList<String>();
    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        lista = (ListView) findViewById(R.id.amigosUserIn);
        database= FirebaseDatabase.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        myRef = database.getReference("users/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myUser = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);
                List<String> aux = myUser.getListaAmigos();

                for (DataSnapshot player : dataSnapshot.getChildren()) {
                   for(String s: aux) {
                       Usuarios userAux = player.getValue(Usuarios.class);
                       if(s.equalsIgnoreCase(userAux.getId())){
                           auxNames.add(userAux.getNombre()+" "+userAux.getApellido());
                       }
                   }
                }


                ArrayAdapter arrayAdapter = new ArrayAdapter(UserFriends.this, android.R.layout.simple_list_item_1,auxNames);
                lista.setAdapter(arrayAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                startActivity(new Intent(UserFriends.this, ActivityPerfil.class));
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
                startActivity(new Intent(UserFriends.this, ActivityMaps.class));
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
        Intent intent = new Intent(UserFriends.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
