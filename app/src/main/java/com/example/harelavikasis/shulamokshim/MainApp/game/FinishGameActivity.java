package com.example.harelavikasis.shulamokshim.MainApp.game;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harelavikasis.shulamokshim.MainApp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinishGameActivity extends AppCompatActivity {

    private boolean isFinish;

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.finish_caption)
    TextView finishCaption;
    @OnClick(R.id.back)
    public void onBackClick(View v) {
        // TODO: 11/12/2016 implement
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        ButterKnife.bind(this);
        getExtras();
        initUI();
    }

    private void getExtras() {
        isFinish = getIntent().getBooleanExtra(GameActivity.KEY_FINISH, false);
    }

    private void initUI() {
        if (isFinish) {
            // finished
            imageView.setImageResource(R.drawable.ic_thumb_up);
            finishCaption.setText(R.string.won_text);
        } else {
            imageView.setImageResource(R.drawable.ic_thumb_down);
            finishCaption.setText(R.string.loss_text);
            finishCaption.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
