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
			process();
		}
	});
	
	//java.awt.Image��ü �غ�(�̹��� �غ�)
	Image back1 = new ImageIcon(
			"src/images/back2.gif").getImage();
	Image gameover = new ImageIcon(
			"src/images/gameOver.PNG").getImage();
	Image su_img = new ImageIcon(
			"src/images/su.png").getImage();
	Image stage_img = new ImageIcon(
			"src/images/stage.png").getImage();
	
	//������ü
	Random rnd = new Random();
	
	int rnd_num; //����(�� ����� ����)
	int rnd_num2; //����(�̸����� �� ����� ����)
	
	int score; //����
	int stage_num;//�������� ������
	
	JButton bt_start;
	boolean isGameStart;//false - ���ӽ��ۿ���
	boolean isGameEnd;//false - �������Ῡ��
	
	BlockShape block_s;// �����
	BlockShape block_sn;// ���� ��
	int selected; // ���°�
	
	//���� 7����
	Color[] color = {Color.RED, 
					Color.GREEN, 
					Color.BLUE,
					new Color(255,255,25),
					new Color(100, 150, 0),
					new Color(20, 255, 255),
					new Color(100, 125, 200)};
	
	public Tetris() {
		initBlock();
		initPan();
		
		//��ư �۾�
		bt_start = new JButton("  Start!  ");
		bt_start.setBounds(320, 470, 260, 100);
		pan.add(bt_start);
		
		setLocation(100, 150);
		setBounds(100, 150, W, H);
		setResizable(false);// â ũ������ ���ϰ�...
		//pack();
		setVisible(true);//â �����ֱ�
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bt_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//������ ó�� ���۵Ǵ� �κ������� �Ǵ�!
				// (isGameStart�� false�϶�)
				//if(isGameStart == false) {
				if(!isGameStart) {
					gameStart();
					
					bt_start.setText("  Pause  ");
				}else {
					
					isGameStart = false;
					bt_start.setText("  Start!  ");
				}
			}
		});
	}
	
	//��(�迭)�ʱ�ȭ ���
	public void initBlock() {
		
		//������ �迭 �ʱ�ȭ (���ڱ׸��� - ũ�� : 30)
		for(int row=0; row<block.length; row++) {//�� �ݺ�
			for(int col=0; col<block[row].length; col++) {//�� �ݺ�
				//�� ��(���� == ����)�� �����Ͽ�
				// �迭�� �����Ѵ�.
				block[row][col] = new Block(
					new Point(col*30-120, row*30-120), 30, Color.YELLOW);
				
				// col���� 4���� �۰ų�, 14�̻��� �� �Ǵ� row����
				// 24�̻��� �迭�� ���(Block)���� isfilled����
				// true�� �����Ѵ�.
				if(col<4 || col>=14 || row>=24)
					block[row][col].isfilled = true;
			}
		}
		
		//�̸������� (��)�迭 �ʱ�ȭ
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
						//�迭�κ��� �ϳ��� ��ϰ�ü�� �����´�.
						Block b = block[row][col];
						
						//ä���� ���̶�� isVisible�� ���� true�̴�.
						if(b.isVisible) {
							g2.setColor(b.color);
							g2.fillRect(
								b.pt.x, b.pt.y, b.b_size, b.b_size);
						}
						
						if(col>3 && row<24 && col<14 && row>3) {
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
					}
				}
				
				// ���ھ� �׸���
				int imsi = score;
				int imsi2 = 0;
				for(int i=0; i<8; i++) {
					imsi2 = (int)(imsi/Math.pow(10, 7-i));
					imsi = (int)(imsi%Math.pow(10, 7-i));
					//System.out.println(imsi2+"/"+imsi);
					g2.drawImage(su_img, 320+32*i, 400,
							320+32*(i+1), 450,
							imsi2%10*50, 0, imsi2%10*50+50, 90, this);
				}
				
				// �������� �� �׸���
				int s = stage_num;
				g2.drawImage(stage_img, 320, 320, 560, 390,
						0, s*90+2, 490, (s+1)*90, this);
			}
			
		};
		
		
		
		this.add(pan);//��Ʈ���� ������ �߰�
		//this.setPreferredSize(new Dimension(W, H));//â ũ��
	}
	
	public void gameStart() {
		if(!isGameStart) {//������ ó�� ���۵Ǵ� �κ������� �Ǵ�!
			isGameStart = true;
			isGameEnd = false;
			addBlock();
		}
		score = 0;
		stage_num = 0;
		timer.start();
		//ȭ�鿡 ��ư ���� ������ ��Ŀ���� ��ư�� ���� ������Ʈ��
		//���� �Ǿ� �ִ�. â�� ��Ŀ���� �α� ���� ������ ���� ȣ���Ѵ�.
		this.requestFocus();
	}
	
	public void addBlock() {
		//������ ������ ��, ��(����)�� �����Ͽ�
		// ��� �̵���Ű�� ���
		if(block_s == null) {
			rnd_num = rnd.nextInt(7);
			block_sn = new BlockShape(rnd_num, color[rnd_num]);
		}else {
			//�̹� ���� ���õǾ �������� �ִ� ����
			//�̸����� �� �����
			for(int i=0; i<4; i++) {
				block_next[block_sn.b_shape[i].x]
						  [block_sn.b_shape[i].y].isVisible = false;
			}
		}
		block_s = block_sn;//���� ���� ���� ������ ����!!
		
		//���� ���� �������� �������� ����� ����
		block_s.cur_pt.x += 1;
		block_s.cur_pt.y += 7;
		
		//���� ���� �����ǿ� �׸��� ����
		//����� (4X4)�� �����Ѵ�.
		for(int i=0; i<4; i++) {
			block_s.b_shape[i].x += 1;
			block_s.b_shape[i].y += 7;
			
			//�����ǿ� �׸���.
			block[block_s.b_shape[i].x]
				 [block_s.b_shape[i].y].isVisible = true;
			block[block_s.b_shape[i].x]
					 [block_s.b_shape[i].y].color = block_s.color;
		}
		
		//�׸��� �׸���
		block_s.getShadowBlock(block);//�׸��ڿ��� �Ÿ� ���ϱ�
		for(int i=0; i<4; i++) {
			block[block_s.b_shape[i].x + block_s.b_shadow_dis]
				 [block_s.b_shape[i].y].isVisible = true;
			block[block_s.b_shape[i].x + block_s.b_shadow_dis]
				[block_s.b_shape[i].y].color = 
					new Color(block_s.color.getRed(),
							block_s.color.getGreen(),
							block_s.color.getBlue(), 
							block_s.shadow_alpha); 
		}
		
		//������ ����(���� ���� �����ؼ� ��Ÿ���� ���� �ݺ���)
		for(rnd_num2 = rnd.nextInt(7); rnd_num == rnd_num2; ) {
			rnd_num2 = rnd.nextInt(7);
		}
		
		//�������� �����Ǿ����� �������� ��������!
		rnd_num = rnd_num2;
		block_sn = new BlockShape(rnd_num, color[rnd_num]);
		
		//�̸����� �ǿ� ������ �׸���.
		for(int i=0; i<4; i++) {
			block_next[block_sn.b_shape[i].x]
					  [block_sn.b_shape[i].y].isVisible = true;
			block_next[block_sn.b_shape[i].x]
					  [block_sn.b_shape[i].y].color = block_sn.color;
		}
	}
	
	public void process() {
		//Ÿ�̸ӿ� ���� 0.5�ʸ��� ȣ��Ǵ� �޼���
		
		//���ӷ���üũ
		
		//���̵�
		block_s.moveDown(block);
//		System.out.println(block[block_s.b_shape[0].x][block_s.b_shape[0].y].isfilled);
		//���� �������� ���� ���⶧ �������� �����Ѵ�.
		if(block[block_s.b_shape[0].x][block_s.b_shape[0].y].isfilled) {
//			System.out.println("�޷�~");
			addBlock();
		}
		
		lineCheck();//������üũ
		
		checkEnd();//��������üũ
		
		pan.repaint();
	}
	int bf_cnt;
	public void lineCheck() {
		boolean check = false;
		int[] r_num = new int[4];//������ ���ȣ
		bf_cnt = 0;
		
		//ȭ�� �Ʒ��ʺ��� �� �ϳ���(23 ~ 4)�˻��ϴ� �ݺ���
		for(int i=23; i>3; i--) {
			check = true;
			for(int j=4; j<14; j++) {
				if(!block[i][j].isfilled) {// ä������ ���� ĭ�� ������
					check = false;
					break;//���� ���� ���񱳸� �׸��ϰ� ���� ������ ��
				}
			}
			//���� ������ ���� �� check�� ���� ��� true��
			// ���� �� ���� ������ �߰��� if���� ���ؼ�
			// false�� ����Ǿ��� ���� �ִ�.
			//false���� ������ �ִٴ� ���� �߰��� ��ä���� ĭ��
			//�ִ� ����.
			if(check) {
				r_num[bf_cnt++] = i;
			}
			
			if(bf_cnt > 3)
				break;
		}
		
		//������ ����� �����Ѵ�. �̶�
		// ������ ���� �ٷ� ���� �ִ� ���� ������ �Ѵ�.
		int r=0;
		while(r < bf_cnt) {
			for(int i=r_num[r]; i>4; i--) {
				//������ ���� �����Ǿ����Ƿ� ���� �ݺ��ϴ� �ݺ���
				for(int j=4; j<14; j++) {
					//������ ĭ�� �ٷ� ���� �ִ� ĭ���� ä���� �Ѵ�.
					if(block[i-1][j].isfilled) {
						block[i][j].isfilled = block[i-1][j].isfilled;
						block[i][j].isVisible = block[i-1][j].isVisible;
						block[i][j].color = block[i-1][j].color;
					}else {
						block[i][j].isfilled = false;
						block[i][j].isVisible = false;
					}
				}
			}
			r++;//������ ���� ���� ��ȣ�� �������� ���� r�� ����
		}//while�� ��
		
		//���� Ȯ��
		switch(bf_cnt) {
			case 1:
				score += 100;
				break;
			case 2:
				score += 300;
				break;
			case 3:
				score += 600;
				break;
			case 4:
				score += 1000;
				break;
		}
		setTitle(score+"��");
	}
	
	public void checkEnd() {
		boolean check = false;
		for(int y=4; y<14; y++) {
			if(block[3][y].isfilled) {
				check = true;
				break;
			}
		}
		//check�� true�� �� �ִٸ� ��������!
		if(check)
			gameOver();
	}
	
	public void gameOver() {
		isGameStart = false;
		isGameEnd = true;
		timer.stop();// Ÿ�̸� ����!
		d = new JDialog(this, "GameOver");
		//��ȭ���ڿ� ǥ���� �г� ����
		pan2 = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				g.clearRect(0, 0, 240, 280);
				
				//�̹��� �׸���
				g.drawImage(gameover, 0, 0, 240, 280, 
						0, 0, 
						gameover.getWidth(this), 
						gameover.getHeight(this), this);
			}			
		};
		pan2.setPreferredSize(new Dimension(240, 280));
		
		d.add(pan2);//��ȭ������ ����� �߰�
		
		//��ȭ������ ��ġ����
		d.setLocation(this.getLocation().x+170,
				this.getLocation().y+100);
		d.pack();
		d.setResizable(false);// â ũ������ ����
		d.setVisible(true);
		
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);//â �ݱ�
	}
	
	public void stageCheck() {
		//������ �������� ������ time_speed�� �����Ѵ�.
	}
	
	public static void main(String[] args) {
		// ���α׷� ����
		new Tetris();
	}

}
