package model;

public record PasswordStatistics(
        int length,
        int uppercaseCount,
        int lowercaseCount,
        int numberCount,
        int symbolCount,
        int uniqueCharacterCount,
        double entropyBits,
        double characterVarietyPercent
) {
}
