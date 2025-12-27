package domain;


import com.github.bhlangonijr.chesslib.Side;
import domain.enums.GamePhase;

public class RawMoveEvaluation {

    private final Side playerColor;
    private final int moveNumber;
    private final String uciMove;

    private final double evalBefore;
    private final double evalAfter;
    private final double cpLoss;
    private final double relativeCpLoss;

    private final boolean forced;
    private final int legalMovesCount;

    private final int materialBalance; // centipawns
    private final GamePhase phase;

    private final boolean capture;
    private final boolean check;
    private final boolean promotion;

    private final String fenBefore;
    private final String fenAfter;


    public RawMoveEvaluation(Side playerColor, int moveNumber, String uciMove, double evalBefore, double evalAfter, boolean forced, int legalMovesCount, int materialBalance, String fenBefore, String fenAfter, double cpLoss, double relativeCpLoss ,GamePhase phase, boolean capture, boolean check, boolean promotion) {
        this.playerColor = playerColor;
        this.moveNumber = moveNumber;
        this.uciMove = uciMove;
        this.evalBefore = evalBefore;
        this.evalAfter = evalAfter;
        this.forced = forced;
        this.legalMovesCount = legalMovesCount;
        this.materialBalance = materialBalance;
        this.fenBefore = fenBefore;
        this.fenAfter = fenAfter;
        this.cpLoss = cpLoss;
        this.relativeCpLoss = relativeCpLoss;
        this.phase = phase;
        this.capture = capture;
        this.check = check;
        this.promotion = promotion;
    }

    public boolean isCapture() {
        return capture;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public double getRelativeCpLoss() {
        return relativeCpLoss;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public double getCentipawnLoss() {
        return cpLoss;
    }

    public Side getPlayerColor() {
        return playerColor;
    }

    public double getEvalBefore() {
        return evalBefore;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getUciMove() {
        return uciMove;
    }

    public double getEvalAfter() {
        return evalAfter;
    }

    public int getLegalMovesCount() {
        return legalMovesCount;
    }

    public boolean isForced() {
        return forced;
    }

    public String getFenBefore() {
        return fenBefore;
    }

    public String getFenAfter() {
        return fenAfter;
    }

    public int getMaterialBalance() {
        return materialBalance;
    }

}
