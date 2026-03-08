package wrapper.model.option;

public enum StringOptions {

    PARALLEL {
        String getHighsOptionName() {
            return "parallel";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(final String value) {
        return new Option(getHighsOptionName(), value);
    }

}
