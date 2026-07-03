package service;

import model.PatternReport;
import model.PasswordStatistics;
import model.StrengthLevel;

public class PasswordScoreCalculator {
    public int calculateScore(String password, PasswordStatistics statistics, PatternReport patterns,
                              boolean commonPassword, boolean containsCommonWord) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;
        score += lengthScore(statistics.length());
        score += statistics.lowercaseCount() > 0 ? 10 : 0;
        score += statistics.uppercaseCount() > 0 ? 10 : 0;
        score += statistics.numberCount() > 0 ? 10 : 0;
        score += statistics.symbolCount() > 0 ? 12 : 0;
        score += statistics.uniqueCharacterCount() >= Math.min(10, statistics.length()) ? 8 : 0;
        score += statistics.entropyBits() >= 70 ? 10 : statistics.entropyBits() >= 50 ? 5 : 0;
        score += isPassphrase(password) ? 8 : 0;

        if (patterns.repeatedCharacters()) {
            score -= 12;
        }
        if (patterns.repeatedWords()) {
            score -= 10;
        }
        if (patterns.keyboardWalk()) {
            score -= 14;
        }
        if (patterns.sequentialNumbers()) {
            score -= 12;
        }
        if (patterns.sequentialLetters()) {
            score -= 12;
        }
        if (patterns.dateLike()) {
            score -= 10;
        }
        if (patterns.phoneLike()) {
            score -= 16;
        }
        if (containsCommonWord) {
            score -= 10;
        }
        if (commonPassword) {
            score = Math.min(score, 15);
        }

        return Math.max(0, Math.min(100, score));
    }

    public StrengthLevel classify(int score) {
        if (score < 20) {
            return StrengthLevel.VERY_WEAK;
        }
        if (score < 40) {
            return StrengthLevel.WEAK;
        }
        if (score < 65) {
            return StrengthLevel.FAIR;
        }
        if (score < 85) {
            return StrengthLevel.STRONG;
        }
        return StrengthLevel.VERY_STRONG;
    }

    private int lengthScore(int length) {
        if (length >= 20) {
            return 32;
        }
        if (length >= 16) {
            return 28;
        }
        if (length >= 12) {
            return 22;
        }
        if (length >= 8) {
            return 12;
        }
        return length * 2;
    }

    private boolean isPassphrase(String password) {
        return password.length() >= 20 && (password.contains(" ") || password.contains("-") || password.contains("_"));
    }
}
