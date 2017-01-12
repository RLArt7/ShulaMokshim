package com.example.harelavikasis.shulamokshim.MainApp.game;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.LevelListDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.harelavikasis.shulamokshim.MainApp.MainApplication;
import com.example.harelavikasis.shulamokshim.MainApp.R;
import com.example.harelavikasis.shulamokshim.MainApp.board.BoardLayoutView;
import com.example.harelavikasis.shulamokshim.MainApp.bus.GeneralEvent;
import com.example.harelavikasis.shulamokshim.MainApp.drawable.ConcentricCirclesDrawable;
import com.example.harelavikasis.shulamokshim.MainApp.exceptions.InitializationException;
import com.example.harelavikasis.shulamokshim.MainApp.scoresTable.MapFragment;
import com.example.harelavikasis.shulamokshim.MainApp.scoresTable.Score;
import com.example.harelavikasis.shulamokshim.MainApp.utils.GyroManager;
import com.example.harelavikasis.shulamokshim.MainApp.utils.Level;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.greysonparrelli.permiso.Permiso;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by harelavikasis on 01/12/2016.
 */

public class GameActivity extends AppCompatActivity implements GameManager.Listener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    static final int IN_PLAY_LEVEL = 0;
    static final int WON_LEVEL = 1;
    static final int LOST_LEVEL = 2;
    static final int MAX_NUM_OF_RECORDS = 10;

    private static final String TAG = GameActivity.class.getName();

    public static final String PREFS_NAME = "ShulaMokshim_Settings";

    public static final String KEY_FINISH = "finish";

    @Bind(R.id.board_layout_view)
    BoardLayoutView mBoardLayoutView;
    @Bind(R.id.remaining_flags_text_view)
    TextView mRemainingFlagsTextView;
    @Bind(R.id.elapsed_time_text_view)
    TextView mElapsedTimeTextView;
    @Bind(R.id.finish_button)
    Button mFinishButton;
    @Bind(R.id.reset_button)
    Button mResetButton;
    @Bind(R.id.status_image_view)
    ImageView mStatusImageView;
    @Bind(R.id.main_relative_layout)
    RelativeLayout mainRelativeLayout;
    @Bind(R.id.gif_view)
    GifImageView gifView;

    private GameManager mGameManager;
    private int mDimension;//= Board.DEFAULT_DIMENSION;
    private int mNumMines;//= Board.DEFAULT_NUM_MINES;
    private LevelListDrawable mStatusImageDrawable;
    private Timer mTimer;
    private Level level;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    public  Score tempScore;
    public  SharedPreferences.Editor tempEditor;
    public  String tempKey;
    private ImageView finisgImageView;

    private GoogleApiClient client;
    private GyroManager gyroManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isFinishNotAlredyTapped = true;
    private Toast toast;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page") // TODO: Define a title for the content shown.
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
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        MainApplication.getGameBus().register(this);

        Permiso.getInstance().setActivity(this);
        initGoogleApiClient();

        requestPermiso();
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        // Receiving the Data
        mNumMines = Integer.parseInt(i.getStringExtra("mines"));
        mDimension = Integer.parseInt(i.getStringExtra("dimentions"));
        setLevel();
        Log.d("uniNote", "you Choose: " + mNumMines + " : " + mDimension);
        setupViews();
        setupGame();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        gyroManager = new GyroManager();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void requestPermiso() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {

                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
//                Toast.makeText(this, "Die fucker!!!", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    private void initGoogleApiClient() {
        //anyway check for android permission. If the user has disabled location services, show Empty Screen
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void setLevel() {
        if (mDimension == 10 && mNumMines == 5) this.level = Level.EASY;
        else if (mDimension == 10 && mNumMines == 10) this.level = Level.MEDIUM;
        else level = Level.HARD;
    }

    private void setupGame() {
        try {
            mGameManager = new GameManager(mDimension, mNumMines, mBoardLayoutView, this);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = getResources().getString(R.string.board_initialization_error);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinishNotAlredyTapped) {
                    mGameManager.finishGame();
                    onGameFinished();
                    isFinishNotAlredyTapped = false;
                }
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    prepareToReset();

                    mGameManager.initGame(mDimension, mNumMines);

                    stopTimer();
                    startTimer();
                    mStatusImageDrawable.setLevel(IN_PLAY_LEVEL);


                } catch (Exception e) {
                    e.printStackTrace();
                    Context context = GameActivity.this;
                    String message = context.getResources().getString(R.string.game_reset_error);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupStatusImageView();
    }

    private void prepareToReset()
    {
        moveImageFinishDown();
        gifView.setVisibility(View.GONE);
        isFinishNotAlredyTapped = true;
        mSensorManager.unregisterListener(gyroManager);
        gyroManager = new GyroManager();
        mSensorManager.registerListener(gyroManager, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        toast.cancel();
    }

    private void setupStatusImageView() {
        float fillPercent = 0.8f;
        int inPlayOuter = getResources().getColor(R.color.blue_grey_300);
        int inPlayInner = getResources().getColor(R.color.blue_grey_600);

        mStatusImageDrawable = new LevelListDrawable();
        mStatusImageDrawable.addLevel(0, IN_PLAY_LEVEL, new ConcentricCirclesDrawable(new int[]{inPlayOuter, inPlayInner}, fillPercent));
        mStatusImageDrawable.addLevel(0, WON_LEVEL, new ConcentricCirclesDrawable(new int[]{Color.GREEN, Color.YELLOW}, fillPercent));
        mStatusImageDrawable.addLevel(0, LOST_LEVEL, new ConcentricCirclesDrawable(new int[]{Color.RED, Color.BLACK}, fillPercent));

        mStatusImageView.setBackground(mStatusImageDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(gyroManager, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (mGameManager != null) {
            updateMineFlagsRemainingCount(mGameManager.getMineFlagsRemainingCount());
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        mSensorManager.unregisterListener(gyroManager);
    }

    void startTimer() {
        if (mGameManager != null && !mGameManager.isGameFinished()) {
            mGameManager.startTimer();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTimeElapsed(mGameManager.getElapsedTime());
                        }
                    });
                }
            };

            mTimer = new Timer();

            // Delay: 0, Interval: 1000ms
            mTimer.schedule(timerTask, 0, 1000);
        }
    }


    void stopTimer() {
        if (mGameManager != null && mTimer != null) {
            mGameManager.stopTimer();
//            timeRecord =
            Log.d("uniNote", "Timer: " + mTimer.toString());
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void updateTimeElapsed(long elapsedTime) {
        int elapsedTimeInSeconds = (int) elapsedTime / 1000;

        mElapsedTimeTextView.setText(String.valueOf(elapsedTimeInSeconds));
    }

    @Override
    public void updateMineFlagsRemainingCount(int flagsRemaining) {
        mRemainingFlagsTextView.setText(String.valueOf(flagsRemaining));
    }

    @Override
    public void onLoss() {
        // here we need to intent new activity with failed view
//        checkRecords();
//        navigateFinishScreen(false);
        moveImageFinishUp(false);
        mStatusImageDrawable.setLevel(LOST_LEVEL);
    }

    @Override
    public void onWin() {
        // here we need to intent new activity with Win view and also store the scores to shared application
        checkRecords();
//        navigateFinishScreen(true);
        mStatusImageDrawable.setLevel(WON_LEVEL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        prepareToReset();
        mGameManager.getmGame().unregistered();
        MainApplication.getGameBus().unregister(this);

    }

    @Override
    public void onGameFinished() {

    }

    private void navigateFinishScreen(boolean isFinish) {
        Intent intent = new Intent(this,FinishGameActivity.class);
        intent.putExtra(KEY_FINISH ,isFinish);
        startActivity(intent);
        finish();
    }

    private void checkRecords() {

        Gson gson = new Gson();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        int size = mSharedPreference.getInt("Status_size", 0);

        for (int i = 0; i < MAX_NUM_OF_RECORDS; i++) {
            String key = this.level.toString() + i;
            String json = settings.getString(key, "");
            if (json == "") {
                Log.d("uniNote",  "json: " + json);
                tempKey = this.level.toString() + i;
                insertRecord(i , json , true);
                return;
            }
            Score score = gson.fromJson(json, Score.class);
            int lastElapsedTime = mGameManager.getmGame().lastElapsedTime;
            Log.d("uniNote",  " score.getTimeRecord(): " + score.getTimeRecord());
            if (score.getTimeRecord() > lastElapsedTime) {
                tempKey = this.level.toString() + i;
                insertRecord(i , json , true);
                return;
            }
        }
    }

    private void insertRecord(int index , String json , Boolean isNewHighScore) {
        Date date = new Date();
        Gson gson = new Gson();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor mEdit1 = settings.edit();

        String key = this.level.toString() + index;

        if (index != 9 && !json.equals("")) {

            insertRecord(index + 1, settings.getString(key, ""), false);
        }

        if (isNewHighScore) {
                //place marker at current position
                //mGoogleMap.clear();
//            Random rand = new Random();
//            int  n = rand.nextInt(5) + 1;
//            LatLng dummylocation = new LatLng(latLng.latitude + (double)(5*n), latLng.longitude + (double)(5*n));

            Score score = new Score(mGameManager.getmGame().lastElapsedTime, date, "player1",latLng);
            this.tempScore = score;
            this.tempEditor = mEdit1;
            showPlayerDialog();

        } else {
            mEdit1.remove(key);
            Log.d("uniNote", "GameRecord1: " + json + " Key: " + tempKey);

            mEdit1.putString(key, json);
            mEdit1.commit();
        }


    }

    private void showPlayerDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage("You Made A Record!");
        alert.setTitle("What Your Name?");

        alert.setView(edittext);


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userChose("YES",edittext.getText().toString());
//                navigateFinishScreen(true);
                moveImageFinishUp(true);
            }
        });

        alert.setNegativeButton("Don't Care", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userChose("NO","");
//                navigateFinishScreen(true);
                moveImageFinishUp(true);
            }
        });

        alert.show();

    }

    private void moveImageFinishUp(boolean isWin)
    {

        if (isWin) {
            if (finisgImageView == null) {
                finisgImageView = new ImageView(this);
                // finished
                finisgImageView.setImageResource(R.drawable.ic_thumb_up);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, R.id.finish_button);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                mainRelativeLayout.addView(finisgImageView, lp);
                // finished
                YoYo.with(Techniques.SlideInUp)
                        .duration(700)
                        .playOn(finisgImageView);
            }
            else
            {

            }

        } else {
            gifView.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Flash)
                    .duration(700)
                    .playOn(gifView);

            YoYo.with(Techniques.FadeOut)
                    .duration(700)
                    .playOn(gifView);
        }

    }

    private void moveImageFinishDown()
    {
        if (finisgImageView != null) {
            finisgImageView.setVisibility(View.GONE);
            finisgImageView = null;
//            YoYo.with(Techniques.SlideOutDown)
//                    .duration(700)
//                    .playOn(finisgImageView);
        }
    }
    public void userChose(String choise, String name){
        Gson gson = new Gson();
        if(choise.equals("YES")) {
            //YOUR CODE FOR YES HERE
            this.tempScore.setName(name);
//            Toast.makeText(this, "YOU CHOSE YES", Toast.LENGTH_LONG).show();
        }else if (choise.equals("NO")) {
            Toast.makeText(this, "YOU CHOSE NO", Toast.LENGTH_LONG).show();
        }

        this.tempEditor.remove(tempKey);
        Log.d("uniNote", "GameRecord1: " + gson.toJson(this.tempScore) + " Key: " + tempKey);

        this.tempEditor.putString(tempKey, gson.toJson(this.tempScore));
        this.tempEditor.commit();

    }

    public void setNumOfMines(int numOfMines) {
        this.mNumMines = numOfMines;
    }

    public void setDimention(int numOfRows) {
        this.mDimension = numOfRows;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());

    }
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        }
    }
    @Subscribe
    public void tiltEvent(GeneralEvent event) throws InitializationException {
        mGameManager.addRandomMine();
        toast = Toast.makeText(this, "Tilt Back! Mine was Added To the Screen", Toast.LENGTH_LONG);
        toast.show();
        if (mGameManager != null) {
//            updateMineFlagsRemainingCount(mGameManager.getMineFlagsRemainingCount());
//            startTimer();
        }
    }

}
