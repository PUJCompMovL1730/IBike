package co.edu.javeriana.proyectoibike;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class ActivityAmigos extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ListView lista;
    private String friendKey;
    Usuarios myUser = new Usuarios();
    private List<String> friends = new ArrayList<String>();
    private Vector<String> llavesFriends = new Vector<String>();
    private Vector<String> nombresFriend = new Vector<String>();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        lista = (ListView) findViewById(R.id.myFriends);


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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot player : dataSnapshot.getChildren()) {
                    myUser = player.getValue(Usuarios.class);
                    friendKey = myUser.getId();
                    final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    Usuarios userAct = dataSnapshot.child(currentFirebaseUser.getUid()).getValue(Usuarios.class);

                    if(friendKey.equals(userAct.getId())){

                    } else {
                        if(userAlreadyAdd(userAct.getListaAmigos(),friendKey)) {
                            llavesFriends.add(myUser.getId());
                            nombresFriend.add(myUser.getNombre()+" "+myUser.getApellido());

                        }

                    }

                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityAmigos.this, android.R.layout.simple_list_item_1,nombresFriend);
                lista.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ActivityDetailFriend.class);
                String aux = llavesFriends.get(position);
                intent.putExtra("llave",aux);
                Log.i("llave a mandar",aux);
                 startActivity(intent);
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
                startActivity(new Intent(ActivityAmigos.this, ActivityPerfil.class));
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
                startActivity(new Intent(ActivityAmigos.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityAmigos.this, Conversaciones.class));
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
        Intent intent = new Intent(ActivityAmigos.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private boolean userAlreadyAdd(List<String> friends,String idUser){

      Log.i("IDUserActual: ",idUser);
        for(String s:friends){
            Log.i("IDUserListaAmigos",s);
            if(s.equalsIgnoreCase(idUser)){
                return false;
            }
        }
        return  true;
    }
}
