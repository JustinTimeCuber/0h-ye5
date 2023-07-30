import java.util.ArrayList;
import java.util.HashMap;

public class GridGenerator {
    private final HashMap<Integer, ArrayList<Grid>> gridMap = new HashMap<>();
    private Thread[] threads  = new Thread[Runtime.getRuntime().availableProcessors() / 2];
    private int size;
    public GridGenerator(int size) {
        this.size = size;
        for(int i = 2; i <= 20; i++) {
            gridMap.put(i, new ArrayList<>());
        }
        startThreads();
    }
    public void setSize(int size) {
        this.size = size;
        startThreads();
    }
    public void startThreads() {
        for(int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                if (!threads[i].isAlive()) {
                    threads[i] = new GridGeneratorThread(this);
                    threads[i].start();
                }
            } else {
                threads[i] = new GridGeneratorThread(this);
                threads[i].start();
            }
        }
    }
    public boolean gridReady(int size) {
        return !gridMap.get(size).isEmpty();
    }
    public Grid getGrid(int size) throws InterruptedException {
        startThreads();
        synchronized (gridMap) {
            while (!gridReady(size)) {
                System.out.println("waiting");
                gridMap.wait();
            }
        }
        Grid g = gridMap.get(size).remove(0);
        startThreads();
        return g;
    }
    public void generate() throws InterruptedException {
        do {
            int s = size;
            Grid g = new Grid(s);
            g.generate();
            synchronized (gridMap) {
                gridMap.get(s).add(g);
                System.out.println("notifying");
                gridMap.notify();
            }
        } while (gridMap.get(size).size() < 20);
    }

}
