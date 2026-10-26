# Java Multithreading: Exchanger

An `Exchanger` is a synchronization utility in Java that allows two threads to exchange objects with each other at a synchronization point.

It belongs to the `java.util.concurrent` package.

The main idea is simple: one thread provides an object and waits until another thread arrives. When both threads meet, they exchange their objects and continue execution.

## 1. Real-world example

Imagine two workers exchanging packages.

Thread A

Package A

Exchange

Thread B

Package B

Thread A receives

Package B

Thread B receives

Package A

Both workers must arrive at the exchange point. Neither can complete the exchange alone.

## 2. Basic syntax

```
import java.util.concurrent.Exchanger;

Exchanger<String> exchanger = new Exchanger<>();
```

Here, `String` is the type of object exchanged between the threads.

The most important method is:

```
String received = exchanger.exchange("Hello");
```

The `exchange()` method:

1. Submits the current thread's object.
2. Waits for another thread to call `exchange()`.
3. Receives the other thread's object.
4. Returns that object.

## 3. Java example

Let's create two threads. Thread A sends `"Apple"`, and Thread B sends `"Banana"`.

```

import java.util.concurrent.Exchanger;

public class Main {

    public static void main(String[] args) {

        Exchanger<String> exchanger = new Exchanger<>();

        Runnable taskA = () -> {
            try {
                String received = exchanger.exchange("Apple");

                System.out.println(
                    "Thread A received: " + received
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable taskB = () -> {
            try {
                String received = exchanger.exchange("Banana");

                System.out.println(
                    "Thread B received: " + received
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(taskA, "Thread-A").start();
        new Thread(taskB, "Thread-B").start();
    }
}

```

### Expected output

```
Thread A received: Banana
Thread B received: Apple
```

The order of these two output lines can vary.

### How does it work?

- Thread A calls `exchange("Apple")` and waits.
- Thread B calls `exchange("Banana")`.
- The Exchanger pairs the two calls.
- Thread A receives `"Banana"`.
- Thread B receives `"Apple"`.

Both threads continue after the exchange completes.

## 4. What happens if only one thread calls `exchange()`?

Suppose Thread A executes:

```
String received = exchanger.exchange("Apple");
```

But Thread B never calls `exchange()`.

Thread A waits indefinitely by default. It can also stop waiting if it is interrupted.

You can use a timeout to avoid waiting forever:

```
String received = exchanger.exchange(
    "Apple",
    2,
    java.util.concurrent.TimeUnit.SECONDS
);
```

If no partner arrives within two seconds, the method throws `TimeoutException`. Handle that exception along with `InterruptedException`.

## 5. Practical use case: Producer and Consumer

Imagine one thread produces data in a buffer, while another thread processes data from a different buffer.

They can exchange buffers when their current work is complete instead of repeatedly copying all the data.

- Producer prepares a buffer.
- Consumer processes its buffer.
- They exchange buffer references.
- Producer fills the newly received buffer.
- Consumer processes the newly received buffer.

This can be useful in high-throughput data processing where buffer reuse reduces allocation and copying overhead.

Important: Exchanger swaps object references, not deep copies of the objects. After exchanging mutable objects, each thread must coordinate any further access appropriately.

## 6. Exchanger vs Semaphore vs Phaser

| Feature              | Exchanger                                                     | Semaphore                   | Phaser                                                        |
| -------------------- | ------------------------------------------------------------- | --------------------------- | ------------------------------------------------------------- |
| Main purpose         | Exchange objects                                              | Limit concurrent access     | Coordinate multiple phases                                    |
| Typical participants | Two threads per exchange                                      | Multiple threads            | Multiple registered parties                                   |
| Transfers an object? | Yes                                                           | No                          | No                                                            |
| Blocks?              | Yes, until a partner arrives or a timeout/interruption occurs | If no permits are available | Until the phase advances when using `arriveAndAwaitAdvance()` |
| Main method          | `exchange()`                                                  | `acquire()` / `release()`   | `arriveAndAwaitAdvance()`                                     |

## 7. Interview points to remember

1. `Exchanger` belongs to `java.util.concurrent`.
2. It exchanges objects between two threads.
3. It is generic, so you can specify the type of object being exchanged.
4. Both threads must call `exchange()` to complete a normal exchange.
5. `exchange()` can throw `InterruptedException`.
6. The timed version can throw `TimeoutException`.
7. It exchanges references, not copies of objects.
8. A common use case is exchanging buffers between producer and consumer threads.
