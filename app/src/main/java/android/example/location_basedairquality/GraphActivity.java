package android.example.location_basedairquality;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jjoe64.graphview.series.DataPoint;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
	
	private NodeViewModel nodeViewModel;
	private Graph graph = new Graph();
	private GraphAdapter adapter;
	private RecyclerView recyclerView;
	private SwipeListener swipeListener;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.graph_activity);
		
		recyclerView = findViewById(R.id.graph_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		
		Intent intent = getIntent();
		nodeViewModel = ViewModelProviders.of(this).get(NodeViewModel.class);
		nodeViewModel.getSingleNodeByKey(intent.getIntExtra("ID", 0)).observe(this, node -> {
			Log.e("Test", node.getBleID());
			//double gas = node.getGas();
			graph.setTemperature(new DataPoint(new Date().getTime(), node.getTemperature()));
			graph.setGas(new DataPoint(new Date().getTime(), node.getGas()));
			graph.setRssi(new DataPoint(new Date().getTime(), node.getRSSI()));
			graph.setHumidity(new DataPoint(new Date().getTime(), node.getHumidity()));
		});
		
		adapter = new GraphAdapter(this, graph.getLineGraphSeriesArrayList());
		recyclerView.setAdapter(adapter);
		
		swipeListener = new SwipeListener(recyclerView);
	}
	
	private class SwipeListener implements View.OnTouchListener {
		GestureDetector gestureDetector;
		
		SwipeListener(View view) {
			int threshhold = 100;
			int velocity_threshhold = 100;
			
			GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDown(MotionEvent e) {
					return true;
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
					try {
						float xDiff = e2.getX() - e1.getX();
						float yDiff = e2.getY() - e2.getY();
						if (Math.abs(xDiff) > Math.abs(yDiff)) {
							if (Math.abs(xDiff) > threshhold && Math.abs(velocityX) > velocity_threshhold) {
								if (xDiff > 0) {
									finish();
								}
							}
						}
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			};
			gestureDetector = new GestureDetector(listener);
			view.setOnTouchListener(this);
		}
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			return gestureDetector.onTouchEvent(motionEvent);
		}
	}
}
