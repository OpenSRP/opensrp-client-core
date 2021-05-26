package org.smartregister.view.customcontrols;

public enum FontVariant {
    THIN(0, "roboto_v1.2/Roboto-Thin.ttf"), LIGHT(1, "roboto_v1.2/Roboto-Light.ttf"), REGULAR(2,
            "roboto_v1.2/Roboto-Regular.ttf"), MEDIUM(3, "roboto_v1.2/Roboto-Medium.ttf"), BOLD(4,
            "roboto_v1.2/Roboto-Bold.ttf"), BLACK(5, "roboto_v1.2/Roboto-Black.ttf");

    private int value;
    private String fontFile;

    FontVariant(int value, String fontFile) {
        this.value = value;
        this.fontFile = fontFile;
    }

    public static FontVariant tryParse(int value, FontVariant defaultValue) {
        for (FontVariant fontVariant : values()) {
            if (fontVariant.value == value) {
                return fontVariant;
            }
        }
        return defaultValue;
    }

    public String fontFile() {
        return fontFile;
    }
}
