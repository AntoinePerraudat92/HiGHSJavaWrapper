package wrapper.exceptions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModelStateException extends WrapperException {

    public ModelStateException(final String message) {
        super(message);
    }

}
