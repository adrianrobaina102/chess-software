package software.ulpgc.chess.architecture.model;

import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.State;

public record GamePosition (Board board, State state) {
    public static  GamePosition initial(){
        return new GamePosition(Board.initial(), State.init());
    }
    public GamePosition move(Move move){
        return new GamePosition(board.move(move), state.nextTurn());
    }
}
