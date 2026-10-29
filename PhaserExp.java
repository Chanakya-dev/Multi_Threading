package MultiThreading;

import java.util.concurrent.Phaser;

public class PhaserExp {
public static void main(String[] args) {
    Phaser ps=new Phaser(3);

     Runnable task = () -> {
            String name = Thread.currentThread().getName();

            System.out.println(name + " is working on Phase 1");
// Helps to wait until all threads complete process 1 
            ps.arriveAndAwaitAdvance();

            System.out.println(name + " is working on Phase 2");
    //  Helps to not wait until all threads complete process 2 
            ps.arrive();

            System.out.println(name + " completed both phases");
        };

        for (int i = 1; i <= 3; i++) {
            new Thread(task, "Thread-" + i).start();
        }
}    
}
