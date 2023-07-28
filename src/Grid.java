import java.util.Random;

public class Grid {
    private static Random random = new Random();
    private final byte EMPTY = -1;
    private final byte RED = -2;
    private byte[][] values;
    private byte[][] adjacent;
    private boolean[][] solvedTiles;
    private byte size;
    public Grid(int size) {
        if(size <= 0) {
            throw new IllegalArgumentException("Grid size must be positive.");
        }
        this.size = (byte)size;
        values = new byte[size][size];
        adjacent = new byte[size][size];
        solvedTiles = new boolean[size][size];
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                values[row][col] = EMPTY;
            }
        }
    }
    public boolean isEmpty(int row, int col) {
        return values[row][col] == EMPTY;
    }
    public boolean isRed(int row, int col) {
        return values[row][col] == RED;
    }
    public boolean isBlue(int row, int col) {
        return values[row][col] >= 0;
    }
    public boolean hasHint(int row, int col) {
        return values[row][col] >= 1;
    }
    public int getHint(int row, int col) {
        return hasHint(row, col) ? values[row][col] : 0;
    }
    public int getAdjacent(int row, int col) {
        return adjacent[row][col];
    }
    public int getSize() {
        return size;
    }
    public void setRed(int row, int col) {
        values[row][col] = RED;
    }
    public void setEmpty(int row, int col) {
        values[row][col] = EMPTY;
    }
    public void setBlue(int row, int col) {
        if(isBlue(row, col)) return;
        values[row][col] = 0;
    }
    public void setBlue(int row, int col, int hint) {
        values[row][col] = (byte)hint;
    }
    public void updateAdjacent(boolean all) {
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                // only calculate adjacent blue tiles to tiles with a hint
                if(!hasHint(row, col) && !all) {
                    adjacent[row][col] = 0;
                    continue;
                }
                byte adj = 0;
                // adjacent blue tiles to the right
                for(int offset = 1; col + offset < size && isBlue(row, col + offset); offset++) {
                    adj++;
                }
                // adjacent blue tiles to the left
                for(int offset = 1; col - offset >= 0 && isBlue(row, col - offset); offset++) {
                    adj++;
                }
                // adjacent blue tiles to the bottom
                for(int offset = 1; row + offset < size && isBlue(row + offset, col); offset++) {
                    adj++;
                }
                // adjacent blue tiles to the top
                for(int offset = 1; row - offset >= 0 && isBlue(row - offset, col); offset++) {
                    adj++;
                }
                adjacent[row][col] = adj;
            }
        }
    }
    public boolean solveOneStep(boolean verbose) {
        updateAdjacent(false);
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(!hasHint(row, col)) {
                    continue;
                }
                // look for blue tiles that see too much - shouldn't happen
                if(adjacent[row][col] > values[row][col]) {
                    System.err.println("Tile at (row, col) = (" + row + ", " + col + ") needs to see " + values[row][col] + " but sees " + adjacent[row][col] + "! Cannot solve from here.");
                    return false;
                }
                // look for blue tiles that are already "completed"
                if(adjacent[row][col] == values[row][col] && !solvedTiles[row][col]) {
                    solvedTiles[row][col] = true;
                    int c = 0;
                    // block view on right
                    for(int offset = 0; col + offset + 1 < size && isBlue(row, col + offset); offset++) {
                        if(isEmpty(row, col + offset + 1)) {
                            setRed(row, col + offset + 1);
                            c++;
                        }
                    }
                    // block view on left
                    for(int offset = 0; col - offset - 1 >= 0 && isBlue(row, col - offset); offset++) {
                        if(isEmpty(row, col - offset - 1)) {
                            setRed(row, col - offset - 1);
                            c++;
                        }
                    }
                    // block view on bottom
                    for(int offset = 0; row + offset + 1 < size && isBlue(row + offset, col); offset++) {
                        if(isEmpty(row + offset + 1, col)) {
                            setRed(row + offset + 1, col);
                            c++;
                        }
                    }
                    // block view on top
                    for(int offset = 0; row - offset - 1 >= 0 && isBlue(row - offset, col); offset++) {
                        if(isEmpty(row - offset - 1, col)) {
                            setRed(row - offset - 1, col);
                            c++;
                        }
                    }
                    if(c > 0) {
                        if(verbose) {
                            System.out.println("Placed " + c + " red tiles to block view of blue tile at (row, col) = (" + row + ", " + col + ")");
                        }
                        return true;
                    }
                }
                // look for blue tiles that are incomplete
                if(adjacent[row][col] < values[row][col]) {
                    int remaining = values[row][col] - adjacent[row][col];
                    // calculate maximum additional tiles that can be added in each direction
                    int maxFreeRight = 0, maxFreeLeft = 0, maxFreeDown = 0, maxFreeUp = 0;
                    int attachedRight = 0, attachedLeft = 0, attachedDown = 0, attachedUp = 0;
                    for(int offset = 1; col + offset < size; offset++) {
                        if(isRed(row, col + offset)) {
                            break;
                        }
                        if(isEmpty(row, col + offset) || maxFreeRight > 0) {
                            maxFreeRight++;
                        } else {
                            attachedRight++;
                        }
                    }
                    for(int offset = 1; col - offset >= 0; offset++) {
                        if(isRed(row, col - offset)) {
                            break;
                        }
                        if(isEmpty(row, col - offset) || maxFreeLeft > 0) {
                            maxFreeLeft++;
                        } else {
                            attachedLeft++;
                        }
                    }
                    for(int offset = 1; row + offset < size; offset++) {
                        if(isRed(row + offset, col)) {
                            break;
                        }
                        if(isEmpty(row + offset, col) || maxFreeDown > 0) {
                            maxFreeDown++;
                        } else {
                            attachedDown++;
                        }
                    }
                    for(int offset = 1; row - offset >= 0; offset++) {
                        if(isRed(row - offset, col)) {
                            break;
                        }
                        if(isEmpty(row - offset, col) || maxFreeUp > 0) {
                            maxFreeUp++;
                        } else {
                            attachedUp++;
                        }
                    }
                    int sum = maxFreeRight + maxFreeLeft + maxFreeDown + maxFreeUp;
                    if(sum < remaining) {
                        System.err.println("Tile at (row, col) = (" + row + ", " + col + ") needs to see " + values[row][col] + " but can't see more than " + (adjacent[row][col] + sum) + "! Cannot solve from here.");
                        return false;
                    }
                    int c = 0;
                    // minimum tiles on each side
                    int minRight = remaining - sum + maxFreeRight;
                    if(minRight > 0) {
                        for(int offset = 1; offset <= minRight; offset++) {
                            if(!isBlue(row, col + offset + attachedRight)) {
                                setBlue(row, col + offset + attachedRight);
                                c++;
                            }
                        }
                    }
                    int minLeft = remaining - sum + maxFreeLeft;
                    if(minLeft > 0) {
                        for(int offset = 1; offset <= minLeft; offset++) {
                            if(!isBlue(row, col - offset - attachedLeft)) {
                                setBlue(row, col - offset - attachedLeft);
                                c++;
                            }
                        }
                    }
                    int minDown = remaining - sum + maxFreeDown;
                    if(minDown > 0) {
                        for(int offset = 1; offset <= minDown; offset++) {
                            if(!isBlue(row + offset + attachedDown, col)) {
                                setBlue(row + offset + attachedDown, col);
                                c++;
                            }
                        }
                    }
                    int minUp = remaining - sum + maxFreeUp;
                    if(minUp > 0) {
                        for(int offset = 1; offset <= minUp; offset++) {
                            if(!isBlue(row - offset - attachedUp, col)) {
                                setBlue(row - offset - attachedUp, col);
                                c++;
                            }
                        }
                    }
                    if(c > 0) {
                        if(verbose) {
                            updateAdjacent(false);
                            System.out.println("Placed " + c + " blue tiles to " + (adjacent[row][col] < values[row][col] ? "partially " : "") + "complete blue tile at (row, col) = (" + row + ", " + col + ")");
                        }
                        return true;
                    }
                }
            }
        }
        int emptyTiles = 0;
        for(int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if(!isEmpty(row, col)) {
                    continue;
                }
                emptyTiles++;
                setBlue(row, col);
                updateAdjacent(false);
                for(int col2 = 0; col2 < size; col2++) {
                    if(!hasHint(row, col2)) {
                        continue;
                    }
                    if(adjacent[row][col2] > values[row][col2]) {
                        setRed(row, col);
                        updateAdjacent(false);
                        if(verbose) {
                            System.out.println("Placed 1 red tile due to blue tile at (row, col) = (" + row + ", " + col2 + ")");
                        }
                        return true;
                    }
                }
                for(int row2 = 0; row2 < size; row2++) {
                    if(!hasHint(row2, col)) {
                        continue;
                    }
                    if(adjacent[row2][col] > values[row2][col]) {
                        setRed(row, col);
                        updateAdjacent(false);
                        if(verbose) {
                            System.out.println("Placed 1 red tile due to blue tile at (row, col) = (" + row2 + ", " + col + ")");
                        }
                        return true;
                    }
                }
                setEmpty(row, col);
                updateAdjacent(false);
                boolean defaultToRed = true;
                // look for hint tile to the left
                for(int offset = 1; col + offset < size; offset++) {
                    if(isRed(row, col + offset)) {
                        break;
                    }
                    if(hasHint(row, col + offset)) {
                        defaultToRed = false;
                    }
                }
                // look for hint tile to the right
                for(int offset = 1; col - offset >= 0; offset++) {
                    if(isRed(row, col - offset)) {
                        break;
                    }
                    if(hasHint(row, col - offset)) {
                        defaultToRed = false;
                    }
                }
                // look for hint tile to the bottom
                for(int offset = 1; row + offset < size; offset++) {
                    if(isRed(row + offset, col)) {
                        break;
                    }
                    if(hasHint(row + offset, col)) {
                        defaultToRed = false;
                    }
                }
                // look for hint tile to the top
                for(int offset = 1; row - offset >= 0; offset++) {
                    if(isRed(row - offset, col)) {
                        break;
                    }
                    if(hasHint(row - offset, col)) {
                        defaultToRed = false;
                    }
                }
                if(defaultToRed) {
                    setRed(row, col);
                    if(verbose) {
                        System.out.println("Placed 1 red tile at (row, col) = (" + row + ", " + col + ") due to no hint tile in line of sight");
                    }
                    return true;
                }
            }
        }
        if(emptyTiles == 0) {
            if(verbose) {
                System.out.println("No more empty tiles - game has been solved!");
            }
            return false;
        }

        // ideally, we shouldn't get to this point
        if(verbose) {
            System.err.println("Failed to find a continuation from here!");
        }
        return false;
    }
    public boolean solve(boolean verbose) {
        long t = System.nanoTime();
        //noinspection StatementWithEmptyBody
        while(solveOneStep(false));
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(values[row][col] == EMPTY) {
                    if(verbose) {
                        System.out.println("Failed in " + ((System.nanoTime() - t) / 1000) + " μs");
                    }
                    return false;
                }
            }
        }
        if(verbose) {
            System.out.println("Finished in " + ((System.nanoTime() - t) / 1000) + " μs");
        }
        return true;
    }
    public int generate(double red, double step, double factor, double maxFill, int iterations) {
        long t = System.nanoTime();
        double initStep = step;
        byte[][] minimal = new byte[size][size];
        int nMinimal = size * size;
        while(nMinimal > maxFill * size * size) {
            for(int row = 0; row < size; row++) {
                for(int col = 0; col < size; col++) {
                    values[row][col] = random.nextDouble() < red ? RED : 0;
                }
            }
            updateAdjacent(true);
            for(int row = 0; row < size; row++) {
                for(int col = 0; col < size; col++) {
                    if(isBlue(row, col)) {
                        values[row][col] = adjacent[row][col];
                        if(!hasHint(row, col)) {
                            setRed(row, col);
                        }
                    }
                }
            }
            int max = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if(values[row][col] > max) {
                        max = values[row][col];
                    }
                }
            }
            while(max > size) {
                forloop1:
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        if(values[row][col] == max) {
                            setRed(row, col);
                            updateAdjacent(true);
                            max = 0;
                            for (int row2 = 0; row2 < size; row2++) {
                                for (int col2 = 0; col2 < size; col2++) {
                                    if(isBlue(row2, col2)) {
                                        values[row2][col2] = adjacent[row2][col2];
                                        if(!hasHint(row2, col2)) {
                                            setRed(row2, col2);
                                        }
                                    }
                                    if(values[row2][col2] > max) {
                                        max = values[row2][col2];
                                    }
                                }
                            }
                            break forloop1;
                        }
                    }
                }
            }
            byte[][] solution = new byte[size][size];
            byte[][] current = new byte[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    solution[row][col] = values[row][col];
                    current[row][col] = values[row][col];
                }
            }
            boolean solvable = true;
            for (int i = 0; i < iterations; i++) {
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        if (random.nextDouble() < step) {
                            if (solvable) {
                                current[row][col] = EMPTY;
                            } else {
                                current[row][col] = solution[row][col];
                            }
                        }
                    }
                }
                for (int row = 0; row < size; row++) {
                    System.arraycopy(current[row], 0, values[row], 0, size);
                }
                if (solve(false)) {
                    solvable = true;
                    forloop2:
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            if (values[row][col] != solution[row][col] && !(values[row][col] >= 0 && solution[row][col] >= 0)) {
                                solvable = false;
                                break forloop2;
                            }
                        }
                    }
                } else {
                    solvable = false;
                }
                if (solvable) {
                    int c = 0;
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            if (current[row][col] != EMPTY) {
                                c++;
                            }
                        }
                    }
                    if (c < nMinimal) {
                        nMinimal = c;
                        for (int row = 0; row < size; row++) {
                            System.arraycopy(current[row], 0, minimal[row], 0, size);
                        }
                    }
                }
                step *= factor;
            }
            step = initStep;
        }
        values = minimal;
        System.out.println("Generated with n=" + nMinimal + " in " + ((System.nanoTime() - t) / 1000) + " μs");
        return nMinimal;
    }
}
