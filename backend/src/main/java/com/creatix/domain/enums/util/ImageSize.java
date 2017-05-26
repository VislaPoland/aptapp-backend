package com.creatix.domain.enums.util;

/**
 * Created by Tomas Michalek on 22/05/2017.
 */
public enum ImageSize {

    ORIGINAL(-1), LARGE(320), SMALL(640);

    int width;

    ImageSize(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }
}
