package wrapper.model;


import wrapper.exceptions.ModelStateException;

import java.util.function.Supplier;

class ModelState {

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

    private void setNewState(final State nextState) {
        checkTransition(nextState);
        this.state = nextState;
    }

    private void checkTransition(final State nextState) {
        final Supplier<String> supplier = () -> formatTransitionErrorMessage(this.state, nextState);
        if (this.state == State.SOLVING && nextState == State.SOLVING) {
            throw new ModelStateException(supplier.get());
        }
        if (this.state == State.SOLVING && nextState == State.BUILDING) {
            throw new ModelStateException(supplier.get());
        }
        if (this.state == State.SOLVED && nextState == State.SOLVED) {
            throw new ModelStateException(supplier.get());
        }
        if (this.state == State.BUILDING && nextState == State.SOLVED) {
            throw new ModelStateException(supplier.get());
        }
        // All other transitions are possible.
    }

    private enum State {
        BUILDING, SOLVED, SOLVING
    }

    private static String formatTransitionErrorMessage(final State currentState, final State nextState) {
        return String.format("Impossible model transition from %s to %s", currentState, nextState);
    }

}
