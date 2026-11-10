package software.ulpgc.chess.architecture.domain;

public interface UseCase<I, O> {
    O execute(I input);


    default O execute() {
        return execute(null);
    }
}
