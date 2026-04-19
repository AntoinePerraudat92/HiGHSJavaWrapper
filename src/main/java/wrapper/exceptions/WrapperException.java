package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class WrapperException extends RuntimeException {

    public WrapperException(final String message) {
        super(message);
    }

}
