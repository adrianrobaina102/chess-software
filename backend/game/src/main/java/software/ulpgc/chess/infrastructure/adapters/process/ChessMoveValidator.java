package software.ulpgc.chess.infrastructure.adapters.process;

import software.ulpgc.chess.architecture.model.*;
import software.ulpgc.chess.architecture.ports.process.MoveValidator;
import software.ulpgc.chess.architecture.rules.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static software.ulpgc.chess.architecture.model.Piece.*;

public class ChessMoveValidator implements MoveValidator {
    private final Map<Piece, PieceRule> pieceRules;

    public ChessMoveValidator() {
        this(initialMap());
    }

    public ChessMoveValidator(Map<Piece, PieceRule> pieceRules) {
        this.pieceRules = Map.copyOf(pieceRules);
    }

    private static Map<Piece, PieceRule> initialMap() {
        PieceRule rookRule = new RookRule();
        PieceRule knightRule = new KnightRule();
        PieceRule bishopRule = new BishopRule();
        PieceRule queenRule = new QueenRule();
        PieceRule kingRule = new KingRule();

        return Map.ofEntries(
                Map.entry(WhiteRook, rookRule),
                Map.entry(BlackRook, rookRule),
                Map.entry(WhiteKnight, knightRule),
                Map.entry(BlackKnight, knightRule),
                Map.entry(WhiteBishop, bishopRule),
                Map.entry(BlackBishop, bishopRule),
                Map.entry(WhiteQueen, queenRule),
                Map.entry(BlackQueen, queenRule),
                Map.entry(WhiteKing, kingRule),
                Map.entry(BlackKing, kingRule),
                Map.entry(WhitePawn, new PawnRule(Color.White)),
                Map.entry(BlackPawn, new PawnRule(Color.Black))
        );
    }

    @Override
    public List<Square> validMoves(Board board, Square from) {
        if (from == null || board.pieceAt(from) == null) return List.of();
        Piece piece = board.pieceAt(from);
        PieceRule rule = pieceRules.get(piece);
        if (rule == null) return List.of();
        List<List<Square>> paths = rule.possibleMoves(from);
        return piece.isPawn()
                ? filterPawnMoves(board, from, paths)
                : filterStandardMoves(board, from, paths);
    }

    @Override
    public boolean isValid(Board board, Move move) {
        return validMoves(board, move.from()).contains(move.to());
    }

    private List<Square> filterStandardMoves(Board board, Square from, List<List<Square>> paths)
    {
        if(from == null) return List.of();
        return paths.stream()
                .map(path -> filterStandardPath(path, board, from))
                .flatMap(List::stream)
                .toList();
    }

    private List<Square> filterPawnMoves(Board board, Square from, List<List<Square>> paths)
    {
        if(from == null) return List.of();
        return filterPawnPath(paths.stream().flatMap(List::stream).toList(), board, from);
    }

    private List<Square> filterStandardPath(List<Square> path, Board board, Square from) {
        List<Square> emptySquares = path.stream()
                .takeWhile(p -> board.pieceAt(p) == null)
                .toList();

        return filterStandardPath(path, emptySquares, board, from);
    }

    private List<Square> filterStandardPath(List<Square> path, List<Square> emptySquares, Board board, Square from) {
        List<Square> captureSquares = path.stream()
                .dropWhile(p -> board.pieceAt(p) == null)
                .limit(1)
                .filter(s -> board.pieceAt(s).color() != board.pieceAt(from).color())
                .toList();
        return mergeMoves(emptySquares, captureSquares);
    }

    private List<Square> filterPawnPath(List<Square> path, Board board, Square from) {
        List<Square> emptySquares = isInitialPawnRank(board, from)
                ? path.stream()
                        .filter(s -> s.file() == from.file())
                        .takeWhile(s -> board.pieceAt(s) == null)
                        .toList()
                : path.stream()
                        .filter(s -> s.file() == from.file())
                        .limit(1)
                        .filter(s -> board.pieceAt(s) == null)
                        .toList();
        return filterPawnPath(path, emptySquares, board, from);
    }

    private List<Square> filterPawnPath(List<Square> path, List<Square> emptySquares, Board board, Square from) {
        List<Square> captureSquares = path.stream()
                .filter(s -> s.file() != from.file())
                .filter(s -> board.pieceAt(s) != null && board.pieceAt(s).color() != board.pieceAt(from).color())
                .toList();
        return mergeMoves(emptySquares, captureSquares);
    }

    private List<Square> mergeMoves(List<Square> emptySquares, List<Square> captureSquares) {
        return Stream.concat(emptySquares.stream(), captureSquares.stream()).toList();
    }

    private boolean isInitialPawnRank(Board board, Square from) {
        Color color = board.pieceAt(from).color();
        return (from.rank() == Rank.R2 && color == Color.White)
                || (from.rank() == Rank.R7 && color == Color.Black);
    }
}
