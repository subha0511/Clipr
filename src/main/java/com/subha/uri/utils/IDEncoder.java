package com.subha.uri.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IDEncoder {

    public String encodeId(Long num) {
        String symbols = "7SVQN0anC4PopJb3Aqfdk8t1ZzcWOvlYHLEeF2jIyXB569gusrKRwMGiDhxTmU";
        final int B = symbols.length();
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(symbols.charAt((int) (num % B)));
            num /= B;
        }
        return sb.reverse()
                .toString();
    }

    public String encodeId(Long num, String symbols) {
        final int B = symbols.length();
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(symbols.charAt((int) (num % B)));
            num /= B;
        }
        return sb.reverse()
                .toString();
    }

    public Long decodeId(String s, String symbols) {
        final int B = symbols.length();
        long num = 0;
        for (char ch : s.toCharArray()) {
            num *= B;
            num += symbols.indexOf(ch);
        }
        return num;
    }

    public Long decodeId(String s) {
        String symbols = "7SVQN0anC4PopJb3Aqfdk8t1ZzcWOvlYHLEeF2jIyXB569gusrKRwMGiDhxTmU";
        final int B = symbols.length();
        long num = 0;
        for (char ch : s.toCharArray()) {
            num *= B;
            num += symbols.indexOf(ch);
        }
        return num;
    }

}
