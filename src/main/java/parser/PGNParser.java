package parser;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.util.ArrayList;
import java.util.List;

public class PGNParser {

    /** Converte PGN in lista di mosse UCI */
    public static List<String> convertPgnToUciMoves(String pgn) throws Exception {
        List<String> uciMoves = new ArrayList<>();

        PgnHolder holder = new PgnHolder(pgn);
        holder.loadPgn();

        var game = holder.getGames().getFirst();
        Board board = new Board();

        for (Move move : game.getHalfMoves()) {
            uciMoves.add(move.toString()); // formato UCI
            board.doMove(move);
        }

        return uciMoves;
    }
}
