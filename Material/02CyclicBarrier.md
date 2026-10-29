# 1. Imagine 3 friends going to a movie 🎬

Three friends need to go to the cinema together.

- Friend 1 arrives at the meeting point.
- Friend 2 arrives at the meeting point.
- Friend 3 arrives at the meeting point.

Nobody starts walking to the cinema until all three friends have arrived.

Friend 1

Arrived

Friend 2

Arrived

Friend 3

On the way

### Meeting point = Barrier

The first two friends wait until the third friend arrives. Then all three can continue.

That is the basic idea of `CyclicBarrier`.

Definition: `CyclicBarrier` is a Java synchronization utility that makes a group of threads wait for one another until all the required threads reach the same point.

It belongs to `java.util.concurrent`.

## 2. How does CyclicBarrier work?

Suppose we create a barrier for 3 threads.

## Threads arriving at the barrier

Press each button to simulate a thread reaching the barrier.

Thread-1

Waiting at barrier

Arrived ✓

Thread-2

Waiting at barrier

Arrived ✓

Thread-3

Waiting at barrier

Arrived ✓

### Threads arrived: 3 / 3

Barrier released!\
All three threads can now continue.

Start another round

The important difference is that each thread waits for the other threads, rather than one main thread waiting for other tasks.

## 3. Let's write Java code

```
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        CyclicBarrier barrier = new CyclicBarrier(3);

        Runnable task = () -> {
            try {
                System.out.println(
                    Thread.currentThread().getName()
                    + " is working"
                );

                Thread.sleep(1000);

                System.out.println(
                    Thread.currentThread().getName()
                    + " reached the barrier"
                );

                barrier.await();

                System.out.println(
                    Thread.currentThread().getName()
                    + " continued after the barrier"
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                System.out.println("Barrier was broken");
            }
        };

        new Thread(task, "Thread-1").start();
        new Thread(task, "Thread-2").start();
        new Thread(task, "Thread-3").start();
    }
}
```

### Understand the execution step by step

1. `new CyclicBarrier(3)` means the barrier needs three threads.
2. Each thread performs its work.
3. Each thread reaches `barrier.await()`.
4. The first thread waits. The second thread also waits.
5. When the third thread arrives, the barrier opens.
6. All three threads can continue past `barrier.await()`.

The exact order of messages may vary because threads execute concurrently.

## 4. Why is it called CyclicBarrier?

Because you can reuse it for another round.

Imagine all three friends reach the first meeting point. They walk together to another location and must wait for one another again.

A `CyclicBarrier` can be used for this repeated coordination without creating a new barrier each time.

For example:

```
barrier.await(); // Wait at round 1

// Perform more work

barrier.await(); // Wait at round 2
```

Each round requires the configured number of threads to reach the barrier again.

## 5. CountDownLatch vs. CyclicBarrier

This is the most important interview distinction.

| Feature                       | CountDownLatch                               | CyclicBarrier                                           |
| ----------------------------- | -------------------------------------------- | ------------------------------------------------------- |
| Main idea                     | Wait for tasks to signal completion          | Threads wait for one another                            |
| Who calls the waiting method? | Any thread that needs to wait                | Each participating thread                               |
| Main waiting method           | `await()`                                    | `await()`                                               |
| Counter mechanism             | `countDown()` decreases the count            | Each `await()` arrival contributes toward the barrier   |
| Reusable?                     | No                                           | Yes                                                     |
| Example                       | Main thread waits for 3 initialization tasks | 3 workers finish a phase before starting the next phase |

### Easy way to remember

- CountDownLatch: "I'll continue when the required tasks are finished."
- CyclicBarrier: "We'll all continue when everyone reaches this point."

One technical detail: if a participating thread is interrupted, times out, or otherwise breaks the barrier, other waiting threads may receive `BrokenBarrierException`. Handle that possibility in real applications.
