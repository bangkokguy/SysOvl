package bangkokguy.android.sysovl.app;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    public BootCompletedReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("SysOvl", "BootCompletedReceiver->entered:");
        if (intent != null) {
            final String action = intent.getAction();
            if (action == "android.intent.action.BOOT_COMPLETED") {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(context);
                boolean p_autostart = prefs.getBoolean("p_autostart", false);
                if (p_autostart) {
                    Intent intent1 = new Intent(context, BarService.class);
                    context.startService(intent1);
                    Log.i("SysOvl", "BarService started");
                } else {
                    Log.i("SysOvl", "BarService auto start disabled");
                }
            } else {
                Log.e("SysOvl", "BootCompletedReceiver->unknown action:"+action);
            }
        }
    }
}
