import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class CallableT {
    public static void main(String[] args) throws InterruptedException,ExecutionException{
        Callable<Integer>call=()->{
            return 10;
        };
        ThreadFactory tf=task->{
            Thread t=new Thread(task);
            t.setName("Payment Worker");
            return  t;
        };

        ExecutorService ex=Executors.newFixedThreadPool(3,tf);
        Future<Integer>future =ex.submit(call);
        System.out.println(future.get());
        ex.shutdown();

    }
}
