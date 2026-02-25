package wrapper.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockObjectCreator {

    static Variable createVariable(long index) {
        final Variable variable = mock(Variable.class);
        when(variable.getIndex()).thenReturn(index);
        return variable;
    }

}
