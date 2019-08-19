package com.km.game;

public class HistoryItem {
    private String board;
    private HistoryItem parent;

    private HistoryItem(String board) {
        this.board = board;
    }

    public static HistoryItem fromGB(GameBoard gameBoard) {
        return new HistoryItem(gameBoard.toDBString());
    }

    public static HistoryItem fromGBWithParent(GameBoard gameBoard, HistoryItem parent) {
        HistoryItem historyItem = fromGB(gameBoard);
        historyItem.parent = parent;
        return historyItem;
    }

    public String getBoard() {
        return board;
    }

    public HistoryItem getParent() {
        return parent;
    }

    HistoryItem copy() {
        HistoryItem item = new HistoryItem(board);
        if (parent != null) {
            item.parent = parent.copy();
        }
        return item;
    }
}
