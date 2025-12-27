package domain;

import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

public class GameError {

    private final ErrorSeverity severity;
    private final ErrorCategory category;
    private final GamePhase phase;
    private final int moveNumber;
    private final double centipawnLoss;

    public GameError(
            ErrorSeverity severity,
            ErrorCategory category,
            GamePhase phase,
            int moveNumber,
            double centipawnLoss) {

        this.severity = severity;
        this.category = category;
        this.phase = phase;
        this.moveNumber = moveNumber;
        this.centipawnLoss = centipawnLoss;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public double getCentipawnLoss() {
        return centipawnLoss;
    }
}
