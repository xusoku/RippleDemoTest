package x.rippledemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    LocalMapView aroundmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aroundmap= (LocalMapView) findViewById(R.id.aroundmap);

        aroundmap.startQueryingAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aroundmap.stopQueryingAnimation(false);
            }
        },3000);
    }
}
