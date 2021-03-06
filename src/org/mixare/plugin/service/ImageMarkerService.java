package org.mixare.plugin.service;

import java.util.HashMap;
import java.util.Map;

import org.mixare.lib.gui.Label;
import org.mixare.lib.marker.PluginMarker;
import org.mixare.lib.marker.draw.ClickHandler;
import org.mixare.lib.marker.draw.DrawCommand;
import org.mixare.lib.marker.draw.ParcelableProperty;
import org.mixare.lib.marker.draw.PrimitiveProperty;
import org.mixare.lib.render.Camera;
import org.mixare.lib.render.MixVector;
import org.mixare.lib.service.IMarkerService;
import org.mixare.plugin.ImageMarker;
import org.mixare.plugin.Obj3DMarker;
import org.mixare.plugin.OfflineCapableImageMarker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ImageMarkerService extends Service {

	public static final String PLUGIN_NAME = "imagemarker";
	public static final String CATEGORY_PLUGIN = "mixare.intent.category.MARKER_PLUGIN";
	public static boolean useOffline = false;
	private Map<String, PluginMarker> markers = new HashMap<String, PluginMarker>();
	private Integer count = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		Log.i(PLUGIN_NAME, "O jee");
	}

	private final IMarkerService.Stub binder = new IMarkerService.Stub() {

		@Override
		public int getPid() throws RemoteException {
			return 0;
		}

		@Override
		public String buildMarker(int id, String title, double latitude,
				double longitude, double altitude, String url, int type,
				int color, String markerType) throws RemoteException {
			PluginMarker marker;
			Log.i("Mixare", "" + markerType);
			if (markerType.equalsIgnoreCase("object3d")) {
				marker = new Obj3DMarker(id, title, latitude, longitude,
						altitude, url, type, color);
			} else if (markerType.equalsIgnoreCase("question")) {
				marker = new Obj3DMarker(id, title, latitude, longitude,
						altitude, url, type, color);
			} else if (markerType.equalsIgnoreCase("image") && useOffline) {

				marker = new OfflineCapableImageMarker(id, title, latitude,
						longitude, altitude, url, type, color);
			} else {
				marker = new ImageMarker(id, title, latitude, longitude,
						altitude, url, type, color);
			}

			String markerName = PLUGIN_NAME + "-" + (++count) + "-"
					+ marker.getID();
			markers.put(markerName, marker);
			return markerName;
		}

		@Override
		public String getPluginName() throws RemoteException {
			return PLUGIN_NAME;
		}

		@Override
		public void calcPaint(String markerName, Camera viewCam, float addX,
				float addY) throws RemoteException {
			markers.get(markerName).calcPaint(viewCam, addX, addY);
		}

		@Override
		public DrawCommand[] remoteDraw(String markerName)
				throws RemoteException {
			return markers.get(markerName).remoteDraw();
		}

		@Override
		public double getAltitude(String markerName) throws RemoteException {
			return markers.get(markerName).getAltitude();
		}

		@Override
		public int getColour(String markerName) throws RemoteException {
			return markers.get(markerName).getColor();
		}

		@Override
		public double getDistance(String markerName) throws RemoteException {
			return markers.get(markerName).getDistance();
		}

		@Override
		public String getID(String markerName) throws RemoteException {
			return markers.get(markerName).getID();
		}

		@Override
		public double getLatitude(String markerName) throws RemoteException {
			return markers.get(markerName).getLatitude();
		}

		@Override
		public MixVector getLocationVector(String markerName)
				throws RemoteException {
			return markers.get(markerName).getLocationVector();
		}

		@Override
		public double getLongitude(String markerName) throws RemoteException {
			return markers.get(markerName).getLongitude();
		}

		@Override
		public int getMaxObjects(String markerName) throws RemoteException {
			return markers.get(markerName).getMaxObjects();
		}

		@Override
		public String getTitle(String markerName) throws RemoteException {
			return markers.get(markerName).getTitle();
		}

		@Override
		public String getURL(String markerName) throws RemoteException {
			return markers.get(markerName).getURL();
		}

		@Override
		public boolean isActive(String markerName) throws RemoteException {
			return markers.get(markerName).isActive();
		}

		@Override
		public void setActive(String markerName, boolean active)
				throws RemoteException {
			markers.get(markerName).setActive(active);
		}

		@Override
		public void setDistance(String markerName, double distance)
				throws RemoteException {
			markers.get(markerName).setDistance(distance);
		}

		@Override
		public void setID(String markerName, String iD) throws RemoteException {
			markers.get(markerName).setID(iD);
		}

		@Override
		public void update(String markerName, Location curGPSFix)
				throws RemoteException {
			markers.get(markerName).update(curGPSFix);
		}

		@Override
		public void removeMarker(String markerName) throws RemoteException {
			markers.remove(markerName);
		}

		@Override
		public ClickHandler fClick(String markerName) throws RemoteException {
			return markers.get(markerName).fClick();
		}

		@Override
		public MixVector getCMarker(String markerName) throws RemoteException {
			return markers.get(markerName).getCMarker();
		}

		@Override
		public MixVector getSignMarker(String markerName)
				throws RemoteException {
			return markers.get(markerName).getSignMarker();
		}

		@Override
		public boolean getUnderline(String markerName) throws RemoteException {
			return markers.get(markerName).getUnderline();
		}

		@Override
		public boolean isVisible(String markerName) throws RemoteException {
			return markers.get(markerName).isVisible();
		}

		@Override
		public void setTxtLab(String markerName, Label txtLab)
				throws RemoteException {
			markers.get(markerName).setTxtLab(txtLab);
		}

		@Override
		public Label getTxtLab(String markerName) throws RemoteException {
			return markers.get(markerName).getTxtLab();
		}

		@Override
		public void setExtrasParc(String markerName, String name,
				ParcelableProperty value) throws RemoteException {
			markers.get(markerName).setExtras(name, value);
		}

		@Override
		public void setExtrasPrim(String markerName, String name,
				PrimitiveProperty value) throws RemoteException {
			markers.get(markerName).setExtras(name, value);

		}

		@Override
		public double getBearing(String name) throws RemoteException {
			return markers.get(name).getBearing();
		}

		@Override
		public void setBearing(String name, double bearing)
				throws RemoteException {
			markers.get(name).setBearing(bearing);
		}
	};

}