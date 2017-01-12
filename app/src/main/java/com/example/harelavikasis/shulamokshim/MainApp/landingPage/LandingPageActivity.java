package com.example.harelavikasis.shulamokshim.MainApp.landingPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.harelavikasis.shulamokshim.MainApp.game.GameActivity;
import com.example.harelavikasis.shulamokshim.MainApp.R;
import com.example.harelavikasis.shulamokshim.MainApp.scoresTable.ScoresTabActivity;
import com.example.harelavikasis.shulamokshim.MainApp.scoresTable.ScoresTableActivity;
import com.example.harelavikasis.shulamokshim.MainApp.utils.Level;
import com.example.harelavikasis.shulamokshim.MainApp.view.BeveledTileTextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.harelavikasis.shulamokshim.MainApp.scoresTable.ScoresTableActivity.EASY;
import static com.example.harelavikasis.shulamokshim.MainApp.scoresTable.ScoresTableActivity.HARD;
import static com.example.harelavikasis.shulamokshim.MainApp.scoresTable.ScoresTableActivity.MEDIUM;

public class LandingPageActivity extends AppCompatActivity {

    private String key = "chosen_level";
    private final float SELECTED_ALPHA = (float) 0.5;
    private final float UNSELECTED_ALPHA = (float) 1.0;



    @Bind(R.id.easyButton)
    BeveledTileTextView easyButton;
    @Bind(R.id.mediumButton)
    BeveledTileTextView mediumButton;
    @Bind(R.id.hardButton)
    BeveledTileTextView hardButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        ButterKnife.bind(this);
//        initUI();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initUI() {
        SharedPreferences settings = getSharedPreferences(ScoresTableActivity.PREFS_NAME, 0);
        int chosenLevel = settings.getInt(key, -1);

        if (chosenLevel >= 0) { //here check what btn is checked) from sp
            switch (chosenLevel) {
                case EASY:
                    easyButton.setAlpha(SELECTED_ALPHA);
                    mediumButton.setAlpha(UNSELECTED_ALPHA);
                    hardButton.setAlpha(UNSELECTED_ALPHA);
                    break;
                case MEDIUM:
                    easyButton.setAlpha(UNSELECTED_ALPHA);
                    mediumButton.setAlpha(SELECTED_ALPHA);
                    hardButton.setAlpha(UNSELECTED_ALPHA);
                    break;
                case HARD:
                    easyButton.setAlpha(UNSELECTED_ALPHA);
                    mediumButton.setAlpha(UNSELECTED_ALPHA);
                    hardButton.setAlpha(SELECTED_ALPHA);
                    break;
            }
        }

    }

    public void chooseLevelTap(View v) {
        Intent nextScreen = new Intent(getApplicationContext(), GameActivity.class);
        switch (v.getId()) {
            // in every click save state to sp
            case R.id.easyButton:
                Log.d("uniNote", "you Choose: Easy");
                nextScreen.putExtra("mines", "5");
                nextScreen.putExtra("dimentions", "10");
                startActivity(nextScreen);
                setSelectedButton(0);
                break;
            case R.id.mediumButton:
                Log.d("uniNote", "you Choose: Medium");
                nextScreen.putExtra("mines", "10");
                nextScreen.putExtra("dimentions", "10");
                startActivity(nextScreen);
                setSelectedButton(1);
                break;
            case R.id.hardButton:

                Log.d("uniNote", "you Choose: Hard");
                nextScreen.putExtra("mines", "10");
                nextScreen.putExtra("dimentions", "5");
                startActivity(nextScreen);
                setSelectedButton(2);
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }
    }

    private void setSelectedButton(int i) {
        SharedPreferences settings = getSharedPreferences(ScoresTableActivity.PREFS_NAME, 0);
        SharedPreferences.Editor mEdit1 = settings.edit();
        mEdit1.remove(key);
        mEdit1.putInt(key,i);
        mEdit1.commit();
    }

    public void showRecoredsTap(View v) {

        Log.d("uniNote", "open records: " + v.getId());
//        Intent nextScreen = new Intent(getApplicationContext(), ScoresTableActivity.class);
        Intent nextScreen = new Intent(getApplicationContext(), ScoresTabActivity.class);

        startActivity(nextScreen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LandingPage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }
}
