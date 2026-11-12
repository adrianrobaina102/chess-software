package software.ulpgc.chess.architecture.rules;

import software.ulpgc.chess.architecture.model.Square;

import java.util.ArrayList;
import java.util.List;

public class QueenRule implements PieceRule {
    private final int[][] directions = {
            {-1, 1},
            {1, 1},
            {-1, -1},
            {1, -1},
            {0, 1},
            {0, -1},
            {1, 0},
            {-1, 0}
    };
    @Override
    public List<List<Square>> possibleMoves(Square from) {
        List<List<Square>> paths = new ArrayList<>();
        for (int[] direction : directions) {
            paths.add(generatePath(direction, from));
        }
        return paths;
    }

    private List<Square> generatePath(int[] direction, Square from) {
        List<Square> path = new ArrayList<>();
        int file = from.file().ordinal() + 1;
        int rank = from.rank().symbol();

        int nextFile = file + direction[0];
        int nextRank = rank + direction[1];

        while (nextFile >= 1 && nextFile <= 8 && nextRank >= 1 && nextRank <= 8) {
            path.add(Square.of(nextFile, nextRank));
            nextFile += direction[0];
            nextRank += direction[1];
        }
        return path;
    }
}
