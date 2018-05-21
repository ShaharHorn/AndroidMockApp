package com.dev.android.mta.androiduserauthentication.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.android.mta.androiduserauthentication.Model.Item;
import com.dev.android.mta.androiduserauthentication.R;
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

import java.util.ArrayList;
import java.util.List;

import static com.dev.android.mta.androiduserauthentication.constans.AuthenticationMethodConstatns.ANONNYMOUSE_USER;
import static com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.CARS;
import static com.dev.android.mta.androiduserauthentication.constans.DatabaseConstans.USERS;

public class AccountInfoActivity extends AppCompatActivity {
    private TextView mUsername;
    private TextView mEmail;
    private ImageView mProfilePic;
    private GoogleSignInClient mAccount ;
    private RecyclerView mRecyclerView;
    private DatabaseReference mMyUserRef;
    private DatabaseReference mUserItemsRef;
    private FirebaseUser mFbUser;
    private List<Item> mItemList;
    private ItemAdapter mItemAdapter;
    public static final String TAG = "accountInfoActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
       // account.getSignInIntent();
        mUsername = findViewById(R.id.textViewUsername);
        mEmail = findViewById(R.id.textEmail);
        mProfilePic =  findViewById(R.id.imageView);

        mItemList = new ArrayList<>();
        mRecyclerView = (RecyclerView)findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mMyUserRef = FirebaseDatabase.getInstance().getReference(USERS);
        mItemAdapter = new ItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mItemAdapter);
        getAllSongsUsingChildListenrs();

        if(mFbUser!=null) {
            if(mFbUser.isAnonymous()) {
                mUsername.setText(ANONNYMOUSE_USER);
            }
            else {
                mEmail.setText("Email: " + mFbUser.getEmail());
                mUsername.setText("Name : " + mFbUser.getDisplayName());
                if (mFbUser.getPhotoUrl() != null) {

                    Glide.with(this)
                            .load(mFbUser.getPhotoUrl().toString())
                            .into(mProfilePic);
                }
            }
        }
    }

    private void getAllSongsUsingChildListenrs() {
        FirebaseUser mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserItemsRef= FirebaseDatabase.getInstance()
                .getReference("Users/" + mFbUser.getUid() + "/myCars/");

        mUserItemsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                Log.e(TAG, "onChildAdded(Item) >> " + snapshot.getKey());
                Item item = null;
                try {
                    item = snapshot.getValue(Item.class);
                } catch (Exception e) {
                    e.getMessage();
                }
                mItemList.add(item);
                mRecyclerView.getAdapter().notifyDataSetChanged();

                Log.e(TAG, "onChildAdded(Songs) <<");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateSongsList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.e(TAG, "onChildRemoved(Songs) >> " + dataSnapshot.getKey());

                Item itemToRemove = dataSnapshot.getValue(Item.class);
                String key = dataSnapshot.getKey();

                for (int i = 0; i < mItemList.size(); i++) {
                    if (mItemList.get(i).equals(itemToRemove)) {
                        mItemList.remove(i);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        Log.e(TAG, "onChildRemoved(Songs) >> i=" + i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        //     findViewById(R.id.sign_out_appMenu).setText()
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.log_out_profile)
        {
            signOut();
        }
        else if(item.getItemId() == R.id.item_menu_profile)
        {
            Intent intent = new Intent(AccountInfoActivity.this, ItemsMenuActivity.class);
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
        Intent intent = new Intent(AccountInfoActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }

}
