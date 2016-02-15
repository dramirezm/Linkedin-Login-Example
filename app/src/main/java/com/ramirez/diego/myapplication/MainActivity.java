package com.ramirez.diego.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    Button loginLnk;
    Button hashKey;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PACKAGE = "com.ramirez.diego.myapplication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginLnk = (Button)findViewById(R.id.btnLogin);
        hashKey = (Button)findViewById(R.id.hashkey);


        loginLnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_linkedin();
            }
        });

        hashKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateHashkey();
                printKeyHash(MainActivity.this);
            }
        });
    }

// Authenticate with linkedin and intialize Session.

    public void login_linkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this,
                buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        Log.e("success",LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                        Log.e( "failed ", error.toString());
                    }
                }, true);
    }

// This method is used to make permissions to retrieve data from linkedin

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }

    public static String printKeyHash(Activity context) {
             PackageInfo packageInfo;
               String key = null;
             try {
                     String packageName = context.getApplicationContext().getPackageName();

                               packageInfo = context.getPackageManager().getPackageInfo(packageName,
                                   PackageManager.GET_SIGNATURES);
                   Log.e("Package Name=", context.getApplicationContext().getPackageName());
                  for (Signature signature : packageInfo.signatures) {
                      MessageDigest md = MessageDigest.getInstance("SHA");
                      md.update(signature.toByteArray());
                       key = new String(Base64.encode(md.digest(), 0));
                 Log.e("Key Hash=", key);
                       }
                  } catch (PackageManager.NameNotFoundException e1) {
                      Log.e("Name not found", e1.toString());
                  }
             catch (NoSuchAlgorithmException e) {
                     Log.e("No such an algorithm", e.toString());
                } catch (Exception e) {
                      Log.e("Exception", e.toString());
                }

                return key;
         }

    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                ((TextView) findViewById(R.id.package_name)).setText(info.packageName);
                ((TextView) findViewById(R.id.hash_key)).setText(Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
                Log.e("hashKey",Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(MainActivity.this, requestCode, resultCode, data);
    }
}
