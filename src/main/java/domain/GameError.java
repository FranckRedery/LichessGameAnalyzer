package domain;

import com.github.bhlangonijr.chesslib.Side;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

public class GameError {

    private final String gameId;
    private final int moveNumber;
    private final Side playerColor;

    private final ErrorSeverity severity;
    private final ErrorCategory category;
    private final GamePhase phase;
    private final double centipawnLoss;

    private final double evalBefore;
    private final double evalAfter;
    private final double bestEval;

    private final String playedMoveUci;
    private final String playedMoveSan;
    private final String bestMoveUci;

    private final String fenBefore;
    private final String fenAfter;

    private final String openingName;
    private final String openingEco;

    public GameError(
            String gameId,
            int moveNumber,
            Side playerColor,
            ErrorSeverity severity,
            ErrorCategory category,
            GamePhase phase,
            double centipawnLoss,
            double evalBefore,
            double evalAfter,
            double bestEval,
            String playedMoveUci,
            String playedMoveSan,
            String bestMoveUci,
            String fenBefore,
            String fenAfter,
            String openingName,
            String openingEco
    ) {
        this.gameId = gameId;
        this.moveNumber = moveNumber;
        this.playerColor = playerColor;
        this.severity = severity;
        this.category = category;
        this.phase = phase;
        this.centipawnLoss = centipawnLoss;
        this.evalBefore = evalBefore;
        this.evalAfter = evalAfter;
        this.bestEval = bestEval;
        this.playedMoveUci = playedMoveUci;
        this.playedMoveSan = playedMoveSan;
        this.bestMoveUci = bestMoveUci;
        this.fenBefore = fenBefore;
        this.fenAfter = fenAfter;
        this.openingName = openingName;
        this.openingEco = openingEco;
    }

    public static GameError from(RawMoveEvaluation eval,
                                 ErrorSeverity severity,
                                 ErrorCategory category) {

        return new GameError(
                eval.getGameId(),
                eval.getMoveNumber(),
                eval.getPlayerColor(),
                severity,
                category,
                eval.getPhase(),
                eval.getCentipawnLoss(),
                eval.getEvalBefore(),
                eval.getEvalAfter(),
                eval.getBestEval(),
                eval.getUciMove(),
                eval.getSanMove(),
                eval.getBestMoveUci(),
                eval.getFenBefore(),
                eval.getFenAfter(),
                eval.getOpeningName(),
                eval.getOpeningEco()
        );
    }


    public String getGameId() {
        return gameId;
    }

    public Side getPlayerColor() {
        return playerColor;
    }

    public double getEvalAfter() {
        return evalAfter;
    }

    public String getPlayedMoveUci() {
        return playedMoveUci;
    }

    public double getBestEval() {
        return bestEval;
    }

    public String getPlayedMoveSan() {
        return playedMoveSan;
    }

    public String getBestMoveUci() {
        return bestMoveUci;
    }

    public String getOpeningEco() {
        return openingEco;
    }

    public String getFenBefore() {
        return fenBefore;
    }

    public String getFenAfter() {
        return fenAfter;
    }

    public String getOpeningName() {
        return openingName;
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

    public double getEvalBefore() {
        return evalBefore;
    }
}
