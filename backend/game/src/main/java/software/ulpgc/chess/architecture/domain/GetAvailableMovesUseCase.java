package software.ulpgc.chess.architecture.domain;

import software.ulpgc.chess.architecture.model.Board;
import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.ports.in.BoardDeserializer;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;
import software.ulpgc.chess.architecture.ports.out.PositionSerializer;
import software.ulpgc.chess.architecture.ports.process.MoveValidator;

import java.util.List;

public class GetAvailableMovesUseCase implements UseCase<GetAvailableMovesUseCase.Input, List<String>>{


    public GetAvailableMovesUseCase(PositionSerializer serializer, PositionDeserializer positionDeserializer, MoveValidator validator, BoardDeserializer boardDeserializer) {
        this.serializer = serializer;
        this.positionDeserializer = positionDeserializer;
        this.validator = validator;
        this.boardDeserializer = boardDeserializer;
    }

    public record Input(String board, String square) {}
    private final PositionSerializer serializer;
    private final PositionDeserializer positionDeserializer;
    private final MoveValidator validator;
    private final BoardDeserializer boardDeserializer;
    @Override
    public List<String> execute(Input input) {
        return process(input);
    }

    private List<String> process(Input input) {
        return process(this.boardDeserializer.deserialize(input.board()), input.square());
    }

    private List<String> process(Board board, String square) {
        return process(board, this.positionDeserializer.deserialize(square));
    }
    private List<String> process(Board board, Square square) {
        return process(this.validator.validMoves(board, square));
    }

    private List<String> process(List<Square> squares) {
        return squares.stream().map(serializer::serialize).toList();
    }
}
