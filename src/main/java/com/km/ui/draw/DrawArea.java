package com.km.ui.draw;

import com.km.game.ScoreListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawArea extends JPanel {
    private static final int MULTI = 3;
    private static final int SIZE = 100;
    private ScoreListener scoreListener;
    private static final Color[] COLORS = new Color[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.MAGENTA};

    public DrawArea(ScoreListener scoreListener) {
        setPreferredSize(new Dimension((3 * SIZE * MULTI) + 1, (SIZE * MULTI) + 1));
        this.scoreListener = scoreListener;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 3 * SIZE * MULTI, SIZE * MULTI);
        drawGrid(g);
        drawLines(g, scoreListener.getTrainProgress());
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.GRAY);
        for (int i = 0; i < 11; i++)
            g.drawLine(0, MULTI * 10 * i, getWidth(), MULTI * 10 * i);
        for (int i = 0; i < 31; i++)
            g.drawLine(MULTI * 10 * i, 0, MULTI * 10 * i, getHeight());
        g.setColor(Color.BLACK);
        g.drawLine(0, MULTI * 50, getWidth(), MULTI * 50);
        for (int i = 0; i < 4; i++)
            g.drawLine(MULTI * 100 * i, 0, MULTI * 100 * i, getHeight());
    }

    private void drawLines(Graphics g, List<List<Integer>> lines) {
        if (lines == null || lines.isEmpty())
            return;
        for (int j = 0; j < lines.get(0).size(); j++) {
            g.setColor(COLORS[j]);
            for (int i = 1; i < lines.size(); i++) {
                g.drawLine((i - 1) * MULTI,
                        getHeight() - (lines.get(i - 1).get(j) * MULTI),
                        i * MULTI,
                        getHeight() - (lines.get(i).get(j) * MULTI));
            }
        }

    }
}
