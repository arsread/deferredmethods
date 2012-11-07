package newTest;

import deferredmethods.Deferred;
import deferredmethods.ProcessingCheckPoint;

public interface CPTestInterfce extends Deferred {

	void foo1();

	void foo2();

	void foo2end();

	void foo3(ProcessingCheckPoint pcp2);
	
	
}
