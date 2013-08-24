package com.advait.saravade.treemapindia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IntroSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_intro_splash);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		TextView treemapindia = (TextView) findViewById(R.id.treemapindia);
		TextView treemapindia2 = (TextView) findViewById(R.id.treemapindia2);
		TextView treemapindia3 = (TextView) findViewById(R.id.treemapindia3);
		Typeface type2 = Typeface.createFromAsset(getAssets(),"GROBOLD.ttf"); 
		treemapindia.setTypeface(type2);
		treemapindia2.setTypeface(type2);
		treemapindia3.setTypeface(type2);
		Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.my_tween_anim);
		myAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Animation myAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_tween_anim2);
				ImageView promoterlogo = (ImageView) findViewById(R.id.promoter_logo);
				promoterlogo.startAnimation(myAnim2);
				myAnim2.setAnimationListener(new AnimationListener(){
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClassName("com.advait.saravade.treemapindia", "com.advait.saravade.treemapindia.Main");
						startActivity(intent);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

		});
		treemapindia.startAnimation(myAnim);
		treemapindia2.startAnimation(myAnim);
		treemapindia3.startAnimation(myAnim);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_intro_splash, menu);
		return true;
	}

}
