package com.example.camil.tabletapp;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class CodeGenerator {
    /**public static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static Random random = new Random();

    String randomString (int len) {
        StringBuilder stringBuilder = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            stringBuilder.append(chars.charAt(random.nextInt(chars.length() ) ) );
        return stringBuilder.toString();
    } **/
    //The above code is really just an attempt I have no idea how to implement in the main activity

    //This should work, but targets API 21. If tablets are old, reconsider
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public String getString () {
        return nextString();
    }


    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";
    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;
    private final char[] buf;

    public CodeGenerator(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }


    //Generates an alphanumeric string generator
    public CodeGenerator(int length, Random random) {
        this(length, random, alphanum);
    }


}
