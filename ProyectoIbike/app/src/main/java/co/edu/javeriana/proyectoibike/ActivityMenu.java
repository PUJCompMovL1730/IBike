package co.edu.javeriana.proyectoibike;

import android.content.Intent;
import android.mtp.MtpObjectInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityMenu extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;
    private Button boton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



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
                startActivity(new Intent(ActivityMenu.this, ActivityPerfil.class));
                break;
            }
            case R.id.noticias: {
                startActivity(new Intent(ActivityMenu.this, ActivityNoticias.class));
                break;
            }
            case R.id.mapa: {
                startActivity(new Intent(ActivityMenu.this, ActivityMaps.class));
                break;
            }
            case R.id.ajustes: {
                startActivity(new Intent(ActivityMenu.this, Conversaciones.class));
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
        Intent intent = new Intent(ActivityMenu.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
