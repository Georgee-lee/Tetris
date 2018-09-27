package am;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JFrame {

	JPanel pan;//����ȭ��
	JPanel pan2;// ���� ����� ���� ȭ��
	JDialog d; //�������� ��ȭ����
	
	//��Ʈ������ �ʿ��� ���迭
	static final int W = 600;// â�� �ʺ�
	static final int H = 630;// â�� ����
	static final int B_ROW = 28; //��Ʈ���� ���迭�� ���� ��
	static final int B_COL = 18; //��Ʈ���� ���迭�� ���� ��
	
	Block[][] block = new Block[B_ROW][B_COL];// ��Ʈ���� ����
	Block[][] block_next = new Block[4][4];// �̸����� ����
	
	int time_speed = 500;
	//javax.swing�� Ÿ�̸� Ȱ��
	Timer timer = new Timer(time_speed, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//process();
		}
	});
	
	//java.awt.Image��ü �غ�(�̹��� �غ�)
	Image back1 = new ImageIcon("src/images/back2.gif").getImage();
	Image gameover = new ImageIcon("src/images/gameOver.PNG").getImage();
	Image su_img = new ImageIcon("src/images/su.png").getImage();
	Image stage_img = new ImageIcon("src/images/stage.png").getImage();
	
	//������ü
	Random rnd = new Random();
	
	int rnd_num; //����(�� ����� ����)
	int rnd_num2; //����(�̸����� �� ����� ����)
	
	int score;	//����   
	int stage_num;	//�������� ��ȣ
	
	boolean isGameStart;
	
	JButton bt_start;
	
	public Tetris() {
		initBlock();
		initPan();
		
		//��ư �۾�
		bt_start = new JButton("   Start!   ");
		bt_start.setBounds(320, 480, 250, 50);
		pan.add(bt_start);
		
		setLocation(100, 150);
		setBounds(400, 80, W, H);
		setResizable(false);// â ũ������ ���ϰ� ����
		//pack();
		setVisible(true);//â �����ֱ�
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bt_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//������ ó�� �����ϴ����� �Ǵ��ؾ� �Ѵ�! == isGameStart�� false�� ��
				if(!isGameStart) {
					bt_start.setText("   Pause!   ");
					isGameStart = true;
				}else {
					bt_start.setText("   Start!   ");
					isGameStart = false;
				}
			}
		});
	}
	
	//��(�迭)�ʱ�ȭ ���
	public void initBlock() {
		
		//�ΰ��� �迭 �ʱ�ȭ (���ڱ׸��� - ũ�� : 30)
		for(int row=0; row<block.length; row++) {//�� �ݺ�
			for(int col=0; col<block[row].length; col++) {//�� �ݺ�
				//�� ��(���� == ����)�� �����Ͽ�
				// �迭�� �����Ѵ�.
				block[row][col] = new Block(
					new Point(col*30-120, row*30-120), 30, Color.BLUE);
			}
		}
		
		//�̸����� (��)�迭 �ʱ�ȭ
		for(int row=0; row<4; row++) {
			for(int col=0; col<4; col++) {
				block_next[row][col] = new Block(
					new Point(col*60+330, row*60+30), 60, Color.RED);
			}
		}
	}
	
	int cnt;
	
	public void initPan() {
		//ȭ�� �г� ����
		pan = new JPanel(null) {
			// JPanel�� ��ӹ޴� �̸����� ����Ŭ����
			
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.clearRect(0, 0, W, H);//ȭ�� ��ü û��
				
				//��� ����
				g2.drawImage(back1, 0, 0, 300, 600, 
						100, 0, 320, back1.getHeight(this), this);
				// back1�̶�� �̹�����ü���� x��ǥ�� 100, y��ǥ 0��
				// ��ġ���� �ʺ� 320, ���̰� back1 �̹����� ���̸�ŭ
				//�߶󳻾� ���� JPanel�� x��ǥ 0, y��ǥ 0�� ������
				// �ʺ� 300, ���̰� 600�� �簢���� �̹����� �׸���.
				
				//���� ���� �׸���
				for(int row=0; row<block.length; row++) {
					for(int col=0; col<block[row].length; col++) {
						//�迭�κ��� �ϳ��� �� ��ü�� �����´�.
						Block b = block[row][col];
						
						//ä���� ���̶�� isVisible�� ���� true�̴�.
						if(b.isVisible) {
							g2.setColor(b.color);
							g2.fillRect(
								b.pt.x, b.pt.y, b.b_size, b.b_size);
						}
						
						if(col>3 && col<14 && row<24 && row>3) {
							g2.setColor(Color.BLACK);
							g2.drawRect(b.pt.x, b.pt.y, b.b_size, b.b_size);
						}
					}
				}
				
				//�̸����� ��(���� ��)
				for(int row=0; row<4; row++) {
					for(int col=0; col<4; col++) {
						Block b = block_next[row][col];
						if(b.isVisible) { // ���� ����� �����ֱ�
							g2.setColor(b.color);
							g2.fillRect(
							  b.pt.x, b.pt.y, b.b_size, b.b_size);
						}
						g2.setColor(Color.BLACK);
						g2.drawRect(b.pt.x, b.pt.y, b.b_size, b.b_size);
						//System.out.println(b.pt);
					}
				}
				
				int tt= score;
				int tt2 = 0;
				for(int i=0; i<8; i++) {
					tt2 = (int)(tt/Math.pow(10, 7-i));  //��
					tt = (int)(tt%Math.pow(10, 7-i));   //������
					
					g2.drawImage(su_img, 320+32*i, 400, 320+32*(i+1), 450, tt2%10*50, 0, tt2%10*50+50, 90, this);
				}
				
				int s = stage_num;
				g2.drawImage(stage_img, 320, 350, 550, 390, 0, s*90, 490, s*90+90, this);
				
			}
			
		};
		
		this.add(pan);//��Ʈ���� ������ �߰�
		//this.setPreferredSize(new Dimension(W, H));//â ũ��
	}
	
	public static void main(String[] args) {
		// ���α׷� ����
		new Tetris();

	}

}
