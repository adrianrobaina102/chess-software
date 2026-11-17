package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.board.Square;

import java.util.ArrayList;
import java.util.List;

public class BishopRule implements PieceRule {
    private final int[][] directions = {
            {-1, 1},
            {1, 1},
            {-1, -1},
            {1, -1}
    };
    private final PathGenerator generator;

    public BishopRule(PathGenerator generator) {
        this.generator = generator;
    }

    @Override
    public List<List<Square>> possibleMoves(Square from) {
        List<List<Square>> paths = new ArrayList<>();
        for (int[] direction : directions) {
            paths.add(generator.generatePath(from, direction));
        }
        return paths;
    }
}
