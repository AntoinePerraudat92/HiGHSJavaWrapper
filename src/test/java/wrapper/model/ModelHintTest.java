package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.HintException;
import wrapper.exceptions.VariableException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;
import static wrapper.util.ObjectCreator.createModel;

class ModelHintTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void parseMustThrowForUnknownVariables() {
        final Model model = createModel();
        final Model otherModel = createModel();
        final Hint hint = Hint.of(Map.of(new Variable(32, otherModel), -45D));

        final VariableException exception = assertThrows(VariableException.class, () -> model.parseHint(hint));
        assertEquals("Trying to access or modify variable associated with wrong model", exception.getMessage());
    }

    @Test
    void parseHintMustThrowIfInvalidInitialValue() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));
        final Hint hint = Hint.of(Map.of(x1, -1.0));

        final HintException exception = assertThrows(HintException.class, () -> model.parseHint(hint));
        assertEquals("Impossible to parse hint", exception.getMessage());
    }

    @Test
    void parseHintMustReturnFalseWhenHintIsEmpty() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));
        final Hint hint = new Hint();

        final HintException exception = assertThrows(HintException.class, () -> model.parseHint(hint));
        assertEquals("Impossible to parse hint with no variable", exception.getMessage());
    }

    @Test
    void parseHintMustReturnTrue() {
        final Model model = createModel();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(
                new LinearExpression.Term(x1, 1.0),
                new LinearExpression.Term(x2, 1.0),
                new LinearExpression.Term(x3, 1.0)
        ));

        assertDoesNotThrow(() -> model.parseHint(Hint.of(Map.of(x1, 1.0, x2, 0.0, x3, 0.0))));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

}
