#./runDacapo.sh batik
#./runDacapo.sh eclipse
#./runDacapo.sh fop 
#./runDacapo.sh h2
##./runDacapo.sh jython
#./runDacapo.sh luindex
#./runDacapo.sh lusearch
#./runDacapo.sh sunflow
#./runDacapo.sh xalan

echo "">DacapoResult_tmp

TESTS="batik eclipse fop h2 luindex lusearch sunflow xalan"
for TEST in ${TESTS}
do
	./runDacapo.sh ${TEST} >> DacapoResult_tmp 2>&1
done

cat DacapoResult_tmp | grep "DaCapo" >DacapoResult
