package software.ulpgc.chess.infrastructure.adapters.algebraic;

import software.ulpgc.chess.application.domain.model.File;
import software.ulpgc.chess.application.domain.model.Rank;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.application.ports.deserializer.PositionDeserializer;

public class AlgebraicPositionDeserializer implements PositionDeserializer {
    @Override
    public Square deserialize(String notation) {
        return new Square(File.getFile(notation.charAt(0)), Rank.getRank(Character.getNumericValue(notation.charAt(1))));
    }
}
