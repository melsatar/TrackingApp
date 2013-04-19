package com.trackingapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

public class LocatorMainActivity extends MapActivity implements
		LocationListener {

	private MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locator_main);

		// Getting reference to MapView
		mapView = (MapView) findViewById(R.id.map_view);

		final TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		
		ParseUser user = ParseUser.getCurrentUser();
		final String user_id = user.getObjectId();
		ParseQuery query = new ParseQuery("Vehicle");
		System.out.println("my user is " + user_id);
		query.whereEqualTo("user_id", user_id);
		query.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				System.out.println(object.getObjectId());

				ParseQuery vehicle_location = new ParseQuery("Vehicle_GPS");
				vehicle_location.whereEqualTo("vehicle_id",
						object.getObjectId());
				PushService.subscribe(LocatorMainActivity.this, (String) object.get("device"), LocatorMainActivity.class);
				vehicle_location.setLimit(10);
				vehicle_location.orderByDescending("createdAt");
				vehicle_location
						.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
				vehicle_location.findInBackground(new FindCallback() {
					public void done(List<ParseObject> PoistionList,
							ParseException e) {
						if (e == null) {
							Log.d("Positions",
									"Retrieved " + PoistionList.size()
											+ " Positions");
							
							double current_latitude = Double.valueOf(PoistionList.get(0).getString(
									"latitude"));
							
							double current_longitude = Double.valueOf(PoistionList.get(0).getString(
									"longitude"));
							
							tvLocation.setText("Cuurent Location =" + "Latitude:" + current_latitude
									+ ", Longitude:" + current_longitude);
							Geocoder geocoder;
							List<Address> addresses;
							geocoder = new Geocoder(LocatorMainActivity.this, Locale.getDefault());
							try {
								addresses = geocoder.getFromLocation(current_latitude, current_longitude, 1);
								String address = addresses.get(0).getAddressLine(0);
								String city = addresses.get(0).getAddressLine(1);
								String country = addresses.get(0).getAddressLine(2);
								System.out.println(address + " " + city + " " + country);
								tvLocation.setText(address + " " + city + " " + country);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							
							for (int i = 0; i < PoistionList.size(); i++) {
								
								double latitude = Double.valueOf(PoistionList.get(i).getString("latitude"));
								double longitude = Double.valueOf(PoistionList.get(i).getString("longitude"));
								GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude*1E6));
								// Getting MapController 
								MapController mapController = mapView.getController();
								mapController.animateTo(point);
								List<Overlay> mapOverlays = mapView.getOverlays();
								Drawable drawable = getResources().getDrawable(R.drawable.position);
								CurrentLocationOverlay currentLocationOverlay = new CurrentLocationOverlay(drawable);
								OverlayItem currentLocation = new OverlayItem(point, "Current Location", "Latitude : " + latitude + ", Longitude:" + longitude);
								currentLocationOverlay.addOverlay(currentLocation);
								mapOverlays.add(currentLocationOverlay);
								mapController.setZoom(100);

							}


						} else {
							Log.d("Positions", "Error: " + e.getMessage());
						}
					}
				});


			}
		});

		// Setting Zoom Controls on MapView
		mapView.setBuiltInZoomControls(true);

		// Getting LocationManager object from System Service LOCATION_SERVICE

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}