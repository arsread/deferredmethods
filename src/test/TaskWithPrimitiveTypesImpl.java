package test;

public class TaskWithPrimitiveTypesImpl implements TaskWithPrimitiveTypes {

    private static MultipleCounter counter;
    
    @Override
    public void setCounter(MultipleCounter c) {
        counter = c;
    }

    @Override
    public void doSomething(int i, byte b, long l, float f, double d, short s, char c, boolean bool) {
        if(i == 1) counter.addNumint();
        if(b == 1) counter.addNumbyte();
        if(l == 1) counter.addNumlong();
        if(f == 1) counter.addNumfloat();
        if(d == 1) counter.addNumdouble();
        if(s == 1) counter.addNumshort();
        if(c == 1) counter.addNumchar();
        if(bool == true) counter.addNumboolean();
    }

}
