package tests.algebraic;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.File;
import software.ulpgc.chess.application.domain.model.Rank;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.infrastructure.adapters.algebraic.AlgebraicPositionDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgebraicPositionDeserializerTest {
    @Test
    public void should_deserialize_lower_bound_position() {
        AlgebraicPositionDeserializer deserializer = new AlgebraicPositionDeserializer();
        Square square = deserializer.deserialize("a1");
        assertEquals(File.A, square.file());
        assertEquals(Rank.R1, square.rank());
    }

    @Test
    public void should_deserialize_upper_bound_position() {
        AlgebraicPositionDeserializer deserializer = new AlgebraicPositionDeserializer();
        Square square = deserializer.deserialize("h8");
        assertEquals(File.H, square.file());
        assertEquals(Rank.R8, square.rank());
    }

    @Test
    public void should_deserialize_center_position() {
        AlgebraicPositionDeserializer deserializer = new AlgebraicPositionDeserializer();
        Square square = deserializer.deserialize("e4");
        assertEquals(File.E, square.file());
        assertEquals(Rank.R4, square.rank());
    }
}
