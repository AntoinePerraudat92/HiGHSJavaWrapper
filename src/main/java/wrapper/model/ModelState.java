package wrapper.model;


import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.ModelStateException;

import java.util.function.Supplier;

@NullMarked
class ModelState {

    @Getter
    private State state = State.BUILDING;

    void onSolveRequested() {
        setNewState(State.SOLVING);
    }

    void onModelChangeRequested() {
        setNewState(State.BUILDING);
    }

    void onSolveCompleted() {
        setNewState(State.SOLVED);
    }

    private synchronized void setNewState(final State nextState) {
        checkTransition(this.state, nextState);
        this.state = nextState;
    }

    private enum State {
        BUILDING, SOLVED, SOLVING
    }

    private static void checkTransition(final State currentState, final State nextState) {
        final Supplier<String> supplier = () -> formatTransitionErrorMessage(currentState, nextState);
        if (currentState == State.SOLVING && nextState == State.SOLVING) {
            throw new ModelStateException(supplier.get());
        }
        if (currentState == State.SOLVING && nextState == State.BUILDING) {
            throw new ModelStateException(supplier.get());
        }
        if (currentState == State.SOLVED && nextState == State.SOLVED) {
            throw new ModelStateException(supplier.get());
        }
        if (currentState == State.BUILDING && nextState == State.SOLVED) {
            throw new ModelStateException(supplier.get());
        }
        // All other transitions are possible.
    }

    private static String formatTransitionErrorMessage(final State currentState, final State nextState) {
        return String.format("Impossible model transition from %s to %s", currentState, nextState);
    }

}
