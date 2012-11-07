package newTest;

import java.util.concurrent.atomic.AtomicInteger;

public class FooImpl implements FooInterface {
	private final AtomicInteger counter;

	public FooImpl(AtomicInteger counter) {
		this.counter = counter;
	}

	@Override
	public void foo1() {
//		try { Thread.sleep(1000); } catch(InterruptedException e) { }
//		System.out.println("Foo1-counter:"+counter);
		counter.incrementAndGet();
	}

	@Override
	public void foo1printend() {
//		System.out.println("ending of Foo1!");
	}

	@Override
	public void foo2() {
//		try { Thread.sleep(1000); } catch(InterruptedException e) { }
//		System.out.println("Foo2-counter:"+counter);
		counter.incrementAndGet();
	}

	@Override
	public void foo3() {
		try { Thread.sleep(1); } catch(InterruptedException e) { }
//		System.out.println("Foo3-counter:"+counter);
		counter.incrementAndGet();		
	}

	@Override
	public void foo2printend() {
//		System.out.println("ending of Foo2!");
	}

	@Override
	public void foo3printend() {
//		System.out.println("ending of Foo3!");	
	}

}
