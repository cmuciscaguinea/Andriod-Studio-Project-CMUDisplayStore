package com.example.cmudisplaystore;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private FirebaseDatabase mdatabase;
    private FirebaseAuth mauth;
    private DatabaseReference userref;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }
    public void filterList(List<Item> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        mauth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance();
        userref = mdatabase.getReference("userinfo");
        Item item = itemList.get(position);
        String userid = item.getUserId();

        userref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String contact = snapshot.child("contactNumber").getValue(String.class);
                    if (username != null) {
                        // Use the retrieved username
                        holder.textViewItemOwner.setText(username);
                        holder.textViewContactNumber.setText(contact);
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
        // Set item details to views
        holder.textViewItemName.setText(item.getItemName());
        holder.textViewItemPrice.setText(item.getItemPrice());




        // Load image using your preferred image loading library (Glide, Picasso, etc.)
        // For example, with Glide:
        Picasso.get()
                .load(item.getImageUrl())
                .placeholder(R.drawable.cmulogo)
                .into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
