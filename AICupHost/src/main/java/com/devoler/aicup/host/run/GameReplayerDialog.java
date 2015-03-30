package com.devoler.aicup.host.run;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.Battlefield.Update;
import com.devoler.aicup.host.model.Game;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.run.GameRunner.Result;

public final class GameReplayerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static final String NEWLINE = "\r\n";

	private Side activePlayer = Game.FIRST_MOVE_TEAM;

	private Battlefield battlefield = Game.initBattlefield();

	private int movePointer = 0;

	public GameReplayerDialog(final JFrame owner, final Result gameResult) {
		super(owner, "Game replayer", true);

		final JTextPane resultArea = new JTextPane();
		resultArea.setEditable(false);
		final StyledDocument doc = resultArea.getStyledDocument();
		// adding styles

		// regular style
		Style regular = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		regular = doc.addStyle("regular", regular);
		StyleConstants.setFontFamily(regular, Font.SANS_SERIF);
		StyleConstants.setFontSize(regular, 12);

		// warning style
		Style warning = doc.addStyle("warning", regular);
		StyleConstants.setForeground(warning, Color.red);

		// result style
		Style result = doc.addStyle("result", regular);
		StyleConstants.setFontSize(result, 20);
		StyleConstants.setBackground(result, new Color(0xeeee00));

		JScrollPane scrollPane = new JScrollPane(resultArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

		final BattlefieldView battlefieldView = new BattlefieldView(battlefield);

		final JSlider ticksPerSecond = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		ticksPerSecond.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
			}
		});

		// Turn on labels at major tick marks.
		ticksPerSecond.setMajorTickSpacing(10);
		ticksPerSecond.setMinorTickSpacing(1);
		ticksPerSecond.setPaintTicks(true);
		ticksPerSecond.setPaintLabels(true);

		scrollPane.setMinimumSize(new Dimension(100, 0));
		scrollPane.setPreferredSize(new Dimension(300, 0));
		scrollPane.setMaximumSize(new Dimension(500, 0));

		getContentPane().add(battlefieldView);
		getContentPane().add(ticksPerSecond);
		getContentPane().add(scrollPane);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		// realtive position of ticks and frame
		springLayout.putConstraint(SpringLayout.WEST, ticksPerSecond, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, ticksPerSecond, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, ticksPerSecond, -10, SpringLayout.EAST, getContentPane());

		// relative position of ticks and battlefield
		springLayout.putConstraint(SpringLayout.NORTH, battlefieldView, 10, SpringLayout.SOUTH, ticksPerSecond);
		springLayout.putConstraint(SpringLayout.WEST, battlefieldView, 0, SpringLayout.WEST, ticksPerSecond);

		// relative position of scroll pane and battlefield
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.EAST, battlefieldView);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, battlefieldView);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, battlefieldView);

		// size of the frame
		springLayout.putConstraint(SpringLayout.EAST, getContentPane(), 10, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, getContentPane(), 10, SpringLayout.SOUTH, battlefieldView);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		centerWindow(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(true);
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				while ((movePointer < gameResult.getHistory().size()) || (battlefieldView.isAnimating())) {
					int tps = ticksPerSecond.getValue();
					if (battlefieldView.isAnimating()) {
						battlefieldView.tick();
						battlefieldView.repaint();
					} else {
						if (tps > 0) {
							Game.Update gameUpdate = gameResult.getHistory().get(movePointer++);
							if (gameUpdate.getComment() != null) {
								try {
									doc.insertString(doc.getLength(),
											"Tick #" + movePointer + ", " + gameUpdate.getComment() + NEWLINE,
											doc.getStyle("warning"));
								} catch (BadLocationException ignored) {
								}
							}
							Update update = battlefield.tick(gameUpdate.getMove(), activePlayer);
							battlefield = update.getBattlefield();
							battlefieldView.update(update.getActualMove(), battlefield);
							try {
								doc.insertString(doc.getLength(), "Tick #" + movePointer + ", " + activePlayer
										+ " move: " + update.getActualMove() + NEWLINE, doc.getStyle("regular"));
							} catch (BadLocationException ignored) {
							}
							battlefieldView.repaint();
							resultArea.repaint();
							activePlayer = Side.values()[Side.values().length - 1 - activePlayer.ordinal()];
							if (movePointer == gameResult.getHistory().size()) {
								try {
									doc.insertString(doc.getLength(),
											gameResult.getFinalState().getDescription() + ", ticks: " + movePointer
													+ ", time bank red: " + (gameResult.getTimeBankRed() / 1000)
													+ "s, time bank blue: " + (gameResult.getTimeBankBlue() / 1000)
													+ "s" + NEWLINE, doc.getStyle("result"));
								} catch (BadLocationException ignored) {
								}
							}
						}
					}
					tps = ticksPerSecond.getValue();
					try {
						Thread.sleep(tps == 0 ? 200 : 200 / tps);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		window.setLocation(screenWidth / 2 - window.getWidth() / 2, screenHeight / 2 - window.getHeight() / 2);
	}
}
