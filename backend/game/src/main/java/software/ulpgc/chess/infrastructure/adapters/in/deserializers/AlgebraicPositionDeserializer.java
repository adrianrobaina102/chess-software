package software.ulpgc.chess.infrastructure.adapters.in.deserializers;

import software.ulpgc.chess.architecture.model.board.File;
import software.ulpgc.chess.architecture.model.board.Rank;
import software.ulpgc.chess.architecture.model.board.Square;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;

public class AlgebraicPositionDeserializer implements PositionDeserializer {
    @Override
    public Square deserialize(String notation) {
        return new Square(File.getFile(notation.charAt(0)), Rank.getRank(Character.getNumericValue(notation.charAt(1))));
    }
}
