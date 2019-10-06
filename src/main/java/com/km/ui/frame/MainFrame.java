package com.km.ui.frame;

import com.km.LogLevel;
import com.km.Logger;
import com.km.engine.EngineType;
import com.km.engine.TreeSearchEngine;
import com.km.game.GameRunner;
import com.km.game.Score;
import com.km.game.ScoreListener;
import com.km.game.Slot;
import com.km.nn.NetUtil;
import com.km.ui.board.Board;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements ScoreListener {
    private static final String TITLE = "Reversi 1.0";
    private Board board;
    private JTextField score = new JTextField("no score yet");
    private JTextField warScore = new JTextField("no score yet");

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
        rightPanel.add(getButtonRow1());
        rightPanel.add(getButtonRow2());
        rightPanel.add(getButtonRow3());
        rightPanel.add(getButtonRow4());
        rightPanel.add(getButtonRow5());
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
        typeW.setSelectedItem(EngineType.MC);
        typeW.setEditable(false);
        JComboBox<EngineType> typeB = new JComboBox<>(EngineType.values());
        typeB.setSelectedItem(EngineType.ANN);
        typeB.setEditable(false);
        JTextField count = new JTextField("5");
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
        JTextField cycleCount = new JTextField(String.valueOf(NetUtil.CYCLE_COUNT));
        JTextField trainCycleLen = new JTextField(String.valueOf(NetUtil.TRAIN_CYCLE_LEN));
        JTextField testCycleLen = new JTextField(String.valueOf(NetUtil.TEST_CYCLE_LEN));
        newGameBatch.addActionListener(click -> {
            board.startBatchTrain(Integer.parseInt(cycleCount.getText()), Integer.parseInt(trainCycleLen.getText()), Integer.parseInt(testCycleLen.getText()));
        });
        p3.add(newGameBatch);
        p3.add(cycleCount);
        p3.add(trainCycleLen);
        p3.add(testCycleLen);
        return p3;
    }

    private JPanel getButtonRow4() {
        JPanel p4 = new JPanel();
        p4.setLayout(new FlowLayout());
        JButton clearANN = new JButton("Clear ANN");
        JButton mcPower = new JButton("MC power");
        JTextField power = new JTextField(String.valueOf(TreeSearchEngine.SIM_TIME));
        clearANN.addActionListener(click -> {
            NetUtil.clear();
        });
        mcPower.addActionListener(click -> {
            TreeSearchEngine.setPower(Long.parseLong(power.getText()));
        });
        p4.add(clearANN);
        p4.add(mcPower);
        p4.add(power);
        return p4;
    }

    private JPanel getButtonRow5() {
        JPanel p5 = new JPanel();
        p5.setLayout(new FlowLayout());
        JButton level = new JButton("Set level");
        JComboBox<LogLevel> list = new JComboBox<>(LogLevel.values());
        list.setSelectedItem(Logger.getLevel());
        list.setEditable(false);
        level.addActionListener(click -> {
            Logger.setLevel(LogLevel.valueOf(list.getSelectedItem().toString()));
        });
        p5.add(list);
        p5.add(level);
        return p5;
    }

    @Override
    public void setScore(Score score) {
        this.score.setText(String.format("B:%d W:%d", score.getBlack(), score.getWhite()));
    }

    @Override
    public void setWarScore(int count, int black, int white) {
        this.warScore.setText(String.format("G:%d %d:%d", count, black, white));
    }
}
