java \
        -server -agentpath:${PWD}/lib/liboverAgent.so \
	-javaagent:/home/arsread/workspace/SyncInstrument/testInstr.jar \
        -Xbootclasspath/p:${PWD}/build/deferred_lock.jar:${PWD}/build/deferred_thread.jar:${PWD}/build/boot_interface.jar \
        -cp ${PWD}/build/deferred.jar:/home/arsread/workspace/disl/lib/asm-debug-all-4.0.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred_test.jar \
        newTest.SyncTest2
