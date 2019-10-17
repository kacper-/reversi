package com.km.ui.frame;

import com.km.Config;
import com.km.LogLevel;
import com.km.engine.EngineType;
import com.km.engine.TreeSearchEngine;
import com.km.game.GameRunner;
import com.km.game.Score;
import com.km.game.ScoreListener;
import com.km.game.Slot;
import com.km.nn.NetUtil;
import com.km.nn.NetVersion;
import com.km.ui.board.Board;
import com.km.ui.draw.DrawArea;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame implements ScoreListener {
    private static final String TITLE = "Reversi " + Config.VERSION;
    private Board board;
    private JTextField score = new JTextField("no score : no score");
    private JTextField warScore = new JTextField("no score : no score");
    private List<List<Integer>> list;
    private DrawArea drawArea = new DrawArea(this);

    public MainFrame() {
        super(TITLE);
        GameRunner runner = new GameRunner();
        runner.setScoreListener(this);
        board = new Board(runner);
    }

    public void createComponents() {
        getContentPane().add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(createLeftPanel());
        topPanel.add(createRightPanel());
        bottomPanel.add(drawArea);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);
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
        rightPanel.add(getButtonRow1());
        rightPanel.add(getButtonRow2());
        rightPanel.add(getButtonRow3());
        rightPanel.add(getInfoPanel1());
        rightPanel.add(getInfoPanel2());
        return rightPanel;
    }

    private JPanel getInfoPanel1() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        infoPanel.add(new JLabel("Score:"));
        score.setEditable(false);
        infoPanel.add(score);
        return infoPanel;
    }

    private JPanel getInfoPanel2() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        infoPanel.add(new JLabel("War Score:"));
        warScore.setEditable(false);
        infoPanel.add(warScore);
        return infoPanel;
    }

    private JPanel getButtonRow1() {
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout());
        JButton newGameBlack = new JButton("BLACK");
        JButton newGameWhite = new JButton("WHITE");
        JComboBox<EngineType> type = new JComboBox<>(EngineType.values());
        type.setSelectedItem(EngineType.MC);
        type.setEditable(false);
        newGameBlack.addActionListener(click -> {
            board.startNewGame(Slot.BLACK, EngineType.valueOf(type.getSelectedItem().toString()));
        });
        newGameWhite.addActionListener(click -> {
            board.startNewGame(Slot.WHITE, EngineType.valueOf(type.getSelectedItem().toString()));
        });
        p1.add(newGameBlack);
        p1.add(newGameWhite);
        p1.add(type);
        return p1;
    }

    private JPanel getButtonRow2() {
        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout());
        JButton newGameWar = new JButton("War");
        JComboBox<EngineType> typeW = new JComboBox<>(EngineType.values());
        typeW.setSelectedItem(EngineType.RANDOM);
        typeW.setEditable(false);
        JComboBox<EngineType> typeB = new JComboBox<>(EngineType.values());
        typeB.setSelectedItem(EngineType.ANN3);
        typeB.setEditable(false);
        JTextField count = new JTextField("1000");
        newGameWar.addActionListener(click -> {
            board.startWarGame(EngineType.valueOf(typeB.getSelectedItem().toString()), EngineType.valueOf(typeW.getSelectedItem().toString()), Integer.parseInt(count.getText()));
        });
        p2.add(newGameWar);
        p2.add(typeB);
        p2.add(typeW);
        p2.add(count);
        return p2;
    }

    private JPanel getButtonRow3() {
        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout());
        JButton newGameBatch = new JButton("Batch");
        JTextField cycleCount = new JTextField(String.valueOf(Config.getCycleCount()));
        newGameBatch.addActionListener(click -> {
            board.startBatchTrain(Integer.parseInt(cycleCount.getText()));
        });
        p3.add(newGameBatch);
        p3.add(cycleCount);
        return p3;
    }

    @Override
    public void setScore(Score score) {
        this.score.setText(String.format("B:%d W:%d", score.getBlack(), score.getWhite()));
    }

    @Override
    public void setWarScore(int count, int black, int white) {
        this.warScore.setText(String.format("G:%d %d:%d", count, black, white));
    }

    @Override
    public List<List<Integer>> getTrainProgress() {
        return list;
    }

    @Override
    public void setTrainProgress(List<List<Integer>> list) {
        this.list = list;
        drawArea.repaint();
    }
}
