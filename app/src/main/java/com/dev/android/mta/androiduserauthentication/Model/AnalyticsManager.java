package com.dev.android.mta.androiduserauthentication.Model;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.analytics.FirebaseAnalytics.Param.SIGN_UP_METHOD;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    public MixpanelAPI getmMixpanel() {
        return mMixpanel;
    }

    private  MixpanelAPI mMixpanel;

    private AnalyticsManager() {



    }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        //Token  can be found in Mixpanel console under Project Settings >> Management >> Token
        mMixpanel = MixpanelAPI.getInstance(context, "b25a1fa596b35dcf4fc25498cc32d049");

    }
    public void setUserID(String id, boolean newUser) {

        mFirebaseAnalytics.setUserId(id);


        if (newUser) {
            mMixpanel.alias(id, null);
        }
        mMixpanel.identify(id);
        mMixpanel.getPeople().identify(mMixpanel.getDistinctId());

        //Sender id can be found in the Firebase console under Project Settings >>CLOUD MESSAGING TAB >> Sender ID

        mMixpanel.getPeople().initPushHandling("375791534225");
    }

    public void setUserProperty(String name , String value) {

        mFirebaseAnalytics.setUserProperty(name,value);

        mMixpanel.getPeople().set(name,value);
    }

    public void trackSignupEvent(String signupMethod) {

        String eventName = "signup";
        Bundle params = new Bundle();
        params.putString(SIGN_UP_METHOD, signupMethod);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);

        //MixPanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("signup method", signupMethod);
        mMixpanel.trackMap(eventName,eventParams2);

    }
    public void trackLoginEvent(String loginMethod) {

        String eventName = "login";
        Bundle params = new Bundle();
        params.putString(SIGN_UP_METHOD, loginMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,params);

        //MixPanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("login method", loginMethod);
        mMixpanel.trackMap(eventName,eventParams2);
    }

    public void trackSearchEvent(String searchString,String searchTrackType) {
        String eventName="";
        String toParam=FirebaseAnalytics.Param.SEARCH_TERM;
        switch(searchTrackType)
        {
            case "search":
                eventName = "search";
                break;
            case "min":
                eventName = "minPriceFilter";
                toParam += "_by_min_price";
                break;
            case "max":
                eventName = "maxPriceFilter";
                toParam += "_by_max_price";
                break;

        }
        //Firebase
        Bundle params = new Bundle();
        params.putString( toParam , searchString);
        mFirebaseAnalytics.logEvent(toParam,params);
        //MixPanel
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("search term", searchString);
         mMixpanel.trackMap(eventName,eventParams);

    }

    public void trackPurchase(Item car) {

        String eventName = "purchase";
        Bundle params = new Bundle();

        params.putString("car_make",car.getCarMake());
        params.putString("car_color", car.getCarColor());
        params.putString("car_price",String.valueOf(car.getPrice()));
        params.putString("car_model", car.getCarModel());
        params.putString("car_model_yeah",car.getCarModelYear());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,params);
        //MixPanel
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("car_make",car.getCarMake());
        eventParams.put("car_color", car.getCarColor());
        eventParams.put("car_price",String.valueOf(car.getPrice()));
        eventParams.put("car_model", car.getCarModel());
        eventParams.put("car_model_yeah",car.getCarModelYear());
        mMixpanel.trackMap(eventName,eventParams);
    }

    public void trackNewReview(Review review) {

        String eventName = "new_review";
        Bundle params = new Bundle();
        params.putString("user_email",review.getUserEmail());
        params.putString("review_text",review.getUserReview());
        params.putInt("user_rating",review.getUserRating());
        mFirebaseAnalytics.logEvent(eventName,params);
        //MixPanel
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("user_email",review.getUserEmail());
        eventParams.put("review_text",review.getUserReview());
        eventParams.put("user_rating",review.getUserRating());
        mMixpanel.trackMap(eventName,eventParams);
    }


}