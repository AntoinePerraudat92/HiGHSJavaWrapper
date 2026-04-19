package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConstraintException extends WrapperException {

    public ConstraintException(final String message) {
        super(message);
    }

}
