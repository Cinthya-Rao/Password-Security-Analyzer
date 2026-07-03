package service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?/";
    private static final String AMBIGUOUS = "0O1Il|`'";

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate(int length, boolean uppercase, boolean lowercase, boolean numbers,
                           boolean symbols, boolean excludeAmbiguous) {
        if (length < 8 || length > 64) {
            throw new IllegalArgumentException("Length must be between 8 and 64.");
        }

        List<String> enabledPools = new ArrayList<>();
        if (uppercase) {
            enabledPools.add(UPPERCASE);
        }
        if (lowercase) {
            enabledPools.add(LOWERCASE);
        }
        if (numbers) {
            enabledPools.add(NUMBERS);
        }
        if (symbols) {
            enabledPools.add(SYMBOLS);
        }
        if (enabledPools.isEmpty()) {
            throw new IllegalArgumentException("Select at least one character type.");
        }

        List<Character> characters = new ArrayList<>();
        for (String pool : enabledPools) {
            characters.add(randomCharacter(cleanPool(pool, excludeAmbiguous)));
        }

        String combinedPool = enabledPools.stream()
                .map(pool -> cleanPool(pool, excludeAmbiguous))
                .reduce("", String::concat);

        while (characters.size() < length) {
            characters.add(randomCharacter(combinedPool));
        }

        Collections.shuffle(characters, secureRandom);
        StringBuilder result = new StringBuilder(length);
        characters.forEach(result::append);
        return result.toString();
    }

    private String cleanPool(String pool, boolean excludeAmbiguous) {
        String cleaned = pool;
        if (excludeAmbiguous) {
            for (char character : AMBIGUOUS.toCharArray()) {
                cleaned = cleaned.replace(String.valueOf(character), "");
            }
        }
        return cleaned;
    }

    private char randomCharacter(String pool) {
        if (pool.isEmpty()) {
            throw new IllegalArgumentException("Character pool cannot be empty.");
        }
        return pool.charAt(secureRandom.nextInt(pool.length()));
    }
}
