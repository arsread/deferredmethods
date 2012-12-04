java \
        -server -agentpath:${PWD}/lib/liboverAgent.jnilib \
        -Xbootclasspath/p:${PWD}/build/deferred_thread.jar:${PWD}/build/boot_interface.jar \
        -cp ${PWD}/build/deferred_test.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred.jar \
        newTest.LockTest
