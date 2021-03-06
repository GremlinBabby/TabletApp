package com.example.camil.tabletapp;

import java.util.Objects;
import java.util.Random;

public class CodeGenerator {

    //This should work, but targets API 19. If tablets are old, reconsider
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public String getString () {
        return nextString();
    }

    public static final String digits = "0123456789";
    public static final String numcode = digits;

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
        this(5, random, numcode);
    }


}
