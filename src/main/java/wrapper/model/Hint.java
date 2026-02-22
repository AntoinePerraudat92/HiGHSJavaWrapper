package wrapper.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;

@NoArgsConstructor
public class Hint {

    private final Map<Variable, Double> hintByVariable = new HashMap<>();

    public static Hint of(@NonNull final Map<Variable, Double> hintByVariable) {
        final Hint hint = new Hint();
        hintByVariable.forEach(hint::addVariableHint);
        return hint;
    }

    public void addVariableHint(@NonNull final Variable variable, double hint) {
        this.hintByVariable.putIfAbsent(variable, hint);
    }

    void consumeHints(final ObjDoubleConsumer<Variable> consumer) {
        this.hintByVariable.forEach(consumer::accept);
    }

    int getNmbHints() {
        return this.hintByVariable.size();
    }

}
