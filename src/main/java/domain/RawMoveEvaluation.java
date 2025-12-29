package domain;

import com.github.bhlangonijr.chesslib.Side;
import domain.enums.GamePhase;

public class RawMoveEvaluation {

    /* =======================
       MOVE CONTEXT
       ======================= */
    private final String gameId;
    private final Side playerColor;
    private final int moveNumber;
    private final String uciMove;
    private final String sanMove;

    private final String fenBefore;
    private final String fenAfter;

    /* =======================
       ENGINE DATA
       ======================= */
    private final double evalBefore;
    private final double evalAfter;
    private final double bestEval;
    private final String bestMoveUci;

    private final double cpLoss;
    private final double relativeCpLoss;

    private final boolean forced;
    private final int legalMovesCount;

    /* =======================
       POSITIONAL DATA
       ======================= */
    private final int materialBalance;      // centipawns
    private final int materialDelta;        // change after move
    private final GamePhase phase;
    private final Side sideToMoveAfter;

    /* =======================
       MOVE FEATURES
       ======================= */
    private final boolean capture;
    private final boolean check;
    private final boolean promotion;
    private final boolean createsMateThreat;

    /* =======================
       OPENING DATA
       ======================= */
    private final boolean inOpeningTheory;
    private final String openingName;
    private final String openingEco;

    /* =======================
       CONSTRUCTOR
       ======================= */
    public RawMoveEvaluation(
            String gameId,
            Side playerColor,
            int moveNumber,
            String uciMove,
            String sanMove,
            double evalBefore,
            double evalAfter,
            double bestEval,
            String bestMoveUci,
            boolean forced,
            int legalMovesCount,
            int materialBalance,
            int materialDelta,
            String fenBefore,
            String fenAfter,
            double cpLoss,
            double relativeCpLoss,
            GamePhase phase,
            Side sideToMoveAfter,
            boolean capture,
            boolean check,
            boolean promotion,
            boolean createsMateThreat,
            boolean inOpeningTheory,
            String openingName,
            String openingEco
    ) {
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.moveNumber = moveNumber;
        this.uciMove = uciMove;
        this.sanMove = sanMove;
        this.evalBefore = evalBefore;
        this.evalAfter = evalAfter;
        this.bestEval = bestEval;
        this.bestMoveUci = bestMoveUci;
        this.forced = forced;
        this.legalMovesCount = legalMovesCount;
        this.materialBalance = materialBalance;
        this.materialDelta = materialDelta;
        this.fenBefore = fenBefore;
        this.fenAfter = fenAfter;
        this.cpLoss = cpLoss;
        this.relativeCpLoss = relativeCpLoss;
        this.phase = phase;
        this.sideToMoveAfter = sideToMoveAfter;
        this.capture = capture;
        this.check = check;
        this.promotion = promotion;
        this.createsMateThreat = createsMateThreat;
        this.inOpeningTheory = inOpeningTheory;
        this.openingName = openingName;
        this.openingEco = openingEco;
    }

    /* =======================
       GETTERS
       ======================= */

    public String getGameId() { return gameId; }
    public Side getPlayerColor() { return playerColor; }
    public int getMoveNumber() { return moveNumber; }
    public String getUciMove() { return uciMove; }
    public String getSanMove() { return sanMove; }

    public double getEvalBefore() { return evalBefore; }
    public double getEvalAfter() { return evalAfter; }
    public double getBestEval() { return bestEval; }
    public String getBestMoveUci() { return bestMoveUci; }

    public double getCentipawnLoss() { return cpLoss; }
    public double getRelativeCpLoss() { return relativeCpLoss; }

    public boolean isForced() { return forced; }
    public int getLegalMovesCount() { return legalMovesCount; }

    public int getMaterialBalance() { return materialBalance; }
    public int getMaterialDelta() { return materialDelta; }

    public GamePhase getPhase() { return phase; }
    public Side getSideToMoveAfter() { return sideToMoveAfter; }

    public boolean isCapture() { return capture; }
    public boolean isCheck() { return check; }
    public boolean isPromotion() { return promotion; }
    public boolean createsMateThreat() { return createsMateThreat; }

    public boolean isInOpeningTheory() { return inOpeningTheory; }
    public String getOpeningName() { return openingName; }
    public String getOpeningEco() { return openingEco; }

    public String getFenBefore() { return fenBefore; }
    public String getFenAfter() { return fenAfter; }
}
