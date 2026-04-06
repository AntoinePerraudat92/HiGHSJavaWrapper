package wrapper.model.option;

import org.jspecify.annotations.NonNull;

public enum StringOptions {

    PARALLEL {
        String getHighsOptionName() {
            return "parallel";
        }
    },

    SOLVER {
        String getHighsOptionName() {
            return "solver";
        }
    },

    LOG_TO_FILE {
        String getHighsOptionName() {
            return "log_to_file";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(@NonNull final String value) {
        return new Option(getHighsOptionName(), value);
    }

}
