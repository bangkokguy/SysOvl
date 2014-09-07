package bangkokguy.android.sysovl.app;

/*import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;*/

public class L {

/*    private String moduleName = "";
    private boolean debug = false;
    private boolean logToFile = false;
    private String logFileName = "l_log.log";
    private String logFileLocation = "";
    private LogWriter logWriter;

    public L(String pModuleName) {
        this(pModuleName, false);
        Log.d("LLL","L(String pModuleName)");
    }

    public L(String pModuleName, boolean pLogToFile){
        Log.d("LLL","L(String pModuleName, boolean pLogToFile)"
                +pModuleName+"."
                +Boolean.toString(pLogToFile));
        this.moduleName = pModuleName;
        this.logToFile=pLogToFile;
        if (this.logToFile) {
            logWriter = new LogAndFile(logFileName, logFileLocation);
        } else {
            logWriter = new JustLog();
        }
    }

    public L(String pModuleName, boolean pLogToFile, String pLogFileName, String pLogFileLocation) {
        Log.d("LLL","L(String pModuleName, boolean pLogToFile, String pLogFileName, String pLogFileLocation) "
                +pModuleName+"."
                +Boolean.toString(pLogToFile)+"."
                +pLogFileName+"."
                +pLogFileLocation);
        this.moduleName = pModuleName;
        this.logToFile=pLogToFile;
        if (pLogFileName!="") this.logFileName=pLogFileName;
        if (pLogFileLocation!="") this.logFileLocation=pLogFileLocation;
        if (this.logToFile) {
            logWriter = new LogAndFile(logFileName, logFileLocation);
        } else {
            logWriter = new JustLog();
        }
    }

    private interface LogWriter {
        public void i(String msg);
        public void w(String msg);
        public void e(String msg);
        public void v(String msg);
        public void d(String msg);
        public void wtf(String msg);
    }
    private class JustLog implements LogWriter {
        public void i(String msg) {Log.i(moduleName, msg);}
        public void w(String msg) {Log.w(moduleName, msg);}
        public void e(String msg) {Log.e(moduleName, msg);}
        public void v(String msg) {Log.v(moduleName, msg);}
        public void d(String msg) {if (debug)Log.d(moduleName, msg);}
        public void wtf(String msg) {Log.wtf(moduleName, msg);}
    }
    private class LogAndFile implements LogWriter {
        private FileWriter f;

        private boolean createLogFile(String pFileName) {
            boolean b = true;
            File file = new File(pFileName);
            File filebak = new File(pFileName+".bak");
            if (file.length() > 1024*10) {
                if (filebak.exists()) {
                    if (!filebak.delete())
                        Log.d("LLL", "delete of " + filebak.toString() + " failed");
                }
                if (!file.renameTo(filebak)) {
                    Log.d("LLL","log file rename to "+filebak.toString()+" failed");
                }
                if (!file.delete()) {
                    Log.d("LLL", "delete of " + file.toString() + " failed");
                }
            }
            return b;
        }

        private String getLogFileName(String pLogFileName, String pLogFileLocation) {
            String s;
            Log.d("LLL","logfilename:"+logFileName+"{}"+logFileLocation);
            if (logFileLocation=="") {
                s=Environment.getExternalStorageDirectory().getPath();
            } else {
                s=logFileLocation;
            }
            if (s.charAt(s.length()-1)!='/') s=s+"/";

            s=s+logFileName;

            Log.i("LLL","s:"+s+":last char:"+s.charAt(s.length()-1));

            return s;

        }

        public LogAndFile(String pLogFileName, String pLogFileLocation) {
            String s;

            s=getLogFileName(pLogFileName, pLogFileLocation);
            if (createLogFile(s));

            try {
                f = new FileWriter(s, true);  //append
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i("LLL", "new FileWriter "+s+" failed");
                e.printStackTrace();
            }
        }

        public void i(String msg) {Log.i(moduleName, msg);textLog("i", msg);}
        public void w(String msg) {Log.w(moduleName, msg);textLog("w", msg);}
        public void e(String msg) {Log.e(moduleName, msg);textLog("e", msg);}
        public void v(String msg) {Log.v(moduleName, msg);textLog("v", msg);}
        public void d(String msg) {if (debug)Log.d(moduleName, msg);textLog("d", msg);}
        public void wtf(String msg) {Log.wtf(moduleName, msg);textLog("wtf", msg);}



        //@return yyyy-MM-dd HH:mm:ss formate date as string
        public String getCurrentTimeStamp(){
            try {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

                return currentTimeStamp;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        public void textLog(String level, String msg) {
            try {
                f.write(getCurrentTimeStamp()+" "+level+"/"+moduleName+":::"+msg+System.getProperty( "line.separator" ));
                //Log.d("LLL","write"+level+"/"+moduleName+":::"+msg);
                f.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("LLL","textLog io error");
                e.printStackTrace();
            }
        }

        @Override
        public void finalize() {
            try {
                f.flush();
            } catch (IOException e) {
                Log.d("LLL","flush io error");
                e.printStackTrace();
            }
            try {
                f.close();
            } catch (IOException e) {
                Log.d("LLL","close io error");
                e.printStackTrace();
            }
        }
    }

    public void setModuleName(String pModuleName) {this.moduleName = pModuleName;}

    public void i(String msg) {logWriter.i(msg);}
    public void w(String msg) {logWriter.w(msg);}
    public void e(String msg) {logWriter.e(msg);}
    public void v(String msg) {logWriter.v(msg);}
    public void d(String msg) {if (debug)logWriter.d(msg);}
    public void wtf(String msg) {logWriter.wtf(msg);}

    public void debugOn() {this.debug=true;}
    public void debugOff() {this.debug=false;}
*/}