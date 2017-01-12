package com.example.harelavikasis.shulamokshim.MainApp.scoresTable;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.harelavikasis.shulamokshim.MainApp.R;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ScoresTableActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "ShulaMokshim_Settings";

    public final static int EASY = 0;
    public final static int MEDIUM = 1;
    public final static int HARD = 2;

    public final int MAX_NUM_OF_RECORDS = 3;

    @Bind(R.id.easyTable) TableLayout easyTable;
    @Bind(R.id.mediumTable) TableLayout mediumTable;
    @Bind(R.id.hardTable) TableLayout hardTable;
//    TableLayout easyTable = (TableLayout) findViewById(R.id.easyTable);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores_table);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fetchScores();
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

    private void fetchScores() {

        Gson gson = new Gson();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        int size = mSharedPreference.getInt("Status_size", 0);
        for (int j = 0; j < MAX_NUM_OF_RECORDS; j++) {

            String level = setLevel(j);
            for (int i = 0; i < MAX_NUM_OF_RECORDS; i++) {
                String key = level.toString() + i;
                String json = settings.getString(key, "");
                Log.d("uniNote",  "record: " + json + "index: " + i);

                if (json != "") {

                    Score score = gson.fromJson(json, Score.class);
                    Log.d("uniNote",  "record1: " + score.toString());
                    TableRow tableRow = getTableRow(j, i);
                    TableRow.LayoutParams params=new TableRow.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    TableRow.LayoutParams  params1=new TableRow.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,1.0f);
                    TextView txt1 = new TextView(this);
                    txt1.setText(score.toString());
                    txt1.setLayoutParams(params1);
                    tableRow.addView(txt1);
                    tableRow.setLayoutParams(params);
                }
            }
        }
    }

    private TableRow getTableRow(int tableIndex,int rowIndex)
    {
        Log.d("uniNote",  "easyTable.getChildAt(rowIndex):     " + easyTable);
        if (tableIndex == EASY) return (TableRow)easyTable.getChildAt(rowIndex);
        else if (tableIndex == MEDIUM) return (TableRow)mediumTable.getChildAt(rowIndex);
        return (TableRow)hardTable.getChildAt(rowIndex);
    }

    private String setLevel(int index) {
        if (index == EASY) return "easy";
        else if (index == MEDIUM) return "medium";
        return "hard";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
