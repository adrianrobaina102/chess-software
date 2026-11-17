package software.ulpgc.chess.architecture.domain.rules;

import software.ulpgc.chess.architecture.model.board.Square;

import java.util.ArrayList;
import java.util.List;

public class SingleStepPathGenerator implements PathGenerator {
    @Override
    public List<Square> generatePath(Square from, int[] direction) {
        List<Square> path = new ArrayList<>();
        int file = from.file().ordinal() + 1;
        int rank = from.rank().symbol();

        int nextFile = file + direction[0];
        int nextRank = rank + direction[1];

        if (isInside(nextFile, nextRank)) {
            path.add(Square.of(nextFile, nextRank));
        }
        return path;
    }
    private boolean isInside(int file, int rank)
    {
        return file >= 1 && file <= 8 && rank >= 1 && rank <= 8;
    }
}
