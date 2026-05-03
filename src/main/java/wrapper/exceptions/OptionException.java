package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class OptionException extends WrapperException {

    public OptionException(final String message) {
        super(message);
    }

}
