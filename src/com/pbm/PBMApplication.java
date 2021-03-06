package com.pbm;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

@SuppressLint("UseSparseArrays")
public class PBMApplication extends Application {
	public enum TrackerName {
	    APP_TRACKER
	}

	private HashMap<Integer, com.pbm.Location>            locations     = new HashMap<Integer, Location>();
	private HashMap<Integer, com.pbm.LocationType>        locationTypes = new HashMap<Integer, LocationType>();
	private HashMap<Integer, com.pbm.Machine>             machines      = new HashMap<Integer, Machine>();
	private HashMap<Integer, com.pbm.LocationMachineXref> lmxes         = new HashMap<Integer, LocationMachineXref>();
	private HashMap<Integer, com.pbm.Zone>                zones         = new HashMap<Integer, Zone>();
	private HashMap<Integer, com.pbm.Region>              regions       = new HashMap<Integer, Region>();
	
	public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	synchronized Tracker getTracker() {
		if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    Tracker t = analytics.newTracker(R.xml.app_tracker);
		    mTrackers.put(TrackerName.APP_TRACKER, t);
		}

		return mTrackers.get(TrackerName.APP_TRACKER);
	}

	public void setLocations(HashMap<Integer, com.pbm.Location> locations) {
		this.locations = locations;
	}
	public void setLocation(Integer index, com.pbm.Location location) {
		this.locations.put(index, location);
	}
	public HashMap<Integer, com.pbm.Location> getLocations() {
		return locations;
	}
	public void setMachines(HashMap<Integer, com.pbm.Machine> machines) {
		this.machines = machines;
	}
	public HashMap<Integer, com.pbm.Machine> getMachines() {
		return machines;
	}
	public String[] getMachineNames() {
		HashMap<Integer, com.pbm.Machine> machines = getMachines();
		
		String names[] = new String[machines.size()];
		
		int i = 0;
		for (Machine machine : machines.values()) {
			names[i] = machine.name;

			i++;
		}
		
		Arrays.sort(names);

		return names;
	}
	public String[] getMachineNamesWithMetadata() {
		HashMap<Integer, com.pbm.Machine> machines = getMachines();
		
		String names[] = new String[machines.size()];
		
		int i = 0;
		for (Machine machine : machines.values()) {
			names[i] = machine.name + " (" + machine.manufacturer + " - " + machine.year + ")";

			i++;
		}
		
		Arrays.sort(names);

		return names;
	}
	public void setZones(HashMap<Integer, com.pbm.Zone> zones) {
		this.zones = zones;
	}
	public HashMap<Integer, com.pbm.LocationMachineXref> getLmxes() {
		return lmxes;
	}
	public void setLmxes(HashMap<Integer, com.pbm.LocationMachineXref> lmxes) {
		this.lmxes = lmxes;
	}
	public void setLmx(com.pbm.LocationMachineXref lmx) {
		this.lmxes.put(lmx.id, lmx);
	}
	public void removeLmx(LocationMachineXref lmx) {
		this.lmxes.remove(lmx.id);
	}
	public HashMap<Integer, com.pbm.Zone> getZones() {
		return zones;
	}
	public void addLocation(Integer id, Location location) {
		this.locations.put(id, location);
	}
	public void addMachine(Integer id, Machine machine) {
		this.machines.put(id, machine);
	}
	public void addLocationType(Integer id, LocationType name) {
		this.locationTypes.put(id, name);
	}
	public LocationType getLocationType(Integer id) {
		return locationTypes.get(id);
	}
	public HashMap<Integer, com.pbm.LocationType> getLocationTypes() {
		return locationTypes;
	}
	public void addLocationMachineXref(Integer id, LocationMachineXref lmx) {
		this.lmxes.put(id, lmx);
	}
	
	public LocationMachineXref getLmxFromMachine(Machine machine, List<LocationMachineXref> lmxes) {
		for (LocationMachineXref lmx : lmxes) {
			if (lmx.machineID == machine.id) {
				return lmx;
			}
		}

		return null;
	}

	public LocationMachineXref getLmx(Integer id) {
		return lmxes.get(id);
	}

	public int numMachinesForLocation(Location location) {
		int numMachines = 0;
		for (LocationMachineXref lmx : getLmxes().values()) {
			if (lmx.locationID == location.id) {
				numMachines += 1;
			}
		}
		
		return numMachines;
	}
	
	public Machine getMachine(Integer id) {
		return machines.get(id);
	}
	public Machine getMachineByName(String name) {
		ArrayList<Machine> machines = getMachineValues(true);
		for (Object baseMachine : machines) {
			Machine machine = (Machine) baseMachine;
			if (machine.name.equalsIgnoreCase(name)) {
				return machine;
			}
		}
		
		return null;
	}
	public Location getLocationByName(String name) {
		Object[] locations = getLocationValues();
		for (int i = 0; i < locations.length; i++) {
			Location location = (Location) locations[i];
			if (location.name.equals(name)) {
				return location;
			}
		}
		
		return null;
	}
	public Location getLocation(Integer id) {
		return locations.get(id);
	}
	public Region getRegion(Integer id) {
		return regions.get(id);
	}
	public void addZone(Integer id, Zone zone) {
		this.zones.put(id, zone);
	}
	public void addRegion(Integer id, Region region) {
		this.regions.put(id, region);
	}
	public void setRegions(HashMap<Integer, com.pbm.Region> regions) {
		this.regions = regions;
	}
	public HashMap<Integer, com.pbm.Region> getRegions() {
		return regions;
	}
	public Object[] getRegionValues() {
		Object[] regionValues = getRegions().values().toArray();

		Arrays.sort(regionValues, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Region r1 = (Region) o1;
				Region r2 = (Region) o2;
				return r1.formalName.compareTo(r2.formalName);
			}
		});

		return regionValues;
	}
	public Object[] getLocationValues() {
		Object[] locationValues = getLocations().values().toArray();

		Arrays.sort(locationValues, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Location l1 = (Location) o1;
				Location l2 = (Location) o2;
				return l1.name.toString().compareTo(l2.name.toString());
			}
		});

		return locationValues;
	}
	public ArrayList<Machine> getMachineValues(boolean displayAllMachines) {
		ArrayList<Machine> machineValues = new ArrayList<Machine>();

		for (Machine machine : getMachines().values()) {
			if (displayAllMachines || machine.existsInRegion) {
				machineValues.add(machine);
			}
		}
		
		Collections.sort(machineValues, new Comparator<Object>() {
			public int compare(Object lhs, Object rhs) {
				Machine m1 = (Machine) lhs;
				Machine m2 = (Machine) rhs;

				return m1.name.replaceAll("^(?i)The ", "").compareTo(m2.name.replaceAll("^(?i)The ", ""));
			}
	    });

		return machineValues;
	}

	public void initializeData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		initializeAllMachines();
		initializeLocations();
		initializeLocationTypes();
		initializeZones();
	}

	public void initializeLocationTypes() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		locationTypes.clear();

		String json = new RetrieveJsonTask().execute(PBMUtil.regionlessBase + "location_types.json", "GET").get();
		if (json == null) {
			return;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray types = jsonObject.getJSONArray("location_types");
		
		for (int i=0; i < types.length(); i++) {
		    try {
		        JSONObject type = types.getJSONObject(i);
		        String name = type.getString("name");
		        String id = type.getString("id");

				if ((id != null) && (name != null)) {
					addLocationType(Integer.parseInt(id), new LocationType(Integer.parseInt(id), name));
				}
		    } catch (JSONException e) {
		    }
		}
	}
	
	public void initializeAllMachines() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		machines.clear();

		String json = new RetrieveJsonTask().execute(PBMUtil.regionlessBase + "machines.json", "GET").get();
		if (json == null) {
			return;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray machines = jsonObject.getJSONArray("machines");
		
		for (int i=0; i < machines.length(); i++) {
		    try {
		        JSONObject machine = machines.getJSONObject(i);
		        String name = machine.getString("name");
		        String id = machine.getString("id");
		        String year = machine.getString("year");
		        String manufacturer = machine.getString("manufacturer");

				if ((id != null) && (name != null)) {
					addMachine(Integer.parseInt(id), new Machine(Integer.parseInt(id), name, year, manufacturer, false));
				}
		    } catch (JSONException e) {
		    }
		}
	}

	public void initializeZones() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		zones.clear();
		
		String json = new RetrieveJsonTask().execute(PBMUtil.regionBase + "zones.json", "GET").get();
		if (json == null) {
			return;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray zones = jsonObject.getJSONArray("zones");
		
		for (int i = 0; i < zones.length(); i++) {
		    JSONObject zone = zones.getJSONObject(i);

			String name = zone.getString("name");
			String id = zone.getString("id");
			Boolean isPrimary = zone.getBoolean("is_primary");

			if ((id != null) && (name != null) && (isPrimary != null)){
				addZone(Integer.parseInt(id), new Zone(Integer.parseInt(id), name, isPrimary ? 1 : 0));
			}
		}
	}

	public void initializeLocations() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		lmxes.clear();
		locations.clear();

		String json = new RetrieveJsonTask().execute(PBMUtil.regionBase + "locations.json", "GET").get();
		if (json == null) {
			return;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray locations = jsonObject.getJSONArray("locations");
		
		for (int i = 0; i < locations.length(); i++) {
		    JSONObject location = locations.getJSONObject(i);
		    
			int id = location.getInt("id");
			String name = location.getString("name");
			String lat = location.getString("lat");
			String lon = location.getString("lon");
			String street = location.getString("street");
			String city = location.getString("city");
			String zip = location.getString("zip");
			String phone = location.getString("phone");
			String state = location.getString("state");
			String website = location.getString("website");

			int zoneID = 0;
			if (location.has("zone_id") && location.getString("zone_id") != "null") {
				zoneID = location.getInt("zone_id");
			}

			int locationTypeID = 0;
			if (location.has("location_type_id") && location.getString("location_type_id") != "null") {
				locationTypeID = location.getInt("location_type_id");
			}
			
			if ((name != null) && (lat != null) && (lon != null)) {
				Location newLocation = new com.pbm.Location(id, name, lat, lon, zoneID, street, city, state, zip, phone, locationTypeID, website);

				SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
				float yourLat = settings.getFloat("yourLat", -1);
				float yourLon = settings.getFloat("yourLon", -1);
							
				if (yourLat != -1 && yourLon != -1) {
					newLocation = setMilesInfoOnNewLocation(newLocation, yourLat, yourLon);
				}
				
				addLocation(id, newLocation);
			}
			
		    JSONArray lmxes = null;
		    if (location.has("location_machine_xrefs")) {
		    	lmxes = location.getJSONArray("location_machine_xrefs");
		    }
			
			if (lmxes != null && lmxes.length() > 0) {
				for (int x = 0; x < lmxes.length(); x++) {
					JSONObject lmx = lmxes.getJSONObject(x);

					int lmxID = lmx.getInt("id");
					int lmxLocationID = lmx.getInt("location_id");
					int machineID = lmx.getInt("machine_id");
					String condition = lmx.getString("condition");
					String conditionDate = lmx.getString("condition_date");
					
					Machine machine = getMachine(machineID);
					
					if (machine != null) {
						machine.setExistsInRegion(true);
					
						addLocationMachineXref(
							lmxID,
							new com.pbm.LocationMachineXref(lmxID, lmxLocationID, machineID, condition, conditionDate)
						);
					}
				}
			}
		}
	}
	
	public Location setMilesInfoOnNewLocation(Location newLocation, float yourLat, float yourLon) {
		android.location.Location yourLocation = new android.location.Location("");
		yourLocation.setLatitude(yourLat);
		yourLocation.setLongitude(yourLon);
	
		float distance = yourLocation.distanceTo(newLocation.toAndroidLocation()); 
		distance = (float) (distance * PBMUtil.METERS_TO_MILES);	
	
		NumberFormat formatter = new DecimalFormat(".00");
		newLocation.setMilesInfo(formatter.format(distance) + " miles");
		
		return newLocation;
	}

	@SuppressWarnings("null")
	public boolean initializeRegions() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(PBMUtil.regionlessBase + "regions.json", "GET").get();
		if (json == null) {
			return false;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray regions = jsonObject.getJSONArray("regions");
		
		for (int i = 0; i < regions.length(); i++) {
		    JSONObject region = regions.getJSONObject(i);
			String id = region.getString("id");
			String name = region.getString("name");
			String formalName = region.getString("full_name");
			String motd = region.getString("motd");
			String lat = region.getString("lat");
			String lon = region.getString("lon");

			List<String> emailAddresses = null;

			if (region.has("all_admin_email_address")) {
				JSONArray jsonEmailAddresses = region.getJSONArray("all_admin_email_address");
				for (int x = 0; x < jsonEmailAddresses.length(); x++) {
				    emailAddresses.add(jsonEmailAddresses.getString(x));
				}
			}

			addRegion(Integer.parseInt(id), new Region(Integer.parseInt(id), name, formalName, motd, lat, lon, emailAddresses));
		}

		return true;
	}

}