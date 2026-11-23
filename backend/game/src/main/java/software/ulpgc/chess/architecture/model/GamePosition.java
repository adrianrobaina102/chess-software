package software.ulpgc.chess.architecture.model;

import software.ulpgc.chess.architecture.model.board.*;

import java.util.Map;

public record GamePosition (Board board, State state) {
    private static final Map<Move, Move> CASTLING_ROOK_MOVES = Map.of(
            new Move(Square.at("e1"), Square.at("g1")), new Move(Square.at("h1"), Square.at("f1")),
            new Move(Square.at("e1"), Square.at("c1")), new Move(Square.at("a1"), Square.at("d1")),
            new Move(Square.at("e8"), Square.at("g8")), new Move(Square.at("h8"), Square.at("f8")),
            new Move(Square.at("e8"), Square.at("c8")), new Move(Square.at("a8"), Square.at("d8"))
    );
    public static  GamePosition initial(){
        return new GamePosition(Board.initial(), State.init());
    }
    public GamePosition move(Move move){
        Board nextBoard = board.move(move);
        if (isCastling(move)) {
            nextBoard = moveCastlingRook(nextBoard, move);
        }
        return new GamePosition(nextBoard, state.nextTurn(move));
    }

    private Board moveCastlingRook(Board nextBoard, Move move) {
        return nextBoard.move(CASTLING_ROOK_MOVES.get(move));
    }

    private boolean isCastling(Move move) {
        Piece piece = board.pieceAt(move.from());
        return (piece == Piece.WhiteKing || piece == Piece.BlackKing)
                && CASTLING_ROOK_MOVES.containsKey(move);
    }
}
