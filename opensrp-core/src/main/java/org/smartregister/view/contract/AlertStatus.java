package org.smartregister.view.contract;

import android.graphics.Color;

import org.smartregister.R;

public enum AlertStatus {
    EMPTY {
        public int backgroundColorResourceId() {
            return android.R.color.transparent;
        }

        public int fontColor() {
            return Color.BLACK;
        }
    }, UPCOMING {
        public int backgroundColorResourceId() {
            return R.color.alert_upcoming_light_blue;
        }

        public int fontColor() {
            return Color.BLACK;
        }
    }, NORMAL {
        public int backgroundColorResourceId() {
            return R.color.alert_in_progress_blue;
        }

        public int fontColor() {
            return Color.WHITE;
        }
    }, URGENT {
        public int backgroundColorResourceId() {
            return R.color.alert_urgent_red;
        }

        public int fontColor() {
            return Color.WHITE;
        }
    }, INPROCESS {
        public int backgroundColorResourceId() {
            return R.color.alert_complete_green;
        }

        public int fontColor() {
            return Color.WHITE;
        }
    }, COMPLETE {
        public int backgroundColorResourceId() {
            return R.color.status_bar_text_almost_white;
        }

        public int fontColor() {
            return Color.BLACK;
        }
    };

    public static AlertStatus from(String value) {
        return valueOf(value.toUpperCase());
    }

    public abstract int backgroundColorResourceId();

    public abstract int fontColor();

}
