/**
 * 
 */
package multiTask;

/**
 * @author medhat
 *
 */
public class MultiTask {
	
	public static class MyRunable implements Runnable {
		@Override
		public void run() {
			System.out.println("MyThread running");
			Thread thread = Thread.currentThread();
			System.out.println("current thread is  "+ thread.getName()+thread.getId()+thread.getPriority());
		}
	}

	public static void main(String[] args) {
		Thread thread = new Thread(new MyRunable(),"lol");
		System.out.println(thread.getName());
		thread.start();
	}
}
