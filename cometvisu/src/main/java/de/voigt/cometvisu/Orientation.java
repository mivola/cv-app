package de.voigt.cometvisu;

public enum Orientation {
    Automagisch(0), Landscape(1), Portrait(2), ReverseLandscape(3), ReversePortrait(4);
    private int value;
    Orientation(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String[] names() {
        Orientation[] values = values();
        String[] names = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }

        return names;
    }
}
