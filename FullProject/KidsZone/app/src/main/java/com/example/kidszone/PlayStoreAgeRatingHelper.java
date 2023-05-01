package com.example.kidszone;
//import android.content.Context;
//import android.util.Log;
//
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.games.Games;
//import com.google.android.gms.games.GamesMetadata;
//import com.google.android.gms.games.GamesMetadata.LoadGamesResult;
//import com.google.android.gms.common.api.ResultCallback;

public class PlayStoreAgeRatingHelper {

//    private static final String TAG = "PlayStoreAgeRatingHelper";
//
//    public static void getAgeRating(Context context, String packageName) {
//        GoogleApiClient apiClient = new GoogleApiClient.Builder(context)
//                .addApi(Games.API)
//                .addScope(Games.SCOPE_GAMES)
//                .build();
//
//        apiClient.connect();
//
//        if (apiClient.isConnected()) {
//            Games.MetadataApi.loadGame(apiClient, packageName).setResultCallback(new ResultCallback<LoadGamesResult>() {
//                @Override
//                public void onResult(LoadGamesResult result) {
//                    if (result.getStatus().isSuccess()) {
//                        int ageRating = result.getMetadata().getContentRating().getRating();
//                        Log.d(TAG, "Age rating of " + packageName + " is " + ageRating);
//                    } else {
//                        Log.d(TAG, "Failed to load metadata for " + packageName);
//                    }
//                    apiClient.disconnect();
//                }
//            });
//        } else {
//            Log.d(TAG, "Failed to connect to Google Play Services");}}

}