package playground.fromthepaper;

import java.io.IOException;
import java.util.Stack;

import deferredmethods.Deferred;
import deferredmethods.DeferredExecution;
import deferredmethods.ProcessingHooks;
import deferredmethods.ThreadPoolProc;

/****** to refactor *******/

class Analysis {

    private static final ThreadLocal<Stack<MethodID>> stackTL = new ThreadLocal<Stack<MethodID>>() {
        @Override
        protected Stack<MethodID> initialValue() {
            return new Stack<MethodID>();
        }
    };

    private static DefMethods def;

    static {
        try {
            def = (DefMethods) DeferredExecution.createDeferredEnv(DefMethods.class, DefMethodsImpl.class,
                    new ThreadPoolProc(4));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onEntry(MethodID thisMID) {
        Stack<MethodID> localStack = stackTL.get();
        def.profCall(localStack.peek(), thisMID);
        localStack.push(thisMID);
    }

    public void onExit() {
        stackTL.get().pop();
    }

    public void onObjectAlloc(Object allocObj) {
        def.profAlloc(stackTL.get().peek(), allocObj);
    }

}

// //////

interface Profile {
    public void profCall(MethodID caller, MethodID callee);

    public void profAlloc(MethodID mid, Object allocObj);

    public void integrate(Profile prof);
}

public interface DefMethods extends Deferred {
    public void profCall(MethodID caller, MethodID callee);

    public void profAlloc(MethodID mid, Object allocObj);
}

class DefMethodsImpl implements DefMethods, ProcessingHooks {

    private Profile profTS;
    private Profile profTL;

    public DefMethodsImpl(Profile prof) {
        profTS = prof;
    }

    @Override
    public void beforeProcessing() {
        profTL = new ProfileTL();
    }

    @Override
    public void afterProcessing() {
        profTS.integrate(profTL);
    }

    @Override
    public void profCall(MethodID caller, MethodID callee) {
        profTS.profCall(caller, callee);
    }

    @Override
    public void profAlloc(MethodID mid, Object allocObj) {
        profTS.profAlloc(mid, allocObj);
    }

}

/*********** TRASH ************/

class MethodID {
}

class ProfileTS implements Profile {

    @Override
    public void profCall(MethodID caller, MethodID callee) {
        // TODO Auto-generated method stub

    }

    @Override
    public void profAlloc(MethodID mid, Object allocObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void integrate(Profile prof) {
        // TODO Auto-generated method stub

    }

}

class ProfileTL implements Profile {

    @Override
    public void profCall(MethodID caller, MethodID callee) {
        // TODO Auto-generated method stub

    }

    @Override
    public void profAlloc(MethodID mid, Object allocObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void integrate(Profile prof) {
        // TODO Auto-generated method stub

    }

}
