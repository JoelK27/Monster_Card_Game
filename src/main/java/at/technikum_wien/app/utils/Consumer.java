package at.technikum_wien.app.utils;

import java.util.concurrent.CompletableFuture;

public class Consumer {
    public void consume(Runnable task) {
        CompletableFuture.runAsync(task);
    }
}