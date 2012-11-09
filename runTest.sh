java \
        -server -agentpath:${PWD}/lib/liboverAgent.jnilib \
        -Xbootclasspath/p:${PWD}/build/deferred_thread.jar \
        -cp ${PWD}/build/deferred_test.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred.jar:${PWD}/build/deferred_test.jar \
        newTest.CheckPointTest
