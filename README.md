ThreadPoolExecutor
=======================
The Basics
----------
The executor framework introduces an interface to manage task execution: Executor. Executor is the interface you use to submit tasks, represented as Runnable instances. This interface also isolates a task submission from a task execution: executors with different execution policies all publish the same submission interface: should you change your execution policy, your submission logic wouldn’t be affected by the change.

Configuring a ThreadPoolExecutor
--------------------------------
The most extensive constructor of ThreadPoolExecutor is

public ThreadPoolExecutor(
	int corePoolSize,
	int maxPoolSize,
	long keepAlive,
	TimeUnit unit,
	BlockingQueue<Runnable> workQueue,
	RejectedExecutionHandler handler);
	
Core and maximum pool sizes
---------------------------
<img align="left" src="http://tutorials.jenkov.com/images/java-concurrency-utils/thread-pool-executor.png" />


Queuing
-------
Any **BlockingQueue** may be used to transfer and hold submitted tasks. The use of this queue interacts with pool sizing:
*If fewer than corePoolSize threads are running, the Executor always prefers adding a new thread rather than queuing.
*If corePoolSize or more threads are running, the Executor always prefers queuing a request rather than adding a new thread.
*If a request cannot be queued, a new thread is created unless this would exceed maximumPoolSize, in which case, the task will be rejected.
There are three general strategies for queuing:
1. ***Direct handoffs***. A good default choice for a work queue is a **SynchronousQueue** that hands off tasks to threads without otherwise holding them. Here, an attempt to queue a task will fail if no threads are immediately available to run it, so a new thread will be constructed. This policy avoids lockups when handling sets of requests that might have internal dependencies. Direct handoffs generally require unbounded maximumPoolSizes to avoid rejection of new submitted tasks. This in turn admits the possibility of unbounded thread growth when commands continue to arrive on average faster than they can be processed.
2. ***Unbounded queues***. Using an unbounded queue (for example a **LinkedBlockingQueue** without a predefined capacity) will cause new tasks to wait in the queue when all corePoolSize threads are busy. Thus, no more than corePoolSize threads will ever be created. (And the value of the maximumPoolSize therefore doesn't have any effect.) This may be appropriate when each task is completely independent of others, so tasks cannot affect each others execution; for example, in a web page server. While this style of queuing can be useful in smoothing out transient bursts of requests, it admits the possibility of unbounded work queue growth when commands continue to arrive on average faster than they can be processed.
3. ***Bounded queues***. A bounded queue (for example, an **ArrayBlockingQueue**) helps prevent resource exhaustion when used with finite maximumPoolSizes, but can be more difficult to tune and control. Queue sizes and maximum pool sizes may be traded off for each other: Using large queues and small pools minimizes CPU usage, OS resources, and context-switching overhead, but can lead to artificially low throughput. If tasks frequently block (for example if they are I/O bound), a system may be able to schedule time for more threads than you otherwise allow. Use of small queues generally requires larger pool sizes, which keeps CPUs busier but may encounter unacceptable scheduling overhead, which also decreases throughput.

Rejected tasks
--------------
New tasks submitted in method **execute(java.lang.Runnable)** will be rejected when the Executor has been shut down, and also when the Executor uses finite bounds for both maximum threads and work queue capacity, and is saturated. In either case, the execute method invokes the **RejectedExecutionHandler.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)** method of its **RejectedExecutionHandler**. Four predefined handler policies are provided:
1. In the default **ThreadPoolExecutor.AbortPolicy**, the handler throws a runtime RejectedExecutionException upon rejection.
2. In **ThreadPoolExecutor.CallerRunsPolicy**, the thread that invokes execute itself runs the task. This provides a simple feedback control mechanism that will slow down the rate that new tasks are submitted.
3. In **ThreadPoolExecutor.DiscardPolicy**, a task that cannot be executed is simply dropped.
4. In **ThreadPoolExecutor.DiscardOldestPolicy**, if the executor is not shut down, the task at the head of the work queue is dropped, and then execution is retried (which can fail again, causing this to be repeated.)
It is possible to define and use other kinds of RejectedExecutionHandler classes. Doing so requires some care especially when policies are designed to work only under particular capacity or queuing policies.
