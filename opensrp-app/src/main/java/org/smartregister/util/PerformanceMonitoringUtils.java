package org.smartregister.util;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;

/**
 * Created by Richard Kareko on 1/25/21.
 */

public class PerformanceMonitoringUtils {

    public static Trace initTrace(String traceName) {
        SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
        if (configs.firebasePerformanceMonitoringEnabled()) {
            return FirebasePerformance.getInstance().newTrace(traceName);
        }
        return null;
    }

    public static void startTrace(Trace trace) {
        SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
        if (configs.firebasePerformanceMonitoringEnabled()) {
            trace.start();
        }
    }

    public static void stopTrace(Trace trace) {
        SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
        if (configs.firebasePerformanceMonitoringEnabled()) {
            trace.stop();
        }
    }

    public static void addAttribute(Trace trace, String attribute, String value) {
        SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
        if (configs.firebasePerformanceMonitoringEnabled()) {
            trace.putAttribute(attribute, value);
        }
    }

    public static void clearTraceAttributes(Trace trace) {
        SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
        if (configs.firebasePerformanceMonitoringEnabled()) {
            trace.getAttributes().clear();
        }
    }

}
