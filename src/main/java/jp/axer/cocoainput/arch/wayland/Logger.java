package jp.axer.cocoainput.arch.wayland;

import com.sun.jna.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Logger {
	
	public static void log(String msg,Object...data){
        LogManager.getLogger("CocoaInputWayland:Java").log( Level.INFO,msg, data);
    }

    public static void error(String msg,Object...data){
        LogManager.getLogger("CocoaInputWayland:Java").log( Level.ERROR,msg, data);
    }

    public static void debug(String msg,Object...data){
            LogManager.getLogger("CocoaInputWayland:Java").log( Level.DEBUG,msg, data);

    }
	
	public static Callback clangLog = new Callback() {
        public void invoke(String msg) {
            LogManager.getLogger("CocoaInputWayland:Clang").log(Level.INFO, msg);
        }
    };
    public static Callback clangError = new Callback() {
        public void invoke(String msg) {
            LogManager.getLogger("CocoaInputWayland:Clang").log(Level.ERROR, msg);
        }
    };
    public static Callback clangDebug = new Callback() {
        public void invoke(String msg) {
                LogManager.getLogger("CocoaInputWayland:Clang").log(Level.DEBUG, msg);
        }
    };
}
