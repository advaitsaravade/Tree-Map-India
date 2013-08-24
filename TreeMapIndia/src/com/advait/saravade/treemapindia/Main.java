package com.advait.saravade.treemapindia;

import java.io.File;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener, OnTouchListener{
	
	private RelativeLayout layout;
	private Button addTrees;
	private LinearLayout layout3;
	public boolean userDB = false;
	public boolean treeDB = false;
	private String android_id;
	private TextView treemapindia2;
	private float isGPS;
	
	// all elements
	private EditText ward_no;
	private EditText clus_no;
	private Button boundary;
	private Button proptype;
	private EditText owner;
	private EditText description;
	private EditText housenumber;
	private EditText area;
	private EditText surveynumber;
	private Button tree_outside_type;
	
	// element text for database
	private int wardnum;
	private int clusnum;
	private String bound_type;
	private String property_type;
	private String property_owner;
	private String property_description;
	private int property_housenumber;
	private int property_area;
	private String tree_outside_type2;
	private int surveynumber_db;

	private String SP_KEY = "firsttimeuser";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		setupElements();
		Typeface type2 = Typeface.createFromAsset(getAssets(),"GROBOLD.ttf");
		registerForContextMenu(boundary);
		registerForContextMenu(proptype);
		registerForContextMenu(tree_outside_type);
		addTrees.setOnClickListener(this);
		addTrees.setOnTouchListener(this);
		boundary.setOnClickListener(this);
		boundary.setLongClickable(false);
		proptype.setOnClickListener(this);
		proptype.setLongClickable(false);
		tree_outside_type.setOnClickListener(this);
		tree_outside_type.setLongClickable(false);
		treemapindia2.setTypeface(type2);
	}

	private void setupElements() {
		// TODO Auto-generated method stub
		layout3 = (LinearLayout) findViewById(R.id.layout3);
		layout = (RelativeLayout) findViewById(R.id.mainlayout);
		treemapindia2 = (TextView) findViewById(R.id.treemapindia2);
		ward_no = (EditText) findViewById(R.id.editText1);
		clus_no = (EditText) findViewById(R.id.editText2);
		boundary = (Button) findViewById(R.id.menuselect);
		proptype = (Button) findViewById(R.id.proptype);
		owner = (EditText) findViewById(R.id.owner);
		description = (EditText) findViewById(R.id.description);
		housenumber = (EditText) findViewById(R.id.houseno);
		area = (EditText) findViewById(R.id.area);
		surveynumber = (EditText) findViewById(R.id.surveyno);
		addTrees = (Button) findViewById(R.id.addtreesbut);
		tree_outside_type = (Button) findViewById(R.id.tree_outside_type);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		if(isServiceRunning() == false)
		{
			checkAndStartGPS();
		}
	}
	@Override
	protected void onPause()
	{
		stopGPStracking();
		super.onPause();
	}
	private boolean isServiceRunning() {
		  ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		  for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		    if (GPSService.class.equals(service.service.getClassName())) {
		      return true;
		    }
		  }
		  return false;
		}
	private void stopGPStracking() {
		// TODO Auto-generated method stub
		Intent gps_service = new Intent(getApplicationContext(), GPSService.class);
		stopService(gps_service);
	}
	private void checkAndStartGPS() {
		// TODO Auto-generated method stub
		String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(provider.contains("gps"))
		{
			Intent gps_service = new Intent(getApplicationContext(), GPSService.class);
			startService(gps_service);
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Activate GPS");
            builder.setCancelable(false);
            builder.setMessage("Please go to settings and activate GPS before using the app.");
          // Set the action buttons
         builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
            	 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	             startActivity(intent);
             }
         });
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
		}
	}
	@Override
	public void onClick(View v)
	{
		int viewid = v.getId();
		if(viewid == R.id.menuselect)
		{
		    this.openContextMenu(v);
		}
		if(viewid == R.id.proptype)
		{
			this.openContextMenu(v);
		}
		if(viewid == R.id.tree_outside_type)
		{
			this.openContextMenu(v);
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu m, View v, ContextMenuInfo i)
	{
		super.onCreateContextMenu(m, v, i);
		int viewid = v.getId();
		if(viewid == R.id.menuselect)
		{
		m.setHeaderTitle("Select Boundary Type");
		getMenuInflater().inflate(R.menu.activity_main, m);
		}
		else if(viewid == R.id.proptype)
		{
		m.setHeaderTitle("Select Property Type");
		getMenuInflater().inflate(R.menu.activity_main2, m);
		}
		else if(viewid == R.id.tree_outside_type)
		{
		m.setHeaderTitle("Select Tree Near");
		getMenuInflater().inflate(R.menu.activity_main3, m);
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.outsidebounds)
		{
			boundary.setText("Outside Boundary");
			setupElements(false);
			return true;
		}
		else if(id == R.id.insidebounds)
		{
			boundary.setText("Enclosed Boundary");
			setupElements(true);
			return true;
		}
		else if(id == R.id.type1)
		{
			proptype.setText("Type 1");
			return true;
		}
		else if(id == R.id.type2)
		{
			proptype.setText("Type 2");
			return true;
		}
		else if(id == R.id.type3)
		{
			proptype.setText("Type 3");
			return true;
		}
		else if(id == R.id.type4)
		{
			proptype.setText("Type 4");
			return true;
		}
		else if(id == R.id.type5)
		{
			proptype.setText("Type 5");
			return true;
		}
		else if(id == R.id.pavement)
		{
			tree_outside_type.setText("Near Pavement");
			return true;
		}
		else if(id == R.id.divider)
		{
			tree_outside_type.setText("Near Divider");
			return true;
		}
		else
		{
		return false;
		}
		
	}
	private void setupElements(boolean yesno)
	{
		if(yesno == true)
		{
			layout3.setVisibility(View.VISIBLE);
			tree_outside_type.setVisibility(View.GONE);
		}
		else
		{
			tree_outside_type.setVisibility(View.VISIBLE);
			layout3.setVisibility(View.GONE);
		}
	}
	@Override
	public void onBackPressed() {
		this.finish();
        System.exit(0);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN)
		{
		if(v.getId() == R.id.addtreesbut)
		{
			addTrees.setBackgroundResource(R.drawable.addtreesbut_pressed);
			// gather data for adding to database from fields
		}
		}
		if(action == MotionEvent.ACTION_UP)
		{
			File myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Captures/");
			if(myFolder.isDirectory() == false) // Check if folder is already there
			{
				myFolder.mkdirs(); // folder isn't there. Ergo, create directory
			}
			SQLiteDatabase db;
			db = openOrCreateDatabase(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Captures/treemapindia.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
			try
			{
			final String CREATE_TABLE_CITIES = "CREATE TABLE userdata (id INTEGER PRIMARY KEY AUTOINCREMENT, device_id INTEGER, ward_no INTEGER, cluster_no INTEGER, boundary_type TEXT, tree_near TEXT, property_type TEXT, property_owner TEXT, property_description TEXT, property_housenumber INTEGER, property_area INTEGER, surveynumber INTEGER);";
			db.execSQL(CREATE_TABLE_CITIES);
			userDB = true;
			}
			catch (SQLiteException e){
			userDB = true;
			}
			if(userDB == true)
			{
				gatherDataFromFields();
				ContentValues cv = new ContentValues();
				if(ward_no.getText().toString().equals("") || clus_no.getText().toString().equals("") || boundary.getText().toString().equals("Select Boundary Type"))
				{}
				else
				{
				cv.put("device_id", android_id);
				cv.put("ward_no", wardnum);
				cv.put("cluster_no", clusnum);
				cv.put("boundary_type", bound_type);
				if(bound_type.equals("Outside Boundary") && (proptype.getText().toString().equals("") || owner.getText().toString().equals("") || description.getText().toString().equals("") || 
						housenumber.getText().toString().equals("") || area.getText().toString().equals("") || surveynumber.getText().toString().equals("")))
				{
					cv.put("tree_near", tree_outside_type2);
				}
				else
				{
				cv.put("property_type", property_type);
				cv.put("property_owner", property_owner);
				cv.put("property_description", property_description);
				cv.put("property_housenumber", property_housenumber);
				cv.put("property_area", property_area);
				cv.put("surveynumber", surveynumber_db);
				}
				}
				db.insert("userdata", null, cv);
				Cursor cursor = db.rawQuery("SELECT * FROM userdata", new String[] {});
				cursor.moveToFirst();
				int session_id = cursor.getInt(cursor.getColumnIndex("id"));
				SharedPreferences latlong = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    	    SharedPreferences.Editor latlongeditor = latlong.edit();
	    	    latlongeditor.putInt("session_id", session_id).commit();
				db.close();
			}
			addTrees.setBackgroundResource(R.drawable.addtreesbut);
			if(ward_no.getText().toString().equals("") || clus_no.getText().toString().equals("") || boundary.getText().toString().equals("Select Boundary Type"))
			{
			}
			else
			{
			if(boundary.getText().toString().equals("Enclosed Boundary") && (proptype.getText().toString().equals("") || owner.getText().toString().equals("") || description.getText().toString().equals("") || 
					housenumber.getText().toString().equals("") || area.getText().toString().equals("") || surveynumber.getText().toString().equals("")))
			{
			}
			else
			{
			Intent intent = new Intent();
			intent.setClassName("com.advait.saravade.treemapindia", "com.advait.saravade.treemapindia.TreeSurveyForm");
			startActivity(intent);
			}
			}
		}
			return true;
	}

	private void gatherDataFromFields() {
		// TODO Auto-generated method stub
		if(ward_no.getText().toString().equals("") || clus_no.getText().toString().equals("") || boundary.getText().toString().equals("Select Boundary Type"))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Problem");
            builder.setMessage("You have to fill in all the fields!")
          // Set the action buttons
         .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
            	 // Therefore store data in db as well as clear all fields
            	 
             }
         });
   
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
		}
		else
		{
			wardnum = Integer.valueOf(ward_no.getText().toString());
			clusnum = Integer.valueOf(clus_no.getText().toString());
			bound_type = boundary.getText().toString();
			if(bound_type.equals("Enclosed Boundary"))
			{
				if(proptype.getText().toString().equals("") || owner.getText().toString().equals("") || description.getText().toString().equals("") || 
						housenumber.getText().toString().equals("") || area.getText().toString().equals("") || surveynumber.getText().toString().equals(""))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
		            builder.setTitle("Problem");
		            builder.setMessage("You have to fill in all the fields!")
		          // Set the action buttons
		         .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
		             @Override
		             public void onClick(DialogInterface dialog, int id) {
		                 //  Your code when user clicked on OK
		                 //  You can write the code  to save the selected item here
		            	 // Therefore store data in db as well as clear all fields
		            	 
		             }
		         });
		   
		            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
		            dialog.show();
				}
				else
				{
				property_type = proptype.getText().toString();
				property_owner = owner.getText().toString();
				property_description = description.getText().toString();
				property_housenumber = Integer.valueOf(housenumber.getText().toString());
				property_area = Integer.valueOf(area.getText().toString());
				surveynumber_db = Integer.valueOf(surveynumber.getText().toString());
				}
			}
			else if(bound_type.equals("Outside Boundary"))
			{
				tree_outside_type2 = tree_outside_type.getText().toString();
			}
		}
	}
}
