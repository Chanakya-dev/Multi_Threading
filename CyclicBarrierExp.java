package MultiThreading;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExp {
public static void main(String[] args) {
        CyclicBarrier cb=new CyclicBarrier(3);
    Runnable rb=()->{
        try{
        System.out.println(Thread.currentThread().getName());
        cb.await();
        System.out.println("Cyclic Barrier Released "+Thread.currentThread().getName());
    }catch(Exception exp){
    System.out.println(exp.getMessage());
    }
    };
    Thread t=new Thread(rb);
    Thread t1=new Thread(rb);
    Thread t2=new Thread(rb);
    t.start();
    t1.start();
    t2.start();
System.out.println("Main Thread is Running");
}    
}
