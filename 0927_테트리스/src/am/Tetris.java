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
			process();
		}
	});
	
	//java.awt.Image객체 준비(이미지 준비)
	Image back1 = new ImageIcon(
			"src/images/back2.gif").getImage();
	Image gameover = new ImageIcon(
			"src/images/gameOver.PNG").getImage();
	Image su_img = new ImageIcon(
			"src/images/su.png").getImage();
	Image stage_img = new ImageIcon(
			"src/images/stage.png").getImage();
	
	//랜덤객체
	Random rnd = new Random();
	
	int rnd_num; //난수(블럭 모양을 선택)
	int rnd_num2; //난수(미리보기 블럭 모양을 선택)
	
	int score; //점수
	int stage_num;//스테이지 레벨값
	
	JButton bt_start;
	boolean isGameStart;//false - 게임시작여부
	boolean isGameEnd;//false - 게임종료여부
	
	BlockShape block_s;// 블럭모양
	BlockShape block_sn;// 다음 블럭
	int selected; // 상태값
	
	//색상 7가지
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
		
		//버튼 작업
		bt_start = new JButton("  Start!  ");
		bt_start.setBounds(320, 470, 260, 100);
		pan.add(bt_start);
		
		setLocation(100, 150);
		setBounds(100, 150, W, H);
		setResizable(false);// 창 크기조절 못하게...
		//pack();
		setVisible(true);//창 보여주기
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bt_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//게임이 처음 시작되는 부분인지를 판단!
				// (isGameStart가 false일때)
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
	
	//블럭(배열)초기화 기능
	public void initBlock() {
		
		//게임판 배열 초기화 (격자그리기 - 크기 : 30)
		for(int row=0; row<block.length; row++) {//행 반복
			for(int col=0; col<block[row].length; col++) {//열 반복
				//각 블럭(상자 == 격자)를 생성하여
				// 배열에 저장한다.
				block[row][col] = new Block(
					new Point(col*30-120, row*30-120), 30, Color.YELLOW);
				
				// col값이 4보다 작거나, 14이상인 곳 또는 row값이
				// 24이상인 배열의 요소(Block)에서 isfilled값을
				// true로 변경한다.
				if(col<4 || col>=14 || row>=24)
					block[row][col].isfilled = true;
			}
		}
		
		//미리보기판 (블럭)배열 초기화
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
						//배열로부터 하나의 블록객체를 가져온다.
						Block b = block[row][col];
						
						//채워진 블럭이라면 isVisible의 값이 true이다.
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
					}
				}
				
				// 스코어 그리기
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
				
				// 스테이지 값 그리기
				int s = stage_num;
				g2.drawImage(stage_img, 320, 320, 560, 390,
						0, s*90+2, 490, (s+1)*90, this);
			}
			
		};
		
		
		
		this.add(pan);//테트리스 게임판 추가
		//this.setPreferredSize(new Dimension(W, H));//창 크기
	}
	
	public void gameStart() {
		if(!isGameStart) {//게임이 처음 시작되는 부분인지를 판단!
			isGameStart = true;
			isGameEnd = false;
			addBlock();
		}
		score = 0;
		stage_num = 0;
		timer.start();
		//화면에 버튼 등이 있으면 포커스가 버튼과 같은 컴포넌트로
		//가게 되어 있다. 창에 포커스를 두기 위해 다음과 같이 호출한다.
		this.requestFocus();
	}
	
	public void addBlock() {
		//다음블럭 생성한 후, 블럭(도형)을 생성하여
		// 계속 이동시키는 기능
		if(block_s == null) {
			rnd_num = rnd.nextInt(7);
			block_sn = new BlockShape(rnd_num, color[rnd_num]);
		}else {
			//이미 블럭이 선택되어서 떨어지고 있는 상태
			//미리보기 판 지우기
			for(int i=0; i<4; i++) {
				block_next[block_sn.b_shape[i].x]
						  [block_sn.b_shape[i].y].isVisible = false;
			}
		}
		block_s = block_sn;//다음 블럭을 현재 블럭으로 지정!!
		
		//현재 블럭의 기준점을 게임판의 가운데로 지정
		block_s.cur_pt.x += 1;
		block_s.cur_pt.y += 7;
		
		//실제 블럭을 게임판에 그리기 전에
		//모양을 (4X4)에 정의한다.
		for(int i=0; i<4; i++) {
			block_s.b_shape[i].x += 1;
			block_s.b_shape[i].y += 7;
			
			//게임판에 그린다.
			block[block_s.b_shape[i].x]
				 [block_s.b_shape[i].y].isVisible = true;
			block[block_s.b_shape[i].x]
					 [block_s.b_shape[i].y].color = block_s.color;
		}
		
		//그림자 그리기
		block_s.getShadowBlock(block);//그림자와의 거리 구하기
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
		
		//다음블럭 선별(같은 블럭을 연속해서 나타남을 막는 반복문)
		for(rnd_num2 = rnd.nextInt(7); rnd_num == rnd_num2; ) {
			rnd_num2 = rnd.nextInt(7);
		}
		
		//다음블럭이 선별되었으니 다음블럭을 생성하자!
		rnd_num = rnd_num2;
		block_sn = new BlockShape(rnd_num, color[rnd_num]);
		
		//미리보기 판에 다음블럭 그린다.
		for(int i=0; i<4; i++) {
			block_next[block_sn.b_shape[i].x]
					  [block_sn.b_shape[i].y].isVisible = true;
			block_next[block_sn.b_shape[i].x]
					  [block_sn.b_shape[i].y].color = block_sn.color;
		}
	}
	
	public void process() {
		//타이머에 의해 0.5초마다 호출되는 메서드
		
		//게임레벨체크
		
		//블럭이동
		block_s.moveDown(block);
//		System.out.println(block[block_s.b_shape[0].x][block_s.b_shape[0].y].isfilled);
		//지금 내려가는 블럭이 멈출때 다음블럭을 생성한다.
		if(block[block_s.b_shape[0].x][block_s.b_shape[0].y].isfilled) {
//			System.out.println("메롱~");
			addBlock();
		}
		
		lineCheck();//블럭라인체크
		
		checkEnd();//게임종료체크
		
		pan.repaint();
	}
	int bf_cnt;
	public void lineCheck() {
		boolean check = false;
		int[] r_num = new int[4];//삭제할 행번호
		bf_cnt = 0;
		
		//화면 아래쪽부터 행 하나씩(23 ~ 4)검사하는 반복문
		for(int i=23; i>3; i--) {
			check = true;
			for(int j=4; j<14; j++) {
				if(!block[i][j].isfilled) {// 채워지지 않은 칸을 만나면
					check = false;
					break;//현재 행의 열비교를 그만하고 다음 행으로 감
				}
			}
			//현재 지점에 왔을 때 check의 값이 계속 true를
			// 유지 할 수도 있지만 중간에 if문에 의해서
			// false로 변경되었을 수도 있다.
			//false값을 가지고 있다는 것은 중간에 안채워진 칸이
			//있는 경우다.
			if(check) {
				r_num[bf_cnt++] = i;
			}
			
			if(bf_cnt > 3)
				break;
		}
		
		//삭제할 행들은 삭제한다. 이때
		// 삭제할 행의 바로 위에 있는 행을 내려야 한다.
		int r=0;
		while(r < bf_cnt) {
			for(int i=r_num[r]; i>4; i--) {
				//위에서 행이 지정되었으므로 열을 반복하는 반복문
				for(int j=4; j<14; j++) {
					//삭제할 칸의 바로 위에 있는 칸으로 채워야 한다.
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
			r++;//삭제할 다음 행의 번호를 가져오기 위해 r값 증가
		}//while의 끝
		
		//점수 확인
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
		setTitle(score+"점");
	}
	
	public void checkEnd() {
		boolean check = false;
		for(int y=4; y<14; y++) {
			if(block[3][y].isfilled) {
				check = true;
				break;
			}
		}
		//check에 true가 들어가 있다면 게임종료!
		if(check)
			gameOver();
	}
	
	public void gameOver() {
		isGameStart = false;
		isGameEnd = true;
		timer.stop();// 타이머 정지!
		d = new JDialog(this, "GameOver");
		//대화상자에 표현할 패널 생성
		pan2 = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				g.clearRect(0, 0, 240, 280);
				
				//이미지 그리기
				g.drawImage(gameover, 0, 0, 240, 280, 
						0, 0, 
						gameover.getWidth(this), 
						gameover.getHeight(this), this);
			}			
		};
		pan2.setPreferredSize(new Dimension(240, 280));
		
		d.add(pan2);//대화상자의 가운데에 추가
		
		//대화상자의 위치지정
		d.setLocation(this.getLocation().x+170,
				this.getLocation().y+100);
		d.pack();
		d.setResizable(false);// 창 크기조정 못함
		d.setVisible(true);
		
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);//창 닫기
	}
	
	public void stageCheck() {
		//점수로 스테이지 레벨과 time_speed를 조정한다.
	}
	
	public static void main(String[] args) {
		// 프로그램 시작
		new Tetris();
	}

}
