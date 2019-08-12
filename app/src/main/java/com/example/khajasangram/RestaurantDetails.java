package com.example.khajasangram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khajasangram.Adaptors.ReviewAdaptor;
import com.example.khajasangram.Classes.RestaurantClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RestaurantDetails extends AppCompatActivity {

    String dcreated_date;

    ImageView imgview;
    TextView name,address,contact,description;
    LinearLayout menu,location,phone;
    Button button;
    RatingBar ratingBar, avg_rating;
    Toolbar toolbar;

    DatabaseReference reference, reference_description;
    DatabaseReference userloc_ref;
    DatabaseReference reference_img;

    String user_lat,user_lon;
    String res_lat,res_lon;

    String first_name_user;

    FirebaseAuth mAuth;
    String intent_id, intent_contact,rating_value;
    RecyclerView recyclerView;

    ReviewAdaptor reviewAdaptor;
    String title;
    String intent_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details_new);

        recyclerView = findViewById(R.id.reviewrecycler);

        description = findViewById(R.id.res_description);
        imgview = findViewById(R.id.res_image);
        location = findViewById(R.id.res_location);
        menu = findViewById(R.id.menu);

        name = findViewById(R.id.res_name);
        address = findViewById(R.id.res_address);
        //contact = findViewById(R.id.res_contact);
        phone = findViewById(R.id.res_contact);

        button = findViewById(R.id.rating_submit_btn);
        ratingBar = findViewById(R.id.rating);
        avg_rating = findViewById(R.id.avg_rating);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        intent_id = extras.getString("id");
        intent_contact = extras.getString("contact");
        intent_title = extras.getString("name");

        setTitle(intent_title);

        DatabaseReference avg_rating_reference;
        final Double[] d = {0.0};
        final Double[] final_val = new Double[1];
        avg_rating_reference = FirebaseDatabase.getInstance().getReference("Rating").child(intent_id);
        avg_rating_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Double star = Double.parseDouble(snapshot.child("stars").getValue(String.class));
                    d[0] = d[0] + star;
                }
                final_val[0] = d[0]/dataSnapshot.getChildrenCount();
                Float f = final_val[0].floatValue();
                avg_rating.setRating(f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




        DatabaseReference rev_curr_value;
        rev_curr_value = FirebaseDatabase.getInstance().getReference("Rating").child(intent_id).child((new UservaluesReturner().getuser_id()));
        rev_curr_value.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rat_val = dataSnapshot.child("stars").getValue(String.class);
                if(rat_val == null)
                {
                    String temp_rat_val = "0";
                    ratingBar.setRating(Float.parseFloat(temp_rat_val));     }
                else
                ratingBar.setRating(Float.parseFloat(rat_val));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Bundle bundle = getIntent().getExtras();
        //intent_id = bundle.getString("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        update_review();

        mAuth = FirebaseAuth.getInstance();

        //Toast.makeText(this, "id= "+intent_id, Toast.LENGTH_SHORT).show();

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:" + intent_contact));
                startActivity(callIntent);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callgoogle_map();
            }
        });
        getrestaurant_details();

         ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
             @Override
             public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                 rating_value = String.valueOf((int) ratingBar.getRating());

             }
         });

         button.setOnClickListener(new View.OnClickListener() {


             @Override
             public void onClick(View view) {

                 if(rating_value == null )
                 {
                    rating_value="0";
                 }
                 final String[] fname = new String[1];
                 String uid;
                 uid = new UservaluesReturner().getuser_id();
                 //fname[0] = new UservaluesReturner().getuser_namefirst(uid);

                 DatabaseReference databaseReference;
                 databaseReference = FirebaseDatabase.getInstance().getReference("Rating").child(intent_id).child(uid);

                 DatabaseReference reference;
                 reference = FirebaseDatabase.getInstance().getReference("Users");
                 reference.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                             if (rating_value == "0") {
                                 databaseReference.removeValue();
                             }
                             else{
                                 String did = snapshot.child("id").getValue(String.class);
                             //Toast.makeText(RestaurantDetails.this, "uid= "+uid, Toast.LENGTH_SHORT).show();
                             if (uid.equals(did)) {
                                 fname[0] = snapshot.child("fname").getValue(String.class);
                                 databaseReference.child("name").setValue(fname[0]);

                                 break;
                             }

                         }
                         }
                        // first_name_user = fname[0];
                        // Toast.makeText(RestaurantDetails.this, "fname= "+fname[0], Toast.LENGTH_SHORT).show();

                     }


                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

                 databaseReference.child("stars").setValue(rating_value);
                 update_review();

             }
         });

         menu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i= new Intent(view.getContext(),MenudisplayActivity.class);
                 Bundle extras = new Bundle();

                 extras.putString("id",intent_id);
                 extras.putString("title",title);
                 i.putExtras(extras);
                 startActivity(i);
             }
         });


    }

    private void update_review() {
        ArrayList<String> list_name = new ArrayList<>();
        ArrayList<String> list_star = new ArrayList<>();

        DatabaseReference review_ref = FirebaseDatabase.getInstance().getReference("Rating").child(intent_id);
        review_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String name = snapshot.child("name").getValue(String.class);
                    String star = snapshot.child("stars").getValue(String.class);

                    list_name.add(name);
                    list_star.add(star);

                    reviewAdaptor = new ReviewAdaptor(recyclerView, getApplicationContext(),list_name,list_star);
                    recyclerView.setAdapter(reviewAdaptor);

                    recyclerView.setHasFixedSize(true);
                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getrestaurant_details()
    {
        reference = FirebaseDatabase.getInstance().getReference("Restaurants");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String id = snapshot.child("id").getValue(String.class);
                   //Toast.makeText(getApplicationContext(), "database id= "+id+" and pased id= "+intent_id, Toast.LENGTH_SHORT).show();

                    if(intent_id.equals(id))
                    {
                        //Toast.makeText(getApplicationContext(), "id= "+id, Toast.LENGTH_SHORT).show();
                        String dname = snapshot.child("name").getValue(String.class);
                        title = dname;
                        String daddress = snapshot.child("address").getValue(String.class);
                        res_lat = snapshot.child("latitude").getValue(String.class);
                        res_lon = snapshot.child("longitude").getValue(String.class);
                        String dcontact = snapshot.child("contact").getValue(String.class);

                        name.setText(dname);
                        address.setText(daddress);
                       // contact.setText(dcontact);

                        setrestaurant_picture();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setrestaurant_picture()
    {
        reference_img = FirebaseDatabase.getInstance().getReference("Images").child(intent_id);
        reference_img.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imgpath = dataSnapshot.child("imgpath").getValue(String.class);
                Picasso.get().load(imgpath).into(imgview);
                get_restaurant_description();
               /* for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("rid").getValue(String.class);

                    if(intent_id.equals(id)) {
                        Toast.makeText(getApplicationContext(), "database id= "+id+" and pased id= "+intent_id, Toast.LENGTH_SHORT).show();
                        String imgpath = snapshot.child("imgpath").getValue(String.class);

                        Picasso.get().load(imgpath).into(imgview);
                        get_restaurant_description();
                        break;
                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void get_restaurant_description()
    {
        reference_description = FirebaseDatabase.getInstance().getReference("Description").child(intent_id);
        reference_description.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String description_txt = dataSnapshot.child("description").getValue(String.class);
                description.setText(description_txt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void callgoogle_map()
    {
        String uid = mAuth.getCurrentUser().getUid();
        userloc_ref = FirebaseDatabase.getInstance().getReference("Users");
        userloc_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String id = snapshot.child("id").getValue(String.class);
                    if(uid.equals(id))
                    {
                     user_lat = snapshot.child("latitude").getValue(String.class);
                     user_lon = snapshot.child("longitude").getValue(String.class);
                     break;
                    }

                }
                String uri = "http://maps.google.com/maps?saddr=" + user_lat + "," + user_lon + "&daddr=" + res_lat + "," + res_lon;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
