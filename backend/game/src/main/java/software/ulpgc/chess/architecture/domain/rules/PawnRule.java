package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.Color;
import software.ulpgc.chess.architecture.model.Square;
import software.ulpgc.chess.architecture.domain.rules.PathGenerator;

import java.util.ArrayList;
import java.util.List;

public class PawnRule implements PieceRule {
    private final Color color;
    private final int[][] directions;
    private final PathGenerator generator;
    public PawnRule(Color color, PathGenerator generator) {
        this.color = color;
        this.generator = generator;
        int step = (color == Color.White) ? 1 : -1;
        this.directions = new int[][] {
                {0, step},
                {0, 2 * step},
                {-1, step},
                {1, step}
        };
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