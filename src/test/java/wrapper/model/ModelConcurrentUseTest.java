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
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();
        @Nullable
        private Future<Optional<Solution>> future;

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

        public void startOptimization() {
            this.future = this.executorService.submit(this::minimize);
        }

        public void stopOptimization() {
            this.optimizationDoneSemaphore.release();
        }

        public void waitUntilOptimizationStarted() throws InterruptedException {
            this.optimizationStartedSemaphore.acquire();
        }

        public void waitUntilOptimizationFinished() throws ExecutionException, InterruptedException, TimeoutException {
            if (this.future != null) {
                this.future.get(1L, TimeUnit.MINUTES);
            }
        }

    }

    @Test
    void addVariableToModelWhileSolvingOnGoingIsNotAllowed() throws InterruptedException, ExecutionException, TimeoutException {
        final ExceptionCatcher exceptionCatcher = new ExceptionCatcher();
        final MockSolverModel model = new MockSolverModel();
        model.startOptimization();
        model.waitUntilOptimizationStarted();

        exceptionCatcher.run(() -> model.addBinaryVariable(2.0));

        model.stopOptimization();
        model.waitUntilOptimizationFinished();
        assertInstanceOf(ModelStateException.class, exceptionCatcher.exception);
    }

}
