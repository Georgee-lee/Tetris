package am;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class BlockShape {
	Point cur_pt = new Point(0, 0);//���� ������
	int b_style; // ���� ���
	
	int rotation; // ���� ����(0,1,2,3)�ᱹ 3�����迭�� �ִ� 2����
					//�迭�� index���̴�.
	
	//���� ǥ���� ������ ��(4��)
	Point[] b_shape = new Point[4];
	
	//���� �׸��ڰ��� y��ǥ �Ÿ�
	int b_shadow_dis;
	
	//�׸��� ���� ���� ���� ��
	//(0�̸� ��Ÿ���� �ʴ´�.)
	int shadow_alpha = 70;
	
	Color color; // ���� ����
	
	Random rnd = new Random();

	public BlockShape(int b_style, Color c) {
		this.b_style = b_style;//���� ���(0~6)
		this.color = c; //���� ����
		
		//this.rotation = (int)(Math.random()*4);
		this.rotation = rnd.nextInt(4);//������ ����� ����
		
		// b_shape��� �迭�� �׸����� �ϴ�
		//4���� ���� ��ġ���� �����Ѵ�.
		int i=0;
		for(int row=0; row<Shape.SHAPE[b_style][rotation].length; row++) {
			for(int col=0; col<Shape.SHAPE[b_style][rotation][row].length; col++) {
				//���� 1�� �͵鸸 ã�Ƴ���.
				if(Shape.SHAPE[b_style][rotation][row][col] == 1)
					this.b_shape[i++] = new Point(row, col);
			}
		}//for���� ��
	}//�������� ��
	
	//�ð��� ���� ���� �������� ���
	//(�ٸ����� �ٴڰ��� �浹üũ)
	public void moveDown(Block[][] b) {
		boolean check = false;// �浹���� Ȯ��
		
		//�浹üũ �ݺ���
		for(int i=0; i<4; i++) {
			//�ٷ� �Ʒ�ĭ�� ä���� �ִٸ� �̵��� �� ����.
			if(b[this.b_shape[i].x+1][this.b_shape[i].y].isfilled) {
				//���� ��ġ�� ���߱� ���� isfilled���� true�� ����
				b[this.b_shape[i].x][this.b_shape[i].y].isfilled = true;
				check = true;
				break;
			}
		}
		
		//�浹�� �Ǿ��ٸ� check������ true�� �� �ְ�, �׷��� �ʴٸ�
		//check�������� false�� ������ �ִ�.
		//���� ��� �����Ϸ��� check������ false���� ������ �Ѵ�.
		if(!check) {
			for(int i=0; i<4; i++) {
				//������ �ڸ�
				b[this.b_shape[i].x][this.b_shape[i].y].isVisible = false;
				
				//�׸��� ó�� - ������ �׸��ڴ� ���ش�.
				b[this.b_shape[i].x + b_shadow_dis]
				  [this.b_shape[i].y].isVisible = false;
				
				this.b_shape[i].x += 1;
				if(this.b_shape[i].x >= 23)
					this.b_shape[i].x = 23;
			}
			
			getShadowBlock(b);//�׸��� �Ÿ� üũ
			
			//�׸��ڿ��� �Ÿ��� ����߱� ������ �׸��ڸ� ǥ��
			for(int i=0; i<4; i++) {
				b[b_shape[i].x + b_shadow_dis]
				  [b_shape[i].y].isVisible = true;
				
				b[b_shape[i].x + b_shadow_dis][b_shape[i].y].color =
					new Color(color.getRed(),
							  color.getGreen(),
							  color.getBlue(), shadow_alpha);
				
				//�������� ���� �����ֱ� ���� isVisible�� ����
				b[b_shape[i].x][b_shape[i].y].isVisible = true;
				b[b_shape[i].x][b_shape[i].y].color = color;
			}
			//���� ������ ����
			this.cur_pt.x += 1; 
		}else {
			//�浹���� ���
			// ���� �������� ���� �ϳ��� ����ٸ�
			// ���� �̷�� 4���� ���� ��ΰ� ����� �Ѵ�.
			// �׷��� ���ؼ��� isfilled�� �����ؾ� ��!
			for(int i=0; i<4; i++) {
				b[b_shape[i].x][b_shape[i].y].isfilled = true;
				b[b_shape[i].x][b_shape[i].y].color = color;
			}
		}
	}
	
	
	//�׸��� ����� ��ġ�� �ľ��ϴ� ���
	public void getShadowBlock(Block[][] b) {
		int value = 1;
		bk:while(value < 20) {
			for(int i=0; i<4; i++) {
				
				//���� �Ʒ��� ĭ���� ä�������� üũ
				if(b_shape[i].x + value >= 24 ||
					b[b_shape[i].x + value][b_shape[i].y].isfilled)
					break bk;
			}
			value++;
		}
		b_shadow_dis = value-1;
	}
}








