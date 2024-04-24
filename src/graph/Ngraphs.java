package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Ngraphs {
	static JButton button_r = new JButton();
	static JButton button_s = new JButton();
	static JSlider slider_a = new JSlider(JSlider.HORIZONTAL, 0, 100, 15);
	static JLabel label_a = new JLabel(String.valueOf((double)slider_a.getValue() / 100));
	public static void main(String s[]) {
		JFrame frame = new JFrame("Ngraphs");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		Graph graph = new Graph();
		button_s.setText("START");
		button_r.setText("RESET");
		JTextField N = new JTextField(4);
		N.setText("" + graph.n);
		
		button_s.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				graph.flag = !graph.flag;
				graph.repaint();
				if (graph.flag) {
					button_s.setText("Stop");
					button_r.setEnabled(false);
				} else {
					button_s.setText("Start");
					button_r.setEnabled(true);
				}
			}
		});

		button_r.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				graph.flag = false;
				graph.reset();
			}
		});

		N.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				graph.flag = false;
				button_s.setText("Start");
				button_r.setEnabled(true);
				graph.reset();
				graph.n = Integer.parseInt(N.getText());
				graph.p1 = 0;
				graph.p2 = graph.n - 1;
				graph.points = null;
				graph.points = new int[graph.n][2];
				graph.start();
			}
		});
		
		slider_a.setMajorTickSpacing(25);
		slider_a.setMinorTickSpacing(1);
		slider_a.setPaintTicks(true);
		slider_a.setPaintLabels(true);
		slider_a.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!slider_a.getValueIsAdjusting()) {
					graph.a = slider_a.getValue();
//					graph.repaint();
					label_a.setText(String.valueOf((double)slider_a.getValue() / 100));
				}
			}
		});
		
		

		panel.add(button_s);
		panel.add(button_r);
		panel.add(N);
		panel.add(slider_a);
		panel.add(label_a);
		panel.setBackground(Color.lightGray);
		panel.add(graph);
		frame.add(panel);
		frame.setSize(graph.scale * graph.size + 100, graph.scale * graph.size + 100);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

class Graph extends JPanel {
	int size = 100; 
	int n = 50;	
	int a = Ngraphs.slider_a.getValue();	
	int scale = 5; 
	int p1 = 0;		
	int p2 = n - 1;	
	int cur_color = 1;	
	boolean flag = false;	
	boolean colored = false;	
	int[][] lattice = new int[size][size]; 
	int[][] points = new int[n][2];			
	List<Integer> con_f = new ArrayList<Integer>();
	List<Integer> con_s = new ArrayList<Integer>();
	List<Integer> color = new ArrayList<Integer>();	

	public Graph() {
		start();
	}

	public void start() {
		int iter = 0;
		int x1, y1;	
		
		setBorder(BorderFactory.createLineBorder(Color.blue, 1));	
		for (int i = 0; i < size; i++) {							
			for (int j = 0; j < size; j++) {
				lattice[i][j] = 0;
			}
		}

		do {
			x1 = (int) (Math.random() * size);						
			y1 = (int) (Math.random() * size);
			if (lattice[x1][y1] == 0) {
				lattice[x1][y1] = 1;								
				points[iter][0] = x1;								
				points[iter][1] = y1;
				iter++;
			}
		} while (iter < n);											
	}

	public void reset() {											
		con_s.clear();												
		con_f.clear();
		p1 = 0;
		p2 = n - 1;
		colored = false;
		for (int i = 0; i < n; i++) {								
			int x1 = points[i][0];
			int y1 = points[i][1];
			lattice[x1][y1] = 1;
		}
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(scale * size, scale * size);	
	}

	void step(int p11, int p22) {
		int x1, x2, y1, y2;											
		
		x1 = points[p11][0];										
		y1 = points[p11][1];
		x2 = points[p22][0];
		y2 = points[p22][1];
		if (Math.sqrt((double) (Math.pow(x1 - x2, 2)) + (double) (Math.pow(y1 - y2, 2))) < (double)a) {	
			con_s.add(p11);											
			con_f.add(p22);
		}
		
		if (p22 == p11 + 1) {										
			p1++;													
			p2 = n - 1;												
		} else {													
			p2--;
		}
		
		if (p1 == n - 1) {
			flag = !flag;											
			Ngraphs.button_s.setText("Start");
			Ngraphs.button_r.setEnabled(true);
			color();
		}
	}
	
	public void color() {
		for (int i = 0; i < con_s.size(); i++) {							
			change_color(i);
		}
		for (int i = 0; i < n; i++) {
			int x1 = points[i][0];											
			int y1 = points[i][1];
			if (lattice[x1][y1] == 1) {										
				cur_color++;
				lattice[x1][y1] = cur_color;
			}
		}
		colored = true;
	}
	
	public void change_color(int i) {
		int x1 = points[con_s.get(i)][0];
		int y1 = points[con_s.get(i)][1];
		if (lattice[x1][y1] == 1) {
			cur_color++;
			color.add(con_s.get(i));
			color.add(con_f.get(i));
			int z = 0;
			while (z < color.size()) {
				for (int j = 0; j < con_s.size(); j++) {
					int con = con_s.get(j);
					if (color.contains(con)) {
						if (!color.contains(con_s.get(j))) {
							color.add(con_s.get(j));
						}
						if (!color.contains(con_f.get(j))) {
							color.add(con_f.get(j));
						}
					}
				}
				
				for (int j = 0; j < con_f.size(); j++) {
					int con = con_f.get(j);
					if (color.contains(con)) {
						if (!color.contains(con_s.get(j))) {
							color.add(con_s.get(j));
						}
						if (!color.contains(con_f.get(j))) {
							color.add(con_f.get(j));
						}
					}
				}
				z++;
			}
			for (int j = 0; j < color.size(); j++) {
				x1 = points[color.get(j)][0];
				y1 = points[color.get(j)][1];
				lattice[x1][y1] = cur_color;
			}
			color.clear();
		}
	}

	public void paintComponent(Graphics graf) {
		int x1, x2, y1, y2, n1, n2;
		super.paintComponent(graf);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (lattice[i][j] == 1) {																						
					graf.setColor(Color.black);																					
					graf.fillRect(i * scale, j * scale, scale, scale);															
				} else if (lattice[i][j] > 1) {																					
					graf.setColor(new Color((lattice[i][j] * 30) % 255, 
					(lattice[i][j] * 50) % 255, (lattice[i][j] * 120) % 255));
					graf.fillRect(i * scale, j * scale, scale, scale);
				}
			}
		}

		for (int i = 0; i < con_s.size(); i++) {
			n1 = con_s.get(i);
			n2 = con_f.get(i);
			x1 = points[n1][0];
			x2 = points[n2][0];
			y1 = points[n1][1];
			y2 = points[n2][1];
			if (colored) {																										
				graf.setColor(new Color((lattice[x1][y1] * 30) % 255, 
				(lattice[x1][y1] * 50) % 255, (lattice[x1][y1] * 120) % 255));	
			}
			graf.drawLine(x1 * scale, y1 * scale, x2 * scale, y2 * scale);														
		}

		if (flag) {
			try {
				Thread.sleep(5);																					
			} catch (InterruptedException t) {
			}
			step(p1, p2);
			repaint();
		}
	}
}
