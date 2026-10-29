# Java Multithreading: Phaser

A Phaser is a synchronization mechanism in Java that allows multiple threads to wait for one another at different stages of execution.

It belongs to the `java.util.concurrent` package.

The key idea is that threads must reach a common phase before they can proceed to the next phase. Unlike `CountDownLatch`, a Phaser can be reused for multiple phases, and the number of participating threads can change dynamically.

## 1. Real-world example

Imagine three developers working on a software project with three phases:

### Project execution

Phase 1: Development

Developer A, B, and C write their code.

Wait until all participating developers finish

Phase 2: Testing

All developers move to testing after reaching the barrier.

Wait until all participating developers finish

Phase 3: Deployment

The team proceeds to deployment together.

This is the kind of coordination Phaser provides.

## 2. Basic syntax

```
import java.util.concurrent.Phaser;

Phaser phaser = new Phaser(3);
```

Here, `3` represents the number of initially registered parties (participants).

The Phaser waits for all three registered parties to arrive at the current phase before advancing.

Important methods:

| Method                    | Purpose                                                     |
| ------------------------- | ----------------------------------------------------------- |
| `arrive()`                | Signals that the current party has arrived, without waiting |
| `arriveAndAwaitAdvance()` | Signals arrival and waits for the current phase to advance  |
| `arriveAndDeregister()`   | Signals arrival and removes the party from future phases    |
| `register()`              | Adds a new participating party                              |
| `bulkRegister(n)`         | Adds `n` parties                                            |
| `getPhase()`              | Returns the current phase number                            |
| `getRegisteredParties()`  | Returns the number of registered parties                    |
| `awaitAdvance(phase)`     | Waits for the specified phase to advance                    |

## 3. Simple Java example

Let's create three threads. Each thread must complete its work in Phase 1 before any of them can proceed to Phase 2.

```

import java.util.concurrent.Phaser;

public class Main {

    public static void main(String[] args) {

        Phaser phaser = new Phaser(3);

        Runnable task = () -> {
            String name = Thread.currentThread().getName();

            System.out.println(name + " is working on Phase 1");

            phaser.arriveAndAwaitAdvance();

            System.out.println(name + " is working on Phase 2");

            phaser.arriveAndAwaitAdvance();

            System.out.println(name + " completed both phases");
        };

        for (int i = 1; i <= 3; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}

```

### How does this execute?

Step 1: All three threads begin Phase 1. They may take different amounts of time to finish their work.

Step 2: Each thread calls `arriveAndAwaitAdvance()`.

- Thread 1 arrives and waits.
- Thread 2 arrives and waits.
- Thread 3 arrives, completing the phase.

Step 3: The Phaser advances from phase `0` to phase `1`. All waiting threads can continue.

Step 4: All three threads perform Phase 2 and call `arriveAndAwaitAdvance()` again.

Step 5: Once all three arrive again, the Phaser advances to phase `2`, and all threads finish.

The precise order of the printed messages is not guaranteed.

Important: A Phaser's phase numbers start at `0`, not `1`.

## 4. Why do we need Phaser when we already have CyclicBarrier?

This is a common interview question.

Both Phaser and CyclicBarrier can coordinate threads across repeated phases, but Phaser offers more flexibility.

| Feature                             | Phaser                                                               | CyclicBarrier            |
| ----------------------------------- | -------------------------------------------------------------------- | ------------------------ |
| Reusable across phases              | Yes                                                                  | Yes                      |
| Dynamically add participants        | Yes                                                                  | No direct equivalent     |
| Dynamically remove participants     | Yes                                                                  | No direct equivalent     |
| Participant arrives without waiting | `arrive()`                                                           | No direct equivalent     |
| Wait for phase completion           | `arriveAndAwaitAdvance()`                                            | `await()`                |
| Custom barrier action               | Not through the same constructor pattern; can override `onAdvance()` | Supported by constructor |

Use `CyclicBarrier` for a fixed group of threads that must meet at a barrier repeatedly. Use `Phaser` when participants or phases need to change dynamically.

## 5. Dynamically register and deregister threads

One of Phaser's most useful features is that the number of participating threads can change.

```
Phaser phaser = new Phaser(1); // Main thread registers itself

phaser.register(); // Register another participant
phaser.register(); // Register another participant

System.out.println(phaser.getRegisteredParties()); // 3
```

Here, the initial party plus the two registered parties give us three registered parties.

To remove a participant permanently:

```
phaser.arriveAndDeregister();
```

This signals that the participant has arrived at the current phase and will not participate in future phases.

Be careful: every registered party must eventually arrive or deregister. Otherwise, the phase may never advance.

## 6. What is `onAdvance()`?

Phaser provides a method called `onAdvance()` that can be overridden to perform an action whenever a phase advances.

It can also determine whether the Phaser should terminate.

```
import java.util.concurrent.Phaser;

public class Main {

    public static void main(String[] args) {

        Phaser phaser = new Phaser(2) {

            @Override
            protected boolean onAdvance(
                    int phase, int registeredParties) {

                System.out.println("Completed phase: " + phase);

                return phase >= 2 || registeredParties == 0;
            }
        };

        Runnable task = () -> {
            for (int i = 0; i < 3 && !phaser.isTerminated(); i++) {
                System.out.println(
                    Thread.currentThread().getName()
                    + " working in phase " + phaser.getPhase()
                );

                phaser.arriveAndAwaitAdvance();
            }
        };

        new Thread(task, "Thread-1").start();
        new Thread(task, "Thread-2").start();
    }
}
```

In this example, `onAdvance()` runs whenever the phase advances. Returning `true` terminates the Phaser; returning `false` allows it to continue.

## 7. Phaser vs CountDownLatch vs Semaphore

| Feature              | Phaser                      | CountDownLatch                 | Semaphore                        |
| -------------------- | --------------------------- | ------------------------------ | -------------------------------- |
| Primary purpose      | Coordinate execution phases | Wait for a count to reach zero | Limit concurrent access          |
| Reusable             | Yes                         | No                             | Yes                              |
| Dynamic participants | Yes                         | No                             | Not applicable in the same sense |
| Main methods         | `arriveAndAwaitAdvance()`   | `countDown()`, `await()`       | `acquire()`, `release()`         |
| Typical use          | Multi-stage processing      | Wait for initialization tasks  | Limit concurrent API calls       |

Remember these three ideas:

- CountDownLatch: Wait until a fixed count of events is completed.
- Semaphore: Control how many threads may access a resource at once.
- Phaser: Coordinate multiple threads across successive phases.
