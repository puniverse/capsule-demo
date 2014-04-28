package foo;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.ReceivePort;
import co.paralleluniverse.strands.channels.SendPort;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        if (false) {
            System.out.println("COMPILER: " + System.getProperty("java.vm.name"));
            System.out.println("VERSION: " + System.getProperty("java.version"));
            System.out.println("OS: " + System.getProperty("os.name"));
            System.out.println();
            System.out.println("ARGS: " + Arrays.toString(args));
            System.out.println("JVM-ARGS: " + ManagementFactory.getRuntimeMXBean().getInputArguments());
            System.out.println("========================================");
            System.out.println("SYSTEM-PROPS:");
            System.out.println(printMap(System.getProperties()));
            System.out.println("========================================");
            System.out.println("ENV: ");
            System.out.println(printMap(System.getenv()));
            System.out.println("========================================");
        }

        final Channel<Integer> cin = Channels.newChannel(0);

        new Fiber<Void>(() -> {
            for (int i = 0; i < 10; i++) {
                Strand.sleep(100);
                cin.send(i);
            }
            cin.close();
        }).start();

        final Channel<Integer> cout = Channels.newChannel(0);

        Channels.fiberTransform(cin, cout, (ReceivePort<Integer> in, SendPort<Integer> out) -> {
            Integer x;
            while ((x = in.receive()) != null) {
                if (x % 2 != 0) {
                    out.send(x);
                    out.send(x * 10);
                    out.send(x * 100);
                }
            }
        });

//        ReceivePort<Integer> cout = Channels.transform(cin)
//                .filter(x -> x % 2 != 0)
//                .flatmap(x -> Channels.toReceivePort(Arrays.asList(x, x * 10, x*100)));
        Fiber<Void> f = new Fiber<Void>(() -> {
            Integer i;
            while ((i = cout.receive()) != null)
                System.out.println("--> " + i);
        }).start();

        f.join();
    }

    private static String printMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream().forEach((entry) -> {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        });
        return sb.toString();
    }
}
