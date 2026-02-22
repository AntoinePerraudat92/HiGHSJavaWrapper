package wrapper.util;

import wrapper.model.Constraint;
import wrapper.model.Variable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectCreator {

    public static Variable mockVariable(long index) {
        final Variable variable = mock(Variable.class);
        when(variable.getIndex()).thenReturn(index);
        return variable;
    }

    public static Constraint mockConstraint(long index) {
        final Constraint constraint = mock(Constraint.class);
        when(constraint.getIndex()).thenReturn(index);
        return constraint;
    }

}
