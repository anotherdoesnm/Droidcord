package com.atipls.chat;

public class Util {
    public static int[] resizeFit(int imgW, int imgH, int maxW, int maxH) {
        int imgAspect = imgW * 100 / imgH;
        int maxAspect = maxW * 100 / maxH;
        int width, height;

        if (imgW <= maxW && imgH <= maxH) {
            width = imgW;
            height = imgH;
        } else if (imgAspect > maxAspect) {
            width = maxW;
            height = (maxW * 100) / imgAspect;
        } else {
            height = maxH;
            width = (maxH * imgAspect) / 100;
        }

        return new int[] { width, height };
    }

    public static String fileSizeToString(int size) {
        if (size >= 1000000)
            return "" + size / 1000000 + " MB";
        if (size >= 1000)
            return "" + size / 1000 + " kB";
        return "" + size + " bytes";
    }

    public static int indexOfAny(String haystack, String[] needles,
            int startIndex) {
        int result = -1;

        for (int i = 0; i < needles.length; i++) {
            int current = haystack.indexOf(needles[i], startIndex);
            if (current != -1 && (current < result || result == -1)) {
                result = current;
            }
        }
        return result;
    }
}
