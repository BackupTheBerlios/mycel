public class ThreadTest {
	public static void main(String[] args) {
		Thread thread;
		ThreadGroup group;
		
		thread = Thread.currentThread();
		group = thread.getThreadGroup();
		thread = new Thread(group, new PrintThread('A'), "Test A");
		thread.start();
		thread = new Thread(new PrintThread('B'), "Test B");
		thread.start();
		printGroup(group);
		System.exit(0);
	}
	
	public static void printGroup(ThreadGroup group) {
		Thread[] list;
		Thread thread;
		int index;
		
		System.out.println("ThreadGroup:");
		System.out.println("\tName = " + group.getName());
		System.out.println("\tMax Priority = " + group.getMaxPriority());
		System.out.println("\tActive Count = " + group.activeCount());
		System.out.println("\tActive Group Count = " + group.activeGroupCount());
		System.out.println("\tIs Daemon = " + group.isDaemon());
		list = new Thread[group.activeCount()];
		group.enumerate(list);
		for (index = 0; index < list.length; index++) {
			System.out.println("\tThread " + index);
			thread = list[index];
			if (thread != null) {
				System.out.println("\t\tName = " + thread.getName());
				System.out.println("\t\tPriority = " + thread.getPriority());
				System.out.println("\t\tIs Alive = " + thread.isAlive());
				System.out.println("\t\tIs Daemon = " + thread.isDaemon());
				System.out.println("\t\tIs Interrupted = " + thread.isInterrupted());
			}
			else {
				System.out.println("\t\tThread is null.");
			}
		}
	}
}
