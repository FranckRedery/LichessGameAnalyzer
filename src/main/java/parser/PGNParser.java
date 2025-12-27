package parser;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.util.ArrayList;
import java.util.List;

public class PGNParser {

    public static List<String> convertPgnToUciMoves(String pgn) throws Exception {
        List<String> uciMoves = new ArrayList<>();

        PgnHolder holder = new PgnHolder(pgn);
        holder.loadPgn();

        var game = holder.getGames().getFirst();
        Board board = new Board();

        for (Move move : game.getHalfMoves()) {
            uciMoves.add(move.toString());
            board.doMove(move);
        }

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

}
