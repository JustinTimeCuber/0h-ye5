import processing.core.PApplet;
import processing.event.MouseEvent;

public class OhYes extends PApplet {
    Grid grid;
    boolean showAdjacent = false;
    float buttonAreaHeight;
    public static void main(String[] args) {
        PApplet.main("OhYes");
    }

    @Override
    public void settings() {
        size(600, 800);
    }
    @Override
    public void setup() {
        buttonAreaHeight = height - width;
        surface.setTitle("0h ye5");
        grid = new Grid(9);
    }

    @Override
    public void draw() {
        background(0);
        int gridSize = grid.getSize();
        textSize(0.7f * width / gridSize);
        textAlign(CENTER, CENTER);
        noStroke();
        for(int row = 0; row < gridSize; row++) {
            for(int col = 0; col < gridSize; col++) {
                if(grid.isRed(row, col)) {
                    fill(200, 20, 20);
                } else if(grid.isBlue(row, col)) {
                    fill(20, 20, 200);
                } else if(grid.isEmpty(row, col)) {
                    fill(20, 20, 20);
                }
                rect((col + 0.1f) * width / gridSize, (row + 0.1f) * width / gridSize, 0.8f * width / gridSize, 0.8f * width / gridSize);
                if(grid.hasHint(row, col)) {
                    if(showAdjacent) {
                        fill(255, 255, 0);
                    } else {
                        fill(255);
                    }
                    text(showAdjacent ? grid.getAdjacent(row, col) : grid.getHint(row, col), (col + 0.5f) * width / gridSize, (row + 0.4f) * width / gridSize);
                }
            }
        }
        fill(255);
        noFill();
        strokeWeight(2);
        stroke(255);
        textSize(0.04f * width);
        rect(0.05f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text(showAdjacent ? "Show hints" : "Show adjacent", 0.18f * width, width + 0.25f * buttonAreaHeight);
        rect(0.37f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text("Solve step", 0.5f * width, width + 0.25f * buttonAreaHeight);
        rect(0.69f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text("Solve board", 0.82f * width, width + 0.25f * buttonAreaHeight);
        rect(0.05f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text("Clear", 0.18f * width, width + 0.7f * buttonAreaHeight);
        rect(0.37f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text("Change size", 0.5f * width, width + 0.7f * buttonAreaHeight);
        rect(0.69f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight);
        text("Generate", 0.82f * width, width + 0.7f * buttonAreaHeight);
    }
    @Override
    public void mousePressed() {
        if(mouseIn(0.05f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            showAdjacent = !showAdjacent;
            grid.updateAdjacent(false);
        } else if(mouseIn(0.37f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            grid.solveOneStep(true);
            grid.updateAdjacent(false);
        } else if(mouseIn(0.69f * width, width + 0.1f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            grid.solve(true);
            grid.updateAdjacent(false);
        } else if(mouseIn(0.05f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            grid = new Grid(grid.getSize());
        } else if(mouseIn(0.37f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            int newSize = grid.getSize();
            if(mouseButton == LEFT) {
                newSize++;
            } else if(mouseButton == RIGHT) {
                newSize--;
            }
            if(newSize < 2) {
                newSize = 2;
            }
            if(newSize > 20) {
                newSize = 20;
            }
            grid = new Grid(newSize);
        } else if(mouseIn(0.69f * width, width + 0.55f * buttonAreaHeight, 0.26f * width, 0.35f * buttonAreaHeight)) {
            grid.generate(0.2, 0.25, 0.99, 0.27 + 0.0067*grid.getSize(), 100);
        } else {
            int mouseRow = mouseY * grid.getSize() / width;
            int mouseCol = mouseX * grid.getSize() / width;
            if(mouseRow >= 0 && mouseCol >= 0 && mouseRow < grid.getSize() && mouseRow < grid.getSize()) {
                if(mouseButton == LEFT) {
                    if(grid.isRed(mouseRow, mouseCol)) {
                        grid.setEmpty(mouseRow, mouseCol);
                    } else if(grid.isEmpty(mouseRow, mouseCol)) {
                        grid.setBlue(mouseRow, mouseCol);
                    } else {
                        if(grid.hasHint(mouseRow, mouseCol)) {
                            int h = grid.getHint(mouseRow, mouseCol);
                            if(h < 2 * (grid.getSize() - 1)) {
                                grid.setBlue(mouseRow, mouseCol, h + 1);
                            }
                        } else {
                            grid.setBlue(mouseRow, mouseCol, 1);
                        }
                    }
                } else if(mouseButton == RIGHT) {
                    if(grid.isEmpty(mouseRow, mouseCol)) {
                        grid.setRed(mouseRow, mouseCol);
                    } else if(grid.isBlue(mouseRow, mouseCol)) {
                        if(grid.hasHint(mouseRow, mouseCol)) {
                            int h = grid.getHint(mouseRow, mouseCol);
                            grid.setBlue(mouseRow, mouseCol, h - 1);
                        } else {
                            grid.setEmpty(mouseRow, mouseCol);
                        }
                    }
                }
                grid.updateAdjacent(false);
            }
        }
    }
    private boolean mouseIn(float x1, float y1, float w, float h) {
        return mouseX > x1 && mouseY > y1 && mouseX - x1 < w && mouseY - y1 < h;
    }
    @Override
    public void mouseWheel(MouseEvent event) {
        int dir = event.getCount();
        int mouseRow = mouseY * grid.getSize() / width;
        int mouseCol = mouseX * grid.getSize() / width;
        if(mouseRow >= 0 && mouseCol >= 0 && mouseRow < grid.getSize() && mouseRow < grid.getSize()) {
            if(dir > 0) {
                if(grid.isRed(mouseRow, mouseCol)) {
                    grid.setEmpty(mouseRow, mouseCol);
                } else if(grid.isEmpty(mouseRow, mouseCol)) {
                    grid.setBlue(mouseRow, mouseCol);
                } else {
                    if(grid.hasHint(mouseRow, mouseCol)) {
                        int h = grid.getHint(mouseRow, mouseCol);
                        if(h < 2 * (grid.getSize() - 1)) {
                            grid.setBlue(mouseRow, mouseCol, h + 1);
                        }
                    } else {
                        grid.setBlue(mouseRow, mouseCol, 1);
                    }
                }
            } else if(dir < 0) {
                if(grid.isEmpty(mouseRow, mouseCol)) {
                    grid.setRed(mouseRow, mouseCol);
                } else if(grid.isBlue(mouseRow, mouseCol)) {
                    if(grid.hasHint(mouseRow, mouseCol)) {
                        int h = grid.getHint(mouseRow, mouseCol);
                        grid.setBlue(mouseRow, mouseCol, h - 1);
                    } else {
                        grid.setEmpty(mouseRow, mouseCol);
                    }
                }
            }
            grid.updateAdjacent(false);
        }
    }

    @Override
    public void keyTyped() {
        int mouseRow = mouseY * grid.getSize() / width;
        int mouseCol = mouseX * grid.getSize() / width;
        if (mouseRow >= 0 && mouseCol >= 0 && mouseRow < grid.getSize() && mouseRow < grid.getSize()) {
            if(Character.isDigit(key)) {
                int val = Integer.parseInt(String.valueOf(key));
                grid.setBlue(mouseRow, mouseCol, val);
            }
            grid.updateAdjacent(false);
        }
    }
}
