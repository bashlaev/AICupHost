package com.devoler.aicup.host.run;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.devoler.aicup.client.DoNothingStrategy;
import com.devoler.aicup.client.ShootOrMoveToBaseStrategy;
import com.devoler.aicup.client.SimpleMinimaxStrategy;
import com.devoler.aicup.client.StayAndDefendStrategy;
import com.devoler.aicup.host.model.RemoteStrategy;
import com.devoler.aicup.host.model.Strategy;
import com.devoler.aicup.host.run.GameRunner.Result;

public final class AICupRunner extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final SortedMap<String, Strategy> strategies = new TreeMap<String, Strategy>() {
		private static final long serialVersionUID = 1L;

		{
			put("Do nothing [built-in]", new DoNothingStrategy());
			put("Simple attack [built-in]", new ShootOrMoveToBaseStrategy());
			put("Simple defence [built-in]", new StayAndDefendStrategy());
			put("Balanced [built-in]", new SimpleMinimaxStrategy());
		}
	};

	private static Strategy strategyFromCombobox(JComboBox<String> comboBox) {
		String s = comboBox.getSelectedItem().toString();
		Strategy strategy = strategies.get(s);
		if (strategy == null) {
			if (!s.startsWith("http")) {
				s = "http://" + s;
			}
			strategy = new RemoteStrategy(s);
		}
		return strategy;
	}

	public AICupRunner() {
		super("Devoler AI Cup Runner");

		BoxLayout hLayout = new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS);
		getContentPane().setLayout(hLayout);

		final JPanel redPanel = new JPanel();
		redPanel.setLayout(new BoxLayout(redPanel, BoxLayout.PAGE_AXIS));
		final JLabel redLabel = new JLabel("RED", SwingConstants.CENTER);
		redLabel.setForeground(Color.red);
		redLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		redLabel.setAlignmentX(CENTER_ALIGNMENT);
		final JLabel redComboLabel = new JLabel("Choose a built-in strategy or enter URL:", SwingConstants.CENTER);
		redComboLabel.setAlignmentX(CENTER_ALIGNMENT);
		final JLabel redComboLabel2 = new JLabel("(e.g. \"localhost:8080\" or \"http://127.0.0.1:8080\")",
				SwingConstants.CENTER);
		redComboLabel2.setAlignmentX(CENTER_ALIGNMENT);
		final JComboBox<String> redCombo = new JComboBox<>(strategies.keySet().toArray(new String[] {}));
		redCombo.setAlignmentX(CENTER_ALIGNMENT);
		redCombo.setEditable(true);

		redPanel.add(Box.createVerticalStrut(10));
		redPanel.add(redLabel);
		redPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		redPanel.add(redComboLabel);
		redPanel.add(redComboLabel2);
		redPanel.add(redCombo);
		redPanel.add(Box.createVerticalStrut(10));

		final JPanel bluePanel = new JPanel();
		bluePanel.setLayout(new BoxLayout(bluePanel, BoxLayout.PAGE_AXIS));
		final JLabel blueLabel = new JLabel("BLUE", SwingConstants.CENTER);
		blueLabel.setForeground(Color.blue);
		blueLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		blueLabel.setAlignmentX(CENTER_ALIGNMENT);
		final JLabel blueComboLabel = new JLabel("Choose a built-in strategy or enter URL:", SwingConstants.CENTER);
		blueComboLabel.setAlignmentX(CENTER_ALIGNMENT);
		final JLabel blueComboLabel2 = new JLabel("(e.g. \"localhost:8080\" or \"http://127.0.0.1:8080\")",
				SwingConstants.CENTER);
		blueComboLabel2.setAlignmentX(CENTER_ALIGNMENT);
		final JComboBox<String> blueCombo = new JComboBox<>(strategies.keySet().toArray(new String[] {}));
		blueCombo.setAlignmentX(CENTER_ALIGNMENT);
		blueCombo.setEditable(true);

		bluePanel.add(Box.createVerticalStrut(10));
		bluePanel.add(blueLabel);
		bluePanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		bluePanel.add(blueComboLabel);
		bluePanel.add(blueComboLabel2);
		bluePanel.add(blueCombo);
		bluePanel.add(Box.createVerticalStrut(10));

		final JPanel vsPanel = new JPanel();
		vsPanel.setLayout(new BoxLayout(vsPanel, BoxLayout.PAGE_AXIS));
		final JLabel vsLabel = new JLabel("VS.");
		vsLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		vsLabel.setAlignmentX(CENTER_ALIGNMENT);

		JButton goButton = new JButton("Go!");
		goButton.setAlignmentX(CENTER_ALIGNMENT);
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Strategy redStrategy = strategyFromCombobox(redCombo);
				Strategy blueStrategy = strategyFromCombobox(blueCombo);
				AICupRunner.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				final Result result = new GameRunner(redStrategy, blueStrategy).call();
				AICupRunner.this.setCursor(Cursor.getDefaultCursor());
				new GameReplayerDialog(AICupRunner.this, result);
			}
		});

		JButton goInfiniteButton = new JButton("\u221E");
		goInfiniteButton.setAlignmentX(CENTER_ALIGNMENT);
		goInfiniteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Strategy redStrategy = strategyFromCombobox(redCombo);
				Strategy blueStrategy = strategyFromCombobox(blueCombo);
				new MultiRunnerDialog(AICupRunner.this, redStrategy, blueStrategy);
			}
		});

		vsPanel.add(Box.createVerticalStrut(10));
		vsPanel.add(vsLabel);
		vsPanel.add(Box.createVerticalStrut(10));
		vsPanel.add(goButton);
		vsPanel.add(Box.createVerticalStrut(10));
		vsPanel.add(goInfiniteButton);
		vsPanel.add(Box.createVerticalStrut(10));

		getContentPane().add(Box.createHorizontalStrut(10));
		getContentPane().add(redPanel);
		getContentPane().add(Box.createHorizontalStrut(20));
		getContentPane().add(vsPanel);
		getContentPane().add(Box.createHorizontalStrut(20));
		getContentPane().add(bluePanel);
		getContentPane().add(Box.createHorizontalStrut(10));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		centerWindow(this);
		setVisible(true);
	}

	private static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		window.setLocation(screenWidth / 2 - window.getWidth() / 2, screenHeight / 2 - window.getHeight() / 2);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AICupRunner();
			}
		});

	}
}
