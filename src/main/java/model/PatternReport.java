package model;

import java.util.List;

public record PatternReport(
        boolean repeatedCharacters,
        boolean repeatedWords,
        boolean keyboardWalk,
        boolean sequentialNumbers,
        boolean sequentialLetters,
        boolean dateLike,
        boolean phoneLike,
        List<String> detectedPatterns
) {
    public boolean hasAnyPattern() {
        return repeatedCharacters || repeatedWords || keyboardWalk || sequentialNumbers
                || sequentialLetters || dateLike || phoneLike;
    }
}
