package org.mixare.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mixare.lib.HtmlUnescape;
import org.mixare.lib.data.PluginDataProcessor;
import org.mixare.lib.gui.Model3D;
import org.mixare.lib.marker.InitialMarkerData;
import org.mixare.lib.marker.draw.ParcelableProperty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ArenaProcessor extends PluginDataProcessor {

	public static final int MAX_JSON_OBJECTS = 1000;
	private static final String TAG = "ArenaProcessor";
	private static final String MARKER_NAME = "imagemarker";

	private static final String[] URL_MATCH = { "arena" };
	private static final String[] DATA_MATCH = { "arena" };

	@Override
	public String[] getUrlMatch() {
		return URL_MATCH;
	}

	@Override
	public String[] getDataMatch() {
		return DATA_MATCH;
	}

	@Override
	public List<InitialMarkerData> load(String rawData, int taskId, int colour)
			throws JSONException {
		List<InitialMarkerData> initialMarkerDatas = new ArrayList<InitialMarkerData>();
		JSONObject root = convertToJSON(rawData);
		JSONArray dataArray = root.getJSONArray("results");
		int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

		for (int i = 0; i < top; i++) {
			JSONObject jo = dataArray.getJSONObject(i);
			if (jo.has("title") && jo.has("lat") && jo.has("lng")
					&& jo.has("elevation")) {

				String link = setWebPageFromJson(jo);
				String type = jo.getString("object_type");

				InitialMarkerData ma = new InitialMarkerData(jo.getInt("id"),
						HtmlUnescape.unescapeHTML(jo.getString("title")),
						jo.getDouble("lat"), jo.getDouble("lng"),
						jo.getDouble("elevation"), link, taskId, colour, type);

				if (type.equalsIgnoreCase("object3d")) {
					Model3D model = new Model3D();

					String blended = jo.getString("blended");
					String schaal = jo.getString("schaal");
					String rotatieX = jo.getString("rotX");
					String rotatieY = jo.getString("rotY");
					String rotatieZ = jo.getString("rotZ");

					System.out.println(blended + " " + schaal + " " + rotatieX
							+ " " + rotatieY + " " + rotatieZ);

					String modelLink = HtmlUnescape.unescapeUnicode(jo
							.getString("object_url"));

					String cacheUrl = getObjectFile(modelLink,
							jo.getString("title"));

					if (cacheUrl != null) {
						model.setObj(cacheUrl);
					}
					
					model.setBlended(Integer.valueOf(blended));
					model.setRot_x(Integer.valueOf(rotatieX));
					model.setRot_y(Integer.valueOf(rotatieY));
					model.setRot_z(Integer.valueOf(rotatieZ));
					model.setSchaal(Integer.valueOf(schaal));
					
					ma.setMarkerName(MARKER_NAME);
					ma.setExtras("obj", new ParcelableProperty(
							"org.mixare.lib.gui.Model3D", model));

				} else {
					Bitmap image = getBitmapFromURL(jo.getString("object_url"));

					ma.setMarkerName(MARKER_NAME);
					ma.setExtras("bitmap", new ParcelableProperty(
							"android.graphics.Bitmap", image));
				}

				if (jo.has("radius")) { // for offline
					ma.setExtras("radius", jo.getDouble("radius"));
				}

				initialMarkerDatas.add(ma);
			}
		}
		return initialMarkerDatas;
	}

	public String getObjectFile(String src, String title) {
		try {
			String fileName = Environment.getExternalStorageDirectory()
					.getPath() + "/model-" + title + ".txt";
			URL url = new URL(src);
			File file = new File(fileName);

			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();

			BufferedInputStream bis = new BufferedInputStream(is, 8096);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);

			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();

			return fileName;
		} catch (IOException e) {
			Log.e(TAG, "io exception, when getting the model from the url: "
					+ src);
			e.printStackTrace();
			return null;
		}
	}

	private String setWebPageFromJson(JSONObject jo) throws JSONException {
		String link = null;
		if (jo.has("has_detail_page") && jo.getInt("has_detail_page") != 0
				&& jo.has("webpage")) {
			link = jo.getString("webpage");
		}
		return link;
	}

	public Bitmap getBitmapFromURL(String src) {
		if (src.startsWith("http://")) {
			return getBitmapFromWebURL(src);
		} else if (src.startsWith("file://")) {
			return getBitmapFromFile(src);
		} else {
			Log.e(TAG, "getbitmapfromurl throwed an unsupported url: " + src);
			return null;
		}
	}

	/**
	 * for offline
	 * 
	 * @param src
	 * @return
	 */
	private Bitmap getBitmapFromFile(String src) {
		try {
			InputStream input = new FileInputStream(new File(src.replace(
					"file://", "")));
			return BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			Log.e(TAG, "io exception, when getting the bitmap from the file: "
					+ src);
			return null;
		}
	}

	/**
	 * for online
	 * 
	 * @param src
	 * @return
	 */
	private Bitmap getBitmapFromWebURL(String src) {
		try {
			URLConnection urlConnection = null;
			URL url = new URL(src);
			urlConnection = url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.connect();
			InputStream input = urlConnection.getInputStream();
			return BitmapFactory.decodeStream(input);

		} catch (IOException e) {
			Log.e(TAG, "io exception, when getting the bitmap from the url: "
					+ src);
			return null;
		}
	}
}
