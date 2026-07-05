package wrapper.model;


import org.jspecify.annotations.NullMarked;
import wrapper.exceptions.ModelStateException;

import java.util.function.Supplier;

@NullMarked
class ModelState {

    private State state = State.BUILDING;

    private static void checkTransition(final State currentState, final State nextState) {
        final Supplier<String> supplier = () -> String.format("Impossible model transition from %s to %s", currentState, nextState);
        if (currentState == State.SOLVING && nextState == State.SOLVING) {
            throw new ModelStateException(supplier.get());
        }
        if (currentState == State.SOLVING && nextState == State.BUILDING) {
            throw new ModelStateException(supplier.get());
        }
        if ((currentState == State.SOLVE_SUCCESSFUL || currentState == State.SOLVE_FAILED) && (nextState == State.SOLVE_SUCCESSFUL || nextState == State.SOLVE_FAILED)) {
            throw new ModelStateException(supplier.get());
        }
        if (currentState == State.BUILDING && (nextState == State.SOLVE_SUCCESSFUL || nextState == State.SOLVE_FAILED)) {
            throw new ModelStateException(supplier.get());
        }
        // All other transitions are possible.
    }

    synchronized void onSolveRequested() {
        setNewState(State.SOLVING);
    }

    synchronized void onModelChangeRequested() {
        setNewState(State.BUILDING);
    }

    synchronized void onSolutionRequested() {
        if (this.state != State.SOLVE_SUCCESSFUL) {
            throw new ModelStateException(String.format("Impossible to retrieve solution when model state is %s", this.state));
        }
    }

    synchronized void onSolveSuccessful() {
        setNewState(State.SOLVE_SUCCESSFUL);
    }

    synchronized void onSolveFailed() {
        setNewState(State.SOLVE_FAILED);
    }

    private void setNewState(final State nextState) {
        checkTransition(this.state, nextState);
        this.state = nextState;
    }

    private enum State {
        BUILDING,
        SOLVE_SUCCESSFUL,
        SOLVE_FAILED,
        SOLVING
    }

}
