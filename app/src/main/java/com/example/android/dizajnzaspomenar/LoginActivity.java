package com.example.android.dizajnzaspomenar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    @InjectView(R.id.link_gost) TextView _gostLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popuniBazu();

        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

       /* getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        ); */

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _gostLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivityForResult(intent, REQUEST_SIGNUP);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c = db.getAllContacts();
        int x = 0;
        if (c.moveToFirst())
        {
            do {
                if(email.equals(c.getString(c.getColumnIndex("email"))) && password.equals(c.getString(c.getColumnIndex("password"))))
                {
                    x = 1;
                    onLoginSuccess(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")));
                }

            } while (c.moveToNext());
        }
        if( x == 0 )
            onLoginFailed();

        db.close();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(int id, String name) {
        _loginButton.setEnabled(true);
        Globals g = Globals.getInstance();
        g.setId(id);
        g.setUsername(name);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Neuspješna prijava!", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Unesite ispravnu email adresu!");
            valid = false;}
        else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Lozinka mora imati 4 - 10 brojki/slova!");
            valid = false;}
        else {
            _passwordText.setError(null);
        }

        return valid;
    }
    public void popuniPitanja() //možda da bude bool da znamo jel uspjesno ili ne?
    {
        DBAdapter db = new DBAdapter(this);
        //---add a question---
        db.open();
        long id;
        id = db.insertQuestion("Kako se zoveš?");
        id = db.insertQuestion("Gdje živiš?");
        id = db.insertQuestion("Koliko imaš godina?");
        id = db.insertQuestion("Boja očiju?");
        id = db.insertQuestion("Najbolji prijatelj?");
        id = db.insertQuestion("Imaš li simpatiju?");
        id = db.insertQuestion("Najdraže jelo?");
        db.close();
    }

    public void popuniKorisnike()
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        long id;
        id = db.insertContact("Tena", "tena@spomenar.hr", "admin", 1);
        id = db.insertContact("Martina", "martina@spomenar.hr", "pass", 0);
        id = db.insertContact("Iva", "iva@spomenar.hr", "pass", 0);

        db.close();
    }


    public void popuniBazu()
    {
        DBAdapter db = new DBAdapter(this);
        db.open();
        if( db.getAllContacts().getCount() == 0 ) {
            popuniPitanja();
            popuniKorisnike();
        }
        db.close();
    }

}

