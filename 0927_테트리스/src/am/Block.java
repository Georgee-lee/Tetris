package am;

import java.awt.Color;
import java.awt.Point;

public class Block {
	Point pt; //���� ��ǥ
	int b_size; //���� ũ��
	Color color; //���� ����
	boolean isVisible; // ���� ȭ�鿡 ǥ���ϴ� ����
	// ����� ���� ���� �������ٰ� ���� ����ü�� �ڸ� ������ isVisible�� ���� true�� �ȴ�.
	
	boolean isfilled; //���� �׿��ִ��� ����
	public Block(Point pt, int b_size, Color color) {
		this.pt = pt;
		this.b_size = b_size;
		this.color = color;
	}
	
	
}










