package game;

import processing.core.PApplet;

import java.util.ArrayList;

@SuppressWarnings ("serial")
public class Sentry extends PApplet {
	byte gameState = 0;
	int frameNum = 0;
	boolean resetFrameNum = true;
	
	int score = 0;
	
	ArrayList<Phalanx> phalanxShips = new ArrayList<>();
	byte totShips = 0;
	
	float phalanxX = 25;
	float phalanxY = 25;
	float phalanxWidth = 50;
	float phalanxHeight = 50;
	
	Guardian sentinel;
	
	float guardianStartX = 250;
	float guardianStartY = 475;
	float guardianWidth = 25;
	float guardianHeight = 50;
	
	public void setup() {
		background(0);
		size(500, 500);
		
		smooth();
		noStroke();
		
		textSize(25);
		textAlign(CENTER);
		
		rectMode(CENTER);
		
		for (int i = 0; i <= 9; i++) {
			phalanxShips.add(new Phalanx(phalanxX, phalanxY, phalanxWidth, phalanxHeight));
			this.phalanxX += this.phalanxWidth;
		}
		
		sentinel = new Guardian(guardianStartX, guardianStartY, guardianWidth, guardianHeight);
	}
	
	public void draw() {
		frameNum++;
		
		if (this.gameState == 0 && this.frameNum <= 300) {
			fill(0, 153, 153, 150);
			text("sentry", this.width/2, this.height/2);
		}
		
		else if (this.gameState == 0) {
			background(0);
			
			textSize(14);
			fill(0, 153, 76, 200);
			text("the invading phalanx stand on the precipice of conquering the galaxy", this.width/2, this.height/3 + 30);
			text("you, guardian, are the last line of defense", this.width/2, this.height/3 + 60);
			text("destroy the phalanx ships with your energy blasters", this.width/2, this.height/3 + 90);
			text("do not let the phalanx cross the adelost way", this.width/2, this.height/3 + 120);
			
			if (this.frameNum < 600)
				this.key = 'n';
			
			if (this.frameNum >= 600) {
				textSize(25);
				fill(204, 0, 102, 120);
				text("press \'y\' for instructions", this.width/2, this.height/3 + 180);
			}
			
			if (this.key == 'y' && this.frameNum >= 600) {
				this.gameState++;
				this.key = 'n';
			}
		}
		
		else if (this.gameState == 1) {
			background(0);
			
			if (resetFrameNum) {
				this.frameNum = 0;
				resetFrameNum = false;
			}
			
			textSize(16);
			fill(0, 102, 204, 200);
			text("use 'a' to move left and 'd' to move right", this.width/2, this.height/3 + 30);
			text("press 'w' to fire energy blasts", this.width/2, this.height/3 + 60);
			
			if (this.frameNum < 120)
				this.key = 'n';
			
			if (this.frameNum >= 120) {
				textSize(25);
				fill(204, 0, 102, 120);
				text("press \'y\' to begin", this.width/2, this.height/3 + 180);
			}
			
			if (this.key == 'y' && this.frameNum >= 120) {
				this.gameState++;
				this.resetFrameNum = true;
				this.key = 'n';
			}
		}
		
		else if (this.gameState == 2) {
			background(0);
			
			if (resetFrameNum) {
				this.frameNum = 0;
				resetFrameNum = false;
			}
			
			for (int i = 0; i < phalanxShips.size(); i++) {
				drawPhalanx(phalanxShips.get(i).xPos, phalanxShips.get(i).yPos, phalanxShips.get(i).width, phalanxShips.get(i).height);
			}
			
			drawGuardian(sentinel.xPos, sentinel.yPos, sentinel.width, sentinel.height);
			
			if (this.frameNum == 300) {
				for (int i = 0; i < phalanxShips.size(); i++) {
					phalanxShips.get(i).move();
					this.gameState += phalanxShips.get(i).checkGameStatus(this.height, sentinel.height);
					if (this.gameState == 3)
						break;
				}
				
				this.frameNum = 0;
			}
			
			if ((this.frameNum == 60 || this.frameNum == 120 || this.frameNum == 180 || this.frameNum == 240 || this.frameNum == 300) && this.phalanxY < this.height-sentinel.height) {
				int phalanxNum = (int)(Math.random()*phalanxShips.size());
				phalanxShips.get(phalanxNum).energyBlast(phalanxShips.get(phalanxNum).xPos, phalanxShips.get(phalanxNum).yPos+this.phalanxHeight/2+Blast.height/2);
			}
			
			for (int i = 0; i < phalanxShips.size(); i++) {
				for (int j = 0; j < phalanxShips.get(i).energyBlasts.size(); j++) {
					drawBlasts(phalanxShips.get(i).energyBlasts.get(j).xPos, phalanxShips.get(i).energyBlasts.get(j).yPos, Blast.width, Blast.height);
					phalanxShips.get(i).energyBlasts.get(j).phalanxBlastMove();
					this.gameState += phalanxShips.get(i).energyBlasts.get(j).phalanxBlastGameStatus(sentinel.xPos, sentinel.yPos, sentinel.width, sentinel.height);
					
					if (phalanxShips.get(i).energyBlasts.get(j).yPos >= this.height+Blast.height/2) {
						phalanxShips.get(i).energyBlasts.remove(j);
						this.score += 2;
					}
				}
			}
			
			if (this.key == 'd') {
				sentinel.moveRight();
				this.key = 'n';
			}
			
			else if (this.key == 'a') {
				sentinel.moveLeft();
				this.key = 'n';
			}
			
			if (this.key == 'w') {
				sentinel.energyBlast(sentinel.xPos, sentinel.yPos-sentinel.height/2-Blast.height/2);
				this.key = 'n';
			}
			
			for (int i = 0; i < sentinel.energyBlasts.size(); i++) {
				drawBlasts(sentinel.energyBlasts.get(i).xPos, sentinel.energyBlasts.get(i).yPos, Blast.width, Blast.height);
				sentinel.energyBlasts.get(i).guardianBlastMove();
				for (int j = 0; j < phalanxShips.size(); j++) {
					if (!phalanxShips.get(j).checkShipStatus(sentinel.energyBlasts.get(i).xPos, sentinel.energyBlasts.get(i).yPos)) {
						phalanxShips.remove(j);
						this.score += 10;
					}
				}
				
				if (sentinel.energyBlasts.get(i).yPos <= 0-Blast.height/2)
					sentinel.energyBlasts.remove(i);
			}
			
			if (phalanxShips.size() == 0)
				this.gameState += 2;
			
			
		}
		
		else if (this.gameState == 3) {
			background(0);
			
			textSize(20);
			fill(102, 0, 102, 200);
			text("darkness rises", this.width/2, this.height/3+30);
			text("total score: "+this.score, this.width/2, this.height/3+60);
		}
		
		else {
			background(0);
			
			textSize(20);
			fill(255, 153, 255, 200);
			text("a new dawn", this.width/2, this.height/3+30);
			text("total score: "+this.score, this.width/2, this.height/3+60);
		}
	}
	
	public void drawPhalanx(float x, float y, float width, float height) {
		fill(255, (float)(Math.random()*180), (float)(Math.random()*150), 200);
		
		rect(x, y, width, height);
	}
	
	public void drawGuardian(float x, float y, float width, float height) {
		fill(255, 255, 102, 200);
		
		rect(x, y, width, height);
	}
	
	public void drawBlasts(float x, float y, float width, float height) {
		fill(0, 255, 255, 150);
		
		rect(x, y, width, height);
	}
}

class Phalanx {
	float xPos, yPos;
	float width, height;
	
	byte level = 1;
	
	ArrayList<Blast> energyBlasts = new ArrayList<>(); 
	int totBlasts = 0;
	
	public Phalanx(float xPos, float yPos, float width, float height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	public void move() {
		this.yPos += this.height;
	}
	
	public void energyBlast(float blastX, float blastY) {
		this.totBlasts++;
		energyBlasts.add(new Blast(blastX, blastY));
	}
	
	public void delBlast() {
		energyBlasts.remove(totBlasts-1);
		totBlasts--;
	}
	
	public boolean checkShipStatus(float blastX, float blastY) {
		if (blastY-Blast.height/2 <= this.yPos+this.height/2 && ((blastX+Blast.width/2 >= this.xPos-this.width/2 && blastX+Blast.width/2 <= this.xPos+this.width/2) || (blastX-Blast.width/2 >= this.xPos-this.width/2 && blastX-Blast.width/2 <= this.xPos+this.width/2)))
			return false;
		else
			return true;
	}
	
	public byte checkGameStatus(float canvasHeight, float guardianHeight) {
		if (this.yPos+this.height/2 > canvasHeight-guardianHeight)
			return 1;
		else
			return 0;
	}
}

class Blast {
	float xPos, yPos;
	static float width = 10, height = 10;
	
	static byte speed = 2;
	
	public Blast(float xPos, float yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public void phalanxBlastMove() {
		this.yPos += Blast.speed;
	}
	
	public void guardianBlastMove() {
		this.yPos -= Blast.speed;
	}
	
	public byte phalanxBlastGameStatus(float guardianX, float guardianY, float guardianWidth, float guardianHeight) {
		if (this.yPos+Blast.height/2 >= guardianY-guardianHeight/2 && ((this.xPos+Blast.width/2 >= guardianX-guardianWidth/2 && this.xPos+Blast.width/2 <= guardianX+guardianWidth/2) || (this.xPos-Blast.width/2 >= guardianX-guardianWidth/2 && this.xPos-Blast.width/2 <= guardianX+guardianWidth/2)))
			return 1;
		else
			return 0;
	}
}

class Guardian {
	float xPos, yPos;
	float width, height;
	
	ArrayList<Blast> energyBlasts = new ArrayList<>();
	int totBlasts = 0;
	
	public Guardian(float xPos, float yPos, float width, float height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	public void moveRight() {
		this.xPos += this.width;
	}
	
	public void moveLeft() {
		this.xPos -= this.width;
	}
	
	public void energyBlast(float blastX, float blastY) {
		this.totBlasts++;
		energyBlasts.add(new Blast(blastX, blastY));
	}
	
	public void delBlast() {
		energyBlasts.remove(totBlasts-1);
		totBlasts--;
	}
}