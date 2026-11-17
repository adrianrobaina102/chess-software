package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.domain.rules.PathGenerator;

import java.util.ArrayList;
import java.util.List;
public class RookRule implements PieceRule {
    private final int[][] directions = {
            {0, 1},
            {0, -1},
            {1, 0},
            {-1, 0}
    };
    private final PathGenerator generator;

    public RookRule(PathGenerator generator) {
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