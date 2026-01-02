package domain;

public record LichessGame(String pgn, String gameId, String white, String black, int ratingWhite, int ratingBlack){
}
