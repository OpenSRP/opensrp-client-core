package org.smartregister.util;

import android.app.Activity;
import android.view.View;

public class Wait {
    public static void waitForProgressBarToGoAway(Activity activity) {
        waitForProgressBarToGoAway(activity, 2000);
    }

    public static void waitForProgressBarToGoAway(Activity activity, int numberOfMillisecondsToWait) {
        waitForProgressBarVisibilityToBe(activity, 500, View.VISIBLE);
        if (waitForProgressBarVisibilityToBe(activity, numberOfMillisecondsToWait, View.INVISIBLE))
            return;
        throw new RuntimeException("Timed out. Progress bar is still visible after " + numberOfMillisecondsToWait + " milliseconds.");
    }

    private static boolean waitForProgressBarVisibilityToBe(Activity activity, int numberOfMillisecondsToWait, int expectedVisibility) {
        return true;
    }

    public static void waitForFilteringToFinish() {
        sleep(400);
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

