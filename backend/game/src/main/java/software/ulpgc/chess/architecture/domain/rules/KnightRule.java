package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.domain.rules.PathGenerator;

import java.util.ArrayList;
import java.util.List;

public class KnightRule implements PieceRule{
    private final int[][] directions = {
            {2, 1},
            {2, -1},
            {-2, 1},
            {-2, -1},
            {1, 2},
            {-1, 2},
            {1, -2},
            {-1, -2}
    };
    private final PathGenerator generator;

    public KnightRule(PathGenerator generator) {
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
