package co.edu.javeriana.proyectoibike;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class FBAuth {

    private Activity activity;
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authenticationListener;
    private StorageReference storage;
    private FirebaseUser user;

    public FBAuth(final Activity activity) {
        this.activity = activity;
        authentication = FirebaseAuth.getInstance();
        authentication.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance().getReference();
        authenticationListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(isAnUserSignedIn()) {
                    onSuccess();
                }
            }
        };
    }

    public void start() {
        authentication.addAuthStateListener(authenticationListener);
        if(isAnUserSignedIn()) {
            onSuccess();
        }
    }

    public void stop() {
        if (authenticationListener != null) {
            authentication.removeAuthStateListener(authenticationListener);
        }
    }

    public void signInWithEmailAndPassword(String email, String password) {
        authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user = authentication.getCurrentUser();
                if (!task.isSuccessful()) {
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    onSuccess();
                }
            }
        });
    }

    public void createUserWithEmailAndPassword(String email, String password, final String displayName) {
        authentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user = authentication.getCurrentUser();
                if (!task.isSuccessful()) {
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                    upcrb.setDisplayName(displayName);
                    upcrb.setDisplayName(displayName);
                    user.updateProfile(upcrb.build()).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FBAuth.this.onSuccess();
                        }
                    });
                }
            }
        });
    }

    public FirebaseUser getUser() {
        return user;
    }

    public boolean isAnUserSignedIn() {
        return user != null;
    }

    public void signOut() {
        authentication.signOut();
    }

    public abstract void onSuccess();
}
