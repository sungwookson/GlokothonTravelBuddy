package com.glocoders.hangout.database;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by devFallingstar on 2017. 7. 22..
 */

public class FirebaseHelper {
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "FB TAG";
    int code;

    public void initUserAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("FB TAG", "signed_in: " + user.getUid());
                } else {
                    Log.d("FB TAG", "signed_out");
                }
            }
        };
    }

    public void createAccount(String email, String password) {
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "A user created failed!");
                            code = -1;
                        } else {
                            code = 1;
                            FirebaseUser user = task.getResult().getUser();
                            databaseRef.child("users").child(user.getUid()).setValue(getUserInformation(user));
                        }
                    }
                });
    }

    public HashMap<String, String> getUserInformation(FirebaseUser user) {
        HashMap<String, String> userDic = new HashMap<>();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            userDic.put("isSet", "YES");
            userDic.put("name", name);
            userDic.put("email", email);
//            userDic.put("photoUrl", photoUrl.toString());
            userDic.put("uid", uid);
        } else {
            userDic.put("isSet", "NO");
        }
        return userDic;
    }

    public void setAuthListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public FirebaseAuth getCurrentAuth() {
        return mAuth;
    }
}
