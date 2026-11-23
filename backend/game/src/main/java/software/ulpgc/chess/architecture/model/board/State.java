package software.ulpgc.chess.architecture.model.board;

import software.ulpgc.chess.architecture.model.CastlingRight;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record State (Color side, Set<CastlingRight> castlingRights) {
    private static final Map<Square, Set<CastlingRight>> RIGHTS_LOST = Map.of(
            Square.at("e1"), Set.of(CastlingRight.WhiteKingSide, CastlingRight.WhiteQueenSide),
            Square.at("e8"), Set.of(CastlingRight.BlackKingSide, CastlingRight.BlackQueenSide),
            Square.at("h1"), Set.of(CastlingRight.WhiteKingSide),
            Square.at("a1"), Set.of(CastlingRight.WhiteQueenSide),
            Square.at("h8"), Set.of(CastlingRight.BlackKingSide),
            Square.at("a8"), Set.of(CastlingRight.BlackQueenSide)
    );

    public static State init(){
        return new State(Color.White, EnumSet.allOf(CastlingRight.class));
    }
    public State nextTurn(Move move) {
        Color next = (side == Color.White) ? Color.Black : Color.White;
        return new State(next, updateCastlingRights(move));
    }
    private Set<CastlingRight> updateCastlingRights(Move move) {
        return castlingRights.stream()
                .filter(c -> !RIGHTS_LOST.getOrDefault(move.from(), Set.of()).contains(c))
                .filter(c -> !RIGHTS_LOST.getOrDefault(move.to(), Set.of()).contains(c))
                .collect(Collectors.toSet());
    }
}
