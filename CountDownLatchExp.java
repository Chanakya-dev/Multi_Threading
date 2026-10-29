package MultiThreading;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExp {
public static void main(String[] args) throws Exception{
    CountDownLatch cd=new CountDownLatch(2);
Runnable r1=()->{
    try{
    System.out.println(Thread.currentThread().getName()+" started for the Count Down Latching");
    Thread.sleep(2000);
    }catch(InterruptedException exp){
        Thread.currentThread().interrupt();     
    }finally{
         System.out.println(
                Thread.currentThread().getName() + " completed Countdown Latch"
            );
        cd.countDown();
    }
};
Thread t4=new Thread(r1);
Thread t5=new Thread(r1);
t4.start();
t5.start();
System.out.println("Main Thread is waiting");
cd.await();
System.out.println("Main Thread Execution completed");
}    
}
