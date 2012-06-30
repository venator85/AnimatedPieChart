package com.animatedpiechart;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.animatedpiechart.PieChart.OnSliceClickListener;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Random random = new Random();

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

		final PieChart p = new PieChart(this);
		LayoutParams lp = new LayoutParams(400, 400);
		p.setLayoutParams(lp);
		p.setBackgroundColor(0xffffffff);
		p.setOnSliceClickListener(new OnSliceClickListener() {
			@Override
			public void onSliceClicked(PieChart pieChart, int sliceNumber) {
				Toast.makeText(MainActivity.this, "slice: " + sliceNumber, Toast.LENGTH_SHORT).show();
			}
		});
		layout.addView(p);

		findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float[] data = new float[2 + random.nextInt(3)];
				for (int i = 0; i < data.length; i++) {
					data[i] = random.nextFloat();
				}
				p.setSlices(data);
				p.anima();
			}
		});
	}
}
