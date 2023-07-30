public class GridGeneratorThread extends Thread {
    private final GridGenerator gridGenerator;
    GridGeneratorThread(GridGenerator gridGenerator) {
        this.gridGenerator = gridGenerator;
    }
    @Override
    public void run() {
        try {
            gridGenerator.generate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
