package android.example.location_basedairquality;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Request;
import okhttp3.Response;
import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import java.util.Date;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ServerReceivedMessages {
	
	private static ServerReceivedMessages instance;
	OkSse okServerSentEvents = new OkSse();
	String httpUrl = new String();
	Request request;
	ServerSentEvent serverSentEvent;
	NodeViewModel nodeViewModel;
	Application application;
	MainActivity mainActivity;
	long nodeID, lastNodeUpdate;
	double temperature, humidity, pressure, altitude, gas;
	NodeAdapter allNodesAdapter, singleNodeAdapter;
	MutableLiveData<Boolean> connection = new MutableLiveData<Boolean>(false) {
		@Override
		public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
			super.observe(owner, observer);
		}
	};
	
	public ServerReceivedMessages() {
	}
	
	private ServerReceivedMessages(String httpUrl, Application application, MainActivity mainActivity, NodeAdapter allNodesAdapter, NodeAdapter singleNodeAdapter) {
		this.httpUrl = httpUrl;
		this.application = application;
		this.mainActivity = mainActivity;
		this.allNodesAdapter = allNodesAdapter;
		this.singleNodeAdapter = singleNodeAdapter;
		
	}
	
	public static ServerReceivedMessages getInstance(String url, Application application, MainActivity mainActivity, NodeAdapter allNodes, NodeAdapter singleNode) {
		if (instance == null) {
			instance = new ServerReceivedMessages(url, application, mainActivity, allNodes, singleNode);
		}
		return instance;
	}
	
	public void requestBuilder() {
		if (this.httpUrl != null) {
			this.request = new Request.Builder()
					.url(httpUrl)
					.build();
		}
	}
	
	public void startServerSentListener() {
		try{
			serverSentEvent = okServerSentEvents.newServerSentEvent(request, new ServerSentEvent.Listener() {
				
				@Override
				public void onOpen(ServerSentEvent sse, Response response) {
					Log.e("OPEN", "Server is OPEN!");
					connection.postValue(true);
					sse.setTimeout((long) 10, SECONDS); //If no message is received from Webserver within 10 seconds... Connection closes!!!
				}
				
				@Override
				public void onMessage(ServerSentEvent sse, String id, String event, String message) {
					nodeViewModel = ViewModelProviders.of(mainActivity).get(NodeViewModel.class);
					if (event.equals("sensor")) {
						try { //When Parsing false
							Log.e("MESSAGE", "Event:" + event + "=> Message; " + message);
							JSONObject airQualityInputStream = new JSONObject(message);
							nodeID = Long.parseLong(airQualityInputStream.getString("nodeID"));
							temperature = Double.parseDouble(airQualityInputStream.getString("temperature"));
							humidity = Double.parseDouble(airQualityInputStream.getString("humidity"));
							pressure = Double.parseDouble(airQualityInputStream.getString("pressure"));
							altitude = Double.parseDouble(airQualityInputStream.getString("altitude"));
							gas = Double.parseDouble(airQualityInputStream.getString("gas"));
							lastNodeUpdate = new Date().getTime();
							try { //When node is not in list
								if (nodeViewModel.getSingleNodeByNodeID(nodeID) != null) {
									nodeViewModel.updateAirQualityOffNode(temperature, humidity, pressure, altitude, gas, nodeID, lastNodeUpdate);
								} else {
									nodeViewModel.insert(new Node(nodeID, temperature, humidity, pressure, altitude, gas, "unknown", -99, new Date().getTime(), new Date(1900, 1, 1, 0, 0).getTime()));
								}
							} catch (Exception e) {
								Log.e("Problem adding/updating Node", e.getMessage());
								return;
							}
						} catch (JSONException e) {
							Log.e("Parsing", e.toString());
						}
						mainActivity.runOnUiThread(() -> {
							allNodesAdapter.notifyDataSetChanged();
							singleNodeAdapter.notifyDataSetChanged();
						});
					}
				}
				
				@Override
				public void onComment(ServerSentEvent sse, String comment) {
				}
				
				@Override
				public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
					return false;
				}
				
				@Override
				public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
					connection.postValue(false);
					Log.e("Retrying:", "Connection");
					return false;
				}
				
				@Override
				public void onClosed(ServerSentEvent sse) {
				}
				
				@Override
				public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
					return null;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public MutableLiveData<Boolean> getConnection() {
		return connection;
	}
}
