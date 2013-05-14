package newTest;

import deferredmethods.Deferred;
import deferredmethods.Ret;

public interface SimpleTestInter extends Deferred {
	public void noRet();
	Ret<Integer> ret();
}
