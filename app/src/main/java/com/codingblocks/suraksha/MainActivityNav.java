package com.codingblocks.suraksha;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivityNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button mButtonCallPolice, mButtonAmbulance, mButtonCallFireForce, mButtonWomenHelpLine, panicButton;
    private static final String TAG = MainActivityNav.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

//    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private boolean mAlreadyStartedService = false;
    MediaPlayer mediaPlayer;
    TextView mMsgView;

    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaPlayer = MediaPlayer.create(this, R.raw.girlshout);

        panicButton = findViewById(R.id.panicButton);
        mButtonCallPolice = findViewById(R.id.buttonCallPolice);
        mButtonAmbulance = findViewById(R.id.buttonAmbulance);
        mButtonCallFireForce = findViewById(R.id.buttonCallFireForce);
        mButtonWomenHelpLine = findViewById(R.id.buttonWomenHelpLine);

        mButtonCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivityNav.this, "Calling Police", Toast.LENGTH_SHORT).show();
                dialContactPhone("100");

            }
        });

        mButtonAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivityNav.this, "Calling Ambulance", Toast.LENGTH_SHORT).show();
                dialContactPhone("108");

            }
        });

        mButtonCallFireForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivityNav.this, "Calling Fire Force", Toast.LENGTH_SHORT).show();
                dialContactPhone("101");


            }
        });

        mButtonWomenHelpLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivityNav.this, "Calling Women Helpline", Toast.LENGTH_SHORT).show();
                dialContactPhone("1091");
            }
        });

        panicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                playsound();
                return true;
            }
        });


//        mMsgView.setText("https://www.google.com/maps/@28.7298838,76.7325634,11z");

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
//                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
//
////                        mMsgView.setText("\n Latitude : " + latitude + "\n Longitude: " + longitude);
//
//
////                        http://maps.google.com/maps?q=24.197611,120.780512
////                        http://maps.google.com/maps?q=28.6773,77.1123
//                        mMsgView.setText("https://www.google.com/maps/@28.7298838,76.7325634,11z");
//
//                    }
//                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
//        );


        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppLocationService appLocationService = new AppLocationService(
                        MainActivityNav.this);

                Location nwLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                if (nwLocation != null) {
                    latitude = nwLocation.getLatitude();
                    longitude = nwLocation.getLongitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (NW): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Enable network", Toast.LENGTH_SHORT).show();
                    showSettingsAlert("NETWORK");
                }


                //getting saved emergeny contact from the SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("ContactPreferences", MODE_PRIVATE);
                String phoneNo = sharedPreferences.getString("EMERGENCY_CONTACT", null);

                if (phoneNo == null) {

                    Toast.makeText(MainActivityNav.this, "No emergency contact saved", Toast.LENGTH_SHORT).show();
                } else {

//                    String message = "Please help... I'm in a trouble. And my current location is : https://www.google.com/maps/@28.7298838,76.7325634,11z";
//                    String message = "Please help... I'm in a trouble. And my current location is : https://www.google.com/maps/@"+latitude+ ","+longitude+",11z";
                    String message = "Please help me. This is my location" + "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

//                    String message = "Please help me. This is my location" + "http://maps.google.com/maps?saddr= 10.1416, 76.1783";

//                smsSendMessage(panicButton);

                    sendSMS(phoneNo, message); //sending message without opening message app

                }


            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    //sending message from app by opening the messaging app

//    public void smsSendMessage(View view) {
//
//        String smsNumber = String.format("smsto: %s",
//               "7594944637");
//
////String smsNumber = String.format("smsto: %s",
////               "8851735067");
////
//
////        String smsNumber = String.format("smsto: %s",
////                );
//
//
//
//        String sms = "I am in trouble . Please help me out! My current location is :- \n\n https://www.google.com/maps/@28.7298838,76.7325634,11z";
//        // Create the intent.
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//        // Set the data for the intent as the phone number.
//        smsIntent.setData(Uri.parse(smsNumber));
//        // Add the message (sms) with the key ("sms_body").
//        smsIntent.putExtra("sms_body", sms);
//        // If package resolves (target app installed), send intent.
//        if (smsIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(smsIntent);
//        } else {
//            Log.d(TAG, "Can't resolve app for ACTION_SENDTO Intent");
//        }
//
////        SmsManager smsManager = SmsManager.getDefault();
////        StringBuffer smsBody = new StringBuffer();
////        smsBody.append(Uri.parse(sms));
////        android.telephony.SmsManager.getDefault().sendTextMessage(smsNumber, null, smsBody.toString(), null,null);
//
//    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    private void playsound() {

        mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(getApplicationContext(), InformationsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.emergency_contact) {
            Intent i = new Intent(getBaseContext(), Contact.class);
            startActivity(i);

        }
//        else if (id == R.id.safe_unsafe) {
//            Intent i = new Intent(getBaseContext(), SafeUnsafeActivity.class);
//            startActivity(i);

//        }
//        else if (id == R.id.start_trip) {

//            Intent i = new Intent(getBaseContext(), StartTripActivity.class);
//            startActivity(i);


//        }
        else if (id == R.id.police) {
            Intent i = new Intent(getBaseContext(), PoliceActivity.class);
            startActivity(i);
        }
        else if (id == R.id.help) {
            Intent i = new Intent(getBaseContext(), InformationsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }


    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }


    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityNav.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService && mMsgView != null) {

            mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//
//                    android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivityNav.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
//                showSnackbar(R.string.permission_denied_explanation,
//                        R.string.settings, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
            }
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
    }

    //Setting alert box for network provider
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivityNav.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivityNav.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}