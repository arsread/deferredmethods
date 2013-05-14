package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.Ret;
import deferredmethods.proc.AdaptiveProc;

public class SimpleTest {
	public static DeferredEnv<SimpleTestInter> env = DeferredExecution
			.createDeferredEnv(SimpleTestInter.class, SimpleTestImpl.class,
					new AdaptiveProc(), 10);
	public static SimpleTestInter st = env.getProxy();
	
	public static void main(String[] args){
		for (int i=0;i<20;i++){
			st.noRet();			
		}
		Ret<Integer> val=st.ret();
		for (int i=0;i<20;i++){
			st.noRet();			
		}
		try {
			System.out.println(val.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
