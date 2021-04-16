class ErsterThread extends Thread {
	long erg;

	public void run() {
		while (true) {
			System.out.println(erg++);
		}
	}
}

class ZweiterThread extends Thread {
	long erg;

	public void run() {
		while (true) {
			System.out.println(erg--);
		}
	}
}

public class ThreadTest1 {
	public static void main(String args[]) {
		ErsterThread p = new ErsterThread();
		ZweiterThread q = new ZweiterThread();
		p.start();
		q.start();
	}
}