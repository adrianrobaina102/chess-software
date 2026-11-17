package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.domain.MoveValidator;
import software.ulpgc.chess.architecture.domain.rules.PathGenerator;
import software.ulpgc.chess.architecture.domain.rules.SlidingPathGenerator;

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
