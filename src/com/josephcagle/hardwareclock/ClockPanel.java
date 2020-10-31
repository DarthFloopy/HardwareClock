package com.josephcagle.hardwareclock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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

import javax.swing.BorderFactory;
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
	
	private JPanel leftClock;
	private JLabel leftTimeLabel;
	private JPanel rightClock;
	private JLabel rightTimeLabel;
	private JPanel controlPanel;
	private Duration leftTime;
	private Duration rightTime;
	private Color leftColor = DARK_GRAY;
	private Color rightColor = DARK_GRAY;


	public static void main(String[] args) {
		int timeControlInSeconds = 300;
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
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenDimension.width;
		int screenHeight = screenDimension.height;

		// setPreferredSize(new Dimension(1500, 600));
		setPreferredSize(new Dimension(screenWidth, (int)(screenHeight*0.9 + 0.5)));
		setLayout(new BorderLayout(0, 0));
		
		leftClock = new JPanel();
		leftClock.setBackground(leftColor);
		add(leftClock, BorderLayout.WEST);
		leftClock.setLayout(new BorderLayout(0, 0));
		
		Box horizontalBox = Box.createHorizontalBox();
		leftClock.add(horizontalBox, BorderLayout.CENTER);
		
		Component horizontalStrut1 = Box.createHorizontalStrut(20);
		int widthofStrut = (int)(0.1302*screenWidth + 0.5);
		// System.out.print(widthofStrut);
		// horizontalStrut1.setPreferredSize(new Dimension(200, 0));
		horizontalStrut1.setPreferredSize(new Dimension(widthofStrut, 0));
		horizontalStrut1.setMinimumSize(new Dimension(widthofStrut, 0));
		horizontalStrut1.setMaximumSize(new Dimension(widthofStrut, 32767));
		horizontalBox.add(horizontalStrut1);
		
		leftTimeLabel = new JLabel("");
		leftTimeLabel.setFont(new Font("Ubuntu", Font.BOLD, 60));
		horizontalBox.add(leftTimeLabel);
		
		Component horizontalStrut2 = Box.createHorizontalStrut(20);
		horizontalStrut2.setPreferredSize(new Dimension(widthofStrut, 0));
		horizontalStrut2.setMinimumSize(new Dimension(widthofStrut, 0));
		horizontalStrut2.setMaximumSize(new Dimension(widthofStrut, 32767));
		horizontalBox.add(horizontalStrut2);
		
		rightClock = new JPanel();
		rightClock.setBackground(rightColor);
		add(rightClock, BorderLayout.EAST);
		rightClock.setLayout(new BorderLayout(0, 0));
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		rightClock.add(horizontalBox_1, BorderLayout.CENTER);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		horizontalStrut_3.setPreferredSize(new Dimension(widthofStrut, 0));
		horizontalStrut_3.setMinimumSize(new Dimension(widthofStrut, 0));
		horizontalStrut_3.setMaximumSize(new Dimension(widthofStrut, 32767));
		horizontalBox_1.add(horizontalStrut_3);
		
		rightTimeLabel = new JLabel("");
		rightTimeLabel.setFont(new Font("Ubuntu", Font.BOLD, 60));
		horizontalBox_1.add(rightTimeLabel);
		
		Component horizontalStrut4 = Box.createHorizontalStrut(20);
		horizontalStrut4.setPreferredSize(new Dimension(widthofStrut, 0));
		horizontalStrut4.setMaximumSize(new Dimension(widthofStrut, 32767));
		horizontalStrut4.setMinimumSize(new Dimension(widthofStrut, 0));
		horizontalBox_1.add(horizontalStrut4);
		
		controlPanel = new JPanel();
		add(controlPanel, BorderLayout.CENTER);
		controlPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		controlPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setPreferredSize(new Dimension((int)((0.1235 - 2*0.0163)*screenWidth + 0.5), (int)((4*0.1235 - 2*0.0787)*screenHeight + 0.5)));
		
		JButton btnReset = new JButton("Reset");
		btnReset.setBorder(BorderFactory.createSoftBevelBorder(0));
		
		panel.add(btnReset, BorderLayout.CENTER);
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetClocks(timeControlInSeconds);
				btnReset.transferFocus();
			}
		});
		
		JLabel lblPressQTo = new JLabel("Press Q to quit");
		lblPressQTo.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblPressQTo, BorderLayout.SOUTH);
		
		JLabel lblPressPTo = new JLabel("Press P to pause");
		lblPressPTo.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblPressPTo, BorderLayout.NORTH);
		
		// Component rigidArea = Box.createRigidArea(new Dimension(25, 68));
		Component rigidArea = Box.createRigidArea(new Dimension((int)(0.0163*screenWidth + 0.5), (int)(0.0787*screenHeight + 0.5)));
		panel.add(rigidArea, BorderLayout.WEST);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension((int)(0.0163*screenWidth + 0.5), (int)(0.0787*screenHeight + 0.5)));
		panel.add(rigidArea_1, BorderLayout.EAST);
		
		// Component rigidArea_2 = Box.createRigidArea(new Dimension(478, 350));
		Component rigidArea_2 = Box.createRigidArea(new Dimension((int)(0.3112*screenWidth + 0.5), (int)(0.4051*screenHeight + 0.5)));
		controlPanel.add(rigidArea_2, BorderLayout.NORTH);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension((int)(0.3112*screenWidth + 0.5), (int)(0.4051*screenHeight + 0.5)));
		controlPanel.add(rigidArea_3, BorderLayout.SOUTH);
		
		
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
