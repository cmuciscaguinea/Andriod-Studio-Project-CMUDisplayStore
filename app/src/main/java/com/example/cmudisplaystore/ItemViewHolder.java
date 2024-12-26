package com.example.cmudisplaystore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageViewItem;
    public TextView textViewItemName;
    public TextView textViewItemPrice;
    public TextView textViewItemOwner;
    public TextView textViewContactNumber;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewItem = itemView.findViewById(R.id.image_view_item);
        textViewItemName = itemView.findViewById(R.id.text_view_item_name);
        textViewItemPrice = itemView.findViewById(R.id.text_view_item_price);
        textViewItemOwner = itemView.findViewById(R.id.text_view_item_owner);
        textViewContactNumber = itemView.findViewById(R.id.text_view_contact_number);
    }
}
