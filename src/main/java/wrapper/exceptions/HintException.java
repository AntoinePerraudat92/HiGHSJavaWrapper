package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class HintException extends WrapperException {

    public HintException(final String message) {
        super(message);
    }

}
