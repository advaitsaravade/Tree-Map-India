package com.advait.saravade.treemapindia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TreeSurveyForm extends Activity implements OnSeekBarChangeListener, OnClickListener, LocationListener {

	private EditText treenum;
	private TextView heading;
	private TextView heightinft;
	private AutoCompleteTextView autocomplete;
	private TextView basictxt;
	private TextView treeheighttxt;
	private TextView girthtxt;
	private TextView girthinmt;
	private TextView extratxt;
	private Button heightmeasure;
	private Button girthmeasure;
	private SeekBar seekbar1;
	private SeekBar seekbar2;
	private String heightVal;
	private String girthVal;
	private int seekprog;
	private Button extramenu1;
	private Button extramenu2;
	private Button extramenu3;
	private Button extramenu4;
	private Button extramenu5;
	private Button extramenu6;
	private ArrayList<String> nuisanceList;
	private ArrayList<String> foundOnTreeList;
	private ArrayList<String> treeStatus;
	private String treehealthList;
	private String soilconditionList;
	private String treehazardsList;
	private Button takephoto;
	private FileWriter mFileWriter;
	private boolean nothingempty = false;
	private String menuselected = null;
	
	// Database elements
	private int db_id;
	private int db_treenumber;
	private String db_treename;
	private int db_height;
	private String db_height_measure_unit;
	private int db_girth;
	private String db_girth_measure_unit;
	private String db_nuisance_on_tree;
	private String db_health_of_tree;
	private String db_soil_condition;
	private String db_found_on_tree;
	private String db_tree_status;
	private String db_tree_hazards;
	private String db_image_location;
	private String db_kml_location;
	
	private StringBuilder location_for_kml;
	private String location_for_kml2;
	
	private double lat = 0;
	private double lon = 0;
	private double alt = 0;
	
	// For Photo
	private static final int REQUEST_CODE = 1;
	private Bitmap bitmap;
    private ImageView imageView;
	// End of For Photo
	
	private String SP_KEY = "firsttimeuser";
	private String fuser;
	public boolean userDB = false;
	public boolean treeDB = false;
	
	// Calandar settings
	Calendar c = Calendar.getInstance();
	SimpleDateFormat df = new SimpleDateFormat("dd MMM yyy HH:mm:ss");
	String formattedDate = df.format(c.getTime());
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tree_survey_form);
		
		setupElements();
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String[] countries = getResources().getStringArray(R.array.trees_array);
		// Create the adapter and set it to the AutoCompleteTextView 
		ArrayAdapter<String> adapter = 
		        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
		autocomplete.setAdapter(adapter);
		
		// Setting Type Faces
		setupTypefaces();
		setupMenus();
		setupSeekBars();
		takephoto.setOnClickListener(this);
		SharedPreferences time = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    fuser = time.getString(SP_KEY, "true");
	    if(fuser.equals("false"))
	    {
	    	String heightVal_string = null;
	    	String girthVal_string = null;
	    	String girthVal_raw = time.getString("girthUnit", "");
	    	if(girthVal_raw.equals("")){
	    		girthVal = "cm";
	    	}
	    	else {
	    		girthVal = girthVal_raw;
	    	}
	    	String heightVal_raw = time.getString("heightUnit", "");
	    	if(heightVal_raw.equals("")){
	    		heightVal = "metres";
	    	}
	    	else {
	    		heightVal = heightVal_raw;
	    	}
	    	if(heightVal.equals("metres"))
	    	{
	    		heightVal_string = "In Metres";
	    		heightinft.setText("32 metres");
	    	}
	    	else if(heightVal.equals("feet"))
	    	{
	    		heightVal_string = "In Feet";
	    		heightinft.setText("32 feet");
	    	}
	    	if(girthVal.equals("cm"))
	    	{
	    		girthVal_string = "In Centimetres";
	    		girthinmt.setText("12 cm");
	    	}
	    	else if(girthVal.equals("inches"))
	    	{
	    		girthVal_string = "In Inches";
	    		girthinmt.setText("12 inches");
	    	}
	    	heightmeasure.setText(heightVal_string);
		    seekbar1.setVisibility(View.VISIBLE);
		    heightinft.setVisibility(View.VISIBLE);
	    	girthmeasure.setText(girthVal_string);
		    seekbar2.setVisibility(View.VISIBLE);
		    girthinmt.setVisibility(View.VISIBLE);
	    }
	}

	private void setupMenus() {
		// TODO Auto-generated method stub
		heightmeasure.setOnClickListener(this);
		girthmeasure.setOnClickListener(this);
		extramenu1.setOnClickListener(this);
		extramenu2.setOnClickListener(this);
		extramenu3.setOnClickListener(this);
		extramenu4.setOnClickListener(this);
		extramenu5.setOnClickListener(this);
		extramenu6.setOnClickListener(this);
		heightmeasure.setLongClickable(false);
		heightmeasure.setLongClickable(false);
		extramenu1.setLongClickable(false);
		extramenu2.setLongClickable(false);
		extramenu3.setLongClickable(false);
		extramenu4.setLongClickable(false);
		extramenu5.setLongClickable(false);
		extramenu6.setLongClickable(false);
		registerForContextMenu(heightmeasure);
		registerForContextMenu(girthmeasure);
		registerForContextMenu(extramenu1);
		registerForContextMenu(extramenu2);
		registerForContextMenu(extramenu3);
		registerForContextMenu(extramenu4);
		registerForContextMenu(extramenu5);
		registerForContextMenu(extramenu6);
	}

	private void setupSeekBars() {
		// TODO Auto-generated method stub
		seekbar1.setMax(80);
		seekbar1.setProgress(23);
		seekbar1.setOnSeekBarChangeListener(this);
		seekbar2.setMax(80);
		seekbar2.setProgress(12);
		seekbar2.setOnSeekBarChangeListener(this);
	}

	private void setupTypefaces() {
		// TODO Auto-generated method stub
		Typeface type2 = Typeface.createFromAsset(getAssets(),"GROBOLD.ttf");
		heading.setTypeface(type2);
		basictxt.setTypeface(type2);
		heightinft.setTypeface(type2);
		treeheighttxt.setTypeface(type2);
		girthtxt.setTypeface(type2);
		girthinmt.setTypeface(type2);
		extratxt.setTypeface(type2);
	}

	private void setupElements() {
		// TODO Auto-generated method stub
		treenum = (EditText) findViewById(R.id.treenumber);
		autocomplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_treecommonename);
		basictxt = (TextView) findViewById(R.id.basictxt);
		heading = (TextView) findViewById(R.id.info);
		heightinft = (TextView) findViewById(R.id.heightinft);
		treeheighttxt = (TextView) findViewById(R.id.treeheighttxt);
		girthtxt = (TextView) findViewById(R.id.girthtxt);
		girthinmt = (TextView) findViewById(R.id.girthinmt);
		extratxt = (TextView) findViewById(R.id.extratxt);
		seekbar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekbar2 = (SeekBar) findViewById(R.id.seekBar2);
		heightmeasure = (Button) findViewById(R.id.menuselect);
		girthmeasure = (Button) findViewById(R.id.menuselect2);
		extramenu1 = (Button) findViewById(R.id.extramenu1);
		extramenu2 = (Button) findViewById(R.id.extramenu2);
		extramenu3 = (Button) findViewById(R.id.extramenu3);
		extramenu4 = (Button) findViewById(R.id.extramenu4);
		extramenu5 = (Button) findViewById(R.id.extramenu5);
		extramenu6 = (Button) findViewById(R.id.extramenu6);
		takephoto = (Button) findViewById(R.id.takephotobut);
		imageView = (ImageView) findViewById(R.id.imageView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tree_survey_form, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onProgressChanged(SeekBar seekbar, int progress, boolean isUser) {
		// TODO Auto-generated method stub
		seekprog = progress;
		int whichBar = seekbar.getId();
		if(whichBar == R.id.seekBar1)
		{
		String progress_raw = String.valueOf(seekprog);
		heightinft.setText(progress_raw+" "+heightVal);
		}
		else if(whichBar == R.id.seekBar2)
		{
			String progress_raw = String.valueOf(seekprog);
			girthinmt.setText(progress_raw+" "+girthVal);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		autocomplete.setFocusable( false );
	    autocomplete.setFocusableInTouchMode( false );
	    treenum.setFocusable( false );
	    treenum.setFocusableInTouchMode( false );
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekbar) {
		// TODO Auto-generated method stub
		autocomplete.setFocusable(true);
	    autocomplete.setFocusableInTouchMode(true);
	    treenum.setFocusable(true);
	    treenum.setFocusableInTouchMode(true);
	}

	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.menuselect) // height measurement
		{
			 this.openContextMenu(v);
		}
		else if(id == R.id.menuselect2)
		{
			this.openContextMenu(v);
		}
		else if(id == R.id.extramenu1)
		{
			final CharSequence[] items = {" Nails"," Posters"," Wires"," Tree Guards", " None", " Other"};
            // array list to keep the selected items
            nuisanceList = new ArrayList();
           
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nuisance on Tree");
            builder.setMultiChoiceItems(items, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
             // indexSelected contains the index of item (of which checkbox checked)
             @Override
             public void onClick(DialogInterface dialog, int indexSelected,
                     boolean isChecked) {
                 if (isChecked) {
                     // If the user checked the item, add it to the selected items
                     // write your code when user checked the checkbox 
                     nuisanceList.add(items[indexSelected].toString());
                 } else if (nuisanceList.contains(indexSelected)) {
                     // Else, if the item is already in the array, remove it 
                     // write your code when user Uchecked the checkbox 
                     nuisanceList.remove(Integer.valueOf(indexSelected));
                 }
             }
         })
          // Set the action buttons
         .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
                isSelectedExtraMenu(v);
             }
         })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on Cancel
               
             }
         });
   
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
		}
		else if(id == R.id.extramenu2)
		{
			menuselected = "health";
			this.openContextMenu(v);
		}
		else if(id == R.id.extramenu3)
		{
			menuselected = "soil";
			this.openContextMenu(v);
		}
		else if(id == R.id.extramenu4)
		{
			final CharSequence[] items = {" Nest"," Pods / Fruits"," Flowers", " Burrows", " None"};
            // arraylist to keep the selected items
            foundOnTreeList =new ArrayList();
           
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Found on Tre");
            builder.setMultiChoiceItems(items, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
             // indexSelected contains the index of item (of which checkbox checked)
             @Override
             public void onClick(DialogInterface dialog, int indexSelected,
                     boolean isChecked) {
                 if (isChecked) {
                     // If the user checked the item, add it to the selected items
                     // write your code when user checked the checkbox 
                     foundOnTreeList.add(items[indexSelected].toString());
                 } else if (foundOnTreeList.contains(indexSelected)) {
                     // Else, if the item is already in the array, remove it 
                     // write your code when user Uchecked the checkbox 
                     foundOnTreeList.remove(Integer.valueOf(indexSelected));
                 }
             }
         })
          // Set the action buttons
         .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
            	 isSelectedExtraMenu(v);
             }
         })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on Cancel
               
             }
         });
   
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
		}
		else if(id == R.id.extramenu5)
		{
			final CharSequence[] items = {" Rare"," Vulnerable"," Endangered"," Pest Affected"," Refer to Department", " None", " Other"};
            // arraylist to keep the selected items
            treeStatus = new ArrayList();
           
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tree Stats");
            builder.setMultiChoiceItems(items, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
             // indexSelected contains the index of item (of which checkbox checked)
             @Override
             public void onClick(DialogInterface dialog, int indexSelected,
                     boolean isChecked) {
                 if (isChecked) {
                     // If the user checked the item, add it to the selected items
                     // write your code when user checked the checkbox 
                     treeStatus.add(items[indexSelected].toString());
                 } else if (treeStatus.contains(indexSelected)) {
                     // Else, if the item is already in the array, remove it 
                     // write your code when user Uchecked the checkbox 
                     treeStatus.remove(Integer.valueOf(indexSelected));
                 }
             }
         })
          // Set the action buttons
         .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
            	 isSelectedExtraMenu(v);
             }
         })
         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on Cancel
               
             }
         });
   
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
		}
		else if(id == R.id.extramenu6)
		{
			menuselected = "hazards";
			this.openContextMenu(v);
		}
		else if(id == R.id.takephotobut)
		{
			checkIfEmpty();
			if(nothingempty) // True if no fields are empty
			{
		// Take a photo now!
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		    startActivityForResult(intent, REQUEST_CODE);
		    // Write firsttimeuser to false
			SharedPreferences time = getSharedPreferences(SP_KEY, MODE_PRIVATE);
		    SharedPreferences.Editor editor = time.edit();
		    editor.putString(SP_KEY, "false").commit();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Problem");
		        builder.setMessage("You have to fill in all the fields!")
		      // Set the action buttons
		     .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
		         @Override
		         public void onClick(DialogInterface dialog, int id) {
		         }
		     });
		        AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
		        dialog.show();
			}
		}
	}
	private void isSelectedExtraMenu(View v) {
		// TODO Auto-generated method stub
			v.setBackgroundResource(R.drawable.menu_but_selected);
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
	private void stopGPStracking() {
		// TODO Auto-generated method stub
		Intent gps_service = new Intent(getApplicationContext(), GPSService.class);
		stopService(gps_service);
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
	
	private void checkAndStartGPS() {
		// TODO Auto-generated method stub
		String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(provider.contains("gps"))
		{
			// Start gps service
			Intent gps_service = new Intent(getApplicationContext(), GPSService.class);
			startService(gps_service);
			//
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Activate GPS");
            builder.setMessage("Do you want to activate GPS?");
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
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    InputStream stream = null;
	    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
	    {
	      try {
	        // We need to recyle unused bitmaps
	        if (bitmap != null) {
	          bitmap.recycle();
	        }
	        stream = getContentResolver().openInputStream(data.getData());
	        bitmap = BitmapFactory.decodeStream(stream);
	        
			File myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Captures/");
			if(myFolder.isDirectory() == false) // Check if folder is already there
			{
				myFolder.mkdirs(); // folder isn't there. Ergo, create directory
			}
			FileOutputStream out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Tree Map India/Tree Captures/" + formattedDate + ".png");
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
	        Toast.makeText(getApplicationContext(), "Saved to: " + Environment.DIRECTORY_PICTURES + "/Tree Map India/Tree Captures", Toast.LENGTH_LONG).show();
	        // Now photo has been taken. Now just add its location to the database and then upload to server!
	        // Asking user whether he/she wants to add more trees?  ALERT BOX
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add more trees?");
            builder
            // Set the action buttons
           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                 //  Your code when user clicked on OK
                 //  You can write the code  to save the selected item here
            	 // Therefore store data in db as well as clear all fields
            	 Intent intent = getIntent();
            	 finish();
            	 startActivity(intent);
            	 SQLiteDatabase db;
      			db = openOrCreateDatabase(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Captures/treemapindia.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
      			try
      			{
      			final String CREATE_TABLE_CITIES = "CREATE TABLE temptreedata (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, treenumber INTEGER, treename TEXT, height INTEGER, height_measure_unit TEXT, girth INTEGER, girth_measure_unit TEXT, nuisance_on_tree TEXT, health_of_tree TEXT, soil_condition TEXT, found_on_tree TEXT, tree_status TEXT, tree_hazards TEXT, image_location TEXT, kml_location TEXT);";
      			db.execSQL(CREATE_TABLE_CITIES);
      		    treeDB = true;
      			}
      			catch (SQLiteException e){
      			treeDB = true;
      			}
      			if(treeDB == true)
      			{
      				if(nothingempty == true)
      				{
      				gatherDataFromFields();
      				
      				SharedPreferences SP = getSharedPreferences(SP_KEY, MODE_PRIVATE);
      			    int current_session_id = SP.getInt("session_id", 1010101);
      			    
      				ContentValues cv = new ContentValues();
      				// Insert other values here
      				
      				
      				
      				
      				
      				cv.put("session_id", current_session_id);
      				cv.put("treenumber", db_treenumber);
      				cv.put("treename", db_treename);
      				cv.put("height", db_height);
      				cv.put("height_measure_unit", db_height_measure_unit);
      				cv.put("girth", db_girth);
      				cv.put("girth_measure_unit", db_girth_measure_unit);
      				cv.put("nuisance_on_tree", db_nuisance_on_tree);
      				cv.put("health_of_tree", db_health_of_tree);
      				cv.put("soil_condition", db_soil_condition);
      				cv.put("found_on_tree", db_found_on_tree);
      				cv.put("tree_status", db_tree_status);
      				cv.put("tree_hazards", db_tree_hazards);
      				cv.put("image_location", db_image_location);
      				cv.put("kml_location", db_kml_location);
      				
      				
      				
      				
      				
      				// Done inserting values
      				db.insert("treedata", null, cv);
     				db.close();
     				SharedPreferences.Editor editor = SP.edit();
     	    	    editor.putInt("session_id", 1010101).commit();
      				}
      			}
             }
         })
         .setNegativeButton("No", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on Cancel
            	//  Package data to be sent to server
            	//  End the gps tracking
     	        Intent gps_service = new Intent(getApplicationContext(), GPSService.class);
     			stopService(gps_service);
     			genKMLfile();
     			SQLiteDatabase db;
     			db = openOrCreateDatabase(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Captures/treemapindia.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
     			try
     			{
     			final String CREATE_TABLE_CITIES = "CREATE TABLE treedata (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, treenumber INTEGER, treename TEXT, height INTEGER, height_measure_unit TEXT, girth INTEGER, girth_measure_unit TEXT, nuisance_on_tree TEXT, health_of_tree TEXT, soil_condition TEXT, found_on_tree TEXT, tree_status TEXT, tree_hazards TEXT, image_location TEXT, kml_location TEXT);";
     			db.execSQL(CREATE_TABLE_CITIES);
     		    treeDB = true;
     			}
     			catch (SQLiteException e){
     			treeDB = true;
     			}
     			if(treeDB == true)
     			{
     				if(nothingempty == true)
     				{
     				gatherDataFromFields();
     				
     				SharedPreferences SP = getSharedPreferences(SP_KEY, MODE_PRIVATE);
     			    int current_session_id = SP.getInt("session_id", 1010101);
     			    
     				ContentValues cv = new ContentValues();
     				// Insert other values here
     				
     				
     				
     				
     				
     				cv.put("session_id", current_session_id);
     				cv.put("treenumber", db_treenumber);
     				cv.put("treename", db_treename);
     				cv.put("height", db_height);
     				cv.put("height_measure_unit", db_height_measure_unit);
     				cv.put("girth", db_girth);
     				cv.put("girth_measure_unit", db_girth_measure_unit);
     				cv.put("nuisance_on_tree", db_nuisance_on_tree);
     				cv.put("health_of_tree", db_health_of_tree);
     				cv.put("soil_condition", db_soil_condition);
     				cv.put("found_on_tree", db_found_on_tree);
     				cv.put("tree_status", db_tree_status);
     				cv.put("tree_hazards", db_tree_hazards);
     				cv.put("image_location", db_image_location);
     				cv.put("kml_location", db_kml_location);
     				
     				
     				
     				
     				
     				// Done inserting values
     				db.insert("treedata", null, cv);
    				db.close();
    				SharedPreferences.Editor editor = SP.edit();
    	    	    editor.putInt("session_id", 1010101).commit();
     				}
     			}
     			// Done sending data
     		    Intent intent = new Intent();
     			intent.setClassName("com.advait.saravade.treemapindia", "com.advait.saravade.treemapindia.Main");
     			startActivity(intent);
             }
         });
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
			// Done adding to database
	      } catch (FileNotFoundException e) {
	    	  Toast.makeText(getApplicationContext(), "There was an error. Image wasn't saved!", Toast.LENGTH_LONG).show();
	        e.printStackTrace();
	      }
	        if (stream != null)
	          try {
	            stream.close();
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
	    }
	}
	private void gatherDataFromFields() {
		// TODO Auto-generated method stub
		db_treenumber = Integer.valueOf(treenum.getText().toString());
		db_treename = autocomplete.getText().toString();
		db_height = seekbar1.getProgress();
		db_height_measure_unit = heightmeasure.getText().toString();
		db_girth = seekbar2.getProgress();
		db_girth_measure_unit = girthmeasure.getText().toString();
		JSONArray jsonArray1 = new JSONArray(nuisanceList);
		String jArray1 = jsonArray1.toString();
		JSONArray jsonArray2 = new JSONArray(foundOnTreeList);
		String jArray2 = jsonArray2.toString();
		JSONArray jsonArray3 = new JSONArray(treeStatus);
		String jArray3 = jsonArray3.toString();
		db_nuisance_on_tree = jArray1;
		db_found_on_tree = jArray2;
		db_tree_status = jArray3;
		db_image_location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Tree Map India/Tree Captures/" + formattedDate + ".png";
		db_kml_location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Tree Map India/Tree Location Paths/path.kml";
	}
	private void checkIfEmpty() {
		// TODO Auto-generated method stub
	if(treenum.getText().toString().equals("") || autocomplete.getText().toString().equals("") || heightinft.getText().toString().equals("") || girthinmt.getText().toString().equals("") || heightmeasure.getText().toString().equals("") || girthmeasure.getText().toString().equals("") || nuisanceList == null || foundOnTreeList == null || treeStatus == null || db_health_of_tree == null || db_soil_condition == null || db_tree_hazards == null)
	{
        nothingempty = false;
	}
	else
	{
		nothingempty = true;
	}
	}
	private void genKMLfile() {
		// TODO Auto-generated method stub
		// Generate KML file from GPSService.class
		SharedPreferences loca = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    String location_stored = loca.getString("location", "not_stored");
	    if(location_stored.equals("not_stored"))
	    {
	    	LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)1, (float)10, this);
		}
	    else
	    {
	    	// Stored! Now convert to KML file and store.
		    String KML_sample = "<?xml version=1.0' encoding='UTF-8'?>"+ 
"<kml xmlns='http://earth.google.com/kml/2.0'> <Document>"+
""+
"<Placemark>"+ 
" <name>Tree Number: "+treenum.getText()+"</name>"+
" <description>Tree Name: "+autocomplete.getText()+"</description>"+
" <Point>"+
"  <coordinates>"+
"   "+location_stored+" "+
"  </coordinates>"+
" </Point>"+
"</Placemark>"+
"</Document> </kml>";
		    
		    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    {
		    	try {
		    		File myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Location Paths/");
					if(myFolder.isDirectory() == false) // Check if folder is already there
					{
						myFolder.mkdirs(); // folder isn't there. Ergo, create directory
					}
		    		  mFileWriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Location Paths/path.kml");
		    		  mFileWriter.append(KML_sample);
		    		  mFileWriter.flush();
		    		  mFileWriter.close();
		    		} catch (IOException e) {
		    		}
		    }

	    }
	}
	@Override
	public void onCreateContextMenu(ContextMenu m, View v, ContextMenuInfo i)
	{
		super.onCreateContextMenu(m, v, i);
		int viewid = v.getId();
		if(viewid == R.id.menuselect)
		{
		m.setHeaderTitle("Select Height Unit");
		getMenuInflater().inflate(R.menu.activity_survey_height, m);
		}
		else if(viewid == R.id.menuselect2)
		{
		m.setHeaderTitle("Select Girth Unit");
		getMenuInflater().inflate(R.menu.activity_survey_girth, m);
		}
		else if(viewid == R.id.extramenu2)
		{
		m.setHeaderTitle("Health of Tree");
		getMenuInflater().inflate(R.menu.activity_survey_health, m);
		
		}
		else if(viewid == R.id.extramenu3)
		{
		m.setHeaderTitle("Soil Condition");
		getMenuInflater().inflate(R.menu.activity_survey_soil, m);
		}
		else if(viewid == R.id.extramenu4)
		{
		// Do nothing as alert box is generated
		}
		else if(viewid == R.id.extramenu5)
		{
		// Do nothing as alert box is generated
		}
		else if(viewid == R.id.extramenu6)
		{
		m.setHeaderTitle("Tree Hazards");
		getMenuInflater().inflate(R.menu.activity_survey_hazards, m);
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
	String title = String.valueOf(item.getTitle());
	int id = item.getItemId();
	if(title.equals("In Feet") || title.equals("In Metre"))
	{
		heightmeasure.setText(title);
		seekbar1.setVisibility(View.VISIBLE);
		heightinft.setVisibility(View.VISIBLE);
		SharedPreferences time = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    SharedPreferences.Editor editor = time.edit();
		if(title.equals("In Feet")) { heightVal = "feet";}
		else { heightVal = "metres";
		}
		editor.putString("heightUnit", heightVal).commit();
	}
	else if(title.equals("In Centimetre") || title.equals("In Inches"))
	{
	    girthmeasure.setText(title);
	    seekbar2.setVisibility(View.VISIBLE);
	    girthinmt.setVisibility(View.VISIBLE);
	    SharedPreferences time = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    SharedPreferences.Editor editor = time.edit();
	    if(title.equals("In Centimetre")){ girthVal = "cm";}
	    else { girthVal = "inches";}
	    editor.putString("girthUnit", girthVal).commit();
	}
    if(menuselected == "health")
	{
		isSelectedExtraMenu(findViewById(R.id.extramenu2));
		db_health_of_tree = title;
		menuselected = "";
	}
	else if(menuselected == "soil")
	{
		isSelectedExtraMenu(findViewById(R.id.extramenu3));
		db_soil_condition = title;
		menuselected = "";
	}
	else if(menuselected == "hazards")
	{
		isSelectedExtraMenu(findViewById(R.id.extramenu6));
		db_tree_hazards = title;
		menuselected = "";
	}
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(location != null)
		{
		lat = location.getLatitude();
		lon = location.getLongitude();
		alt = location.getAltitude();
    	location_for_kml2 = lat+", "+lon+", "+alt;
		}
		// Stored! Now convert to KML file and store.
	    String KML_sample = "<?xml version=1.0' encoding='UTF-8'?>"+ 
"<kml xmlns='http://earth.google.com/kml/2.0'> <Document>"+
""+
"<Placemark>"+ 
" <name>Tree Number: "+treenum.getText()+"</name>"+
" <description>Tree Name: "+autocomplete.getText()+"</description>"+
" <Point>"+
"  <coordinates>"+
"   "+location_for_kml2+" "+
"  </coordinates>"+
" </Point>"+
"</Placemark>"+
"</Document> </kml>";
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
    {
    	try {
    		File myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Location Paths/");
			if(myFolder.isDirectory() == false) // Check if folder is already there
			{
				myFolder.mkdirs(); // folder isn't there. Ergo, create directory
			}
    		  mFileWriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tree Map India/Tree Location Paths/path.kml");
    		  mFileWriter.append(KML_sample);
    		  mFileWriter.flush();
    		  mFileWriter.close();
    		} catch (IOException e) {
    		}
    }
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
}