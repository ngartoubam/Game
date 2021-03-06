package com.umbrella.stfctracker.DataTypes.Enums;

import com.umbrella.stfctracker.R;

public enum Rarity {
    NONE        (-1,-1),
    COMMON      (R.color.rarity_greyBorder, R.color.rarity_greyInner),
    UNCOMMON    (R.color.rarity_greenBorder, R.color.rarity_greenInner),
    RARE        (R.color.rarity_blueBorder, R.color.rarity_blueInner),
    EPIC        (R.color.rarity_purpleBorder, R.color.rarity_purpleInner);

    private int colorBorder;
    private int colorInner;

    Rarity(int colorBorder, int colorInner) {
        this.colorBorder = colorBorder;
        this.colorInner = colorInner;
    }

    public int getColorBorder() {
        return colorBorder;
    }

    public int getColorInner() {
        return colorInner;
    }

    @Override
    public String toString() {
        String[] temp = super.name().toLowerCase().split("_");

        StringBuilder builder = new StringBuilder();
        for(String psc : temp) {
            builder.append(psc.substring(0,1).toUpperCase()).append(psc.substring(1)).append(" ");
        }

        return builder.toString().trim();
    }
}
