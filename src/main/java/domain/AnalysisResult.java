package domain;


public class AnalysisResult {
    private int inaccuracies;
    private int mistakes;
    private int blunders;
    // Altri dati utili (centipawn loss, suggerimenti, ecc.)

    public int getInaccuracies() {
        return inaccuracies;
    }

    public void setInaccuracies(int inaccuracies) {
        this.inaccuracies = inaccuracies;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public int getBlunders() {
        return blunders;
    }

    public void setBlunders(int blunders) {
        this.blunders = blunders;
    }


}
