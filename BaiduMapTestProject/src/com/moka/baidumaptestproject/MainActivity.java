package com.moka.baidumaptestproject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private EditText poiEditText = null;
	private EditText poiRadiusEditText = null;
	private Button poiBtn = null;
	private Button locationBtn = null;
	private MapView mapView = null;
	
	private MapController mapController = null;
	private MapApplication mapApplication = null;
	private BMapManager mapManager = null;
	private MKSearch mkSearch = null;
	
	// 定位相关
	private  GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener();
	private LocationClient locationClient = null;
	private LocationData locationData = null;
	
	//定位图层
	private MyLocationOverlay myLocationOverlay = null;
	
	private boolean isRequest = false;//是否手动触发请求定位
	private boolean isFirstLoc = true;//是否首次定位
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 检查mapManager是否成功初始化
		mapApplication = (MapApplication) this.getApplication();
		mapManager = mapApplication.mapManager;
		if (mapManager == null) {
			mapManager = new BMapManager(this);
			mapApplication.initBMapManager(this);
		}
		setContentView(R.layout.activity_main);
		
		poiEditText = (EditText) findViewById(R.id.poiEditText); 
		poiRadiusEditText = (EditText) findViewById(R.id.poiRadiusEditText); 
		poiBtn = (Button) findViewById(R.id.poiBtn); 
		locationBtn = (Button) findViewById(R.id.locationBtn); 
		mapView = (MapView) findViewById(R.id.mapView); 
		
		
		mapController = mapView.getController();
		mapController.setZoom(14);
//		mapController.enableClick(true);
//		mapView.setBuiltInZoomControls(true);
		
		//定位初始化
        locationClient = new LocationClient(this);
        locationData = new LocationData();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.start();
       
        //定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mapView);
		//设置定位数据
	    myLocationOverlay.setData(locationData);
	    //添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		//修改定位数据后刷新图层生效
		mapView.refresh();
		
		mkSearch = new MKSearch();
		mkSearch.init(mapManager, new PoiSearchListener());
		
		poiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPoiSearchCondition();				
			}
		});
		
		locationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickToLocate();
			}
		});
		
	}
	
    /**
     * 手动触发一次定位请求
     */
    public void clickToLocate(){
    	isRequest = true;
        locationClient.requestLocation();
        Toast.makeText(MainActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
    }
	
	private void setPoiSearchCondition() {
		try {
			int radius = ( Integer.parseInt ( poiRadiusEditText.getText().toString() ) ) * 1000;
			String key = poiEditText.getText().toString();
			if (key == null || "".equals(key)) {
				Toast.makeText(this, "请输入兴趣点", Toast.LENGTH_SHORT).show();
				return;
			}
			mkSearch.poiSearchNearBy(key, myGeoPoint, radius);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "半径一栏请输入数字", Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			Toast.makeText(this, "请等待定位完毕", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private class PoiSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
			if (result == null)
				return;
			
			PoiOverlay poiOverlay = new PoiOverlay(MainActivity.this, mapView);
			poiOverlay.setData(result.getAllPoi());
			MainActivity.this.mapView.getOverlays().clear();
			MainActivity.this.mapView.getOverlays().add(poiOverlay);
			MainActivity.this.mapView.invalidate(mapView.getLeft(),
				mapView.getTop(),mapView.getRight(),mapView.getBottom());
			
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}
		
	}
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            locationData.latitude = location.getLatitude();
            locationData.longitude = location.getLongitude();
            myGeoPoint = new GeoPoint( (int) (locationData.latitude * 1e6), (int) (locationData.longitude * 1e6) );
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locationData.accuracy = location.getRadius();
            locationData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(locationData);
            //更新图层数据执行刷新后生效
            mapView.refresh();
        	//移动地图到定位点
            if (isRequest || isFirstLoc) {
            	//移动地图到定位点
                mapController.animateTo(myGeoPoint);
                isRequest = false;
            }
            //首次定位完成
            isFirstLoc = false;
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null)
                return ;
        }
    }
	
	@Override
	protected void onDestroy() {
		if (locationClient != null)
			locationClient.stop();
		if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		mapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mapManager != null)
			mapManager.stop();
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mapManager != null)
			mapManager.start();
		mapView.onResume();
		super.onResume();
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mapView.onRestoreInstanceState(savedInstanceState);
    }

}
