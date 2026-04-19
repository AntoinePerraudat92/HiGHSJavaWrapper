package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class VariableException extends WrapperException {

    public VariableException(final String message) {
        super(message);
    }

}
