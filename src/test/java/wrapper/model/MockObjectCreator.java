package wrapper.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockObjectCreator {

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
