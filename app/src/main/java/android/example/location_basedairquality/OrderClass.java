package android.example.location_basedairquality;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;

public class OrderClass{
	List<String> orderList = new ArrayList<String>();
	String isAscDesc = "ASC";
	String orderType = "rssi";
	
	MutableLiveData<List<String>> order;
	
	public OrderClass(){
		orderList.add(orderType);
		orderList.add(isAscDesc);
		order =  new MutableLiveData<List<String>>(orderList){
			@Override
			public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
				super.observe(owner, observer);
			}
		};
	}
	
	public String getIsAscDesc() {
		return isAscDesc;
	}
	
	public String getOrderType() {
		return orderType;
	}
	
	public void setIsAscDesc(String isAscDesc) {
		this.isAscDesc = isAscDesc;
		orderList.set(1, isAscDesc);
	}
	
	public void setOrderList(List<String> orderList) {
		this.orderList = orderList;
		order.postValue(orderList);
	}
	
	public List<String> getOrderList() {
		return orderList;
	}
	
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public MutableLiveData<List<String>> getOrder(){
		return order;
	}
	
}
