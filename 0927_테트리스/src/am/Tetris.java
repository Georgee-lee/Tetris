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

	JPanel pan;//메인화면
	JPanel pan2;// 게임 종료시 작은 화면
	JDialog d; //게임종료 대화상자
	
	//테트리스의 필요한 블럭배열
	static final int W = 600;// 창의 너비
	static final int H = 630;// 창의 높이
	static final int B_ROW = 28; //테트리스 블럭배열의 행의 수
	static final int B_COL = 18; //테트리스 블럭배열의 열의 수
	
	Block[][] block = new Block[B_ROW][B_COL];// 테트리스 블럭판
	Block[][] block_next = new Block[4][4];// 미리보기 블럭판
	
	int time_speed = 500;
	//javax.swing의 타이머 활용
	Timer timer = new Timer(time_speed, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//process();
		}
	});
	
	//java.awt.Image객체 준비(이미지 준비)
	Image back1 = new ImageIcon("src/images/back2.gif").getImage();
	Image gameover = new ImageIcon("src/images/gameOver.PNG").getImage();
	Image su_img = new ImageIcon("src/images/su.png").getImage();
	Image stage_img = new ImageIcon("src/images/stage.png").getImage();
	
	//랜덤객체
	Random rnd = new Random();
	
	int rnd_num; //난수(블럭 모양을 선택)
	int rnd_num2; //난수(미리보기 블럭 모양을 선택)
	
	int score;	//점수   
	int stage_num;	//스테이지 번호
	
	boolean isGameStart;
	
	JButton bt_start;
	
	public Tetris() {
		initBlock();
		initPan();
		
		//버튼 작업
		bt_start = new JButton("   Start!   ");
		bt_start.setBounds(320, 480, 250, 50);
		pan.add(bt_start);
		
		setLocation(100, 150);
		setBounds(400, 80, W, H);
		setResizable(false);// 창 크기조절 못하게 방지
		//pack();
		setVisible(true);//창 보여주기
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bt_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//게임이 처음 시작하는지를 판단해야 한다! == isGameStart가 false일 때
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
	
	//블럭(배열)초기화 기능
	public void initBlock() {
		
		//인게임 배열 초기화 (격자그리기 - 크기 : 30)
		for(int row=0; row<block.length; row++) {//행 반복
			for(int col=0; col<block[row].length; col++) {//열 반복
				//각 블럭(상자 == 격자)를 생성하여
				// 배열에 저장한다.
				block[row][col] = new Block(
					new Point(col*30-120, row*30-120), 30, Color.BLUE);
			}
		}
		
		//미리보기 (블럭)배열 초기화
		for(int row=0; row<4; row++) {
			for(int col=0; col<4; col++) {
				block_next[row][col] = new Block(
					new Point(col*60+330, row*60+30), 60, Color.RED);
			}
		}
	}
	
	int cnt;
	
	public void initPan() {
		//화면 패널 생성
		pan = new JPanel(null) {
			// JPanel을 상속받는 이름없는 내부클래스
			
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.clearRect(0, 0, W, H);//화면 전체 청소
				
				//배경 설정
				g2.drawImage(back1, 0, 0, 300, 600, 
						100, 0, 320, back1.getHeight(this), this);
				// back1이라는 이미지객체에서 x좌표가 100, y좌표 0인
				// 위치에서 너비가 320, 높이가 back1 이미지의 높이만큼
				//잘라내어 현재 JPanel의 x좌표 0, y좌표 0인 곳에서
				// 너비가 300, 높이가 600인 사각형에 이미지를 그린다.
				
				//게임 블럭판 그리기
				for(int row=0; row<block.length; row++) {
					for(int col=0; col<block[row].length; col++) {
						//배열로부터 하나의 블럭 객체를 가져온다.
						Block b = block[row][col];
						
						//채워진 블럭이라면 isVisible의 값이 true이다.
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
				
				//미리보기 판(다음 블럭)
				for(int row=0; row<4; row++) {
					for(int col=0; col<4; col++) {
						Block b = block_next[row][col];
						if(b.isVisible) { // 다음 블럭모양 보여주기
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
					tt2 = (int)(tt/Math.pow(10, 7-i));  //몫
					tt = (int)(tt%Math.pow(10, 7-i));   //나머지
					
					g2.drawImage(su_img, 320+32*i, 400, 320+32*(i+1), 450, tt2%10*50, 0, tt2%10*50+50, 90, this);
				}
				
				int s = stage_num;
				g2.drawImage(stage_img, 320, 350, 550, 390, 0, s*90, 490, s*90+90, this);
				
			}
			
		};
		
		this.add(pan);//테트리스 게임판 추가
		//this.setPreferredSize(new Dimension(W, H));//창 크기
	}
	
	public static void main(String[] args) {
		// 프로그램 시작
		new Tetris();

	}

}
