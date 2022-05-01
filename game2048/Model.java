package game2048;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Observable;


/**
 * The state of a game of 2048.
 *
 * @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /**
     * Current contents of the board.
     */
    private Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;
    /**
     * True iff game is ended.
     */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        String s = side.name();
        switch (s) {
            case "NORTH":
                changed = moveUpColumns(board,changed);
                break;
            case "EAST":
                board.setViewingPerspective(Side.EAST);
                changed = moveUpColumns(board,changed);
                board.setViewingPerspective(Side.NORTH);
                break;
            case "SOUTH":
                board.setViewingPerspective(Side.SOUTH);
                changed = moveUpColumns(board,changed);
                board.setViewingPerspective(Side.NORTH);
                break;
            case "WEST":
                board.setViewingPerspective(Side.WEST);
                changed = moveUpColumns(board,changed);
                board.setViewingPerspective(Side.NORTH);
                break;
        }

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public boolean moveUpColumns(Board board, boolean changed) {
        //loop through the columns
        for (int i = 0; i < board.size(); i++) {
            //for each column, set a variable to monitor merges.
            ArrayList<Integer> mergedRow = new ArrayList<>();

            //start moving up each tile from the secondly high row
            for (int j = board.size() - 2; j >= 0; j--) {
                Tile currentTile = board.tile(i, j);
                if (currentTile != null) {
                    //look at the row above the current row
                    int destRow = j + 1;
                    while (true) {
                        //get the tile there (the above cell)
                        Tile destTile = board.tile(i, destRow);
                        if (destRow == board.size() - 1) {
                            //if the top cell has a tile with diff value
                            if (destTile != null && destTile.value() != currentTile.value()){
                                //return to the original cell
                                destRow--;
                            }
                            //otherwise stop at the top cell.
                            break;
                        } else if (destTile == null) {
                            //if it isn't top cell and there isn't any tile
                            //then keep looking up the above one
                            destRow++;
                        } else if (destTile.value() == currentTile.value()) {
                            //if it isn't top cell and there is a tile of same value
                            //stop at this cell
                            break;
                        } else {
                            //otherwise go one cell down
                            destRow--;
                            break;
                        }
                    }

                    //finally see if we need to move this cell
                    if (destRow != j) {
                        changed = true;
                        //if the dest cell is merged before, move the
                        //tile one row down.
                        if (mergedRow.contains(destRow)) {
                            destRow--;
                            board.move(i, destRow, currentTile);
                        } else {
                            //otherwise check if this movement is a merge.
                            //If so, update score.
                            if (board.move(i, destRow, currentTile)) {
                                score += board.tile(i,destRow).value();
                                mergedRow.add(destRow);
                            }
                        }
                    } else continue;
                }
            }
        }
        return changed;
    }

    /**
     * Return the number of cells that the current tile
     * could be move up
     */
    //this is a trial version
    public static int stepsCouldMoveUp(Board board, Tile thisTile) {
        int destRow = thisTile.row() + 1;
        while (true) {
            if (destRow == board.size() - 1) {
                break;
            }
            Tile destTile = board.tile(thisTile.col(), destRow);
            if(destTile == null) {
                destRow++;
            } else if (destTile.value() == thisTile.value()) {
                break;
            } else {
                destRow--;
            }
        }
        return destRow - thisTile.row();
    }

    /*
    public static int stepsCouldMoveUp(Board board, Tile thisTile) {
        int destRow = thisTile.row() + 1;
        while (true) {
            if (destRow == board.size() - 1) {
                break;
            }
            Tile destTile = board.tile(thisTile.col(), destRow);
            if(destTile == null) {
                destRow++;
            } else if (destTile.value() == thisTile.value()) {
                break;
            } else {
                destRow--;
            }
        }
        return destRow - thisTile.row();
    }

     */

    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /**
     * Determine whether game is over.
     */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) != null && b.tile(i, j).value() == MAX_PIECE) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if (emptySpaceExists(b) || twoAdjacentTilesHaveSameValue(b)) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if there are
     * two adjacent tiles with the same value.
     */
    public static boolean twoAdjacentTilesHaveSameValue(Board b) {
        Iterator<Tile> itr = b.iterator();
        while (itr.hasNext()) {
            Tile currentTile = itr.next();
            ArrayList<Tile> tilesAround = tilesAround(b, currentTile);
            for (Tile tile : tilesAround) {
                if (tile != null && tile.value() == currentTile.value()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the adjacent tiles of current tile on board b.
     */
    public static ArrayList<Tile> tilesAround(Board b, Tile currentTile) {
        ArrayList<Tile> tilesAround = new ArrayList<>();
        //check whether the tile is at edge, otherwise add adjacent tile
        if (currentTile.row() > 0) { //add top
            tilesAround.add(b.tile(currentTile.col(), currentTile.row() - 1));
        }
        if (currentTile.col() < b.size() - 1) {//add right
            tilesAround.add(b.tile(currentTile.col() + 1, currentTile.row()));
        }
        if (currentTile.row() < b.size() - 1) {//add down
            tilesAround.add(b.tile(currentTile.col(), currentTile.row() + 1));
        }
        if (currentTile.col() > 0) {//add left
            tilesAround.add(b.tile(currentTile.col() - 1, currentTile.row()));
        }
        return tilesAround;
    }

    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
