public class PrintThread implements Runnable {
	private char zeichen;
	
	public PrintThread(char zeichen) {
		this.zeichen = zeichen;
	}
	
	public void run() {
		try {
			while(!Thread.interrupted()) {
				System.out.print(this.zeichen);
				Thread.sleep(1000);
			}
			System.out.println("Thread " + this.zeichen + ": Interrupted");
		}
		catch (InterruptedException ie) {
			System.out.println("Thread " + this.zeichen + ":\n"+ ie.getLocalizedMessage());
		}
	}
}
