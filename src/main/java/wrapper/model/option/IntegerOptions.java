package wrapper.model.option;

import org.jspecify.annotations.NonNull;

public enum IntegerOptions {

    NB_THREADS {
        String getHighsOptionName() {
            return "threads";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(@NonNull final Integer value) {
        return new Option(getHighsOptionName(), value);
    }

}
