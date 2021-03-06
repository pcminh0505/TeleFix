package com.example.telefixmain.Activity.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Activity.Customer.MainActivity;

import com.example.telefixmain.Model.User;
import com.example.telefixmain.Model.Vehicle;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("ClickableViewAccessibility")
public class LoginActivity extends AppCompatActivity {
    // xml element containers
    LinearLayout llLogin;
    TextView jumpToSignup;
    Button btnLogIn;
    EditText inputEmail;

    // for password
    EditText inputPwd;
    boolean pwdIsVisible = false;

    // progress dialog
    CustomProgressDialog cpd;

    // [START declare_auth]
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END declare_auth]

    // declare vehicles data containers
    ArrayList<User> userResult;
    ArrayList<String> vehiclesIdResult;
    ArrayList<Vehicle> vehiclesResult;
    ArrayList<HashMap<String, String>> vehiclesHashMapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // init progress dialog
        cpd = new CustomProgressDialog(this, R.style.SheetDialog);

        // login contents fade in
        llLogin = findViewById(R.id.ll_login);
        llLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // toggle password
        inputPwd = findViewById(R.id.pwd_login);
        inputPwd.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= inputPwd.getRight() -
                        inputPwd.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = inputPwd.getSelectionEnd();

                    if (pwdIsVisible) {
                        // change visibility icon
                        inputPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility_off,
                                0);

                        // change displayed content
                        inputPwd.setTransformationMethod(
                                PasswordTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = false;
                    } else {
                        // change visibility icon
                        inputPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_login_pwd,
                                0,
                                R.drawable.ic_pwd_visibility,
                                0);

                        // change displayed content
                        inputPwd.setTransformationMethod(
                                HideReturnsTransformationMethod.getInstance()
                        );

                        // change state tracking variable
                        pwdIsVisible = true;
                    }

                    inputPwd.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // signing in
        btnLogIn = findViewById(R.id.btn_login);
        inputEmail = findViewById(R.id.email_login);
        btnLogIn.setOnClickListener(view -> {
            try {
                // call verifying log on method
                signIn(inputEmail.getText().toString(), inputPwd.getText().toString(),
                        () -> {
                            // get new instance of mUser
                            mUser = mAuth.getCurrentUser();
                            userResult = new ArrayList<>();

                            // if there is a logged in user
                            if (mUser != null) {
                                DatabaseHandler.getSingleUser(
                                        db,
                                        mUser.getUid(),
                                        userResult, () -> {
                                            // intent to jump to main activity
                                            Intent toMainActivity = new Intent(this, MainActivity.class);
                                            toMainActivity.putExtra("loggedInUser", userResult.get(0));
                                            if (!Boolean.parseBoolean(userResult.get(0).getIsMechanic())) {

                                                // init vehicles data containers
                                                vehiclesIdResult = new ArrayList<>();
                                                vehiclesResult = new ArrayList<>();
                                                vehiclesHashMapList = new ArrayList<>();

                                                // get user's vehicle list
                                                DatabaseHandler.getUserVehicleList(db, this, userResult.get(0).getId(),
                                                        vehiclesIdResult, vehiclesResult, () -> {
                                                            // do only if there is any vehicle id, otherwise cut short the process
                                                            if (vehiclesResult.size() > 0) {
                                                                // populate here
                                                                for (Vehicle currentVehicle : vehiclesResult) {
                                                                    // single vehicle hash map
                                                                    HashMap<String, String> tempContainer = new HashMap<>();

                                                                    // inject vehicle data
                                                                    tempContainer.put("vehicleTitle",
                                                                            currentVehicle.getVehicleBrand() + " "
                                                                                    + currentVehicle.getVehicleModel() + " "
                                                                                    + currentVehicle.getVehicleYear());
                                                                    tempContainer.put("vehicleColor",
                                                                            currentVehicle.getVehicleColor());
                                                                    tempContainer.put("vehicleNumberPlate",
                                                                            currentVehicle.getVehicleNumberPlate());

                                                                    // add to vehicle hash map list
                                                                    vehiclesHashMapList.add(tempContainer);
                                                                }
                                                            }

                                                            // attach vehicle list before going to Main Activity
                                                            toMainActivity.putExtra("vehiclesHashMapList", vehiclesHashMapList);
                                                            toMainActivity.putExtra("vehiclesIdList", vehiclesIdResult);

                                                            // jump into main activity
                                                            // show msg and hide progress dialog
                                                            cpd.dismiss();
                                                            Toast.makeText(this,
                                                                    "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                                            startActivity(toMainActivity);
                                                            finish();
                                                        });
                                            } else {
                                                // show msg and hide progress dialog
                                                cpd.dismiss();
                                                Toast.makeText(this,
                                                        "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                                startActivity(toMainActivity);
                                                finish();
                                            }
                                        }
                                );
                            }
                        });
            } catch (IllegalArgumentException | NullPointerException e) {
                // hide progress dialog
                cpd.dismiss();

                // print error
                System.out.println(e.getMessage());

                // show msg on screen
                Toast.makeText(this,
                        "Email or Password is empty", Toast.LENGTH_SHORT).show();
            }
        });

        // jump to Sign Up Activity
        jumpToSignup = findViewById(R.id.jump_to_signup);
        jumpToSignup.setOnClickListener(view -> {
            jumpToSignup.setTextColor(getResources().getColor(R.color.orange));
            new Handler().postDelayed(() -> {
                jumpToSignup.setTextColor(getResources().getColor(R.color.bmw_white));
                startActivity(new Intent(this, SignUpActivity.class));
                finish();
            }, 500);
        });
    }

    /**
     * Method to get user verified from Firebase Authentication and log in
     *
     * @param email    : string from email text input
     * @param password : string from password text input
     */
    private void signIn(String email, String password, Runnable callback)
            throws IllegalArgumentException, NullPointerException {
        // [START sign_in_with_email]

        // show progress dialog
        cpd.changeText("Logging in ...");
        cpd.show();

        // verifying log in info
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    System.out.println("LOGIN VERIFIED COMPLETED!");
                    if (task.isSuccessful()) {
                        // log to keep track
                        System.out.println("LOGIN VERIFIED SUCCESSFULLY!");

                        // run callback when done
                        callback.run();
                    } else {
                        // show msg and hide progress dialog
                        new Handler().postDelayed(() -> {
                            cpd.dismiss();
                            Toast.makeText(this,
                                    "Email or Password is incorrect!", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }
                });
        // [END sign_in_with_email]
    }
}