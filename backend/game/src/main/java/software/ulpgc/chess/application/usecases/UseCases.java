package software.ulpgc.chess.application.usecases;

public interface UseCases<I, O> {
    O execute(I input);


    default O execute() {
        return execute(null);
    }
}
