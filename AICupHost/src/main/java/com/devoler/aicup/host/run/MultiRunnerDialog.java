package com.devoler.aicup.host.run;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import com.devoler.aicup.host.model.Game;
import com.devoler.aicup.host.model.Strategy;

public final class MultiRunnerDialog extends JDialog {
	private static final Dimension SIZE = new Dimension(400, 100);
	private static final Dimension MAX_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

	private class MultiRunnerView extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getMinimumSize() {
			return SIZE;
		}

		@Override
		public Dimension getMaximumSize() {
			return MAX_SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return SIZE;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int w = getWidth();
			int h = getHeight();

			int redWins = multiRunner.getResultCount(Game.State.VICTORY_RED);
			int blueWins = multiRunner.getResultCount(Game.State.VICTORY_BLUE);
			int draws = multiRunner.getResultCount(Game.State.DRAW);
			int totalGames = redWins + blueWins + draws;

			int redWidth = totalGames == 0 ? 0 : (redWins * w / totalGames);
			int blueWidth = totalGames == 0 ? 0 : (blueWins * w / totalGames);
			int drawWidth = w - redWidth - blueWidth;

			g.setColor(Color.red);
			g.fillRect(0, 0, redWidth, h);
			g.setColor(Color.yellow);
			g.fillRect(redWidth, 0, drawWidth, h);
			g.setColor(Color.blue);
			g.fillRect(redWidth + drawWidth, 0, blueWidth, h);
		}
	}

	private static final long serialVersionUID = 1L;

	private final MultiRunner multiRunner;

	public MultiRunnerDialog(final JFrame owner, final Strategy strategyRed, final Strategy strategyBlue) {
		super(owner, "Multiple runner", true);

		multiRunner = new MultiRunner(strategyRed, strategyBlue);

		final JLabel resultsLabel = new JLabel("Games: 0");
		final MultiRunnerView resultsPanel = new MultiRunnerView();

		getContentPane().add(resultsLabel);
		getContentPane().add(resultsPanel);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.WEST, resultsLabel, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, resultsLabel, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, resultsLabel, -10, SpringLayout.EAST, getContentPane());

		springLayout.putConstraint(SpringLayout.NORTH, resultsPanel, 10, SpringLayout.SOUTH, resultsLabel);
		springLayout.putConstraint(SpringLayout.WEST, resultsPanel, 0, SpringLayout.WEST, resultsLabel);

		// size of the frame
		springLayout.putConstraint(SpringLayout.EAST, getContentPane(), 10, SpringLayout.EAST, resultsPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, getContentPane(), 10, SpringLayout.SOUTH, resultsPanel);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		centerWindow(this);

		final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(true);
				executor.shutdown();
				multiRunner.shutdown();
			}
		});

		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				int redWins = multiRunner.getResultCount(Game.State.VICTORY_RED);
				int blueWins = multiRunner.getResultCount(Game.State.VICTORY_BLUE);
				int draws = multiRunner.getResultCount(Game.State.DRAW);
				int totalGames = redWins + blueWins + draws;

				resultsLabel.setText("Games: " + totalGames + ", red/draw/blue: " + redWins + "/" + draws + "/"
						+ blueWins);
				resultsPanel.repaint();
			}
		}, 50, 50, TimeUnit.MILLISECONDS);
		multiRunner.start();
	}

	private static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		window.setLocation(screenWidth / 2 - window.getWidth() / 2, screenHeight / 2 - window.getHeight() / 2);
	}
}
