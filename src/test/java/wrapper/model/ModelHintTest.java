package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.HintException;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelHintTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void maximizeMustThrowIfInvalidInitialValue() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));
        x1.setHint(-1.0);

        final HintException hintException = assertThrows(HintException.class, model::maximize);
        assertEquals("Impossible to parse hints", hintException.getMessage());
    }

    @Test
    void hintsMustBeClearedAfterFirstSolveEvenIfNotSuccessful() {
        final Model model = createModel();
        final Variable x1 = model.addIntegerVariable(5.0, 12.0, 1.0);
        model.addEqualityConstraint(11.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));
        x1.setHint(-3.0);

        assertThrows(HintException.class, model::maximize);
        assertDoesNotThrow(model::maximize);
    }

    @Test
    void maximizeMustNotThrowIfNoHint() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));

        assertDoesNotThrow(model::maximize);
    }

    @Test
    void maximizeMustReturnOneIfHintsSetForAllVariables() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(
                1.0, LinearExpression.of(
                        new LinearExpression.Term(x1, 1.0),
                        new LinearExpression.Term(x2, 1.0),
                        new LinearExpression.Term(x3, 1.0)
                )
        );

        x1.setHint(1.0);
        x2.setHint(0.0);
        x3.setHint(0.0);

        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

}
