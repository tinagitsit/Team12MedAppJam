package com.example.medappjam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

/*
 * SHUOLD NOT BE ABLE TO ENTER HOME ACTIVITY WITHOUT LOGGING IN FIRST
 */
public class HomeActivity extends AppCompatActivity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
//    private GoogleApiClient client;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_logout:
//                startActivity(new Intent(this, LoginActivity.class));
                logout(new View(this));
                return true;

//            case R.id.action_about:
//                startActivity(new Intent(this, About.class));
//                return true;

            case R.id.action_setting:
//                startActivity(new Intent(this, SettingsActivity.class));
                settingsScreen(new View(this));
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPreferenceFile), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.user), "");

        TextView greeting = (TextView) findViewById(R.id.home_greeting);
        greeting.setText(getString(R.string.home_activity_greeting) + " " + username);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPreferenceFile), Context.MODE_PRIVATE);

        // if logged in...
        if(sharedPref.getBoolean(getString(R.string.isLoggedIn), false)) {
           String username = sharedPref.getString(getString(R.string.user), "");

            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
            int currentYear = calendar.get(Calendar.YEAR);

            DatabaseHandler db = new DatabaseHandler(this);

            InputDate prevDate = db.getDate(username);
            InputDate currentDate = new InputDate(currentDay, currentYear);

            // assumes that the calendar will never move back. If a date becomes smaller that is only because it looped.
            // causes bug where if they user opens the app a year later on the same day, it will not prompt for vitals. (Can be resolved by also keeping track of previous year)

            // if not the first time running today...
            if (prevDate != null && prevDate.getDay() != currentDate.getDay() /*&& prevDate.getYear() == currentDate.getYear()*/) {
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putBoolean(getString(R.string.user) + "_did_feel", false);
                edit.putBoolean(getString(R.string.user) + "_did_vitals", false);
                edit.commit();

                db.updateDate(username, currentDate);
            }
            else {
                // all good, forms should already be completed for the day
            }

            // the latter view is seen first since it loads ontop of the first view
            if (!sharedPref.getBoolean(getString(R.string.user) + "_did_vitals", true)) {
                startActivity(new Intent(this, NumberInputActivity.class));
            }
            if (!sharedPref.getBoolean(getString(R.string.user) + "_did_feel", true)) {
//                Toast.makeText(this, "Loading Feel", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HowYouFeelActivity.class));
            }
        }
        else {

            // user is not logged in, should redirect to login screen

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            // we shouldn't finish() here. We want to be able to return once loginng in or signing up is complete
//            finish();
        }
    }

    public void launchLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void launchRecordsTable(View view) {
        Intent intent = new Intent(this, RecordsTableActivity.class);
        startActivity(intent);
    }

    public void settingsScreen(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void myProvidersScreen(View view) {
        Intent intent = new Intent(this, MyProvidersActivity.class);
        startActivity(intent);
    }


    public void logout(View view) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPreferenceFile), Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();

        // for debug database purging
//        this.deleteDatabase("patientProviderInfo");

        onResume();
        /**
         SharedPreferences.Editor editor = sharedPref.edit();
         editor.clear();
         editor.commit();
         */
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Home Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


