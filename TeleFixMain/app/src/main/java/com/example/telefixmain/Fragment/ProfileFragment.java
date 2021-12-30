package com.example.telefixmain.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.telefixmain.Dialog.CustomProgressDialog;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    // xml
    LinearLayout profileContent;
    EditText profileName, profileEmail, profilePhone;
    Button updateProfileBtn;

    // progress dialog
    CustomProgressDialog cpd;

    // database objects
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Global Arraylist to store result
    ArrayList<User> userResult = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment activity
        Activity fragmentActivity = getActivity();

        // init progress dialog
        cpd = new CustomProgressDialog(Objects.requireNonNull(fragmentActivity));

        // root
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,
                container, false);

        // fade in content
        profileContent = root.findViewById(R.id.ll_profile);
        profileContent.setVisibility(View.VISIBLE);
        profileContent.startAnimation(AnimationUtils.loadAnimation(fragmentActivity,
                R.anim.fade_in));


        // if there is a logged in user
        // binding with xml
        profileEmail = root.findViewById(R.id.et_profile_email);
        profileName = root.findViewById(R.id.et_profile_name);
        profilePhone = root.findViewById(R.id.et_profile_phone);
        // auto fill edit text
        if (mUser != null) {
            DatabaseHandler.getSingleUser(
                    db,
                    fragmentActivity,
                    mUser.getUid(),
                    userResult, () -> {
                        // render on ui
                        if (userResult.size() > 0) {
                            // log to keep track
                            System.out.println(userResult.toString());

                            // render user name on UI
                            profileEmail.setText(userResult.get(0).getEmail());
                            profileName.setText(userResult.get(0).getName());
                            profilePhone.setText(userResult.get(0).getPhone());
                        }
                    }
            );
        }

        // update button related
        updateProfileBtn = root.findViewById(R.id.btn_update_profile);

        // detect on text change
        profileEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_2));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!profileEmail.getText().toString().equals(userResult.get(0).getEmail()))
                    updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_8));
            }
        });

        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_2));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!profileName.getText().toString().equals(userResult.get(0).getName()))
                    updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_8));
            }
        });

        profilePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_2));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!profilePhone.getText().toString().equals(userResult.get(0).getPhone()))
                    updateProfileBtn.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg_8));
            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}