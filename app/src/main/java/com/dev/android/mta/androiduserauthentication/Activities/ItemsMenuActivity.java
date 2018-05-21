package com.dev.android.mta.androiduserauthentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.dev.android.mta.androiduserauthentication.Model.AnalyticsManager;
import com.dev.android.mta.androiduserauthentication.Model.User;
import com.dev.android.mta.androiduserauthentication.R;
import com.dev.android.mta.androiduserauthentication.Model.Item;
import com.dev.android.mta.androiduserauthentication.implementaion.ItemAdapter;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.*;
import static com.dev.android.mta.androiduserauthentication.constans.ActivityExtrasConstans.MAX_PRICE;
import java.util.ArrayList;
import java.util.List;

import static com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.CARS;
import static com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.USERS;

public class ItemsMenuActivity extends AppCompatActivity {
    private final String TAG = "MusicPlayerMain";
    private DatabaseReference mAllItemsRef;
    private DatabaseReference mMyUserRef;
    private FirebaseUser mFbUser;
    private RecyclerView mRecyclerView;
    private ItemAdapter mItemAdapter;
    private List<Item> mItemList;
    private List<Item> mOriginalItemList;
    private Toolbar mToolBar;
    private boolean mIsExist=false;
    private EditText mSearchText;
    private EditText mMinPrice,mMaxPrice;
    private AnalyticsManager mAnalyticsManager = AnalyticsManager.getInstance();
    private GoogleSignInClient mAccount ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_menu);
        mMaxPrice = findViewById(R.id.maxPriceText);
        mMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceFilter();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mMinPrice = findViewById(R.id.minPriceText);
        mMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceFilter();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchText = findViewById(R.id.searchText);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().toLowerCase();
                ArrayList<Item> newItemList = new ArrayList<>();
                for(Item item : mOriginalItemList){
                    String currentName = item.getCarMake().toLowerCase();
                    if(currentName.contains(newText)){
                        newItemList.add(item);
                    }
                }
                mItemAdapter.setFilter(newItemList);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();
                if(!searchString.isEmpty()) {
                    mAnalyticsManager.trackSearchEvent(searchString,"search");
                }
            }
        });
        mItemList = new ArrayList<>();
        mOriginalItemList = new ArrayList<>();
        mRecyclerView = (RecyclerView)findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFbUser =  FirebaseAuth.getInstance().getCurrentUser();
        mMyUserRef = FirebaseDatabase.getInstance().getReference(USERS);
        mToolBar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        mItemAdapter = new ItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mItemAdapter);
        getAllSongsUsingChildListenrs();

    }

    private void priceFilter() {
        int minPrice,maxPrice;

        if(mMinPrice.getText().toString().isEmpty()) {
            minPrice=0;
        }
        else {
            minPrice = Integer.parseInt(mMinPrice.getText().toString());
            mAnalyticsManager.trackSearchEvent(mMinPrice.getText().toString(),"min");

        }
        if(mMaxPrice.getText().toString().isEmpty()) {
            maxPrice=MAX_PRICE;
        }
        else {
            maxPrice = Integer.parseInt(mMaxPrice.getText().toString());
            mAnalyticsManager.trackSearchEvent(mMinPrice.getText().toString(),"max");
        }
        ArrayList<Item> newItemList = new ArrayList<>();
        for (Item item : mOriginalItemList) {
            String price = item.getPrice();
            if (Integer.parseInt(price) > minPrice && Integer.parseInt(price)<maxPrice ) {
                newItemList.add(item);
            }
        }
        mItemAdapter.setFilter(newItemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMyUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userSnapshotId = userSnapshot.getKey();
                    if(mFbUser != null)
                    {
                        if (mFbUser.getUid().equals(userSnapshotId)) {
                            mIsExist = true;
                            break;
                         }
                     }
                }
                if(!mIsExist && mFbUser != null) {
                    mMyUserRef.child(mFbUser.getUid()).setValue(new User(mFbUser.getEmail(), "0$", null));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()== R.id.sign_out_appMenu)
         {
             signOut();
         }
         else if(item.getItemId() == R.id.profile)
         {
             Intent intent = new Intent(ItemsMenuActivity.this, AccountInfoActivity.class);
             startActivity(intent);
         }
         return true;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        if(mAccount!=null) {
            mAccount.signOut();
        }
        disconnectFromFacebook();
        Intent intent = new Intent(ItemsMenuActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }

    private void getAllSongsUsingChildListenrs() {

        mAllItemsRef = FirebaseDatabase.getInstance().getReference(CARS);
        mAllItemsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                Log.e(TAG, "onChildAdded(Item) >> " + snapshot.getKey());
                Item item=null;
                try {
                    item = snapshot.getValue(Item.class);
                    item.setKey(snapshot.getKey());

                }catch (Exception e){
                    e.getMessage();
                }
                mItemList.add(item);
                mOriginalItemList.add(item);
                mRecyclerView.getAdapter().notifyDataSetChanged();

                Log.e(TAG, "onChildAdded(Cars) <<");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.e(TAG, "onChildRemoved(Songs) >> " + dataSnapshot.getKey());

                Item itemToRemove =dataSnapshot.getValue(Item.class);
                String key = dataSnapshot.getKey();

                for (int i = 0 ; i < mItemList.size() ; i++) {
                    if (mItemList.get(i).equals(itemToRemove)) {
                        mItemList.remove(i);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        Log.e(TAG, "onChildRemoved(Songs) >> i="+i);
                        break;
                    }
                }

                Log.e(TAG, "onChildRemoved(Songs) <<");
            }


            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateSongsList(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Item item = dataSnapshot.getValue(Item.class);
            Log.e(TAG, "updateItemsList() >> adding Item: " + item.getCarMake());
            String key = dataSnapshot.getKey();
            mItemList.add(item);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onRadioButtonCLick(View v) {
        switch (v.getId()) {
            case R.id.priceSortButton:
                ((RadioButton)findViewById(R.id.yearSortButton)).setChecked(false);
                mItemAdapter.sort("price");
                break;
            case R.id.yearSortButton:
                ((RadioButton)findViewById(R.id.priceSortButton)).setChecked(false);
                mItemAdapter.sort("year");
                break;
        }
    }

}
