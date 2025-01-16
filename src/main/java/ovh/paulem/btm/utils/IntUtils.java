package ovh.paulem.btm.utils;

public class IntUtils {
    public static int constrainToRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
