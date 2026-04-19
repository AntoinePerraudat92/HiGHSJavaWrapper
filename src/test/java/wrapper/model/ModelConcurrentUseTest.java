package wrapper.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import wrapper.exceptions.ModelStateException;

import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ModelConcurrentUseTest {

    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }

    @NullMarked
    private static class ExceptionCatcher {

        @Nullable
        private Exception exception;

        void run(final Runnable runnable) {
            try {
                runnable.run();
            } catch (final Exception e) {
                this.exception = e;
            }
        }

    }

    @NullMarked
    private static class MockSolverModel extends Model {

        private final Semaphore optimizationStartedSemaphore = new Semaphore(0);
        private final Semaphore optimizationDoneSemaphore = new Semaphore(0);
        private final ExecutorService executorService = Executors.newFixedThreadPool(2);

        @Override
        protected Optional<Solution> solve() {
            this.optimizationStartedSemaphore.release();
            try {
                this.optimizationDoneSemaphore.acquire();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        }

        public Future<Optional<Solution>> startOptimization() {
            return this.executorService.submit(this::minimize);
        }

        public void stopOptimization() {
            this.optimizationDoneSemaphore.release();
        }

        public void waitUntilOptimizationStarted() throws InterruptedException {
            this.optimizationStartedSemaphore.acquire();
        }

        public void waitUntilOptimizationFinished(final Future<Optional<Solution>> future) throws ExecutionException, InterruptedException, TimeoutException {
            future.get(1L, TimeUnit.MINUTES);
        }

    }

    @Test
    void addVariableToModelWhileSolvingOnGoingIsNotAllowed() throws InterruptedException, ExecutionException, TimeoutException {
        final ExceptionCatcher exceptionCatcher = new ExceptionCatcher();
        final MockSolverModel model = new MockSolverModel();
        final var future = model.startOptimization();
        model.waitUntilOptimizationStarted();

        exceptionCatcher.run(() -> model.addBinaryVariable(2.0));

        model.stopOptimization();
        model.waitUntilOptimizationFinished(future);
        assertInstanceOf(ModelStateException.class, exceptionCatcher.exception);
    }

    @Test
    void startOptimizationTwiceIsNotAllowed() throws InterruptedException, ExecutionException, TimeoutException {
        final ExceptionCatcher exceptionCatcher = new ExceptionCatcher();
        final MockSolverModel model = new MockSolverModel();
        final var future = model.startOptimization();
        model.waitUntilOptimizationStarted();

        exceptionCatcher.run(model::minimize);

        model.stopOptimization();
        model.waitUntilOptimizationFinished(future);
        assertInstanceOf(ModelStateException.class, exceptionCatcher.exception);
    }

}
