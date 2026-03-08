package wrapper.model.option;

public enum BooleanOptions {

    SOLVER_OUTPUT {
        String getHighsOptionName() {
            return "output_flag";
        }
    },

    MIP_DETECT_SYMMETRY {
        String getHighsOptionName() {
            return "mip_detect_symmetry";
        }
    },

    MIP_ALLOW_RESTART {
        String getHighsOptionName() {
            return "mip_allow_restart";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(final Boolean value) {
        return new Option(getHighsOptionName(), value);
    }

}
