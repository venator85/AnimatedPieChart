package com.animatedpiechart;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Random r = new Random();
		float[] data = new float[2 + r.nextInt(3)];
		for (int i = 0; i < data.length; i++) {
			data[i] = r.nextFloat();
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

		final PieChart p = new PieChart(this, data);
		LayoutParams lp = new LayoutParams(400, 400);
		p.setLayoutParams(lp);
		p.setBackgroundColor(0xffffffff);
		layout.addView(p);

		findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p.anima();
			}
		});
	}
}
