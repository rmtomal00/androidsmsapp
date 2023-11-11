package com.photocleaner.smsapp;

public class CharacterCheck {

    public boolean isAscii(char c) {
        // The Unicode code point of ASCII characters ranges from 0 to 127 (0x00 to 0x7F)
        return c >= 0 && c <= 127;
    }

}
