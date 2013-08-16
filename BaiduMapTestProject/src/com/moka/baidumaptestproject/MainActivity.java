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
	
	// ��λ���
	private  GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener();
	private LocationClient locationClient = null;
	private LocationData locationData = null;
	
	//��λͼ��
	private MyLocationOverlay myLocationOverlay = null;
	
	private boolean isRequest = false;//�Ƿ��ֶ���������λ
	private boolean isFirstLoc = true;//�Ƿ��״ζ�λ
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���mapManager�Ƿ�ɹ���ʼ��
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
		
		//��λ��ʼ��
        locationClient = new LocationClient(this);
        locationData = new LocationData();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//��gps
        option.setCoorType("bd09ll");     //������������
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.start();
       
        //��λͼ���ʼ��
		myLocationOverlay = new MyLocationOverlay(mapView);
		//���ö�λ����
	    myLocationOverlay.setData(locationData);
	    //��Ӷ�λͼ��
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		//�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
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
     * �ֶ�����һ�ζ�λ����
     */
    public void clickToLocate(){
    	isRequest = true;
        locationClient.requestLocation();
        Toast.makeText(MainActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT).show();
    }
	
	private void setPoiSearchCondition() {
		try {
			int radius = ( Integer.parseInt ( poiRadiusEditText.getText().toString() ) ) * 1000;
			String key = poiEditText.getText().toString();
			if (key == null || "".equals(key)) {
				Toast.makeText(this, "��������Ȥ��", Toast.LENGTH_SHORT).show();
				return;
			}
			mkSearch.poiSearchNearBy(key, myGeoPoint, radius);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "�뾶һ������������", Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			Toast.makeText(this, "��ȴ���λ���", Toast.LENGTH_SHORT).show();
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
     * ��λSDK��������
     */
    public class MyLocationListener implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            locationData.latitude = location.getLatitude();
            locationData.longitude = location.getLongitude();
            myGeoPoint = new GeoPoint( (int) (locationData.latitude * 1e6), (int) (locationData.longitude * 1e6) );
            //�������ʾ��λ����Ȧ����accuracy��ֵΪ0����
            locationData.accuracy = location.getRadius();
            locationData.direction = location.getDerect();
            //���¶�λ����
            myLocationOverlay.setData(locationData);
            //����ͼ������ִ��ˢ�º���Ч
            mapView.refresh();
        	//�ƶ���ͼ����λ��
            if (isRequest || isFirstLoc) {
            	//�ƶ���ͼ����λ��
                mapController.animateTo(myGeoPoint);
                isRequest = false;
            }
            //�״ζ�λ���
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
