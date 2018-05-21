package com.dev.android.mta.androiduserauthentication.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.android.mta.androiduserauthentication.Model.Item;
import com.dev.android.mta.androiduserauthentication.Model.Review;
import com.dev.android.mta.androiduserauthentication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import static com.dev.android.mta.androiduserauthentication.constans.ActivityExtrasConstans.CAR_ITEM_OBJECT;

public class ReviewActivity extends AppCompatActivity {

    private final String TAG = "ReviewActivity";
    private Item car;
    private String key;
    private String userEmail;
    private int prevRating = -1;

    private TextView userReview;
    private RatingBar userRating;
    private DatabaseReference carRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        key = getIntent().getStringExtra("key");
        car = getIntent().getParcelableExtra("car");
        userEmail = getIntent().getStringExtra("user");

        userReview = findViewById(R.id.new_user_review);
        userRating = findViewById(R.id.new_user_rating);


        carRef = FirebaseDatabase.getInstance().getReference("Cars/" + key);

        carRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            userReview.setText(review.getUserReview());
                            userRating.setRating(review.getUserRating());
                            prevRating = review.getUserRating();
                        }

                        Log.e(TAG, "onDataChange(Review) <<");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
                    }
                });

        Log.e(TAG, "onCreate() <<");

    }

    public void onSubmitClick(View v) {

        Log.e(TAG, "onSubmitClick() >>");


        carRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.e(TAG, "doTransaction() >>" );


                Item car = mutableData.getValue(Item.class);
                car.setKey(key);
                if (car == null ) {
                    Log.e(TAG, "doTransaction() << car is null" );
                    return Transaction.success(mutableData);
                }

                mutableData.setValue(car);
                Log.e(TAG, "doTransaction() << car was set");
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                Log.e(TAG, "onComplete() >>" );

                if (databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + databaseError.getMessage());
                    return;
                }

                if (committed) {
                    Review review = new Review(
                            userReview.getText().toString(),
                            (int)userRating.getRating(),
                            userEmail);

                    carRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(review);
                }



                Intent intent = new Intent(getApplicationContext(), CarDetailsActivity.class);
                intent.putExtra(CAR_ITEM_OBJECT, car);
                intent.putExtra("key" ,key);
                startActivity(intent);
                finish();

                Log.e(TAG, "onComplete() <<" );
            }
        });



        Log.e(TAG, "onSubmitClick() <<");
    }
}
