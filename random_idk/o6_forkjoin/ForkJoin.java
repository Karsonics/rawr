package o6_forkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class ForkJoin
{
  private static final int THRESHOLD = 10;
  
  @SuppressWarnings("serial")
  static class Task extends RecursiveTask<Integer>
  {
    private final int[] array;
    private final int start, end;
    
    Task(int[] array, int start, int end)
    {
      this.array = array;
      this.start = start;
      this.end = end;
    }

    @Override
    protected Integer compute()
    {
      if( end - start < THRESHOLD )
      {
         //int maxIndex = start;
         int minIndex = start;
         for(int i=start+1; i<end; i++)
         {
           if( array[i] < array[minIndex] )
           {
             //maxIndex = i;
             minIndex = i;
           }
         }
         return array[minIndex];
      }
      else
      {
        int mid = (start + end)/2;
        Task leftTask = new Task(array, start, mid);
        Task rightTask = new Task(array, mid, end);
        invokeAll( leftTask, rightTask );
            
        return Math.min(leftTask.join(), rightTask.join());
        //return Math.max(leftTask.join(), rightTask.join());
      }
    } 
  }

  public static void main(String[] args)
  {
    int[] array = new int[100_000];
    Arrays.parallelSetAll(array, i -> ThreadLocalRandom.current().nextInt(0, 1_000_000));
    
    ForkJoinPool executor = new ForkJoinPool(8);
    Task root = new Task(array, 0, array.length);
    executor.execute(root);
    
    // using the build-in pool
    // ForkJoinPool.commonPool().execute(root);
    
    System.out.println("min " +  root.join() );
  }

}