package wrapper.model.option;

import org.jspecify.annotations.NonNull;

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
    },

    LOG_TO_CONSOLE {
        String getHighsOptionName() {
            return "log_to_console";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(@NonNull final Boolean value) {
        return new Option(getHighsOptionName(), value);
    }

}
