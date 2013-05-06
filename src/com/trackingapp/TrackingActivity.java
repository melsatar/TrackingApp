package com.trackingapp;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class TrackingActivity extends MapActivity implements LocationListener{
	
	private MapView mapView;
	private List<GeoPoint> path = new ArrayList<GeoPoint>();
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking);
		
		mapView = (MapView) findViewById(R.id.tracking_map_view);
		PushService.subscribe(this, ParseUser.getCurrentUser().getObjectId() , LocatorMainActivity.class);
		ParseUser user = ParseUser.getCurrentUser();
		final String user_id = user.getObjectId();
		ParseQuery query = new ParseQuery("Vehicle");
		query.whereEqualTo("user_id", user_id);
		query.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				System.out.println(object.getObjectId());

				ParseQuery vehicle_location = new ParseQuery("Vehicle_GPS");
				vehicle_location.whereEqualTo("vehicle_id",object.getObjectId());
				vehicle_location.setLimit(5);
				vehicle_location.orderByDescending("createdAt");
				vehicle_location
						.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
				vehicle_location.findInBackground(new FindCallback() {
					public void done(List<ParseObject> PoistionList,
							ParseException e) {
						if (e == null) {
							
							for (int i = 0; i < PoistionList.size(); i++) {
								
								double latitude = Double.valueOf(PoistionList.get(i).getString("latitude"));
								double longitude = Double.valueOf(PoistionList.get(i).getString("longitude"));
								GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude*1E6));
								path.add(point);
								MapController mapController = mapView.getController();
								mapController.animateTo(point);
								mapController.setZoom(15);
								

							}
							mapView.getOverlays().add(new RoutePathOverlay(path));


						} else {
							Log.d("Positions", "Error: " + e.getMessage());
						}
					}
				});


			}
		});

		// Setting Zoom Controls on MapView
		mapView.setBuiltInZoomControls(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracking, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
