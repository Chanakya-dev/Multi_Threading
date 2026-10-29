package MultiThreading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQ {
    public static void main(String[] args) {
        BlockingQueue<Integer>bq=new ArrayBlockingQueue<>(2);
        Thread t1=new Thread(()->{
           
            try{for(int  i=1;i<=5;i++){
                 System.out.println("Producing: " + i);

                    bq.put(i);

                    System.out.println("Added to queue: " + i);
                Thread.sleep(2000);
            }}
            catch(Exception exp){
                System.out.println(exp.getMessage());
            }
        });

        Thread t2=new Thread(()->{
            try{
            for(int i=1;i<=5;i++){
                 int result=bq.take();
                  System.out.println("Consuming: " + result);
                  Thread.sleep(3000);
            }}catch(Exception exp){
                System.out.println(exp.getMessage());
            }
        });
        t1.setName("Producer ");
        t1.start();
        t2.setName("Consumer ");
        t2.start();
    }
}
