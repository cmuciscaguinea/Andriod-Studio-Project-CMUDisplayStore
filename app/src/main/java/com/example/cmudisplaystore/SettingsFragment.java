package com.example.cmudisplaystore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private ImageView imageViewProfile;

    private EditText editTextContactNumber;
    private TextView textViewUserEmail;
    private EditText editTextFullName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference userref;

    private Button buttonUpdateProfile;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        editTextFullName = view.findViewById(R.id.editTextFullName);
        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);
        editTextContactNumber = view.findViewById(R.id.editTextContactNumber);
        buttonUpdateProfile.setOnClickListener(this);

        userref = mDatabase.getReference("userinfo");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();

        userref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);
                    if (username != null && contactNumber != null) {
                        // Use the retrieved username
                        editTextFullName.setText(username);
                        editTextContactNumber.setText(contactNumber);
                    } else {
                        // Username field doesn't exist or is null
                    }
                } else {
                    // User ID doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String username = editTextFullName.getText().toString();

            // Update the user profile
            currentUser.updateProfile(buildProfileChangeRequest(username));

            // Retrieve the contact number from the Realtime Database
            retrieveContactNumber(uid);
        }
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Load the username if it exists
            userref.child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.getValue(String.class);
                        editTextFullName.setText(username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });

            // Load the contact number if it exists
            retrieveContactNumber(uid);
        }
    }

    private void retrieveContactNumber(String uid) {
        userref.child(uid).child("contactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String contactNumber = snapshot.getValue(String.class);
                    editTextContactNumber.setText(contactNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    @Override
    public void onClick(View v) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String username = editTextFullName.getText().toString();
            String contactNumber = editTextContactNumber.getText().toString();


            userref.child(uid).child("username").setValue(username);
            // Update the user profile
            currentUser.updateProfile(buildProfileChangeRequest(username));
            userref.child(uid).child("contactNumber").setValue(contactNumber);
        }
    }

    private UserProfileChangeRequest buildProfileChangeRequest(String username) {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                // Other profile updates can be added here if needed
                .build();
    }
}