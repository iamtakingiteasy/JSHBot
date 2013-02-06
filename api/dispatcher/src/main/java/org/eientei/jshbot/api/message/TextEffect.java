package org.eientei.jshbot.api.message;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 11:57
 */
public class TextEffect {
    public static final int TYPE_COLOR     = 1 << 1;
    public static final int TYPE_BOLD      = 1 << 2;
    public static final int TYPE_UNDERLINE = 1 << 3;
    public static final int TYPE_ITALIC    = 1 << 4;

    private final int begin;
    private final int end;
    private final int type;
    private final int colorValue;

    public TextEffect(int begin, int end, int type) {
        this.begin = begin;
        this.end = end;
        this.type = type;
        this.colorValue = 0;
    }

    public TextEffect(int begin, int end, int type, int colorValue) {
        this.begin = begin;
        this.end = end;
        this.type = type | TYPE_COLOR;
        this.colorValue = colorValue;
    }

    public int getColorValue() {
        return colorValue;
    }

    public int getType() {
        return type;
    }

    public int getEnd() {
        return end;
    }

    public int getBegin() {
        return begin;
    }
}
