package io.fulmicotone.fqueue;

import io.fulmicotone.fqueue.enums.BatchReason;
import io.fulmicotone.fqueue.interfaces.FQueueConsumer;
import io.fulmicotone.fqueue.model.ComplexObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@RunWith(JUnit4.class)
public class FQueueExceptionsTest {



    @Test
    public void testSizeBatching_EXCEPTION() throws InterruptedException {

        /**
         * Test size batching.
         * With 100 elements and 5 chunks size I expect 20 calls with 5 elements each.
         * */
        int elements = 5;
        int chunkSize = 5;
        int flushTimeoutInMilliSeconds = 500;
        int resultTimeoutInMilliSeconds = 1_000;
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger stopped = new AtomicInteger();


        FQueueRegistry registry = new FQueueRegistry();
        Consumer<Exception> exceptionConsumer = e -> stopped.incrementAndGet();


        FQueue<String> fqueue = new FQueue<>(String.class, registry)
                .withRunningExceptionHandler(exceptionConsumer)
                .batch()
                .withFlushTimeUnit(TimeUnit.MILLISECONDS)
                .withFlushTimeout(flushTimeoutInMilliSeconds)
                .withChunkSize(chunkSize)
                .done()
                .consume(() -> (broadcaster, reason, elms) -> calls.incrementAndGet());



        for(int i = 0; i < elements; i++){
            registry.sendBroadcast("a"+1);
        }

        Thread.sleep(resultTimeoutInMilliSeconds);


        fqueue.destroyAndAwait(10, TimeUnit.SECONDS);

        Assert.assertEquals(1, stopped.get());


    }



    @Test
    public void testSizeBatching_EXCEPTION_FAN_OUT() throws InterruptedException {

        /**
         * Test size batching.
         * With 100 elements and 5 chunks size I expect 20 calls with 5 elements each.
         * */
        int elements = 5;
        int chunkSize = 5;
        int flushTimeoutInMilliSeconds = 500;
        int resultTimeoutInMilliSeconds = 1_000;
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger stopped = new AtomicInteger();


        FQueueRegistry registry = new FQueueRegistry();
        Consumer<Exception> exceptionConsumer = e -> stopped.incrementAndGet();


        FQueue<String> fqueue = new FQueue<>(String.class, registry)
                .fanOut(2)
                .withRunningExceptionHandler(exceptionConsumer)
                .batch()
                .withFlushTimeUnit(TimeUnit.MILLISECONDS)
                .withFlushTimeout(flushTimeoutInMilliSeconds)
                .withChunkSize(chunkSize)

                .done()
                .consume(() -> (broadcaster, reason, elms) -> calls.incrementAndGet());


        for(int i = 0; i < elements; i++){
            registry.sendBroadcast("a"+1);
        }


        Thread.sleep(resultTimeoutInMilliSeconds);


        fqueue.destroyAndAwait(10, TimeUnit.SECONDS);


        Assert.assertEquals(3, stopped.get());

    }



    @Test
    public void testSizeBatching_EXCEPTION_FAN_OUT_DESTROY() throws InterruptedException {

        /**
         * Test size batching.
         * With 100 elements and 5 chunks size I expect 20 calls with 5 elements each.
         * */
        int elements = 5;
        int chunkSize = 5;
        int flushTimeoutInMilliSeconds = 500;
        int resultTimeoutInMilliSeconds = 1_000;
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger stopped = new AtomicInteger();


        FQueueRegistry registry = new FQueueRegistry();
        Consumer<Exception> exceptionConsumer = e -> stopped.incrementAndGet();


        FQueue<String> fqueue = new FQueue<>(String.class, registry)
                .fanOut(2)
                .withRunningExceptionHandler(exceptionConsumer)
                .batch()
                .withFlushTimeUnit(TimeUnit.MILLISECONDS)
                .withFlushTimeout(flushTimeoutInMilliSeconds)
                .withChunkSize(chunkSize)

                .done()
                .consume(() -> (broadcaster, reason, elms) -> calls.incrementAndGet());


        for(int i = 0; i < elements; i++){
            registry.sendBroadcast("a"+1);
        }


        Thread.sleep(resultTimeoutInMilliSeconds);


        fqueue.destroy();

        Thread.sleep(10_000);


        Assert.assertEquals(3, stopped.get());

    }

}
