package com.example.samspc.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static java.lang.Math.floor;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    AnimationDrawable connorAnimation;
    public boolean openMenu;
    public int health,happy,hunger,weight;
    public boolean isSick,isPoop;
    public boolean feedMenu,healthMenu,bathMenu,gamesMenu;
    GameView gameView;
    SharedPreferences sharedPref=null;
    SharedPreferences.Editor editor=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        gameView = new GameView(this);


        setContentView(gameView);

        setContentView(R.layout.activity_main);

        weight=120;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        ImageView connorImage = (ImageView) findViewById(R.id.connor_sprite);
        connorImage.setImageResource(R.drawable.animate_connor_neutral);
        connorAnimation = (AnimationDrawable) connorImage.getDrawable();


        Thread gameViewThread = new Thread(gameView);
        gameViewThread.start();
        connorAnimation.start();
    }





    class GameView extends SurfaceView implements Runnable{

        Thread gameThread = null;
        SurfaceHolder holder;
        volatile boolean playing;
        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;
        Bitmap bitmapConnor;


        public GameView(Context context){
            super(context);
            //ourHolder = getHolder();
            //paint = new Paint();
            playing=true;
            //bitmapConnor= BitmapFactory.decodeResource(this.getResources(),R.drawable.connor);
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }
        public void resume() {
            hunger=sharedPref.getInt("hunger",100);
            health=sharedPref.getInt("health",100);
            happy=sharedPref.getInt("happy",100);
            weight=sharedPref.getInt("weight",140);
            isPoop=sharedPref.getBoolean("isPoop",false);
            isSick=sharedPref.getBoolean("isSick",false);
            playing = true;
            gameThread = new Thread(this);

            gameThread.start();


        }

        public void run(){

            Log.d("tag","running");
            int threadCount=0;
            while(playing){

                if (threadCount%100==0) {
                    doEvents(1);
                }
                if (threadCount%5==0){

                }
                preUpdateValues();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateValues();
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                threadCount++;

            }
        }
        public void doEvents(int hour) {

            for (int i=0;i<hour;i++)
            {
                int sickChance=0;
                isPoop=true;
                //TODO impliment poop
                if (isPoop) {
                    sickChance= 60-health;
                    health -= 10;
                }
                else{
                    sickChance=45-health;
                }
                isSick=false;
                //TODO implimet sick
                if (sickChance>Math.random()*100){
                    isSick=true;
                }
                if (floor(Math.random() * 10) == 0) {
                    isPoop = true;
                }
                if (hunger<50){
                    weight-=2;
                }
                if (hunger<25){
                    weight-=2;
                }
                hunger -= 3;
                happy -= 1;
            }
        }
    }


    public void onPause(View view){
        super.onPause();
        gameView.pause();
    }
    public void onResume(View view){
        super.onResume();
        gameView.resume();
    }

    public void showFoodOptions(View view){

            if (feedMenu) {
                feedMenu=false;
            }
        else{

                feedMenu=true;
                bathMenu=false;
                gamesMenu=false;
                healthMenu=false;
            }
        updateMenu();
    }

    public void showBathOptions(View view){

        if (bathMenu) {
            bathMenu=false;
        }
        else{

            feedMenu=false;
            bathMenu=true;
            gamesMenu=false;
            healthMenu=false;
        }
        updateMenu();
    }
    public void showHealthOptions(View view){

        if (healthMenu) {
            healthMenu=false;
        }
        else{

            feedMenu=false;
            bathMenu=false;
            gamesMenu=false;
            healthMenu=true;
        }
        updateMenu();
    }
    public void showGamesOptions(View view){

        if (gamesMenu) {
            gamesMenu=false;
        }
        else{

            feedMenu=false;
            bathMenu=false;
            gamesMenu=true;
            healthMenu=false;
        }
        updateMenu();
    }
    public void eatMeal(View view){
        hunger+=10;
        happy+=2;
        weight+=5;
    }

    public void eatSnack(View view){
        hunger+=4;
        happy+=8;
        weight+=20;
        health-=10;
        Log.d("tag","atemeal");
    }
    public void eatFruit (View view){
        hunger+=4;
        happy-=1;
        health+=10;
        Log.d("tag","atemeal");
    }

    public void takeVitamin(View view){
        health += 10;
    }
    public void takeMedicine(View view){
        happy-=40;
        health+=80;
        isSick=false;
    }
    public void flush(View view){
        isPoop=false;
    }
    public void preUpdateValues() {
        if (hunger>100){
            hunger=100;
        }
        if (hunger<0){
            hunger=0;
        }
        if (happy>100){
            happy=100;
        }
        if (happy<0) {
            happy = 0;
        }
        if (health >100){
            health=100;
        }
        if (health<0){
            health=0;
        }
        editor.putInt("hunger",hunger);
        editor.putInt("happy",happy);
        editor.putInt("health",health);
        editor.putInt("weight",weight);
        editor.putBoolean("isPoop",isPoop);
        editor.putBoolean("isSick",isSick);
        editor.commit();

    }
    public void updateValues(){
        Resources res = getResources();

        ImageView hungerBar = (ImageView) findViewById(R.id.hunger_bar);

        int hungerResource = res.getIdentifier(getBarResource(hunger) , "drawable", getPackageName());
        hungerBar.setImageResource(hungerResource);

        ImageView happyBar = (ImageView) findViewById(R.id.happiness_bar);

        int happyResource = res.getIdentifier(getBarResource(happy) , "drawable", getPackageName());
        happyBar.setImageResource(happyResource);

        ImageView healthBar = (ImageView) findViewById(R.id.health_bar);

        int healthResource = res.getIdentifier(getBarResource(health) , "drawable", getPackageName());
        healthBar.setImageResource(healthResource);

        ImageView weightBar = (ImageView) findViewById(R.id.weight_bar);

        int weightResource = res.getIdentifier(getBarResource(weight) , "drawable", getPackageName());
        weightBar.setImageResource(weightResource);




//        TextView hungerText = (TextView)findViewById(R.id.text_hunger);
//        hungerText.setText("Hunger: " + hunger);
//        TextView happyText = (TextView)findViewById(R.id.text_happy);
//        happyText.setText("Happiness: " + happy);
//        TextView healthText = (TextView)findViewById(R.id.text_health);
//        healthText.setText("Health: " + health);
        TextView weightText = (TextView)findViewById(R.id.text_weight);
        weightText.setText("Weight: " + weight +"kgs");
    }
    public String getBarResource (int percent){
        if (percent>98){
            return "bar100percent";
        }
        else if  (percent>80){
            return "@bar80percent";
        }
        else if  (percent>60){
            return "bar60percent";
        }
        else if  (percent>40){
            return "bar40percent";
        }
        else if (percent > 20){
            return "bar20percent";
        }
        else {
            return "bar1percent";
        }

    }
    public void doEventsButton(View view){
        connorAnimation.start();
    }
    public void gamePlaceholder(View view){
        happy+=8;
        hunger-=5;
        health+=5;
        weight-=4;
    }
    public void resetGame(View view){
        health=100;
        hunger=100;
        weight=140;
        happy=100;


    }
    public void nothing (View view){

    }
    public void updateMenu(){
        if (feedMenu){
            this.findViewById(R.id.options_food).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.options_food).setVisibility(View.GONE);
        }
        if (healthMenu){
            this.findViewById(R.id.options_health).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.options_health).setVisibility(View.GONE);
        }        if (bathMenu){
            this.findViewById(R.id.options_bath).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.options_bath).setVisibility(View.GONE);
        }        if (gamesMenu){
            this.findViewById(R.id.options_games).setVisibility(View.VISIBLE);
        }else{
            this.findViewById(R.id.options_games).setVisibility(View.GONE);
        }

    }
}
