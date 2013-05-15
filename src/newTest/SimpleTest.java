package newTest;

import deferredmethods.DeferredEnv;
import deferredmethods.DeferredExecution;
import deferredmethods.Ret;
import deferredmethods.proc.ShadowThreadProc;

public class SimpleTest {
	public static DeferredEnv<SimpleTestInter> env = DeferredExecution
			.createDeferredEnv(SimpleTestInter.class, SimpleTestImpl.class,
					new ShadowThreadProc(), 100);
	public static SimpleTestInter st = env.getProxy();
	
	public static void main(String[] args){
		for (int i=0;i<5;i++){
			st.noRet(i+1);			
		}
		Ret<Integer> val=st.ret();
		for (int i=0;i<5;i++){
			st.noRet(i+6);			
		}
		try {
			System.out.println(val.get(env));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
