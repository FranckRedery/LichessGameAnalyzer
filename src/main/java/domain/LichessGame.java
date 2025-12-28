package domain;

public class LichessGame {
    String pgn;
    String gameId;
    String white;
    String black;
    int ratingWhite;
    int ratingBlack;

    public LichessGame(String pgn, String gameId, String white, String black, int ratingWhite, int ratingBlack) {
        this.pgn = pgn;
        this.gameId = gameId;
        this.white = white;
        this.black = black;
        this.ratingWhite = ratingWhite;
        this.ratingBlack = ratingBlack;
    }

    public String getPgn() {
        return pgn;
    }

    public String getGameId() {
        return gameId;
    }

    public String getWhite() {
        return white;
    }

    public int getRatingWhite() {
        return ratingWhite;
    }

    public String getBlack() {
        return black;
    }

    public int getRatingBlack() {
        return ratingBlack;
    }
}
