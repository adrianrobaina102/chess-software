package software.ulpgc.chess.architecture.usecases.board;

import software.ulpgc.chess.architecture.usecases.UseCase;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Board;
import software.ulpgc.chess.architecture.model.board.Move;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.model.board.State;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.in.MoveDeserializer;
import software.ulpgc.chess.architecture.ports.out.BoardSerializer;
import software.ulpgc.chess.architecture.domain.MoveValidator;
import software.ulpgc.chess.architecture.ports.out.GamePositionSerializer;

public class MovePieceUseCase implements UseCase<MovePieceUseCase.Input, String> {

    public record Input(String board, String move) {}

    private final GamePositionDeserializer gamePositionDeserializer;
    private final MoveDeserializer moveDeserializer;
    private final GamePositionSerializer gamePositionSerializer;
    private final MoveValidator validator;

    public MovePieceUseCase(GamePositionDeserializer boardDeserializer,
                            MoveDeserializer moveDeserializer,
                            GamePositionSerializer gamePositionSerializer,
                            MoveValidator validator) {
        this.gamePositionDeserializer = boardDeserializer;
        this.moveDeserializer = moveDeserializer;
        this.gamePositionSerializer = gamePositionSerializer;
        this.validator = validator;
    }

    @Override
    public String execute(Input input) {
        return process(input);
    }

    private String process(Input input) {
        return process(gamePositionDeserializer.deserialize(input.board), input.move());
    }

    private String process(GamePosition position, String move) {
        return process(position, moveDeserializer.deserialize(move));
    }

    private String process(GamePosition position, Move move) {
        Piece piece = position.board().pieceAt(move.from());
        if(piece != null
                && piece.color() == position.state().side()
                && validator.isValid(position.board(), move) ){
            return process(position.move(move));
        }
        return gamePositionSerializer.serialize(new GamePosition(position.board(), position.state()));
    }

    private String process(GamePosition position) {
        return gamePositionSerializer.serialize(position);
    }
}
