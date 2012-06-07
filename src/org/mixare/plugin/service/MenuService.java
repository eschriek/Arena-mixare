package org.mixare.plugin.service;

import org.mixare.lib.service.IBootStrap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MenuService extends Service{

	private static final String ACTIVITY_PACKAGE = "org.mixare.plugin";	
	private static final String ACTIVITY_NAME = "org.mixare.plugin.MenuActivity";
	private static final String PLUGIN_NAME = "menu";
	private static final int Z_INDEX = 100;
	public static final int ACTIVITY_REQUEST_CODE = 2118; 
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private final IBootStrap.Stub binder = new IBootStrap.Stub() {

		@Override
		public String getActivityName() throws RemoteException {
			return ACTIVITY_NAME;
		}

		@Override
		public String getActivityPackage() throws RemoteException {
			return ACTIVITY_PACKAGE;
		}
		
		@Override
		public int getZIndex() throws RemoteException {
			return Z_INDEX;
		}

		@Override
		public String getPluginName() throws RemoteException {
			return PLUGIN_NAME;
		}

		@Override
		public int getActivityRequestCode() throws RemoteException {
			return ACTIVITY_REQUEST_CODE;
		}

	};
	
}
