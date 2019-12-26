package uz.suhrob.eslatmalar;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ImageView suhrobLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        suhrobLogoView = findViewById(R.id.suhrob_logo_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 4000);

    }

    @Override
    public void onStart() {
        super.onStart();
        suhrobLogoView.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                suhrobLogoView.setVisibility(View.VISIBLE);
                AnimatedVectorDrawable anim = (AnimatedVectorDrawable) suhrobLogoView.getDrawable();
                anim.start();
            }
        }, 500);
    }
}
