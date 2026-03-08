package wrapper.model.option;

public enum IntegerOptions {

    NB_THREADS {
        String getHighsOptionName() {
            return "threads";
        }
    };

    abstract String getHighsOptionName();

    public Option getOption(final Integer value) {
        return new Option(getHighsOptionName(), value);
    }

}
