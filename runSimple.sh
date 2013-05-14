#echo "origin"
#java -jar /home/arsread/dacapo-9.12-bach.jar $1

java \
	-javaagent:${PWD}/build/sync_instr.jar \
        -Xbootclasspath/p:${PWD}/build/deferred_lock.jar:${PWD}/build/deferred_thread.jar:${PWD}/build/boot_interface.jar:/home/arsread/Downloads/asm-debug-all-4.1.jar \
        -cp .:${PWD}/build/deferred.jar:${PWD}/lib/asm-4.0.jar:${PWD}/build/deferred_test.jar \
	test1.test2.SimpleTest
