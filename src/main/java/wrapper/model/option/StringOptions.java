package wrapper.model.option;

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

    public Option getOption(final String value) {
        return new Option(getHighsOptionName(), value);
    }

}
