package com.pbm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LookupByLocationType extends PBMUtil {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_location_type);
		
		logAnalyticsHit("com.pbm.LookupByLocationType");

		PBMApplication app = (PBMApplication) getApplication();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Region region = app.getRegion(settings.getInt("region",  1));
		List<LocationType> locationTypes = region.locationTypes(this);
		
		ListView table = (ListView)findViewById(R.id.locationTypeTable);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();	
				myIntent.putExtra("LocationType", (LocationType) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		sort(locationTypes);

		table.setAdapter(new ArrayAdapter<LocationType>(this, android.R.layout.simple_list_item_1, locationTypes));
		
		super.onCreate(savedInstanceState, table);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void sort(List<LocationType> types) {
		Collections.sort(types, new Comparator<LocationType>() {
			public int compare(LocationType t1, LocationType t2) {
				return t1.name.compareTo(t2.name);
			}
		});
	}
}