package com.km.ui.frame;

import com.km.engine.EngineType;
import com.km.game.Score;
import com.km.game.Slot;
import com.km.ui.board.Board;
import com.km.ui.board.ScoreListener;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kacper on 10.01.16.
 */
public class MainFrame extends JFrame implements ScoreListener {
    private static final String TITLE = "Reversi 1.0";
    private Board board;
    private TextField score = new TextField("no score yet");

    public MainFrame() {
        super(TITLE);
        board = new Board(this);
    }

    public void createComponents() {
        getContentPane().add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());
        return mainPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.add(board);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(getButtonPanel());
        rightPanel.add(getInfoPanel());
        return rightPanel;
    }

    private JPanel getInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        infoPanel.add(new Label("Score:"));
        score.setEditable(false);
        infoPanel.add(score);
        return infoPanel;
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        Button newGameBlack = new Button("BLACK");
        Button newGameWhite = new Button("WHITE");
        Button newGameWar = new Button("War");
        JComboBox<EngineType> type = new JComboBox<>(EngineType.values());
        type.setPrototypeDisplayValue(EngineType.TREE_SEARCH);
        type.setEditable(false);
        newGameBlack.addActionListener(click -> {
            board.startNewGame(Slot.BLACK, type.getPrototypeDisplayValue());
        });
        newGameWhite.addActionListener(click -> {
            board.startNewGame(Slot.WHITE, type.getPrototypeDisplayValue());
        });
        newGameWar.addActionListener(click -> {
            // TODO start war
        });
        buttonPanel.add(newGameBlack);
        buttonPanel.add(newGameWhite);
        buttonPanel.add(newGameWar);
        buttonPanel.add(type);
        return buttonPanel;
    }

    @Override
    public void setScore(Score score) {
        this.score.setText(score.toString());
    }
}
