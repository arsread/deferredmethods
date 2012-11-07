java \
        -server -agentpath:${PWD}/lib/liboverAgent.so \
        -Xbootclasspath/p:${PWD}/build/deferred_thread.jar \
        -cp ${PWD}/lib/junit-4.10.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred.jar:${PWD}/build/deferred_test.jar \
        org.junit.runner.JUnitCore test.deferredmethods.DeferredEnvTest test.deferredmethods.bytecodegeneration.DeferredInterfaceReaderTest
