package com.moka.baidumaptestproject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MapApplication extends Application {
	
	private static final String APP_KEY = "2101f25b22e0b0245139e9c24d70b588";
	private static MapApplication mapApplicationInstance = null;
	BMapManager mapManager = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mapApplicationInstance = this;
		initBMapManager(this);
	}
	
	public void initBMapManager(Context context) {
		if (mapManager == null)
			mapManager = new BMapManager(context);
		boolean initSucceed = mapManager.init(APP_KEY, new MyGeneralListener());
        if (!initSucceed)
            Toast.makeText(mapApplicationInstance.getApplicationContext(), "BMapManager��ʼ������", Toast.LENGTH_SHORT).show();
	}
	
	public static MapApplication getInstance() {
		return mapApplicationInstance;
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT)
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "�������Ӵ���", Toast.LENGTH_SHORT).show();
            else if (iError == MKEvent.ERROR_NETWORK_DATA)
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "�������ݴ���", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED)
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "���� MapApplication.java�ļ�������ȷ����ȨKey", Toast.LENGTH_LONG).show();
        }
    }
	
}
