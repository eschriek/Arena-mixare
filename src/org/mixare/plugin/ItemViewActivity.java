package org.mixare.plugin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mixare.lib.MixUtils;
import org.mixare.plugin.util.WebReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class ItemViewActivity extends Activity {

	private static final String TAG = "ItemViewActivity";
	private static final String URL = "url";
	private static final String QUESTION_TYPE = "Question";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,	LayoutParams.FLAG_NOT_TOUCH_MODAL);
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		setTheme(android.R.style.Theme_Dialog);
		buildView(getIntent().getExtras().getString(URL));
		LayoutParams params = getWindow().getAttributes(); 
        params.width= LayoutParams.FILL_PARENT; 
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	}
	
	private void buildView(String url){
		JSONObject json = processUrlToJson(url);
		if(json == null){
			Log.e(TAG, "exception: json == null");
			return;
		}
		try {
			buildGuiDialog(json);
		} catch (JSONException e) {}
	}
	
	private JSONObject convertStringToJson(String content){
		try {
			if(content == null){
				return null;
			}
			return new JSONObject(content);
		} catch (JSONException e) {
			return null;
		}
	}
	
	private void buildGuiDialog(JSONObject json) throws JSONException{
		if(json.getString("type").equals(QUESTION_TYPE)){
			buildQuestion(json);
		}
		else if(json.getString("type").equals("Question")){
			buildQuestion(json);
		}
	}
	
	private void buildQuestion(JSONObject json) throws JSONException{
		setContentView(R.layout.questionitem);
		TextView question = (TextView)findViewById(R.id.question);
		question.setText(json.getString("description"));
		JSONArray answers = json.getJSONArray("answers");
		if(answers.length() > 0){
			fillMultipleChoiceAnswers(answers);
		}else{
			fillOpenAnswer();
		}
	}
	
	private void fillMultipleChoiceAnswers(JSONArray answers) throws JSONException{
		for(int i = 0; i < 4; i++){
			if(answers.length() > i){
				int identifier = getResources().getIdentifier("answer"+(i+1), null, null);
				RadioButton answer = (RadioButton)findViewById(identifier);
				answer.setText(answers.getString(i));
				((LinearLayout)answer.getParent()).setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void fillOpenAnswer() throws JSONException{
		EditText answerField = (EditText)findViewById(R.id.answerField);
		answerField.setVisibility(View.VISIBLE);	
	}
	
	/**
	 * Close when pressing outside of activity.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private JSONObject processUrlToJson(String src){
		String url = MixUtils.parseAction(src);
		if(url.startsWith("http://")){
			WebReader webReader = new WebReader(url);
			return convertStringToJson(webReader.getResult());
		}else if(url.startsWith("file://")){
			url = encodeUrlAgain(url);
			String result = processFileToString(url);
			if(result != null){
				return convertStringToJson(result);
			}
		}
		Log.e(TAG, "Unable to parse the url to JSON, null returned");
		return null;
	}
	
	private String processFileToString(String filename){
		try{
			InputStream in = new FileInputStream(new File(filename.replace("file://", "")));
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while(result != -1) {
				byte b = (byte)result;
				buf.write(b);
				result = bis.read();
			}        
			return buf.toString();
		}catch(IOException io){
			return null;
		}
	}

	/**
	 * Url gets decoded in PluginMarker. We need the encoded url, 
	 * because the offline file gets stored as: file://mnt/sdcard/mixare-offline-ds/(encoded web url)
	 * this methods re-encodes everything after mixare-offline-ds/
	 * @param url
	 */
	private String encodeUrlAgain(String url){
		String split = "mixare-offline-ds/";
		String[] u = url.split(split);
		return u[0] + split + URLEncoder.encode(u[1]);		
	}
	
	protected void onDestroy() {
		Intent i = new Intent();
		i.setClassName("org.mixare", "org.mixare.MixView");
		startActivity(i);	
		super.onDestroy();
	}
	
}