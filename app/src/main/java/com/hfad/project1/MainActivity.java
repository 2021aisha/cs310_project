package com.hfad.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kotlin.random.Random;

public class MainActivity extends AppCompatActivity {

    public int gameStatus = -1; //0 is lost, 1 if won

    private int clockCount = 0;
    private boolean running = false;

    private Activity activity;

    private static final int COLUMN_COUNT = 2;

    public boolean modeGame = false;

    public boolean clickAgain = false;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private ArrayList<String> mineNeighborCount = new ArrayList<String>();

    public ArrayList<Integer> checkNeighbors = new ArrayList<Integer>();

    public ArrayList<Boolean> alreadyVisited = new ArrayList<Boolean>();

    //public TextView digMode;
    //public TextView flagMode;

    public GridLayout grid;
    public TextView gameLost;
    public TextView clock;
    public TextView flag;
    public TextView flagCount;
    public TextView gameWon;
    public Button playAgain;

    public TextView mode;

    int clickedCells = 0;

    int countFlag = 4;


    //for play again try restarting activity

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            clockCount = savedInstanceState.getInt("clockCount");
            running = savedInstanceState.getBoolean("running");
        }
        clockCount = 0;
        running = true;
        runTimer();



        clock = (TextView) findViewById(R.id.top);

        clock.setText(R.string.clock);

        flag = (TextView) findViewById(R.id.flag);
        flag.setText(R.string.flag);
        flag.setId(2);

        flagCount = (TextView) findViewById(R.id.flagcount);
        flagCount.setText(String.valueOf(countFlag));

       /* flagMode = (TextView) findViewById(R.id.flagmode);
        flagMode.setText(R.string.flag);
        flagMode.setId(0);
        flagMode.setVisibility(View.INVISIBLE);
        flagMode.setOnClickListener(this::onClickMode);
        //flagMode.setTextColor(Color.TRANSPARENT);

        digMode = (TextView) findViewById(R.id.digmode);
        digMode.setText(R.string.pick);

        digMode.setId(1);
        digMode.setVisibility(View.VISIBLE);
        digMode.setOnClickListener(this::onClickMode);
        */
        mode = (TextView) findViewById(R.id.mode);
      //  mode.setId(0);
        modeGame = false;
        mode.setText(R.string.pick);
        mode.setVisibility(View.VISIBLE);
        mode.setOnClickListener(this::onClickMode);

        gameLost = (TextView) findViewById(R.id.gamelost);

        gameWon = (TextView) findViewById(R.id.gamewon);

        playAgain = (Button) findViewById(R.id.playagain);
        //playAgain.setOnClickListener(this::onClickPlayAgain);

        for(int i=0; i<80; i++){
            mineNeighborCount.add(" ");
           // checkNeighbors.add(-1);
            alreadyVisited.add(false);
        }
        cell_tvs = new ArrayList<TextView>();


        // Method (2): add four dynamically created cells
        //GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        grid = (GridLayout) findViewById(R.id.gridLayout01);

        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(40) ); //64
                tv.setWidth( dpToPixel(40) ); //64
                tv.setTextSize( 32 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.parseColor("lime"));
                tv.setOnClickListener(this::onClickTV);
                tv.setId(8);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }



        Mines();




    }




    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    @SuppressLint("ResourceType")
    public void onClickTV(View view){

        System.out.println(view.getId());
        System.out.println("GameStatus: " + gameStatus);

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        //tv.setText(String.valueOf(i)+String.valueOf(j));


        if(gameStatus==1 || gameStatus==0){
            System.out.println("apple");
            clickAgain = true; //i.e. they click the cell to show results page
        }

        if(gameStatus==0) {
            if (clickAgain) {
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("gameStatus", gameStatus);
                intent.putExtra("seconds", clockCount);
                startActivity(intent);
            }
        }


         else if(clickAgain==false && modeGame==true && tv.getId()==5 && tv.getCurrentTextColor()==Color.CYAN){
            tv.setText(R.string.flag);
            countFlag--;
            tv.setTextColor(Color.MAGENTA);
            //tv.setId(9);
            flagCount.setText(String.valueOf(countFlag));
        }

        else if(clickAgain==false && modeGame==false && tv.getCurrentTextColor()==Color.MAGENTA){

        }

         else if(clickAgain==false && modeGame==true && tv.getId()==5 && tv.getCurrentTextColor()==Color.MAGENTA){
             tv.setText("");
             countFlag++; //do we bring flag out up or leave it?
             tv.setTextColor(Color.CYAN);
             flagCount.setText(String.valueOf(countFlag));
         }

        else if(clickAgain==false && tv.getId()==5 && modeGame==false){

             int minesRevealed = 0;
             int index=0;

             while(minesRevealed!=4){
                 if(cell_tvs.get(index).getId()==5){
                     cell_tvs.get(index).setText(R.string.mine);
                     minesRevealed++;
                 }
                 index++;
             }

             for(int z=0; z<80; z++){ //set all cells background to grey for win or lose
                 cell_tvs.get(z).setBackgroundColor(Color.LTGRAY);
                 cell_tvs.get(z).setTextColor(Color.GRAY);
                 
             }

             running = false;


            gameStatus = 0;

            System.out.print("LOST");



            if(clickAgain) {
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("gameStatus", gameStatus);
                intent.putExtra("seconds", clockCount);
                startActivity(intent);
            }

           /* grid.setVisibility(View.INVISIBLE);
            gameLost.setVisibility(View.VISIBLE);
            gameWon.setVisibility(View.INVISIBLE);
            flagCount.setVisibility(View.INVISIBLE);
            flag.setVisibility(View.INVISIBLE);
            clock.setVisibility(View.INVISIBLE);
            digMode.setVisibility(View.INVISIBLE);
            flagMode.setVisibility(View.INVISIBLE);
            playAgain.setVisibility(View.VISIBLE);*/
        }

        /*else if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.GRAY);

            if(tv.getText().equals(R.string.mine)){
                tv.setTextSize(50);
            }

        }*/

       /* else if(tv.getCurrentTextColor() == Color.TRANSPARENT){
            tv.setTextColor(Color.RED);
        }*/

        /*else if(view.getId()==2){ // putting flag on cells
            tv.setText(R.string.flag);
        }*/

        else if(clickAgain==false && modeGame==true && tv.getId()==8 && tv.getCurrentTextColor()!=Color.GRAY){
            tv.setText(R.string.flag);
            countFlag--;
            tv.setId(9);
            flagCount.setText(String.valueOf(countFlag));
        }
        else if(clickAgain==false && modeGame==true && tv.getId()==5 && tv.getCurrentTextColor()==Color.MAGENTA){
            tv.setText(R.string.flag);
            countFlag--;
            tv.setTextColor(Color.MAGENTA);
            //tv.setId(9);

            flagCount.setText(String.valueOf(countFlag));
        }

        else if(clickAgain==false && modeGame==true && tv.getId()==9){
            tv.setText(mineNeighborCount.get(n));
            countFlag++; //do we bring flag out up or leave it?
            tv.setId(8);
            flagCount.setText(String.valueOf(countFlag));
        }
       /* else if(mode.getId()==1 && (tv.getId()==9 || tv.getId()==5)){
            if(tv.getId()==5)){
                if(tv.getText()==R.string.flag){
                    tv.setText("");
                }
            }



            tv.setText("");
            countFlag++; //do we bring flag out up or leave it?
            tv.setId(8);
            flagCount.setText(String.valueOf(countFlag));
        }

        */

        else if(modeGame==false && (tv.getId()!=9) && tv.getId()!=5) {


            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);

            int parent = n;


            checkNeighbors.add(n);


            System.out.println("wow");
            System.out.println(checkNeighbors.get(0));

            while (checkNeighbors.isEmpty() == false) {
                revealCells(checkNeighbors.get(0));
                //System.out.println("index: " + checkNeighbors.get(0));
            }


            System.out.println("wow2");


           /*  if(clickedAllCells()==true){
                   /* grid.setVisibility(View.INVISIBLE);
                    gameLost.setVisibility(View.INVISIBLE);
                    gameWon.setVisibility(View.VISIBLE);
                    flagCount.setVisibility(View.INVISIBLE);
                    flag.setVisibility(View.INVISIBLE);
                    clock.setVisibility(View.INVISIBLE);
                    digMode.setVisibility(View.INVISIBLE);
                    flagMode.setVisibility(View.INVISIBLE);
                    playAgain.setVisibility(View.VISIBLE);



                 int minesRevealed = 0;
                 int index=0;

                 while(minesRevealed!=4){
                     if(cell_tvs.get(index).getId()==5){
                         cell_tvs.get(index).setText(R.string.mine);
                         minesRevealed++;
                     }
                     index++;
                 }
                 gameStatus = 1;



                 Intent intent = new Intent(this, ResultsActivity.class);
                 intent.putExtra("gameStatus", gameStatus);
                 intent.putExtra("seconds", clockCount);

                 startActivity(intent);

             }
        */


            if (clickedAllCells() == true) {
                   /* grid.setVisibility(View.INVISIBLE);
                    gameLost.setVisibility(View.INVISIBLE);
                    gameWon.setVisibility(View.VISIBLE);
                    flagCount.setVisibility(View.INVISIBLE);
                    flag.setVisibility(View.INVISIBLE);
                    clock.setVisibility(View.INVISIBLE);
                    digMode.setVisibility(View.INVISIBLE);
                    flagMode.setVisibility(View.INVISIBLE);
                    playAgain.setVisibility(View.VISIBLE);
                    */

                int minesRevealed = 0;
                int index = 0;

                while (minesRevealed != 4) {
                    if (cell_tvs.get(index).getId() == 5) {
                        cell_tvs.get(index).setText(R.string.mine);
                        cell_tvs.get(index).setBackgroundColor(Color.LTGRAY);
                        minesRevealed++;
                    }
                    index++;
                }

                for (int z = 0; z < 80; z++) { //set all cells background to grey for win or lose
                    cell_tvs.get(z).setBackgroundColor(Color.LTGRAY);
                    cell_tvs.get(z).setTextColor(Color.GRAY);
                }


                running = false;


                gameStatus = 1;


                if (clickAgain) {
                    Intent intent = new Intent(this, ResultsActivity.class);
                    intent.putExtra("gameStatus", gameStatus);
                    intent.putExtra("seconds", clockCount);

                    startActivity(intent);
                }

            }
        }
    }



    @SuppressLint("ResourceType")
    public void revealCells(int n){
        boolean coastClear = true;

        boolean allNeighborsChecked = false;

        if(alreadyVisited.get(n)==true){
            System.out.println("removed");
            checkNeighbors.remove(0);
            return;
        }

        //if all adjacent neighbors don't have mines, reveal them

        while(!allNeighborsChecked) {
            if (((n % 8) != 7) && cell_tvs.get(n + 1).getId() == 5) { //right side neighbor
                coastClear = false;
                break;
            }

            if (((n%8)!=0) && cell_tvs.get(n-1).getId() == 5) { //left side neighbor
                coastClear = false;
                break;
            }

            if(n>7 && cell_tvs.get(n-8).getId()==5){ //top neighbor
                coastClear = false;
                break;
            }

            if (n<=70 && cell_tvs.get(n+8).getId() == 5) { //bottom neighbor
                coastClear = false;
                break;
            }
            if (n<=70 && ((n%8)!=7) && cell_tvs.get(n+9).getId() == 5) { //bottom right neighbor
                coastClear=false;

                break;
            }
            if (n<=70 && ((n%8)!=0) && cell_tvs.get(n+7).getId() == 5) { //bottom left neighbor
                coastClear=false;

                break;
            }
            if (n>=9 && ((n%8)!=0) &&cell_tvs.get(n-9).getId() == 5) { //top left neighbor
                coastClear=false;

                break;
            }
            if (n>=9 && ((n%8)!=7) &&cell_tvs.get(n-7).getId() == 5) { //top right neighbor
                coastClear=false;

                break;
            }

            allNeighborsChecked = true;

            if(coastClear){

                if(((n%8)!=7)){
                    checkNeighbors.add(n+1);
                    cell_tvs.get(n + 1).setTextColor(Color.GRAY);
                    cell_tvs.get(n + 1).setBackgroundColor(Color.LTGRAY);
                }

                if(((n%8)!=0)){
                    checkNeighbors.add(n-1);
                    cell_tvs.get(n - 1).setTextColor(Color.GRAY);
                    cell_tvs.get(n - 1).setBackgroundColor(Color.LTGRAY);
                }

                if(n>7){
                    checkNeighbors.add(n-8);
                    cell_tvs.get(n - 8).setTextColor(Color.GRAY);
                    cell_tvs.get(n - 8).setBackgroundColor(Color.LTGRAY);
                }

                if(n<=70){
                    checkNeighbors.add(n+8);
                    cell_tvs.get(n + 8).setTextColor(Color.GRAY);
                    cell_tvs.get(n + 8).setBackgroundColor(Color.LTGRAY);
                }

                if(n<=70 && ((n%8)!=7)){
                    checkNeighbors.add(n+9);
                    cell_tvs.get(n + 9).setTextColor(Color.GRAY);
                    cell_tvs.get(n + 9).setBackgroundColor(Color.LTGRAY);
                }

                if(n<=70 && ((n%8)!=0)) {
                    checkNeighbors.add(n+7);
                    cell_tvs.get(n + 7).setTextColor(Color.GRAY);
                    cell_tvs.get(n + 7).setBackgroundColor(Color.LTGRAY);
                }

                if(n>=9 && ((n%8)!=0)) {
                    checkNeighbors.add(n-9);
                    cell_tvs.get(n - 9).setTextColor(Color.GRAY);
                    cell_tvs.get(n - 9).setBackgroundColor(Color.LTGRAY);
                }

                if(n>=9 && ((n%8)!=7)) {
                    checkNeighbors.add(n-7);
                    cell_tvs.get(n - 7).setTextColor(Color.GRAY);
                    cell_tvs.get(n - 7).setBackgroundColor(Color.LTGRAY);
                }
            }




        }

        checkNeighbors.remove(0);
        alreadyVisited.add(n, true);
    }

    @SuppressLint("ResourceType")
    public void onClickMode(View view){
        TextView mode = (TextView) view;
        if(modeGame==false){ //flagMode
            //flagMode.setTextColor(Color.TRANSPARENT);
            //digMode.setTextColor(Color.alpha(255));
           // flagMode.setVisibility(View.INVISIBLE);
           // digMode.setVisibility(View.VISIBLE);

            mode.setText(R.string.flag);
            modeGame = true;
            //mode.setId();

        }
        else if(modeGame==true){
            //flagMode.setTextColor(Color.GREEN);
            // digMode.setTextColor(Color.TRANSPARENT);
        //    flagMode.setVisibility(View.VISIBLE);
         //   digMode.setVisibility(View.INVISIBLE);

            mode.setText(R.string.pick);
           // mode.setId(0);
            modeGame = false;
        }
    }



    @SuppressLint("ResourceType")
    public boolean clickedAllCells(){
        int clickedCells = 0;

       /* if(clickedCells==1){
            //runTimer();
        }*/
        for(int i=0; i<80; i++){
            if(cell_tvs.get(i).getId()!=5 && cell_tvs.get(i).getCurrentTextColor()==Color.GRAY){
                clickedCells++;
            }
        }

        if(clickedCells==76){
            return true;
        }

        System.out.println(clickedCells);

        return false;
    }

    @SuppressLint("ResourceType")
    public void Mines() {

        // mine = (ImageView) findViewById(R.id.mine);


        int index = -1;

        int mineCount = 0;
        while(mineCount<4) {
            index = Random.Default.nextInt(9);

            System.out.println(index);

            while (cell_tvs.get(index).getId()==5) {
                index = Random.Default.nextInt(9);
                System.out.println(index);
            }

            mineCount++;

            cell_tvs.get(index).setText("");
            //cell_tvs.get(index).setText(R.string.mine);
            // System.out.println(cell_tvs.get(index).getText());

            cell_tvs.get(index).setTextColor(Color.CYAN);
            cell_tvs.get(index).setId(5);


            Neighbors();




        }
    }

    @SuppressLint("ResourceType")
    public void Neighbors(){
        //visited notes
        //start w/ node x, check its neighbor then check that nodes neighbor and so on, if node does not have neighbor back track until you get to node that does have unvisited neighbor

        int index = 0;
        int mineNeighbors = 0;

        //0, 8, 16, 24, 32, 40, 48, 56, 64, 72
        for(index=0; index<=72; index+=8) {
            if ((index % 8 == 0) && cell_tvs.get(index).getId() != 5) { //0s neighbors are 1, 8, 9

                if (cell_tvs.get(index+1).getId() == 5) { //right side neighbor
                    mineNeighbors++;
                }
                if(index!=0 && cell_tvs.get(index-8).getId()==5){ //top neighbor
                    mineNeighbors++;
                }
                if (index!=72 && cell_tvs.get(index+8).getId() == 5) { //bottom neighbor
                    mineNeighbors++;
                }
                if (index!=72 && cell_tvs.get(index+9).getId() == 5) { //bottom right neighbor
                    mineNeighbors++;
                }
                if(index!=0 && cell_tvs.get(index-7).getId()==5 ){ //top right neigbor
                    mineNeighbors++;
                }

                cell_tvs.get(index).setText(String.valueOf(mineNeighbors));
                cell_tvs.get(index).setTextColor(Color.parseColor("lime"));
                mineNeighborCount.set(index,String.valueOf(mineNeighbors));
                mineNeighbors = 0;
            }
        }

        //7, 15, 23, 31, 39, 47, 55, 63, 71, 79
        for(index=7; index<=79; index+=8) {
            if ((index % 8 == 7) && cell_tvs.get(index).getId() != 5) {

                if (cell_tvs.get(index-1).getId() == 5) { //left side neighbor
                    mineNeighbors++;
                }
                if(index!=7 && cell_tvs.get(index-8).getId()==5){ //top neighbor
                    mineNeighbors++;
                }
                if (index!=79 && cell_tvs.get(index+8).getId() == 5) { //bottom neighbor
                    mineNeighbors++;
                }
                if (index!=79 && cell_tvs.get(index+7).getId() == 5) { //bottom left neighbor
                    mineNeighbors++;
                }
                if(index!=7 && cell_tvs.get(index-9).getId()==5 ){ //top left neigbor
                    mineNeighbors++;
                }

                cell_tvs.get(index).setText(String.valueOf(mineNeighbors));
                cell_tvs.get(index).setTextColor(Color.parseColor("lime"));
                mineNeighborCount.set(index,String.valueOf(mineNeighbors));
                mineNeighbors = 0;
            }
        }

        //1, 2, 3, 4, 5, 6; 9, 10, 11, 12, 13, 14; 17, 18, 19, 20, 21, 22;...; 73, 74, 75, 76, 77, 78

        int jumpThree = 0;
        for(index=1; index<=78; index++){
            if (cell_tvs.get(index).getId() != 5) {

                if (cell_tvs.get(index-1).getId() == 5) { //left side neighbor
                    mineNeighbors++;
                }
                if (cell_tvs.get(index+1).getId() == 5) { //right side neighbor
                    mineNeighbors++;
                }
                if(index>=9 && cell_tvs.get(index-8).getId()==5){ //top neighbor
                    mineNeighbors++;
                }
                if (index<=70 && cell_tvs.get(index+8).getId() == 5) { //bottom neighbor
                    mineNeighbors++;
                }
                if (index<=70 && cell_tvs.get(index+9).getId() == 5) { //bottom right neighbor
                    mineNeighbors++;
                }
                if (index<=70 && cell_tvs.get(index+7).getId() == 5) { //bottom left neighbor
                    mineNeighbors++;
                }
                if (index>=9 && cell_tvs.get(index-9).getId() == 5) { //top left neighbor
                    mineNeighbors++;
                }
                if (index>=9 && cell_tvs.get(index-7).getId() == 5) { //top right neighbor
                    mineNeighbors++;
                }


                cell_tvs.get(index).setText(String.valueOf(mineNeighbors));
                cell_tvs.get(index).setTextColor(Color.parseColor("lime"));
                mineNeighborCount.set(index,String.valueOf(mineNeighbors));
                mineNeighbors = 0;
            }

            jumpThree++;

            if(jumpThree==6){
                jumpThree=0;
                index+=2; //add 1 in for loop so it adds 3
            }
        }





    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clockCount", clockCount);
        savedInstanceState.putBoolean("running", running);
    }

    public void onClickStart(View view) {
        running = true;
    }

    public void onClickStop(View view) {
        running = false;
    }
    public void onClickClear(View view) {
        running = false;
        clockCount = 0;
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textView);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
               // int hours =clockCount/3600;
               // int minutes = (clockCount%3600) / 60;

               //
                // int seconds = clockCount%60;
                //String time = String.format("%d:%02d:%02d", hours, minutes, seconds);
               //
                // String time = String.format("%02d",seconds);
                String time = String.format("%03d",clockCount);
                timeView.setText(time);

                if (running) {
                    clockCount++;
                }


                handler.postDelayed(this, 1000);
            }
        });
    }

    private void delayTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textView);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 30000); //30 seconds
            }
        });
    }
}




//finished version 5
//lost game fixed





