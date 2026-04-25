package wrapper.util;

import wrapper.model.Model;
import wrapper.model.option.BooleanOptions;

public class ObjectCreator {

    /**
     * This method is used to avoid corrupted channel error when working with native stream and maven.
     */
    public static Model createModel() {
        final Model model = new Model();
        model.parseOption(BooleanOptions.SOLVER_OUTPUT.getOption(true));
        return model;
    }

}
