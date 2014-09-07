/*
 *
 * a batteryre van három rutin! ezeket össze kell vonni!!! battery changed, battery power changet és getbatterystate!!!
 * dóri telója nem érzékeli a drót kihúzását/bedugását!!!
 *
 */
package bangkokguy.android.sysovl.app;

//import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
//import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
//import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
//import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class BarService extends Service {

    /*private static L l;*/

    public static Context context;
    public static WindowManager wm;
    public static Display display;
    public static Point size;
    public static int screenWidth;
    public static int screenHeight;
    public static WindowManager.LayoutParams params;

    public static BarView barView;

    public static boolean ChargeStatus;
    public static boolean uncertain_ChargeStatus;
    public static int BatteryLevel;
    public static int ChargeInput;

    public static Handler mHandler;
    public static TextView textView;
    public static IntentFilter ifilter;
    public static IntentFilter filter;
    public static IntentFilter filter1;
    public static IntentFilter filter2;

    public static Intent batteryStatus;
    //public static ActionTimeTick_RCV actionTimeTick_RCV;
    public static ScreenOnOff_RCV screenOnOff_RCV;
    public static MyBatteryStateChanged_RCV myBatteryStateChanged_RCV;
    public static MyBatteryPower_RCV myBatteryPower_RCV;
    public static MyPreferenceListener isPrefChanged;
    public static String  p_thickness   = "4";
    public static boolean p_debugtext   = false;
    public static boolean p_devmode     = false;
    public static boolean p_log         = false;
    public static boolean p_chargeled   = false;
    public static boolean p_textlog     = false;
    public static String  p_logfilename = "";

    public static NotificationManager nm;
    public static int notifyID=1;

    public static Runnable myRunnableDraw;

    public /*final*/ static int STROKE_WIDTH = 4;
    public final static int MAX_STROKE_WIDTH = 16;
    public final static int CHARGE_INPUT_USB = 0;
    public final static int CHARGE_INPUT_AC = 1;
    public final static boolean CHARGING = true;
    public final static boolean NOT_CHARGING = false;

    private final static boolean DEBUG=true;
    private final static String TAG="sysovl";

    public static int kkk=1; //just for debugging

    /*--------------------------------------------------------------*/
    /* BarService - Constructor     */
    /*--------------------------------------------------------------*/
    public BarService() {
    }

    /*==============================================================*/
    /* BarView - Class     */
    /*==============================================================*/
    public class BarView extends View {

        Paint barPaint;

        int barWidth = 1;
        int barColor = Color.RED;
        int barSpeed = 1;
        int barDrawDelay = 1;
        int barRunLength = 0;

        final float[] a = {4, 4};
        DashPathEffect DPF;
        final static int BAR_DOTTED = 1;
        final static int BAR_NO_EFFECT = 0;

        /*--------------------------------------------------------------*/
        /* barView - Constructor     */
        /*--------------------------------------------------------------*/
        public BarView(Context context) {
            super(context);
            DPF = new DashPathEffect(a, 0);
            barPaint = new Paint();
            barPaint.setStyle(Paint.Style.FILL);
            barPaint.setStyle(Paint.Style.STROKE);
            barPaint.setColor(barColor);
            barPaint.setStrokeWidth(STROKE_WIDTH);
            setBackgroundColor(Color.TRANSPARENT);
        }

        public int dltme=0; //just for debugging

        /*--------------------------------------------------------------*/
        /* setBarThickness()                                            */
        /*--------------------------------------------------------------*/
        public void setBarThickness(int thickness) {
            barPaint.setStrokeWidth(thickness);
        }

        /*--------------------------------------------------------------*/
        /* setBarColor()     */
        /*--------------------------------------------------------------*/
        public void setBarColor(int BatteryLevel) {
            int i = 0;
            int j = (int) (BatteryLevel/(100/6));
            int r = 0, g = 0, b = 0;
            int grade=(int)((BatteryLevel%(100/6))*255/(100/6));
            //debugging
            if (DEBUG) {
                if (dltme != BatteryLevel) {
                    dltme = BatteryLevel;
                    Log.d(TAG, "Battery-grade:" + Integer.toString(grade));
                }
            }
            //debugging
            switch (j) {
                case 0: //0-16 red_to_pink         255,0:255,0   ---> pink to red
                    r = 255;
                    g = 0;
                    b = 255-grade;
                    break;
                case 1: //17-33 pink_to_orange      255:0,255,0 ---> red to orange
                    r = 255;
                    g = (int)(grade/2);
                    b = 0; //255-(int)(grade)*2;if (b<0)b=0;
                    break;
                case 2: //34-50 orange_to_yellow   0,255,0:255
                    r = 255;
                    g = 128+(int)(grade/2);
                    b = 0;
                    break;
                case 3: //51-66 yellow_to_green    0,255:0,255
                    r = 255-grade;
                    g = 255;
                    b = 0;
                    break;
                case 4: //67-83 green_to_cyan         0:255,0,255
                    r = 0;
                    g = 255;
                    b = grade;
                    break;
                case 5: //84-100 cyan_to_blue
                    r = 0;
                    g = 255-grade;
                    b = 255;
                    break;
                default:
                    r = 255;
                    g = 255;
                    b = 255;
                    //L.e("setBarColor ->default");
                    break;
            }
            barColor = Color.argb(255, r, g, b);
            barPaint.setColor(barColor);
        }

        /*--------------------------------------------------------------*/
        /* incBarRunLength()                                            */
        /*--------------------------------------------------------------*/
        public void incBarRunLength(boolean ChargeStatus) {
            if (ChargeStatus == NOT_CHARGING) {
                barRunLength = 0;
            } else {
                int i;
                barRunLength = barRunLength + barSpeed;
                if (barWidth + barRunLength > screenWidth) {
                    barRunLength = 0;
                }
            }
        }

        /*--------------------------------------------------------------*/
        /* setBarSpeed()     */
        /*--------------------------------------------------------------*/
        public void setBarSpeed(boolean ChargeStatus, int BatteryLevel) {
            if (ChargeStatus == NOT_CHARGING) {
                barSpeed = 0;
                barDrawDelay = 500;
            } else {
                switch ((int) (BatteryLevel / 10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        barSpeed = 4;
                        barDrawDelay = 50;
                        break;
                    case 7:
                        barSpeed = 2;
                        barDrawDelay = 50;
                        break;
                    case 8:
                        barSpeed = 2;
                        barDrawDelay = 100;
                        break;
                    case 9:
                        barSpeed = 1;
                        barDrawDelay = 100;
                        break;
                    default:
                        barSpeed = 1;
                        barDrawDelay = 200;
                        //L.d("step case default");
                        break;
                }
            }
        }

        /*--------------------------------------------------------------*/
        /* setBarWidth()     */
        /*--------------------------------------------------------------*/
        public void setBarWidth(int ScreenWidth, int BatteryLevel) {
            barWidth = (int) (ScreenWidth * BatteryLevel / 100);
           // L.d("batterylevel"+Integer.toString(BatteryLevel));
        }

        /*--------------------------------------------------------------*/
        /* setBarPattern()     */
        /*--------------------------------------------------------------*/
        public void setBarPattern(boolean ChargeStatus, int ChargeInput) {
            if (ChargeStatus == CHARGING && ChargeInput == CHARGE_INPUT_USB) {
                barPaint. setPathEffect(DPF /*new DashPathEffect(new float[]{5, 10}, 0)*/); //dotted if usb}
            } else {
                barPaint. setPathEffect(null); //no effect otherwise}
            }
        }

        /***********************************************************************/
        /* sndLEDMessage()                                                     */
        /* turn led on with charging color                                     */
        /***********************************************************************/
        //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void sndLEDMessage() {

            if (!p_chargeled) return;

            boolean charging=false;
            int ledon=0;

            //create notification handler in order to be able to send a notification
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (ChargeStatus==CHARGING) {ledon=1;} else ledon=0;

            if (DEBUG) Log.d(TAG,Integer.toString(ledon));
            //prepare the new notification
            Notification/*Compat*/.Builder mNotifyBuilder = new Notification/*Compat*/.Builder(context)
                    .setContentTitle("Power")
                    .setContentText("BarService")//intent.getAction())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setSubText("long-long-text")
                    .setLights(barColor/*0xFF0000FF*/, ledon, 0)
                    .setPriority(Notification.PRIORITY_DEFAULT);

            //send the prepared notification
            Notification n = mNotifyBuilder.build();
            n.flags=Notification.FLAG_SHOW_LIGHTS;
            n.ledARGB = barColor;
            n.ledOnMS = ledon;
            n.ledOffMS =0;

            nm.notify(
                    notifyID,
                    n);
        }

        /*--------------------------------------------------------------*/
        /* onDraw()     */
        /*--------------------------------------------------------------*/
        public void rmvLEDMessage() {
            try {
                nm.cancel(notifyID);
            } catch (NullPointerException e) {if(DEBUG)Log.d(TAG,"rmvledmsg null pointer exception"); e.printStackTrace();}

        }

        /*--------------------------------------------------------------*/
        /* onDraw()     */
        /*--------------------------------------------------------------*/
        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawLine(0, 0, (barWidth + barRunLength), 0, barPaint);
            if (ChargeStatus==CHARGING) {
                barPaint.setColor(Color.WHITE);
                canvas.drawLine((barWidth + barRunLength), 0,(barWidth + barRunLength+2), 0, barPaint);
            } else
                if (++kkk>100) if(DEBUG)Log.d (TAG,"***ONDRAW-NOTcharging-drawline"+Float.toString((barRunLength)));kkk=1;

        }
    }
    /***********************************************************************/
    /* ActionTimeTick_RCV                                     */
    /* receives control in every minute                                    */
    /***********************************************************************/
    /*static public class ActionTimeTick_RCV extends BroadcastReceiver {

        public ActionTimeTick_RCV() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG)Log.d(TAG,"ACTION_TIME_TICK");
            if (ChargeStatus==NOT_CHARGING) {
                // draw the actual battery bar every minute
                if(DEBUG)Log.d(TAG,"draw the actual battery bar every minute");
                mHandler.post(myRunnableDraw);
            }
        }
    }*/

    /***********************************************************************/
    /* ScreenOnOff_RCV                                                     */
    /* receives screen on/off events                                       */
    /***********************************************************************/
    static public class ScreenOnOff_RCV extends BroadcastReceiver {

        public ScreenOnOff_RCV() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if(DEBUG)Log.d(TAG,"SCREEN OFF");
                if (ChargeStatus==NOT_CHARGING) {
                    context.unregisterReceiver(myBatteryStateChanged_RCV);
                } else {
                    barView.sndLEDMessage();
                }


            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if(DEBUG)Log.d(TAG,"SCREEN ON");
                context.registerReceiver(myBatteryStateChanged_RCV, filter1);
                //draw the bar in order to show the current state
                if (ChargeStatus==NOT_CHARGING) {
                    // draw the actual battery in case the phone is not charging
                    if(DEBUG)Log.d(TAG,"draw the actual battery in case the phone is not charging");
                    mHandler.post(myRunnableDraw);
                }
            }
        }
    }

    /***********************************************************************/
    /* myBatteryState_RCV()                                                */
    /* receives battery events                                             */
    /***********************************************************************/
    static public class MyBatteryPower_RCV extends BroadcastReceiver {

        public MyBatteryPower_RCV() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG)Log.d(TAG,"battery power changed:" + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                ChargeStatus = CHARGING;
                barView.sndLEDMessage();
                mHandler.post(myRunnableDraw);
            } else {
                if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    ChargeStatus = NOT_CHARGING;
                    barView.rmvLEDMessage();
                }
            }
        }
    }

    /***********************************************************************/
    /* myBatteryState_RCV()                                                */
    /* receives battery events                                             */
    /***********************************************************************/
    static public class MyBatteryStateChanged_RCV extends BroadcastReceiver {

        public MyBatteryStateChanged_RCV() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG)Log.d(TAG,"battery_state_changed:" + intent.getAction());
            if(DEBUG){
                if(intent.getAction()=="android.intent.action.ACTION_POWER_DISCONNECTED"){
                    Log.d(TAG, "new charging criteria");
                }
            }
            getBatteryStatus();
            if (ChargeStatus == NOT_CHARGING) {
                // draw the actual battery bar every minute
                if(DEBUG)Log.d(TAG,"draw the actual battery bar when battery event---"+ChargeStatus);
                mHandler.post(myRunnableDraw);
            } else {
                barView.setBarPattern(ChargeStatus, ChargeInput);
                barView.sndLEDMessage();
            }
        }
    }

    /*--------------------------------------------------------------*/
    /* getBatteryStatus() - retrieves the battery parameters*/
    /*--------------------------------------------------------------*/
    public static void getBatteryStatus() {

        batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //if(DEBUG)Log.d(TAG, "battery status:::"+Integer.toString(status));

        if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL) {
            uncertain_ChargeStatus = CHARGING;
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                ChargeInput = CHARGE_INPUT_USB;
            }
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                ChargeInput = CHARGE_INPUT_AC;
            }

        } else {
            uncertain_ChargeStatus = NOT_CHARGING;
        }
        BatteryLevel = level;
    }

    /*--------------------------------------------------------------*
     * onConfigurationChanged()                                     *
     * callback activated when screen orientation had changed       *
     *--------------------------------------------------------------*/
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        //if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        //if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)

        if (display != null) {
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
            params.width = size.x;
            params.height = size.y;
            wm.updateViewLayout(barView, params);
        }

        if(DEBUG)Log.d(TAG,"draw the actual battery bar if orientation changed");
        if (ChargeStatus==NOT_CHARGING) {
            if(DEBUG)Log.d(TAG,"draw the actual battery bar if orientation changed - if not charging");
            mHandler.post(myRunnableDraw);
        }
    }

    public static class MyPreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if(DEBUG)Log.d(TAG,"onPreferenceChange:" + s);

            for (int i = 0; i < 1; i++) {
                if (s.equals("p_thickness")) {
                    p_thickness = sharedPreferences.getString("p_thickness", "16");
                    STROKE_WIDTH = Integer.parseInt(p_thickness);
                    barView.setBarThickness(STROKE_WIDTH);
                    if(DEBUG)Log.d(TAG,"onPreferenceChange:newbarwidth" + p_thickness);
                    break;
                }
                if (s.equals("p_devmode")) {
                    p_devmode = sharedPreferences.getBoolean("p_devmode", false);
                    if (p_devmode) {/*l.debugOn();*/} else /*l.debugOff()*/;
                    break;
                }
                if (s.equals("p_debugtext")) {
                    p_debugtext = sharedPreferences.getBoolean("p_debugtext", false);
                    if (p_devmode)
                        if (p_debugtext) {
                            textView.setVisibility(View.VISIBLE);
                        } else
                            textView.setVisibility(View.INVISIBLE);
                    break;
                }
                if (s.equals("p_chargeled")) {
                    p_chargeled = sharedPreferences.getBoolean("p_chargeled", false);
                    if (!p_chargeled) barView.rmvLEDMessage();
                    break;
                }
                if (s.equals("p_textlog")) {
                    p_textlog = sharedPreferences.getBoolean("p_textlog", false);
                    /*if (p_textlog) {l=new L("SysOvl", true, p_logfilename, "");} else l=new L("SysOvl", false);*/
                    if (p_log&&p_devmode) {/*l.debugOn();*/} else /*l.debugOff()*/;
                    break;
                }
                if (s.equals("p_logfilename")) {
                    p_logfilename = sharedPreferences.getString("p_logfilename", null);
                    break;
                }
            }

            if (ChargeStatus==NOT_CHARGING) mHandler.post(myRunnableDraw);
        }
    }

    /*--------------------------------------------------------------*/
    /* initBarView() */
    /*--------------------------------------------------------------*/
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void initBarView(Context context) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        params = new
                WindowManager.LayoutParams
                (
                        screenWidth, MAX_STROKE_WIDTH,
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, //TYPE_SYSTEM_ALERT
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, //FLAG_WATCH_OUTSIDE_TOUCH,
                        PixelFormat.TRANSPARENT
                );

        params.gravity = Gravity.TOP; //CENTER

        barView = new BarView(context);

        wm.addView(barView, params);

    }

    /*--------------------------------------------------------------*/
    /* initTextView()                                               */
    /*--------------------------------------------------------------*/
    static private void initTextView(Context context) {
        textView = new TextView(context);
        textView.setText(Float.toString(BatteryLevel)+":"+Integer.toString(screenWidth));
        textView.setTextColor(Color.RED);
        textView.setBackgroundColor(Color.TRANSPARENT);
        if (p_debugtext&&p_devmode) {
            textView.setVisibility(View.VISIBLE);
        } else
            textView.setVisibility(View.INVISIBLE);

        params.width=screenWidth;
        params.height=50;
        wm.addView(textView, params);
    }

    /*--------------------------------------------------------------*/
    /* registerMyStaff() */
    /*--------------------------------------------------------------*/
    static private void registerMyStaff(Context context) {
        //register ActionTimeTick_RCV for ACTION_TIME_TICK event --- android.intent.action.TIME_TICK
     //   actionTimeTick_RCV = new ActionTimeTick_RCV();
     //   context.registerReceiver(actionTimeTick_RCV, new IntentFilter("android.intent.action.TIME_TICK"));

        //register ScreenOnOff_RCV for screen on event --- android.intent.action.TIME_TICK
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnOff_RCV = new ScreenOnOff_RCV();
        //context.registerReceiver(screenOnOff_RCV, new IntentFilter("android.intent.action.SCREEN_ON"));
        context.registerReceiver(screenOnOff_RCV, filter);

        //register MyBatteryStateChanged for battery change event
        filter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        myBatteryStateChanged_RCV = new MyBatteryStateChanged_RCV();
        context.registerReceiver(myBatteryStateChanged_RCV, filter1);

        //register MyBatteryPower for battery plug/unplug
        filter2 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        filter2.addAction(Intent.ACTION_POWER_DISCONNECTED);
        myBatteryPower_RCV = new MyBatteryPower_RCV();
        context.registerReceiver(myBatteryPower_RCV, filter2);


        //used in getBatteryStatus()
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    /*--------------------------------------------------------------*/
    /* initSharedPrefs() */
    /*--------------------------------------------------------------*/
    static private void initSharedPrefs(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        p_thickness   = prefs.getString ("p_thickness",   "16");
        p_debugtext   = prefs.getBoolean("p_debugtext",   false);
        p_chargeled   = prefs.getBoolean("p_chargeled",   false);
        p_devmode     = prefs.getBoolean("p_devmode",     false);
        p_log         = prefs.getBoolean("p_log",         false);
        p_textlog     = prefs.getBoolean("p_textlog",     false);
        p_logfilename = prefs.getString ("p_logfilename", "");

        //if (!p_devmode) {p_debugtext = false; p_log=false;}

        if (p_log&&p_devmode) {/*l.debugOn()*/;} else /*l.debugOff()*/;
        if(DEBUG)Log.d(TAG,"p_thickness:"+p_thickness);
        STROKE_WIDTH = Integer.parseInt(p_thickness);

        isPrefChanged = new MyPreferenceListener();
        prefs.registerOnSharedPreferenceChangeListener(isPrefChanged);
    }

    /*--------------------------------------------------------------*/
    /* onCreate() */
    /*--------------------------------------------------------------*/
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onCreate() {

        /*l = new L("SysOvl", false);*/
        /*l.debugOn();*/

        context = getApplicationContext();

        initSharedPrefs(context); //init shared preferences

        if (p_textlog&&p_devmode) {

            /*l = new L("SysOvl", true, p_logfilename, "");*/
            /*l.debugOn();*/
        }

        if(DEBUG)Log.d(TAG,"onCreate()");

        registerMyStaff(context); //register receivers
        initBarView(context); //init battery bar drawable
        initTextView(context); //init debug text view

        getBatteryStatus();
        ChargeStatus = uncertain_ChargeStatus;

        //init drawing loop and submit the drawer process to handler
        myRunnableDraw = new MyRunnableDraw();
        mHandler = new Handler();
        mHandler.post(myRunnableDraw);

    }



    public class MyRunnableDraw implements Runnable {
        public void run() {

            getBatteryStatus();

            barView.setBarColor(BatteryLevel);
            barView.setBarWidth(screenWidth, BatteryLevel);
            barView.setBarPattern(ChargeStatus, ChargeInput);
            barView.setBarSpeed(ChargeStatus, BatteryLevel);
            barView.incBarRunLength(ChargeStatus);
            barView.invalidate();

            if (p_debugtext&&p_devmode) {
                textView.setText(Float.toString(BatteryLevel)+":"+Integer.toString(screenWidth));
                textView.invalidate();
            }

            if (ChargeStatus==CHARGING) {
                mHandler.postDelayed(this, barView.barDrawDelay);
            }
        }
    }

    /*--------------------------------------------------------------*/
    /* IBinder ()     */
    /*--------------------------------------------------------------*/
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

}
