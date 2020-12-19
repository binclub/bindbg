import java.util.*;
import java.lang.management.*;

public class LoopTest {

	public static void main(String[] args) throws Throwable {
		int sum = 0;
		Random random = new Random();
		while (true) {
			sum += random.nextInt();
			System.out.print(ManagementFactory.getRuntimeMXBean().getName());
			System.out.print('\t');
			System.out.println(sum);
			Thread.sleep(1500);
		}
	}

}
