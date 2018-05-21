package com.dev.android.mta.androiduserauthentication.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.Model.Item;
import com.dev.android.mta.androiduserauthentication.Model.Review;
import com.dev.android.mta.androiduserauthentication.Model.User;
import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.implementaion.ItemAdapter;
import com.dev.android.mta.androiduserauthentication.implementaion.ReviewsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import static com.dev.android.mta.androiduserauthentication.constans.ActivityExtrasConstans.CAR_ITEM_OBJECT;

public class CarDetailsActivity extends AppCompatActivity {

    private Item mCar;
    private ImageView mImageView;
    private DatabaseReference mMyUserRef;
    private DatabaseReference mReviewRef;
    private ArrayList<Review> mReviewList = new ArrayList<>();
    private ReviewsAdapter mReviewAdapter;
    private RecyclerView mRecyclerView;
    private User mMyUser;
    public static final String TAG = "carDetails";
    private TextView mCarMakeText;
    FirebaseUser   mFbUser;
    private TextView mCarModelText;
    private TextView mCarYearText;
    private TextView mCarPriceText;
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();
    private Button mPurchaseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewAdapter = new ReviewsAdapter(mReviewList);
        mRecyclerView.setAdapter(mReviewAdapter);
        try {
            mCar = getIntent().getParcelableExtra(CAR_ITEM_OBJECT);
            mCar.setKey(getIntent().getStringExtra("key"));


            mFbUser = FirebaseAuth.getInstance().getCurrentUser();
            mReviewRef = FirebaseDatabase.getInstance().getReference("Cars/" + mCar.getKey() + "/reviews/");

            mReviewRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Review review=null;
                    try {
                        review = dataSnapshot.getValue(Review.class);
                    }catch (Exception e){
                        e.getMessage();
                    }
                    mReviewList.add(review);
                    //mRecyclerView.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (mFbUser != null) {
                mMyUserRef = FirebaseDatabase.getInstance().getReference("Users/" + mFbUser.getUid());

                mMyUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        mMyUser = snapshot.getValue(User.class);

                        Log.e(TAG, "onDataChange(User) <<");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Users) >>" + databaseError.getMessage());
                    }
                });
            }
            bindClassToLayout();

            mPurchaseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    try {
                        if(mFbUser.isAnonymous()){
                            Toast.makeText(CarDetailsActivity.this,
                                    "You have to log in first!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CarDetailsActivity.this,MainActivityOpening.class);
                            startActivity(intent);
                        }
                        else {
                            Log.e(TAG, "purchase click file=" + mCar.getCarModel());
                            //Purchase the car.
                            mMyUser.getMyCars().add(mCar);
                            mMyUser.upgdateTotalPurchase(mCar.getPrice());
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(mMyUser);
                            mAnalyticsManager.trackPurchase(mCar);
                            Toast.makeText(CarDetailsActivity.this, "Your purchase has been completed.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CarDetailsActivity.this, ItemsMenuActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            });

        }catch (Exception e){
            e.getMessage();
        }
    }


    public void onClickAddReview(View view){
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("car", mCar);
        intent.putExtra("user", mMyUser.getEmail());
        intent.putExtra("key" , mCar.getKey());
        startActivity(intent);
    }



    private void bindClassToLayout() {
         mImageView = findViewById(R.id.carDetailsImageView);
         mCarMakeText = findViewById(R.id.carMakeLabel);
         mCarModelText = findViewById(R.id.carModelLabel);
         mCarYearText = findViewById(R.id.carYearLabel);
         mCarPriceText = findViewById(R.id.carPriceLabel);
         mPurchaseButton = findViewById(R.id.carPurchaseButton);


         if(mCar != null){
             Glide.with(this)
                     .load(mCar.getCarImage().toString())
                     .into(mImageView);
             mCarMakeText.setText(mCar.getCarMake());
             mCarModelText.setText(mCar.getCarModel());
             mCarYearText.setText(mCar.getCarModelYear());
             mCarPriceText.setText("$" + mCar.getPrice());
         }

    }
}
