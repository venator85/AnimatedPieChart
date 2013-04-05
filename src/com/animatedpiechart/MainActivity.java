package com.animatedpiechart;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ViewGroup layout = (ViewGroup) findViewById(R.id.layout);

		final CountDownCircleTimer p = new CountDownCircleTimer(this);
		LayoutParams lp = new LayoutParams(100, 0);
		p.setLayoutParams(lp);
		layout.addView(p);

		findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p.setPaintColor(0xfff7a404);
				p.setInnerCircleRadiusPercentage(0.7f);
//				p.setTimerListener(new TimerListener() {
//					@Override
//					public void onTick(CountDownCircleTimer timerView, long secondsLeft) {
//						Log.e("TICK", "> TICK " + secondsLeft);
//					}
//				});
				p.start(5000);
			}
		});
		
		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p.stop();
			}
		});
	}
}
