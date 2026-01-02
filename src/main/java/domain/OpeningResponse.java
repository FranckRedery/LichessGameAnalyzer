package domain;

public record OpeningResponse(OpeningInfo opening, Integer white, Integer draws, Integer black, Object[] moves, Object[] topGames) {

}
