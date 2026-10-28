package tests.uci;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.File;
import software.ulpgc.chess.application.domain.model.Move;
import software.ulpgc.chess.application.domain.model.Rank;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.infrastructure.adapters.uci.UciMoveSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UciMoveSerializerTest {

    @Test
    public void should_serialize_center_move() {
        UciMoveSerializer serializer = new UciMoveSerializer();
        String notation = serializer.serialize(new Move(new Square(File.E, Rank.R2), new Square(File.E, Rank.R4)));

        assertEquals("e2e4", notation);
    }

    @Test
    public void should_serialize_boundary_move() {
        UciMoveSerializer serializer = new UciMoveSerializer();
        String notation = serializer.serialize(new Move(new Square(File.A, Rank.R1), new Square(File.H, Rank.R8)));

        assertEquals("a1h8", notation);
    }
}
