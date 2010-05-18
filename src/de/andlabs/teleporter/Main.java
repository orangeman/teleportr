package de.andlabs.teleporter;

import java.net.URLDecoder;
import java.util.Map.Entry;

import de.andlabs.teleporter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    
    private static final String TAG = "Teleporter";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (getSharedPreferences("autocompletion", MODE_PRIVATE).getAll().isEmpty())
            startActivity(new Intent(this, DownloadsActivity.class));
        
        
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "newIntent: "+intent.toString());

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            MediaPlayer.create(this, R.raw.sound_long).start();
            TextView view = (TextView)findViewById(R.id.text);
            String destination = "\n\n\n"+intent.getStringExtra(SearchManager.QUERY);
            float factor = (((ViewGroup)view.getParent()).getWidth()-42) / view.getPaint().measureText(destination);
            view.setTextSize(view.getTextSize()*factor);
            view.setText(destination);
            view.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
                    new AlertDialog.Builder(Main.this)
                    .setTitle("Error")
                    .setMessage("Teleportation failed. \n May be not enough battery..")
                    .show();
                }
            });
        }
        super.onNewIntent(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.search:
            onSearchRequested();
            break;

        case R.id.settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
        case R.id.feedback:
            LogCollector.feedback(this, "flo@andlabs.de", "blah blah blah");
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}