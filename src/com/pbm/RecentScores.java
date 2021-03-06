package com.pbm;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentScores extends PBMUtil {
	private List<Spanned> recentScores = new ArrayList<Spanned>();
	final private static int NUM_RECENT_SCORES_TO_SHOW = 20;	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, String> ranks = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;
	{
		put(1, "GC");
		put(2, "1st");
		put(3, "2nd");
		put(4, "3rd");
		put(5, "4th");
	}};


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.recent_scores);

		logAnalyticsHit("com.pbm.RecentScores");

		new Thread(new Runnable() {
	        public void run() {
	        	try {
					getLocationData();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	RecentScores.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ListView table = (ListView)findViewById(R.id.recentscorestable);
						table.setAdapter(new ArrayAdapter<Spanned>(RecentScores.this, android.R.layout.simple_list_item_1, recentScores));
					}
	        	});
	        }
	    }).start();
	}

	public void getLocationData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(regionBase + "machine_score_xrefs.json?limit=" + NUM_RECENT_SCORES_TO_SHOW, "GET").get();

		if (json == null) {
			return;
		}
		
		DecimalFormat formatter = new DecimalFormat("#,###");
		JSONObject jsonObject = new JSONObject(json);
		JSONArray scores = jsonObject.getJSONArray("machine_score_xrefs");
		PBMApplication app = (PBMApplication) getApplication();
		for (int i=0; i < scores.length(); i++) {
			JSONObject msx = scores.getJSONObject(i);
			
			int lmxID = msx.getInt("location_machine_xref_id");
			LocationMachineXref lmx = app.getLmx(lmxID);

			int rank = msx.getInt("rank");
			long score = msx.getLong("score");
			String initials = msx.getString("initials");
			String scoreDate = msx.getString("created_at").split("T")[0];
			Location location = lmx.getLocation(this);
			Machine machine = lmx.getMachine(this);
			
			String title = location.name + "'s " + machine.name + "<br />" +
			ranks.get(rank) + " with " + formatter.format(score) + " by <b>" + initials + "</b>" + "<br />" +
			"<small>" + scoreDate + "</small>";

			recentScores.add(Html.fromHtml(title));
		}
	}
}