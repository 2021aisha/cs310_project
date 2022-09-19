package com.hfad.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {


    public TextView gameLost;
    public TextView gameWon;
    public Button playAgain;
    public int gameResult;
    public int secondsCount;

    public String secondsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Intent intent = getIntent();
        gameResult = intent.getExtras().getInt("gameStatus", 0);
        secondsCount = intent.getExtras().getInt("seconds", 0);

        secondsString = String.valueOf(secondsCount);

        gameLost = (TextView) findViewById(R.id.gamelost);

        gameWon = (TextView) findViewById(R.id.gamewon);

        playAgain = (Button) findViewById(R.id.playagain);

        playAgain.setOnClickListener(this::onClickPlayAgain);

        playAgain.setVisibility(View.VISIBLE);
        if(gameResult==1) {

            gameLost.setVisibility(View.INVISIBLE);

            gameWon.setText("Used "+ secondsString + " seconds.\n You won.\n Good job!");

            gameWon.setVisibility(View.VISIBLE);

            playAgain.setVisibility(View.VISIBLE);
        }
        else{

            gameLost.setText("Used "+ secondsString + " seconds.\n You lost.\n Nice try!");

            gameLost.setVisibility(View.VISIBLE);

            gameWon.setVisibility(View.INVISIBLE);

            playAgain.setVisibility(View.VISIBLE);
        }
    }


    public void onClickPlayAgain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }




}