package com.shollmann.events.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.shollmann.events.ui.EventbriteApplication;

public class ResourcesHelper {
    private static DisplayMetrics metrics = new DisplayMetrics();
    private static Point screenSize = new Point();

    static {
        setScreenSize();
    }

    public static void setScreenSize() {
        ((WindowManager) EventbriteApplication.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(metrics);
        screenSize.x = ((WindowManager) EventbriteApplication.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        screenSize.y = ((WindowManager) EventbriteApplication.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }

    public static Point getScreenSize() {
        return screenSize;
    }

    public static boolean isLandscape() {
        setScreenSize();
        return (screenSize.x > screenSize.y);
    }

    public static DisplayMetrics getMetrics() {
        return metrics;
    }

    public static String getString(int resource) {
        return EventbriteApplication.getApplication().getString(resource);
    }

    public static String getString(int resource, Object... formatArgs) {
        return EventbriteApplication.getApplication().getString(resource, formatArgs);
    }

    public static Resources getResources() {
        return EventbriteApplication.getApplication().getResources();
    }

    public static String[] getStringArray(int resource) {
        return EventbriteApplication.getApplication().getResources().getStringArray(resource);
    }

    public static int getDimensionPixelSize(int resource) {
        EventbriteApplication app = EventbriteApplication.getApplication();
        TypedValue rawValue = new TypedValue();
        app.getResources().getValue(resource, rawValue, true);
        if (rawValue.type == TypedValue.TYPE_FIRST_INT) {
            return app.getResources().getInteger(resource);
        } else {
            return app.getResources().getDimensionPixelSize(resource);
        }
    }

    public static Drawable getDrawable(int resource) {
        return EventbriteApplication.getApplication().getResources().getDrawable(resource);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}
