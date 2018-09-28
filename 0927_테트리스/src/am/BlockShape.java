package am;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class BlockShape {
	Point cur_pt = new Point(0, 0);//블럭의 시작점
	int b_style; // 블럭의 모양
	
	int rotation; // 블럭의 각도(0,1,2,3)결국 3차원배열의 있는 2차원
					//배열의 index값이다.
	
	//실제 표현할 도형의 값(4개)
	Point[] b_shape = new Point[4];
	
	//블럭의 그림자간의 y좌표 거리
	int b_shadow_dis;
	
	//그림자 블럭의 색상 투명도 값
	//(0이면 나타나지 않는다.)
	int shadow_alpha = 70;
	
	Color color; // 블럭의 색상
	
	Random rnd = new Random();

	public BlockShape(int b_style, Color c) {
		this.b_style = b_style;//블럭의 모양(0~6)
		this.color = c; //블럭의 색상
		
		//this.rotation = (int)(Math.random()*4);
		this.rotation = rnd.nextInt(4);//지정된 모양의 각도
		
		// b_shape라는 배열에 그리고자 하는
		//4개의 도형 위치값을 저장한다.
		int i=0;
		for(int row=0; row<Shape.SHAPE[b_style][rotation].length; row++) {
			for(int col=0; col<Shape.SHAPE[b_style][rotation][row].length; col++) {
				//값이 1인 것들만 찾아낸다.
				if(Shape.SHAPE[b_style][rotation][row][col] == 1)
					this.b_shape[i++] = new Point(row, col);
			}
		}//for문의 끝
	}//생성자의 끝
	
	//시간에 따라 블럭이 내려가는 기능
	//(다른블럭과 바닥과의 충돌체크)
	public void moveDown(Block[][] b) {
		boolean check = false;// 충돌여부 확인
		
		//충돌체크 반복문
		for(int i=0; i<4; i++) {
			//바로 아래칸이 채워져 있다면 이동할 수 없다.
			if(b[this.b_shape[i].x+1][this.b_shape[i].y].isfilled) {
				//현재 위치에 멈추기 위해 isfilled값을 true로 변경
				b[this.b_shape[i].x][this.b_shape[i].y].isfilled = true;
				check = true;
				break;
			}
		}
		
		//충돌이 되었다면 check변수에 true가 들어가 있고, 그렇지 않다면
		//check변수에는 false를 가지고 있다.
		//블럭이 계속 진행하려면 check변수가 false값을 가져야 한다.
		if(!check) {
			for(int i=0; i<4; i++) {
				//지나온 자리
				b[this.b_shape[i].x][this.b_shape[i].y].isVisible = false;
				
				//그림자 처리 - 기존의 그림자는 없앤다.
				b[this.b_shape[i].x + b_shadow_dis]
				  [this.b_shape[i].y].isVisible = false;
				
				this.b_shape[i].x += 1;
				if(this.b_shape[i].x >= 23)
					this.b_shape[i].x = 23;
			}
			
			getShadowBlock(b);//그림자 거리 체크
			
			//그림자와의 거리를 계산했기 때문에 그림자를 표현
			for(int i=0; i<4; i++) {
				b[b_shape[i].x + b_shadow_dis]
				  [b_shape[i].y].isVisible = true;
				
				b[b_shape[i].x + b_shadow_dis][b_shape[i].y].color =
					new Color(color.getRed(),
							  color.getGreen(),
							  color.getBlue(), shadow_alpha);
				
				//떨어지는 블럭을 보여주기 위해 isVisible값 변경
				b[b_shape[i].x][b_shape[i].y].isVisible = true;
				b[b_shape[i].x][b_shape[i].y].color = color;
			}
			//현재 기준점 증가
			this.cur_pt.x += 1; 
		}else {
			//충돌했을 경우
			// 현재 내려오던 블럭이 하나라도 멈췄다면
			// 블럭을 이루는 4개의 상자 모두가 멈춰야 한다.
			// 그러기 위해서는 isfilled값 변경해야 함!
			for(int i=0; i<4; i++) {
				b[b_shape[i].x][b_shape[i].y].isfilled = true;
				b[b_shape[i].x][b_shape[i].y].color = color;
			}
		}
	}
	
	
	//그림자 모양의 위치를 파악하는 기능
	public void getShadowBlock(Block[][] b) {
		int value = 1;
		bk:while(value < 20) {
			for(int i=0; i<4; i++) {
				
				//블럭의 아래쪽 칸들을 채워졌는지 체크
				if(b_shape[i].x + value >= 24 ||
					b[b_shape[i].x + value][b_shape[i].y].isfilled)
					break bk;
			}
			value++;
		}
		b_shadow_dis = value-1;
	}
}








