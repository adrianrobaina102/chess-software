package tests.fen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.application.domain.model.Board;
import software.ulpgc.chess.application.domain.model.Piece;
import software.ulpgc.chess.application.domain.model.Square;
import software.ulpgc.chess.infrastructure.adapters.fen.FenBoardDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

public class FenBoardDeserializerTest {
    private FenBoardDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new FenBoardDeserializer();
    }

    @Test
    @DisplayName("Debe deserializar correctamente la posición inicial de ajedrez")
    void shouldDeserializeInitialBoard() {
        String fenInitial = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board expectedBoard = Board.initial();
        Board actualBoard = deserializer.deserialize(fenInitial);

        assertThat(actualBoard).isNotNull();
        assertThat(actualBoard.pieces()).isEqualTo(expectedBoard.pieces());
    }

    @Test
    @DisplayName("Debe deserializar un tablero vacío cuando el FEN indica 8 filas vacías")
    void shouldDeserializeEmptyBoard() {
        Board board = deserializer.deserialize("8/8/8/8/8/8/8/8");

        assertThat(board).isNotNull();
        assertThat(board.pieces()).isEmpty();
    }

    @Test
    @DisplayName("Debe deserializar una posición personalizada con pocas piezas")
    void shouldDeserializeCustomPosition() {
        String fenCustom = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
        Board actualBoard = deserializer.deserialize(fenCustom);

        assertThat(actualBoard).isNotNull();
        assertThat(actualBoard.pieces())
                .hasSize(2)
                .containsEntry(Square.at("e1"), Piece.WhiteKing)
                .containsEntry(Square.at("e8"), Piece.BlackKing);
    }

    @Test
    @DisplayName("Debe ignorar los metadatos de FEN y centrarse solo en el tablero")
    void shouldIgnoreFenMetadata() {
        String fenWithMetadata = "4k3/8/8/8/8/8/8/4K3 b KQkq e3 5 12";
        Board actualBoard = deserializer.deserialize(fenWithMetadata);

        assertThat(actualBoard.pieces())
                .hasSize(2)
                .containsEntry(Square.at("e1"), Piece.WhiteKing)
                .containsEntry(Square.at("e8"), Piece.BlackKing);
    }

    @Test
    @DisplayName("Debe deserializar correctamente una posición compleja con múltiples saltos numéricos")
    void shouldDeserializeComplexPosition() {
        String fenComplex = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
        Board actualBoard = deserializer.deserialize(fenComplex);

        assertThat(actualBoard).isNotNull();
        assertThat(actualBoard.pieces())
                .containsEntry(Square.at("f3"), Piece.WhiteQueen)
                .containsEntry(Square.at("b6"), Piece.BlackKnight)
                .containsEntry(Square.at("h8"), Piece.BlackRook);
    }

    @Test
    @DisplayName("Debe colocar piezas correctamente en los bordes extremos del tablero (a8 y h1)")
    void shouldDeserializeEdgesCorrectly() {
        String fenEdges = "k7/8/8/8/8/8/8/7K w - - 0 1";
        Board actualBoard = deserializer.deserialize(fenEdges);

        assertThat(actualBoard.pieces())
                .hasSize(2)
                .containsEntry(Square.at("a8"), Piece.BlackKing)
                .containsEntry(Square.at("h1"), Piece.WhiteKing);
    }
}
