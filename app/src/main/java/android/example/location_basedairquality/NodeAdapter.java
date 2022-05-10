package android.example.location_basedairquality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NodeAdapter extends ListAdapter<Node, NodeAdapter.NodeHolder>{
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm:ss");
	private onItemClickListener listener;
	int numberOfItemsShown = new Integer(0);
	OrderClass order;
	
	protected NodeAdapter(int numberOfItemsShown, OrderClass order) {
		super(DIFF_CALLBACK);
		this.numberOfItemsShown = numberOfItemsShown;
		this.order = order;
	}
	protected NodeAdapter(OrderClass order) {
		super(DIFF_CALLBACK);
		this.order = order;
	}
	
	@Override
	protected Node getItem(int position) {
		return super.getItem(position);
	}
	
	private static final DiffUtil.ItemCallback<Node> DIFF_CALLBACK = new DiffUtil.ItemCallback<Node>() {
		@Override
		public boolean areItemsTheSame(@NonNull Node oldItem, @NonNull Node newItem) {
			return oldItem.getId() == newItem.getId();
		}
		
		@Override
		public boolean areContentsTheSame(@NonNull Node oldItem, @NonNull Node newItem) {
			return  oldItem.getGas() == newItem.getGas() &&
					oldItem.getBleID().equals(newItem.getBleID()) &&
					oldItem.getAltitude() == newItem.getAltitude() &&
					oldItem.getHumidity() == newItem.getHumidity() &&
					oldItem.getNodeID() == newItem.getNodeID() &&
					oldItem.getPressure() == newItem.getPressure() &&
					oldItem.getTemperature() == newItem.getTemperature() &&
					oldItem.getRSSI() == newItem.getRSSI();
		}
	};
	
	@NonNull
	@Override
	public NodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.node_item, parent, false);
		
		return new NodeHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull NodeHolder holder, int position) {
		if(numberOfItemsShown != 0){
			position = 0;
			holder.imageViewPopupMenu.setOnClickListener(view -> {
				PopupMenu popupMenu = new PopupMenu(holder.context, view);
				MenuInflater inflater = popupMenu.getMenuInflater();
				inflater.inflate(R.menu.recyclerview_singlemosttop_menu, popupMenu.getMenu());
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(menuItem -> {
					order.setIsAscDesc("DESC");
					switch (menuItem.getItemId()) {
						case R.id.item_closestNode:
							order.setIsAscDesc("ASC");
							order.setOrderType("rssi");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_highestGas:
							order.setOrderType("gas");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_highestHumidity:
							order.setOrderType("humidity");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_highestPressure:
							order.setOrderType("pressure");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_highestTemperature:
							order.setOrderType("temperature");
							order.setOrderList(order.getOrderList());
							return true;
						default:
							return false;
					}
				});
				
			});
		}else{
			holder.imageViewPopupMenu.setOnClickListener(view -> {
				PopupMenu popupMenu = new PopupMenu(holder.context, view);
				MenuInflater inflater = popupMenu.getMenuInflater();
				inflater.inflate(R.menu.recyclerview_allnodes_menu, popupMenu.getMenu());
				popupMenu.show();
				popupMenu.setOnMenuItemClickListener(menuItem -> {
					switch (menuItem.getItemId()) {
						case R.id.item_temperatureASC:
							order.setOrderType("temperature");
							order.setIsAscDesc("ASC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_temperatureDESC:
							order.setOrderType("temperature");
							order.setIsAscDesc("DESC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_humidityASC:
							order.setOrderType("humidity");
							order.setIsAscDesc("ASC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_humidityDESC:
							order.setOrderType("humidity");
							order.setIsAscDesc("DESC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_pressureASC:
							order.setOrderType("pressure");
							order.setIsAscDesc("ASC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_pressureDESC:
							order.setOrderType("pressure");
							order.setIsAscDesc("DESC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_rssiASC:
							order.setOrderType("rssi");
							order.setIsAscDesc("ASC");
							order.setOrderList(order.getOrderList());
							return true;
						case R.id.item_rssiDESC:
							order.setOrderType("rssi");
							order.setIsAscDesc("DESC");
							order.setOrderList(order.getOrderList());
							return true;
						default:
							return false;
					}
				});
				
			});
		}
		Node currentNode = getItem(position);
		holder.textViewNodeID.setText(String.valueOf(
				currentNode.getNodeID()));
		holder.textViewTemperature.setText(Double.toString(
				currentNode.getTemperature()) + " °C");
		holder.textViewHumidity.setText(Double.toString(
				currentNode.getHumidity()) + " %");
		holder.textViewAltitude.setText(Double.toString(
				currentNode.getAltitude()) + " m");
		holder.textViewGas.setText(Double.toString(
				currentNode.getGas()) + " kΩ");
		holder.textViewPressure.setText(Double.toString(
				currentNode.getPressure()) + " mPa");
		holder.textViewBLEID.setText(String.valueOf(
				currentNode.getBleID()));
		holder.textViewRSSI.setText(String.valueOf(
				currentNode.getRSSI()));
		holder.textViewNodelastSeen.setText(simpleDateFormat.
				format(new Date((long) currentNode.getLastNodeUpdate())));
		holder.textViewBLElastSeen.setText(simpleDateFormat.
				format(new Date((long) currentNode.getLastSeenBLE())));
	}
	
	public Node getNodeAt(int position) {
		return getItem(position);
	}
	
	class NodeHolder extends RecyclerView.ViewHolder {
		
		private TextView textViewNodeID;
		private TextView textViewTemperature;
		private TextView textViewHumidity;
		private TextView textViewAltitude;
		private TextView textViewGas;
		private TextView textViewPressure;
		private TextView textViewBLEID;
		private TextView textViewRSSI;
		private TextView textViewBLElastSeen;
		private TextView textViewNodelastSeen;
		private ImageView imageViewPopupMenu;
		
		private final Context context;
		
		public NodeHolder(@NonNull View itemView) {
			super(itemView);
			
			context = itemView.getContext();
			textViewNodeID = itemView.findViewById(R.id.text_view_nodeID);
			textViewTemperature = itemView.findViewById(R.id.text_view_temperature);
			textViewHumidity = itemView.findViewById(R.id.text_view_humidity);
			textViewAltitude = itemView.findViewById(R.id.text_view_altitude);
			textViewGas = itemView.findViewById(R.id.text_view_gas);
			textViewPressure = itemView.findViewById(R.id.text_view_pressure);
			textViewBLEID = itemView.findViewById(R.id.text_view_bleID);
			textViewRSSI = itemView.findViewById(R.id.text_view_RSSI);
			textViewBLElastSeen = itemView.findViewById(R.id.text_view_UpdateBLE);
			textViewNodelastSeen = itemView.findViewById(R.id.text_view_UpdateNode);
			imageViewPopupMenu = itemView.findViewById(R.id.menuIconRecyclerview);
			
			itemView.setOnClickListener(view -> {
				
				int position = getAdapterPosition();
				if (listener != null && position != RecyclerView.NO_POSITION) {
					
					listener.onItemClick(getItem(position));
				}
			});
		}
	}
	
	public interface onItemClickListener {
		void onItemClick(Node node);
	}
	
	public void setOnItemClickListener(onItemClickListener listener) {
		this.listener = listener;
	}
}
