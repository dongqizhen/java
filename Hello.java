import java.util.Scanner;

// 类名要求：

//类名必须以英文字母开头，后接字母，数字和下划线的组合
//习惯以大写字母开头


public class Hello {
    public static void main(String[] args) {
        // int x=100;
        // System.out.println(x);
        // x=200;
        // System.out.println(x);
        // int n=x;
        // System.out.println(n);
        // n=n+100;
        // System.out.println("x = " + x); // 打印x的值
        // System.out.println("n = " + n); // 再次打印n的值，n应该是200还是300？
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input your name:"); // 打印提示
        String name = scanner.nextLine(); // 读取一行输入并获取字符串
        System.out.print("Input your age: "); // 打印提示
        int age = scanner.nextInt(); // 读取一行输入并获取整数
        System.out.printf("Hi, %s, you are %d\n", name, age); // 格式化输出
    }
}

//变量 ：基本类型的变量和引用类型的变量。

// 整数类型：byte，short，int，long
// 浮点数类型：float，double
// 字符类型：char
// 布尔类型：boolean