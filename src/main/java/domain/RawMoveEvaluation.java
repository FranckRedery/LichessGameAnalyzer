package domain;


public class RawMoveEvaluation {

    private final int moveNumber;
    private final double centipawnLoss;

    public RawMoveEvaluation(int moveNumber, double centipawnLoss) {
        this.moveNumber = moveNumber;
        this.centipawnLoss = centipawnLoss;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public double getCentipawnLoss() {
        return centipawnLoss;
    }
}
