package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import domain.LichessGame;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PGNParser {

    public static List<LichessGame> parseNdjson(String ndjson) {
        List<LichessGame> games = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String line : ndjson.split("\n")) {
            if (line.isBlank()) continue;
            try {
                JsonNode node = mapper.readTree(line);

                String gameId = node.get("id").asText();
                String pgn = node.get("pgn").asText();

                JsonNode players = node.get("players");
                String white = players.get("white").get("user").get("name").asText();
                String black = players.get("black").get("user").get("name").asText();

                int whiteRating = players.get("white").get("rating").asInt();
                int blackRating = players.get("black").get("rating").asInt();

                games.add(new LichessGame(pgn, gameId, white, black, whiteRating, blackRating));
            } catch (Exception e) {
                System.err.println("Errore parsing line: " + line);
                e.printStackTrace();
            }
        }

        return games;
    }


    public static List<String> convertPgnToUciMoves(String pgn) throws Exception {

        // 1. Crea file temporaneo
        Path tempPgn = Files.createTempFile("lichess-game-", ".pgn");
        Files.writeString(tempPgn, pgn, StandardCharsets.UTF_8);

        // 2. PgnHolder con filename
        PgnHolder holder = new PgnHolder(tempPgn.toString());
        holder.loadPgn();

        var game = holder.getGames().getFirst();
        Board board = new Board();
        List<String> uciMoves = new ArrayList<>();

        for (Move move : game.getHalfMoves()) {
            board.doMove(move);
            uciMoves.add(move.toString()); // UCI
        }

        // 3. Cleanup
        Files.deleteIfExists(tempPgn);

        return uciMoves;
    }



    public static Move convertUciToMove(String uciMove, Side sideToMove) {
        if (uciMove.length() < 4)
            throw new IllegalArgumentException("UCI move too short: " + uciMove);

        Square from = Square.fromValue(uciMove.substring(0, 2).toUpperCase());
        Square to = Square.fromValue(uciMove.substring(2, 4).toUpperCase());
        Move move;

        if (uciMove.length() == 5) {
            char promo = Character.toLowerCase(uciMove.charAt(4));
            Piece promotionPiece = getPromotionPiece(sideToMove, promo);
            move = new Move(from, to, promotionPiece);
        } else {
            move = new Move(from, to);
        }

        return move;
    }

    private static Piece getPromotionPiece(Side sideToMove, char promo) {
        Piece promotionPiece;
        switch (promo) {
            case 'q' -> promotionPiece = (sideToMove == Side.WHITE) ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
            case 'r' -> promotionPiece = (sideToMove == Side.WHITE) ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
            case 'b' -> promotionPiece = (sideToMove == Side.WHITE) ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
            case 'n' -> promotionPiece = (sideToMove == Side.WHITE) ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
            default -> throw new IllegalArgumentException("Invalid promotion: " + promo);
        }
        return promotionPiece;
    }

    private static String extractField(String json, String startToken, String endToken) {
        int start = json.indexOf(startToken);
        if (start == -1) return null;

        start += startToken.length();
        int end = json.indexOf(endToken, start);
        if (end == -1) return null;

        return json.substring(start, end);
    }

    private static int extractIntField(String json, String startToken, String endToken) {
        try {
            int start = json.indexOf(startToken);
            if (start == -1) return 0;

            start += startToken.length();
            int end = json.indexOf(endToken, start);

            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) {
            return 0;
        }
    }


}
