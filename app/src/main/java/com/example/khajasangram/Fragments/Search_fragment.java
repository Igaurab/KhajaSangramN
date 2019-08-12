package com.example.khajasangram.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.khajasangram.Classes.Restaurant_SQLite_comparision;
import com.example.khajasangram.R;
import com.example.khajasangram.RestaurantDetails;
import com.example.khajasangram.SQLite.Databasehelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Search_fragment extends Fragment {
    Databasehelper db;
    LinearLayout first_layout,compare_criteria_layout, compare_rating,compare_price;
    LinearLayout container1;
    ArrayList<String> id;
    ArrayList<String> name;
    ArrayList<String> address;
    ArrayList<String> contact;
    ArrayList<String> latitude;
    ArrayList<String> longitude;
    ArrayList<String> rating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        db = new Databasehelper(getContext());
        View view = inflater.inflate(R.layout.search_fragment,null);

        first_layout = view.findViewById(R.id.first_layout);
        compare_criteria_layout = view.findViewById(R.id.compare_criteria_layout);
        compare_rating = view.findViewById(R.id.compare_rating);
        compare_price = view.findViewById(R.id.compare_price);
        container1 = view.findViewById(R.id.container1);
        first_layout.setVisibility(View.VISIBLE);
        compare_criteria_layout.setVisibility(View.INVISIBLE);

        first_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compare_criteria_layout.setVisibility(View.VISIBLE);

                compare_rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        compare_rating.setBackgroundResource(R.drawable.menu_item_pressed);

                        compare_price.setBackgroundResource(R.drawable.rec_box_rounded_corner);
//                        mRatings.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        container1.removeAllViews();
                        display_restaurant_rating();

                    }
                });
            }
        });

        return view;
    }

    public void display_restaurant_rating()
    {
        id = new ArrayList<>();
        name = new ArrayList<>();
        address = new ArrayList<>();
        contact = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        rating = new ArrayList<>();

        DatabaseReference reference;
        container1.removeAllViews();

        ArrayList<Restaurant_SQLite_comparision> list = db.comparision_rating();
        Restaurant_SQLite_comparision obj ;

        for(int i=0 ; i< list.size() ; i++)
        {
            obj = list.get(i);
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.restaurant_layout, null);

            LinearLayout.LayoutParams value = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    value.setMargins(10,10,10,10);
            view1.setLayoutParams(value);
            //view1.setLayoutParams(10,10,10,10);
            TextView res_name,res_address,res_contact, distance;
            RatingBar ratingBar;
            ImageView img;

            ratingBar = view1.findViewById(R.id.restaurant_rating_bar);
            res_name = view1.findViewById(R.id.res_name);
            res_address = view1.findViewById(R.id.res_address);
            res_contact = view1.findViewById(R.id.res_contact);
            distance = view1.findViewById(R.id.distance);
            img = view1.findViewById(R.id.img_restaurant);

            reference = FirebaseDatabase.getInstance().getReference("Images").child(obj.id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imgpath = dataSnapshot.child("imgpath").getValue(String.class);
                    Picasso.get().load(imgpath).into(img);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Toast.makeText(getContext(), "name "+obj.name, Toast.LENGTH_SHORT).show();
            res_name.setText(obj.name);
            res_address.setText(obj.address);
            res_contact.setText(obj.contact);
            ratingBar.setRating(Float.valueOf(obj.rating));

            Restaurant_SQLite_comparision finalObj = obj;
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), RestaurantDetails.class);
                    Bundle extras = new Bundle();
                    //view.getContext().startActivity(new
                    //      Intent(view.getContext(),RestaurantDetails.class));
                    extras.putString("id", finalObj.id);
                    extras.putString("contact", finalObj.contact);
                    i.putExtras(extras);
                    //i.putExtra("id",id.get(position));
                    view.getContext().startActivity(i);
                }
            });
           container1.addView(view1);
        }

    }
}