package net.covers1624.curl4j.tests;

import java.util.Random;

/**
 * Created by covers1624 on 6/9/23.
 */
public class TestBase {


    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public static String randomHex(int len) {
        Random rand = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(HEX[rand.nextInt(HEX.length)]);
        }
        return builder.toString();
    }

    public static byte[] randomBytes(int len) {
        byte[] data = new byte[len];
        new Random().nextBytes(data);
        return data;
    }
}
