package org.zywx.cooldev.entity.app;

public class test {
	public static void main(String[] args) {
		 int i = getValue(2);
		 System.out.println(i);

	}
	public static int getValue(int i) {
        int result = 0;
        switch (i) {
        case 1:
            result = result + i;
        case 2:
            result = result + i * 2;
        case 3:
            result = result + i * 3;
        }
        return result;
    }
}
  abstract class MyClass2 {

    public int constInt = 5;
    public abstract void anotherMethod()  ;
    public void method() {
    }
}
class T1 {

	int i = 0;

	public void Test1() {
		System.out.println(i);
	}

	public void Test2() {
		System.out.println(i);
	}
}

class T2 extends T1 {

	int i = 2;

	public void Test2() {
		System.out.println(i);
	}

	
}