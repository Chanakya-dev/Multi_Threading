package MultiThreading;

import java.util.concurrent.CompletableFuture;

public class CompleteFuture {
    public static void main(String[] args) {

        // Completable Future With Void
        CompletableFuture<Void> cb= CompletableFuture.runAsync(()->{
            System.out.println("Task is RUnning");
        });
        System.out.println("Main Thread is Running");
        cb.join();

        // Completable Future With Integer
        CompletableFuture<Integer>cb1=CompletableFuture.supplyAsync(()->{
            return 10*2;
        });
        Integer result= cb1.join();
        System.out.println(result);

        // Completeble Future With Then Apply
        CompletableFuture<Integer> future=CompletableFuture.supplyAsync(()->{
            return 10*20;
        }).thenApply(x->{
            return x*100;
        });
        Integer result1=future.join();
        System.out.println(result1);
        
        // Completeble Future using thenAccept and supplyAsync Function
        CompletableFuture<Void>cb5=CompletableFuture
        .supplyAsync(()->
        {
            return 10*10;
        }).thenAccept((x)->{
            System.out.println(x);
        }).thenRun(()->{
            System.out.println("Hello World");
        });
        
        cb5.join();


        // Completeble Future to work dependent Async Activities
        CompletableFuture<String>future1=CompletableFuture.supplyAsync(()->"ORDER-101").thenCompose(
            orderid->CompletableFuture.supplyAsync(
                ()->" Payment for "+orderid
            )
        );
        System.out.println(future1.join());


        // Completable Future working with thenCombine()
    CompletableFuture<Integer> productPrice =
            CompletableFuture.supplyAsync(() -> 1000);

    CompletableFuture<Integer> deliveryCharge =
            CompletableFuture.supplyAsync(() -> 100);

    CompletableFuture<Integer> total =
            productPrice.thenCombine(
                    deliveryCharge,
                    (price, delivery) -> price + delivery
            );

    System.out.println(total.join());

// Immediate Completion or Executions 

CompletableFuture<Integer>imcb=new CompletableFuture<>();
imcb.complete(10);
System.out.println(imcb.isDone());
System.out.println(imcb.join());

CompletableFuture<Integer>imcb1=new CompletableFuture<>();
imcb1.completeExceptionally(new RuntimeException("Something Went Wrong"));
try{
    System.out.println(imcb1.join());
}catch(RuntimeException exp){
    System.out.println(exp.getMessage());
}

// Immediately getting the result static methods

CompletableFuture<Integer>future2=
CompletableFuture.completedFuture(100).thenApply(x->x*2);
System.out.println(future2.join());

 CompletableFuture<Integer> future4 =
                CompletableFuture.failedFuture(
                        new RuntimeException("Something went wrong")
                );

        System.out.println(future4.isDone());

        System.out.println(future4.isCompletedExceptionally());

        try {
            System.out.println(future4.join());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



    }
}
