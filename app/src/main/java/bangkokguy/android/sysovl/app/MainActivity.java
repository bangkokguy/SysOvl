package bangkokguy.android.sysovl.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import static android.preference.PreferenceManager.setDefaultValues;


public class MainActivity extends Activity {

    public boolean b=false;
    private static final int RESULT_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, BarService.class);
        //startService(intent);
        ComponentName cn=startService(intent);

        if (cn != null) {
            Log.i("SysOvl", "---ComponentName:" + cn.getClassName());
        }
        else {
            Log.i("SysOvl","---ComponentName:NULL");
        }
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        TextView versionText = (TextView) findViewById(R.id.versionName);
        versionText.setText(/*"Version: " + */version);
        //setDefaultValues(this, R.xml.preferences, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void displaySharedPreferences() {
        // p_autostart
        // p_thickness
        // p_devmode
        // p_debugtext
        // p_log

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);

        boolean p_autostart   = prefs.getBoolean("p_autostart",   false);
        String  p_thickness   = prefs.getString ("p_thickness",   "NF");
        boolean p_devmode     = prefs.getBoolean("p_devmode"  ,   false);
        boolean p_chargeled   = prefs.getBoolean("p_chargeled",   false);
        boolean p_debugtext   = prefs.getBoolean("p_debugtext",   false);
        boolean p_log         = prefs.getBoolean("p_log"      ,   false);
        boolean p_textlog     = prefs.getBoolean("p_textlog",     false);
        String  p_logfilename = prefs.getString ("p_logfilename", "NF");

        StringBuilder builder = new StringBuilder();
        builder.append("autostart: " + Boolean.toString(p_autostart) + "\n");
        builder.append("thickness: " + p_thickness                   + "\n");
        builder.append("chargeled: " + Boolean.toString(p_chargeled) + "\n");
        builder.append("devmode  : " + Boolean.toString(p_devmode)   + "\n");
        builder.append("debugtext: " + Boolean.toString(p_debugtext) + "\n");
        builder.append("log      : " + Boolean.toString(p_log)       + "\n");
        builder.append("textlog  : " + Boolean.toString(p_textlog)   + "\n");
        builder.append("logfname : " + p_logfilename                 + "\n");

        TextView mytext = (TextView) findViewById(R.id.textView2);
        mytext.setText(builder.toString());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        b=false;
        //thx.interrupt();
        displaySharedPreferences();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        b = !b;
        Log.i("SysOvl", "b:"+Boolean.toString(b));
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent i = new Intent(this, ResultActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);

            }
        return super.onOptionsItemSelected(item);
    }

}
