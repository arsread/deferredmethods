echo "DaCapo:origin"
java -jar /home/arsread/dacapo-9.12-bach.jar $1 

echo "DaCapo:sync-instr"
java \
	-javaagent:${PWD}/build/sync_instr.jar \
        -Xbootclasspath/p:${PWD}/build/deferred_lock.jar:${PWD}/build/deferred_thread.jar:${PWD}/build/boot_interface.jar:/home/arsread/Downloads/asm-debug-all-4.1.jar \
        -cp ${PWD}/build/deferred.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred_test.jar \
	-jar /home/arsread/dacapo-9.12-bach.jar $1 
