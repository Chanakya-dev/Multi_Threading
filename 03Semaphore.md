# Java Multithreading: Semaphore

A Semaphore in Java is a synchronization mechanism that controls how many threads can access a shared resource at the same time.

It belongs to the `java.util.concurrent` package.

Think of a Semaphore as a system of limited permits. A thread must acquire a permit before accessing a resource. When it finishes, it releases the permit so another thread can use it.

## 1. Real-world example

Imagine a parking area with only 3 parking spaces.

### Parking lot

3 permits

Car 1

Occupied

Car 2

Occupied

Car 3

Occupied

When all 3 spaces are occupied, the fourth car must wait until a car leaves.

The mapping to Java is:

- Parking spaces → Permits
- Cars → Threads
- Entering parking → `acquire()`
- Leaving parking → `release()`

## 2. Basic syntax

```
import java.util.concurrent.Semaphore;

Semaphore semaphore = new Semaphore(3);
```

Here, `3` means the Semaphore initially has three permits, so up to three threads can acquire permits simultaneously.

The two most important methods are:

| Method      | Purpose                                               |
| ----------- | ----------------------------------------------------- |
| `acquire()` | Acquires a permit; waits if none is available         |
| `release()` | Releases a permit, allowing another thread to proceed |

Other useful methods include:

- `tryAcquire()` — attempts to acquire a permit immediately without waiting.
- `availablePermits()` — returns the number of currently available permits.
- `tryAcquire(timeout, unit)` — waits only up to a specified time.
- `drainPermits()` — removes all currently available permits and returns their count.

## 3. Java example — Semaphore with 3 permits

Let's create five threads, but allow only three to access the resource simultaneously.

```

import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(3);

        Runnable task = () -> {
            try {
                semaphore.acquire();

                System.out.println(
                    Thread.currentThread().getName()
                    + " acquired permit"
                );

                Thread.sleep(2000);

                System.out.println(
                    Thread.currentThread().getName()
                    + " finished work"
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Release only if this thread acquired a permit.
            }
        };

        for (int i = 1; i <= 5; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}

```

There is an important correction needed in this initial example: we must release the permit after acquiring it. Here is the complete, correct version.

```

import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(3);

        Runnable task = () -> {
            boolean acquired = false;

            try {
                semaphore.acquire();
                acquired = true;

                System.out.println(
                    Thread.currentThread().getName()
                    + " acquired permit"
                );

                Thread.sleep(2000);

                System.out.println(
                    Thread.currentThread().getName()
                    + " finished work"
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (acquired) {
                    semaphore.release();
                }
            }
        };

        for (int i = 1; i <= 5; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}

```

### How does it execute?

1. Threads 1, 2, and 3 acquire the three available permits.
2. Threads 4 and 5 wait because no permits remain.
3. After one of the first three threads finishes, it releases a permit.
4. One waiting thread can acquire that permit and proceed.
5. This continues until all five threads finish.

The exact order of thread execution and printed messages is not guaranteed.

Important: The `finally` block ensures that a permit is released even if the work throws an exception. The `acquired` flag prevents releasing a permit when the thread was interrupted before acquiring one.

## 4. What happens if we use `Semaphore(1)`?

```
Semaphore semaphore = new Semaphore(1);
```

Only one thread can hold a permit at a time.

This is useful when you want to restrict access to one thread at a time.

A Semaphore with one permit can resemble a mutex, but it is not identical: Java's Semaphore does not require the thread that acquired a permit to be the thread that releases it.

## 5. Fair vs. non-fair Semaphore

Java supports two ways of choosing waiting threads.

```
// Non-fair Semaphore (default)
Semaphore s1 = new Semaphore(3);

// Fair Semaphore
Semaphore s2 = new Semaphore(3, true);
```

- Non-fair: A newly arriving thread may acquire a permit before a thread that has been waiting longer.
- Fair: Threads waiting to acquire permits are generally served in FIFO order.

Fairness can reduce the chance of a waiting thread being repeatedly bypassed, but it can reduce throughput. Also, untimed `tryAcquire()` can succeed even when other threads are waiting on a fair Semaphore.

## 6. Semaphore vs. CountDownLatch vs. CyclicBarrier

Since these are all part of Java concurrency, understand their different purposes.

| Feature                        | Semaphore                 | CountDownLatch             | CyclicBarrier                            |
| ------------------------------ | ------------------------- | -------------------------- | ---------------------------------------- |
| Main purpose                   | Limit concurrent access   | Wait for tasks to complete | Wait for threads to reach a common point |
| Reusable?                      | Yes                       | No                         | Yes                                      |
| Main method                    | `acquire()` / `release()` | `countDown()` / `await()`  | `await()`                                |
| Can control concurrent access? | Yes                       | No                         | Not its primary purpose                  |

For example:

- Semaphore: Only 3 threads may call an external API simultaneously.
- CountDownLatch: The main thread waits until 3 initialization tasks complete.
- CyclicBarrier: 3 worker threads must all finish a phase before moving to the next phase.

## 7. Interview points to remember

1. A Semaphore maintains a number of permits.
2. `acquire()` blocks when no permits are available.
3. `release()` increases the available permit count.
4. A Semaphore can have multiple permits.
5. A Semaphore can be fair or non-fair.
6. Unlike an intrinsic Java monitor, a Semaphore does not enforce ownership between acquiring and releasing threads.
7. Releasing without a corresponding acquisition is allowed by the API and can increase the number of available permits beyond the initial count.
8. Always manage permit release carefully, typically with `try`/`finally`.
