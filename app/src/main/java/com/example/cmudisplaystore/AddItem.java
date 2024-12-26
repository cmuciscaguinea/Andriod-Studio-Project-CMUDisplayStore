package com.example.cmudisplaystore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddItem extends AppCompatActivity {

    Spinner spinner;
    private EditText itemNameEditText, itemCategoryEditText, itemPriceEditText;
    private ImageView itemImageView;
    private Button addItemButton;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize Firebase components
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("item_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        itemNameEditText = findViewById(R.id.edit_text_item_name);

        itemPriceEditText = findViewById(R.id.edit_text_item_price);
        itemImageView = findViewById(R.id.image_view_item);
        addItemButton = findViewById(R.id.button_add_item);

        spinner = findViewById(R.id.spinner_item_category);

        List<String> itemList = new ArrayList<>();
        itemList.add("Clothing");
        itemList.add("Accessories");
        itemList.add("Learning Materials");

        // Create an ArrayAdapter using the list and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
               this,
                android.R.layout.simple_spinner_item,
                itemList
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        itemImageView.setOnClickListener(v -> chooseImage());

        addItemButton.setOnClickListener(v -> uploadItem());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            itemImageView.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String itemName = itemNameEditText.getText().toString().trim();
                            String itemCategory = spinner.getSelectedItem().toString();
                            String itemPrice = itemPriceEditText.getText().toString().trim();
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            Item item = new Item(itemName, itemCategory, itemPrice, userId, uri.toString());

                            String itemId = databaseReference.push().getKey();
                            databaseReference.child(itemId).setValue(item);

                            Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}