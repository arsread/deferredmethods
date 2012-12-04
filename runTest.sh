java \
        -server -agentpath:${PWD}/lib/liboverAgent.jnilib \
        -Xbootclasspath/p:${PWD}/build/deferred_lock.jar:${PWD}/build/deferred_thread.jar:${PWD}/build/boot_interface.jar \
        -cp ${PWD}/build/deferred.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred_test.jar \
        newTest.LockTest
