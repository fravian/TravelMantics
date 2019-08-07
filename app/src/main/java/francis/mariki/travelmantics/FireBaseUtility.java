package francis.mariki.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.opencensus.resource.Resource;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FireBaseUtility {
    public static final int RC_SIGN_IN = 123;
    public  static ArrayList<TravelDeals> mDeals;
    public  static FirebaseDatabase mFireBaseDatabase;
    public  static DatabaseReference mDatabaseReference;
    public  static FireBaseUtility fireBaseUtility;
    public  static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    public static FirebaseAuth mFireBaseAuth;
    public  static  FirebaseAuth.AuthStateListener mAuthListener;
    public static boolean isAdmin;
    private  static ListActivity caller;

    public FireBaseUtility(){}

    public static FireBaseUtility getInstance(String ref, final ListActivity callerActivity){
        if (fireBaseUtility==null){
            fireBaseUtility=new FireBaseUtility();
            mFireBaseDatabase=FirebaseDatabase.getInstance();
            mFireBaseAuth=FirebaseAuth.getInstance();
            caller=callerActivity;
            mAuthListener=new FirebaseAuth.AuthStateListener(){

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser()==null) {
                        signIn();

                    }else{
                        checkAdmin(firebaseAuth.getUid());
                    }
                }
            };

            connectStorage();
        }
        mDeals=new ArrayList<TravelDeals>();
        mDatabaseReference=mFireBaseDatabase.getReference().child(ref);
        return  fireBaseUtility;
    }


    private static void checkAdmin(String uid) {
        FireBaseUtility.isAdmin=false;
        DatabaseReference databaseReference=mFireBaseDatabase.getReference().child("administrators").child(uid);
        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FireBaseUtility.isAdmin=true;
                caller.showMenu();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    public static void attachAuthListerner(){
        mFireBaseAuth.addAuthStateListener(mAuthListener);
    }
    public  static  void detachAuthListerner(){
        mFireBaseAuth.removeAuthStateListener(mAuthListener);
    }
    public static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        Toast.makeText(caller, R.string.Signin_feedback,Toast.LENGTH_LONG).show();

    }

    public static  void signOut(){
        AuthUI.getInstance()
                .signOut(caller)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public static  void connectStorage(){
        mFirebaseStorage=FirebaseStorage.getInstance();
        mStorageReference=mFirebaseStorage.getReference().child("deals_pictures");
    }
}
