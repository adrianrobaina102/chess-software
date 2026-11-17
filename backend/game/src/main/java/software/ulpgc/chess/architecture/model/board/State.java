package software.ulpgc.chess.architecture.model.board;

import software.ulpgc.chess.architecture.model.CastlingRight;

import java.util.EnumSet;
import java.util.Set;

public record State (Color side, Set<CastlingRight> castlingRights) {
    public static State init(){
        return new State(Color.White, EnumSet.allOf(CastlingRight.class));
    }
    public State nextTurn() {
        Color next = (side == Color.White) ? Color.Black : Color.White;
        return new State(next, castlingRights);
    }
}
