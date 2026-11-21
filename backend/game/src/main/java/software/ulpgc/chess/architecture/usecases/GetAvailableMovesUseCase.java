package software.ulpgc.chess.architecture.usecases;

import software.ulpgc.chess.architecture.model.GamePosition;
import software.ulpgc.chess.architecture.model.board.Piece;
import software.ulpgc.chess.architecture.model.board.Square;
import software.ulpgc.chess.architecture.ports.in.GamePositionDeserializer;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;
import software.ulpgc.chess.architecture.domain.MoveValidator;

import java.util.List;

public class GetAvailableMovesUseCase implements UseCase<GetAvailableMovesUseCase.Input, List<String>>{


    public GetAvailableMovesUseCase(PositionSerializer serializer, PositionDeserializer positionDeserializer, MoveValidator validator, GamePositionDeserializer gamePositionDeserializer) {
        this.serializer = serializer;
        this.positionDeserializer = positionDeserializer;
        this.validator = validator;
        this.gamePositionDeserializer = gamePositionDeserializer;
    }

    public record Input(String board, String square) {}
    private final PositionSerializer serializer;
    private final PositionDeserializer positionDeserializer;
    private final MoveValidator validator;
    private final GamePositionDeserializer gamePositionDeserializer;
    @Override
    public List<String> execute(Input input) {
        return process(input);
    }

    private List<String> process(Input input) {
        return process(this.gamePositionDeserializer.deserialize(input.board()), input.square());
    }

    private List<String> process(GamePosition position, String square) {
        return process(position, this.positionDeserializer.deserialize(square));
    }
    private List<String> process(GamePosition position, Square square) {
        Piece piece = position.board().pieceAt(square);
        if (piece == null || piece.color() != position.state().side()) {
            return List.of();
        }
        return process(this.validator.validMoves(position.board(), square));
    }

    private List<String> process(List<Square> squares) {
        return squares.stream().map(serializer::serialize).toList();
    }
}
