package service;

import model.PatternReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PatternDetector {
    private static final List<String> KEYBOARD_PATTERNS = List.of(
            "qwerty", "asdf", "zxcv", "1qaz", "2wsx", "qazwsx", "poiuy", "lkjhg", "mnbvc", "qwer", "wert", "sdfg"
    );
    private static final Pattern DATE_PATTERN = Pattern.compile(
            ".*((0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[0-2])((19|20)\\d{2}|\\d{2})|((19|20)\\d{2})(0?[1-9]|1[0-2])(0?[1-9]|[12][0-9]|3[01])).*"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(".*\\d{10,}.*");

    public PatternReport detect(String password) {
        String value = password == null ? "" : password;
        String lower = value.toLowerCase(Locale.ROOT);
        List<String> detected = new ArrayList<>();

        boolean repeatedCharacters = hasRepeatedCharacters(lower);
        addIf(detected, repeatedCharacters, "Repeated characters");

        boolean repeatedWords = hasRepeatedChunk(lower);
        addIf(detected, repeatedWords, "Repeated words or chunks");

        boolean keyboardWalk = KEYBOARD_PATTERNS.stream()
                .anyMatch(pattern -> lower.contains(pattern) || lower.contains(new StringBuilder(pattern).reverse()));
        addIf(detected, keyboardWalk, "Keyboard walk");

        boolean sequentialNumbers = hasSequentialRun(lower, "0123456789") || hasSequentialRun(lower, "9876543210");
        addIf(detected, sequentialNumbers, "Sequential numbers");

        boolean sequentialLetters = hasSequentialRun(lower, "abcdefghijklmnopqrstuvwxyz")
                || hasSequentialRun(lower, "zyxwvutsrqponmlkjihgfedcba");
        addIf(detected, sequentialLetters, "Sequential letters");

        boolean dateLike = DATE_PATTERN.matcher(lower).matches();
        addIf(detected, dateLike, "Date-like value");

        boolean phoneLike = PHONE_PATTERN.matcher(lower).matches();
        addIf(detected, phoneLike, "Phone-number-like value");

        return new PatternReport(repeatedCharacters, repeatedWords, keyboardWalk, sequentialNumbers,
                sequentialLetters, dateLike, phoneLike, List.copyOf(detected));
    }

    private boolean hasRepeatedCharacters(String value) {
        return Pattern.compile("(.)\\1{2,}").matcher(value).find();
    }

    private boolean hasRepeatedChunk(String value) {
        int maxChunk = Math.min(8, value.length() / 2);
        for (int size = 2; size <= maxChunk; size++) {
            for (int start = 0; start + (size * 2) <= value.length(); start++) {
                String chunk = value.substring(start, start + size);
                String next = value.substring(start + size, start + (size * 2));
                if (chunk.equals(next)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasSequentialRun(String value, String alphabet) {
        for (int length = 4; length <= 6; length++) {
            for (int index = 0; index + length <= alphabet.length(); index++) {
                if (value.contains(alphabet.substring(index, index + length))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addIf(List<String> values, boolean condition, String label) {
        if (condition) {
            values.add(label);
        }
    }
}
