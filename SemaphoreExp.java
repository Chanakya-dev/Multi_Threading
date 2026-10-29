package MultiThreading;

import java.util.concurrent.Semaphore;

public class SemaphoreExp {
    public static void main(String[] args) {
        Semaphore sp = new Semaphore(2);

        Runnable r3 = () -> {
            boolean isAcquired = false;
            try {
                sp.acquire();
                System.out.println(
                        "Waiting to acquire: "
                                + Thread.currentThread().getName());

                isAcquired = true;
                System.out.println("Thread Acquired Lock " + Thread.currentThread().getName());
                Thread.sleep(2000);
            } catch (InterruptedException exp) {
                System.out.println(exp.getMessage());
            } finally {
                if (isAcquired) {
                    System.out.println("Released Lock " + Thread.currentThread().getName());
                    sp.release();
                }
            }

        };
        for (int i = 0; i < 3; i++) {
            new Thread(r3, "TS - " + i).start();
        }

    }
}
