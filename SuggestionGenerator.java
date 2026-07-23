package service;

import model.PatternReport;
import model.PasswordStatistics;

import java.util.ArrayList;
import java.util.List;

public class SuggestionGenerator {
    public List<String> generate(PasswordStatistics statistics, PatternReport patterns,
                                 boolean commonPassword, boolean containsCommonWord) {
        List<String> suggestions = new ArrayList<>();

        if (commonPassword) {
            suggestions.add("Change this password immediately; it appears in a common-password list.");
        }
        if (statistics.length() < 12) {
            suggestions.add("Increase the password to at least 12 characters.");
        } else if (statistics.length() < 16) {
            suggestions.add("Use 16 or more characters for stronger long-term protection.");
        }
        if (statistics.uppercaseCount() == 0) {
            suggestions.add("Add uppercase letters.");
        }
        if (statistics.lowercaseCount() == 0) {
            suggestions.add("Add lowercase letters.");
        }
        if (statistics.numberCount() == 0) {
            suggestions.add("Add numbers.");
        }
        if (statistics.symbolCount() == 0) {
            suggestions.add("Add symbols such as !, #, %, or ?.");
        }
        if (patterns.repeatedCharacters() || patterns.repeatedWords()) {
            suggestions.add("Avoid repeated characters, words, or chunks.");
        }
        if (patterns.sequentialLetters() || patterns.sequentialNumbers() || patterns.keyboardWalk()) {
            suggestions.add("Avoid predictable sequences and keyboard patterns.");
        }
        if (patterns.dateLike()) {
            suggestions.add("Avoid birthdays, anniversaries, or other date-like values.");
        }
        if (patterns.phoneLike()) {
            suggestions.add("Avoid phone-number-like passwords.");
        }
        if (containsCommonWord) {
            suggestions.add("Avoid common dictionary words or predictable substitutions.");
        }
        if (statistics.length() >= 12 && statistics.characterVarietyPercent() < 55) {
            suggestions.add("Increase character diversity instead of reusing the same characters.");
        }
        if (statistics.length() < 20) {
            suggestions.add("Consider a passphrase made from several unrelated random words.");
        }

        return List.copyOf(suggestions);
    }
}
