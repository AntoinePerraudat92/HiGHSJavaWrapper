package wrapper.model;

import org.junit.jupiter.api.Test;
import wrapper.exceptions.VariableException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static wrapper.util.Constants.EPSILON;

class ModelHintTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @Test
    void parseMustThrowForUnknownVariables() {
        final Model model = new Model();
        final Hint hint = Hint.of(Map.of(new Variable(32), -45D));

        final VariableException exception = assertThrows(VariableException.class, () -> model.parseHint(hint));
        assertEquals("Variable with index 32 does not exist in the model", exception.getMessage());
    }

    @Test
    void parseHintMustReturnFalseIfInvalidInitialValue() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));

        assertFalse(model.parseHint(Hint.of(Map.of(x1, -1.0))));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void parseHintMustReturnFalseWhenHintIsEmpty() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(new LinearExpression.Term(x1, 1.0)));

        assertFalse(model.parseHint(new Hint()));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

    @Test
    void parseHintMustReturnTrue() {
        final Model model = new Model();
        final Variable x1 = model.addBinaryVariable(1.0);
        final Variable x2 = model.addBinaryVariable(1.0);
        final Variable x3 = model.addBinaryVariable(1.0);
        model.addEqualityConstraint(1.0, LinearExpression.of(
                new LinearExpression.Term(x1, 1.0),
                new LinearExpression.Term(x2, 1.0),
                new LinearExpression.Term(x3, 1.0)
        ));

        assertTrue(model.parseHint(Hint.of(Map.of(x1, 1.0, x2, 0.0, x3, 0.0))));
        final Solution solution = model.maximize().orElseThrow();
        assertEquals(1.0, solution.getObjectiveValue(), EPSILON);
    }

}
