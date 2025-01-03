package at.technikum_wien.app.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Producer {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void produce(Runnable task) {
        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}