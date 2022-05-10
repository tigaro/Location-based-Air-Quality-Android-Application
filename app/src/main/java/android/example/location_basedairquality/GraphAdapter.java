package android.example.location_basedairquality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.GraphHolder> {
	
	private ArrayList<LineGraphSeries<DataPoint>> lineGraphSeriesArrayList;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm:ss");
	LineGraphSeries currentGraph;
	Context context;
	
	@NonNull
	@Override
	public GraphAdapter.GraphHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View graphView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.graph_item, parent, false);
		
		return new GraphAdapter.GraphHolder(graphView);
	}
	
	public GraphAdapter(Context context, ArrayList<LineGraphSeries<DataPoint>> lineGraphSeries){
		this.lineGraphSeriesArrayList = lineGraphSeries;
		this.context = context;
	}
	
	@Override
	public void onBindViewHolder(@NonNull GraphHolder holder, int position) {
	
		currentGraph = lineGraphSeriesArrayList.get(position);
		
		holder.graphView.addSeries(currentGraph);
		holder.graphView
				.getGridLabelRenderer()
				.setLabelFormatter(new DateAsXAxisLabelFormatter(context, simpleDateFormat));
		
		holder.graphView.getViewport().setScalable(true);
		holder.graphView.getViewport().setMinX(currentGraph.getLowestValueX());
		holder.graphView.getViewport().setMaxX(currentGraph.getHighestValueX());
		holder.graphView.getViewport().setMinY(currentGraph.getLowestValueY());
		holder.graphView.getViewport().setMaxY(currentGraph.getHighestValueY());
		holder.graphView.getViewport().setYAxisBoundsManual(true);
		holder.graphView.getViewport().setXAxisBoundsManual(true);
		holder.graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);
		holder.graphView.setTitle(currentGraph.getTitle());
		holder.graphView.getGridLabelRenderer().setHumanRounding(false);
	}
	
	@Override
	public int getItemCount() {
		return lineGraphSeriesArrayList.size();
	}
	
	class GraphHolder extends RecyclerView.ViewHolder {
		private GraphView graphView;
		public GraphHolder(@NonNull View itemView) {
			super(itemView);
			graphView = itemView.findViewById(R.id.GraphView);
		}
	}
}