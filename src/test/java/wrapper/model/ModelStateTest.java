package wrapper.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wrapper.exceptions.ModelStateException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelStateTest {

    private ModelState modelState;

    @BeforeEach
    void createModelState() {
        this.modelState = new ModelState();
    }

    @Test
    void modelCannotBeSolvedBeforeSolveRequested() {
        assertThrows(ModelStateException.class, () -> this.modelState.onSolveCompleted());
    }

    @Test
    void modelCannotBeSolvedTwiceInARows() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveCompleted();
        
        assertThrows(ModelStateException.class, () -> this.modelState.onSolveCompleted());
    }

    @Test
    void requestingTwoSolvesInARowIsNotAllowed() {
        this.modelState.onSolveRequested();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolveRequested());
    }

    @Test
    void requestingModelChangeDuringSolveIsNotAllowed() {
        this.modelState.onSolveRequested();

        assertThrows(ModelStateException.class, () -> this.modelState.onModelChangeRequested());
    }

    @Test
    void requestingModelChangeAfterSolveIsAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveCompleted();

        assertDoesNotThrow(() -> this.modelState.onModelChangeRequested());
    }

    @Test
    void requestingTwoModelChangesInARowIsAllowed() {
        this.modelState.onModelChangeRequested();

        assertDoesNotThrow(() -> this.modelState.onModelChangeRequested());
    }

}