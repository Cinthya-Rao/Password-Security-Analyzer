package service;

import model.EntropyLevel;

public class EntropyCalculator {
    public double calculateEntropy(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int poolSize = 0;
        if (password.chars().anyMatch(Character::isLowerCase)) {
            poolSize += 26;
        }
        if (password.chars().anyMatch(Character::isUpperCase)) {
            poolSize += 26;
        }
        if (password.chars().anyMatch(Character::isDigit)) {
            poolSize += 10;
        }
        if (password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) {
            poolSize += 33;
        }

        return password.length() * (Math.log(poolSize) / Math.log(2));
    }

    public EntropyLevel classify(double entropyBits) {
        if (entropyBits < 40) {
            return EntropyLevel.LOW;
        }
        if (entropyBits < 60) {
            return EntropyLevel.MEDIUM;
        }
        if (entropyBits < 90) {
            return EntropyLevel.HIGH;
        }
        return EntropyLevel.VERY_HIGH;
    }

    public String estimateCrackTime(double entropyBits) {
        if (entropyBits <= 0) {
            return "Instant";
        }

        double guessesPerSecond = 1_000_000_000_000d;
        double seconds = Math.pow(2, entropyBits - 1) / guessesPerSecond;

        if (seconds < 1) {
            return "Instant";
        }
        if (seconds < 60) {
            return "Seconds";
        }
        if (seconds < 3_600) {
            return "Minutes";
        }
        if (seconds < 86_400) {
            return "Hours";
        }
        if (seconds < 2_592_000) {
            return "Days";
        }
        if (seconds < 31_536_000) {
            return "Months";
        }

        double years = seconds / 31_536_000;
        if (years < 100) {
            return "Years";
        }
        if (years < 1_000_000) {
            return "Centuries";
        }
        return "Millions of years";
    }
}
