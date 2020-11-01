package com.josephcagle.hardwareclock;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.time.Duration;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class ClockPanel extends JPanel {
	private Color DARK_GRAY = new Color(0x999999);
	private Color LIGHT_GRAY = new Color(0xbbbbbb);
	private Color ORANGE = Color.ORANGE;
	private Color RED = new Color(0xff6d4d);
	
	private static Dimension screenDimension;
	private JPanel leftClock;
	private JLabel leftTimeLabel;
	private JPanel rightClock;
	private JLabel rightTimeLabel;
	private JPanel controlPanel;
	private Duration leftTime;
	private Duration rightTime;
	private Color leftColor = DARK_GRAY;
	private Color rightColor = DARK_GRAY;
	private static int timeControlInSeconds;


	public static void main(String[] args) {
		timeControlInSeconds = 300;
		if (args.length > 0) {
			try (Scanner scanner = new Scanner(args[0])) {
				if (scanner.hasNextInt())
					timeControlInSeconds = scanner.nextInt();
			}
		}

		JFrame frame = new JFrame();
		frame.getContentPane().add(new ClockPanel(timeControlInSeconds));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Create the panel.
	 */
	public ClockPanel(int timeControlInSeconds) {

		// get screen size in pixels
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)(screenDimension.width*0.6 + 0.5);
		int screenHeight = (int)(screenDimension.height*0.6 + 0.5);

		// setPreferredSize(new Dimension(1500, 600));
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setLayout(new GridLayout(3, 1));
		
		leftClock = new JPanel();
		leftClock.setBackground(leftColor);
		leftClock.setLayout(new BorderLayout(0, 0));
		
		leftTimeLabel = new JLabel("");
		int fontSize = (int)(screenHeight*0.115 + 0.5); 
		// leftTimeLabel.setFont(new Font("Ubuntu", Font.BOLD, 60));
		leftTimeLabel.setFont(new Font("Ubuntu", Font.BOLD, fontSize));
		leftTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftClock.add(leftTimeLabel, BorderLayout.CENTER);
		
		rightClock = new JPanel();
		rightClock.setBackground(rightColor);
		rightClock.setLayout(new BorderLayout(0, 0));
		
		rightTimeLabel = new JLabel("");
		rightTimeLabel.setFont(new Font("Ubuntu", Font.BOLD, fontSize));
		rightTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightClock.add(rightTimeLabel, BorderLayout.CENTER);
		
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout(0, 0));
				
		JButton btnReset = new JButton("Reset (or Press R)");
		
		controlPanel.add(btnReset, BorderLayout.CENTER);
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetClocks(timeControlInSeconds);
				btnReset.transferFocus();
			}
		});
		
		JLabel lblPressQTo = new JLabel("Press Q to quit");
		lblPressQTo.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(lblPressQTo, BorderLayout.SOUTH);
		
		JLabel lblPressPTo = new JLabel("Press P to pause");
		lblPressPTo.setHorizontalAlignment(SwingConstants.CENTER);
		controlPanel.add(lblPressPTo, BorderLayout.NORTH);

		add(leftClock);
		add(controlPanel);
		add(rightClock);
		
		
		leftTime = Duration.ofSeconds(timeControlInSeconds);
		rightTime = Duration.ofSeconds(timeControlInSeconds);
		leftTimeLabel.setText(leftTime.getSeconds()/60+" : "+leftTime.getSeconds()%60);
		rightTimeLabel.setText(rightTime.getSeconds()/60+" : "+rightTime.getSeconds()%60);
		
		leftClock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				hitLeftClock();
			}
		});
		;
		
		rightClock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				hitRightClock();
			}
		});
		
		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == ' ')
					if (paused)
						togglePause();
					else
						toggleClock();
				else if (e.getKeyChar() == 'p')
					togglePause();
				else if (e.getKeyChar() == 'q') {
					pauseClock();
					if (JOptionPane.showConfirmDialog(null,
							"Are you sure you want to quit?",
							"Quit?",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null) == JOptionPane.OK_OPTION)
						System.exit(0);
				} else if (e.getKeyChar() == 'r') {
					resetClocks(timeControlInSeconds);
				} else if (e.getKeyCode() ==
				    KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK)
					  .getKeyCode())                                                        {
					if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT)
						hitLeftClock();
					if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT)
						hitRightClock();
				}
			}
		});
		
		
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				leftTimeLabel.setText(leftTime.getSeconds()/60+" : "+
						(String.format("%02d", leftTime.getSeconds()%60))+" . "+
						(String.format("%02d", leftTime.getNano()/10_000_000)+""));
				rightTimeLabel.setText(rightTime.getSeconds()/60+" : "+
						(String.format("%02d", rightTime.getSeconds()%60))+" . "+
						(String.format("%02d", rightTime.getNano()/10_000_000)+""));
				if (!paused && leftClockRunning && !leftClockFlagged)
					subtractFromLeftTime(Duration.ofMillis(1));
				if (!paused && rightClockRunning && !rightClockFlagged)
					subtractFromRightTime(Duration.ofMillis(1));
				
				if (leftTime.isZero() || leftTime.isNegative())
					leftClockFlagged = true;
				if (rightTime.isZero() || rightTime.isNegative())
					rightClockFlagged = true;
				
				if (leftClockFlagged && ! leftColor.equals(RED))
					leftColor = RED;
				if (rightClockFlagged && ! rightColor.equals(RED))
					rightColor = RED;
				
				if (! leftClock.getBackground().equals(leftColor))
					leftClock.setBackground(leftColor);
				if (! rightClock.getBackground().equals(rightColor))
					rightClock.setBackground(rightColor);
			}
		};
		timer.scheduleAtFixedRate(task, 0L, 1L);

	}
	
	
	private boolean leftClockRunning = false;
	private boolean rightClockRunning = false;
	private boolean leftClockFlagged = false;
	private boolean rightClockFlagged = false;
	private boolean paused = false;
	
	
	private void hitLeftClock() {
		leftClockRunning = false;
		rightClockRunning = true;
		leftColor = LIGHT_GRAY;
		rightColor = ORANGE;
		paused = false;
	}
	
	private void hitRightClock() {
		rightClockRunning = false;
		leftClockRunning = true;
		leftColor = ORANGE;
		rightColor = LIGHT_GRAY;
		paused = false;
	}
	
	private void toggleClock() {
		if (leftClockRunning)
			hitLeftClock();
		else
			hitRightClock();
	}
	
	private void togglePause() {
		paused = !paused;
		if (paused) {
			leftColor = LIGHT_GRAY;
			rightColor = LIGHT_GRAY;
		} else {
			if (leftClockRunning)
				leftColor = ORANGE;
			if (rightClockRunning)
				rightColor = ORANGE;
		}
	}
	
	private void pauseClock() {
		paused = true;
		leftColor = LIGHT_GRAY;
		rightColor = LIGHT_GRAY;
	}
	
	private void resetClocks(int timeControlInSeconds) {
		pauseClock();
		leftColor = DARK_GRAY;
		rightColor = DARK_GRAY;
		leftClockFlagged = false;
		rightClockFlagged = false;
		leftTime = Duration.ofSeconds(timeControlInSeconds);
		rightTime = Duration.ofSeconds(timeControlInSeconds);
	}
	
	private void subtractFromLeftTime(Duration d) {
		leftTime = leftTime.minus(d);
	}
	
	private void subtractFromRightTime(Duration d) {
		rightTime = rightTime.minus(d);
	}
	
	
}   // end ClockPanel
