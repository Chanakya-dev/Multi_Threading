## 1. Imagine this situation 🎂

You are waiting for your birthday party to start.

Before the party starts, you need 3 things to happen:

Task 1: Balloons\
Someone must blow up the balloons.

Task 2: Cake\
Someone must bring the cake.

Task 3: Gifts\
Someone must bring the gifts.

You say:

"I will start the party only when all 3 things are ready!"

That is exactly the idea behind `CountDownLatch`.

## 2. How does CountDownLatch work?

Imagine you have a counter that starts at 3.

THINGS READY

# 0 / 3

Balloons

Cake

Gifts

The party is waiting for all three things.

Balloons are readyCake is readyGifts are readyReset and try again

Every time one thing is ready, the counter goes down by one.

- Initially: `3`
- Balloons ready: `2`
- Cake ready: `1`
- Gifts ready: `0`

When the counter becomes zero, the waiting person is allowed to continue.

## 3. Now connect this to Java

There are only two important methods you need to understand first.

```
CountDownLatch latch = new CountDownLatch(3);
```

This means: "I am waiting for 3 things to finish."

```
latch.countDown();
```

This means: "One thing has finished. Reduce the counter by one."

```
latch.await();
```

This means: "Wait here until the counter becomes zero."

Think of them this way:

| Java code               | Meaning in our birthday example |
| ----------------------- | ------------------------------- |
| `new CountDownLatch(3)` | We need 3 things ready          |
| `countDown()`           | One thing is ready              |
| `await()`               | Wait until everything is ready  |

## 4. What does this look like in a Java program?

```
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args)
            throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(3);

        new Thread(() -> {
            System.out.println("Balloons are ready");
            latch.countDown();
        }).start();

        new Thread(() -> {
            System.out.println("Cake is ready");
            latch.countDown();
        }).start();

        new Thread(() -> {
            System.out.println("Gifts are ready");
            latch.countDown();
        }).start();

        latch.await();

        System.out.println("Party starts!");
    }
}
```

Read the code in this order:

1. Create a counter starting at `3`.
2. Start three separate threads to complete three tasks.
3. Each thread calls `countDown()` when its task is finished.
4. The main thread reaches `latch.await()` and waits.
5. After all three tasks finish, the counter reaches zero.
6. The main thread continues and prints `Party starts!`.

The three tasks can finish in any order, but the final message won't print until all three have called `countDown()`.

Remember just this sentence for now:

`CountDownLatch` means "Wait until the required number of things are finished."
