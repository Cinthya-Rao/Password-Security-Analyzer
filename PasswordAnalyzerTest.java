package service;

import model.PasswordAnalysis;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordAnalyzerTest {
    private final PasswordAnalyzer analyzer = new PasswordAnalyzer();

    @Test
    void commonPasswordIsFlaggedAsWeak() {
        PasswordAnalysis analysis = analyzer.analyze("password123");

        assertTrue(analysis.commonPassword());
        assertTrue(analysis.score() <= 20);
        assertFalse(analysis.warnings().isEmpty());
    }

    @Test
    void longDiversePasswordScoresHigherThanCommonPassword() {
        PasswordAnalysis weak = analyzer.analyze("123456");
        PasswordAnalysis strong = analyzer.analyze("River#Orbit42!Lantern");

        assertTrue(strong.score() > weak.score());
        assertTrue(strong.statistics().entropyBits() > weak.statistics().entropyBits());
    }
}
