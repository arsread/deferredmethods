package test;

import deferredmethods.Deferred;

public interface DefMethods extends Deferred {
    void profCall(String caller, String callee);
    void profAlloc(String mid, Object allocObj);
}
