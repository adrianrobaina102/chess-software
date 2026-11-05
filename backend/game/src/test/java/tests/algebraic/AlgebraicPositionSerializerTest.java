package tests.algebraic;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.File;
import software.ulpgc.chess.application.domain.model.Rank;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.infrastructure.adapters.algebraic.AlgebraicPositionSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgebraicPositionSerializerTest {

    @Test
    public void should_serialize_lower_bound_position() {
        AlgebraicPositionSerializer serializer = new AlgebraicPositionSerializer();
        String notation = serializer.serialize(new Square(File.A, Rank.R1));

        assertEquals("a1", notation);
    }

    @Test
    public void should_serialize_upper_bound_position() {
        AlgebraicPositionSerializer serializer = new AlgebraicPositionSerializer();
        String notation = serializer.serialize(new Square(File.H, Rank.R8));

        assertEquals("h8", notation);
    }

    @Test
    public void should_serialize_center_position() {
        AlgebraicPositionSerializer serializer = new AlgebraicPositionSerializer();
        String notation = serializer.serialize(new Square(File.E, Rank.R4));

        assertEquals("e4", notation);
    }
}
