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
        assertThrows(ModelStateException.class, () -> this.modelState.onSolveSuccessful());
        assertThrows(ModelStateException.class, () -> this.modelState.onSolveFailed());
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
    void requestingModelChangeAfterSuccessfulSolveIsAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveSuccessful();

        assertDoesNotThrow(() -> this.modelState.onModelChangeRequested());
    }

    @Test
    void requestingModelChangeAfterSolveFailsIsAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveFailed();

        assertDoesNotThrow(() -> this.modelState.onModelChangeRequested());
    }

    @Test
    void requestingTwoModelChangesInARowIsAllowed() {
        this.modelState.onModelChangeRequested();

        assertDoesNotThrow(() -> this.modelState.onModelChangeRequested());
    }

    @Test
    void requestingSolutionWhenSolvingModelIsNotAllowed() {
        this.modelState.onSolveRequested();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolutionRequested());
    }

    @Test
    void requestingSolutionWhenBuildingModelIsNotAllowed() {
        this.modelState.onModelChangeRequested();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolutionRequested());
    }

    @Test
    void requestingSolutionWhenModelSolveSuccessfulIsAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveSuccessful();

        assertDoesNotThrow(() -> this.modelState.onSolutionRequested());
    }

    @Test
    void requestingSolutionWhenModelSolveFailsIsNotAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveFailed();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolutionRequested());
    }

    @Test
    void twoConsecutiveSolveFailsIsNotAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveFailed();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolveFailed());
    }

    @Test
    void failedSolveThenSuccessfulSolveIsNotAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveFailed();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolveSuccessful());
    }

    @Test
    void successfulSolveThenFailedSolveIsNotAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveSuccessful();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolveFailed());
    }

    @Test
    void twoConsecutiveSuccessfulSolveIsNotAllowed() {
        this.modelState.onSolveRequested();
        this.modelState.onSolveSuccessful();

        assertThrows(ModelStateException.class, () -> this.modelState.onSolveSuccessful());
    }

}
