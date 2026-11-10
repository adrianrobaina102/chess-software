package software.ulpgc.chess.infrastructure.adapters.in;

import software.ulpgc.chess.architecture.domain.model.File;
import software.ulpgc.chess.architecture.domain.model.Rank;
import software.ulpgc.chess.architecture.domain.model.Square;
import software.ulpgc.chess.architecture.ports.in.PositionDeserializer;

public class AlgebraicPositionDeserializer implements PositionDeserializer {
    @Override
    public Square deserialize(String notation) {
        return new Square(File.getFile(notation.charAt(0)), Rank.getRank(Character.getNumericValue(notation.charAt(1))));
    }
}
