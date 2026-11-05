package tests.fen;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardDeserializer;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FenBoardSerializerTest {

    @Test
    public void should_serialize_initial_board() {
        Board board = Board.initial();
        FenBoardSerializer serializer = new FenBoardSerializer();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", serializer.serialize(board));
    }

    @Test
    public void should_serialize_midgame_board() {
        FenBoardDeserializer deserializer = new FenBoardDeserializer();
        Board board = deserializer.deserialize("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R");
        FenBoardSerializer serializer = new FenBoardSerializer();
        assertEquals("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R", serializer.serialize(board));
    }

    @Test
    public void should_serialize_endgame_board_with_many_empty_squares() {
        FenBoardDeserializer deserializer = new FenBoardDeserializer();
        Board board = deserializer.deserialize("8/8/8/4k3/8/8/4K3/8");
        FenBoardSerializer serializer = new FenBoardSerializer();
        assertEquals("8/8/8/4k3/8/8/4K3/8", serializer.serialize(board));
    }
}
