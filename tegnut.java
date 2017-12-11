import java.util.*;
import javax.swing.*;
import java.awt.*;
/**
* klasse for å tegne et punktsett med n punkter (n < 200)  og
* den konvekse innhyllinga
******************************************************************************/
class TegnUt extends JFrame{
	Oblig3 d;
	IntList theCoHull;

	TegnUt(Oblig3 d, IntList CoHull ){
		  theCoHull = CoHull;
		  this.d =d;
		  size = 500;
		  margin = 50;
	      scale =size/d.MAX_X;
		  setTitle("Oblig3Demo, num points:"+d.n);
		  grafen = new Graph();
		  getContentPane().add(grafen, BorderLayout.CENTER);
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  pack();
		  setVisible(true);
		// angir foretrukket størrelse på dette lerretet.
		setPreferredSize(new Dimension(d.MAX_X+2*margin,d.MAX_Y+2*margin));
	}

	Graph grafen;
	int size , margin;
	double scale ;

    class Graph extends JPanel{

	    void drawPoint(int p, Graphics g) {
			     int SIZE =(int) (7);
			     g.drawString(p+"",xDraw(d.x[p]),yDraw(d.y[p]));
				 g.drawOval (xDraw(d.x[p]),yDraw(d.y[p]),SIZE,SIZE);
				 g.fillOval (xDraw(d.x[p]),yDraw(d.y[p]),SIZE,SIZE);
	     }

		 Graph() {
			 setPreferredSize(new Dimension(size+2*margin+10,size+2*margin+10));
		 }

		 int  xDraw(int x){return (int)(x*scale-2)+margin;}
		 int  yDraw(int y){return (int)((d.MAX_Y-y +2)*scale);}

		 public void paintComponent(Graphics g) {
			super.paintComponent(g);
			 g.setColor(Color.black);
			 for (int i = 0; i < d.n; i++){
			    drawPoint(i,g);
		     }
			  g.setColor(Color.red);
			 // draw cohull
			 int x2 = d.x[theCoHull.get(0)], y2 = d.y[theCoHull.get(0)],x1,y1;
			 for (int i = 1; i < theCoHull.size(); i++){
				 y1 = y2; x1=x2;
				 x2 = d.x[theCoHull.get(i)];
				 y2 = d.y[theCoHull.get(i)];
		         g.drawLine (xDraw(x1),yDraw(y1), xDraw(x2),yDraw(y2));
			 }

			  g.drawLine (xDraw(d.x[theCoHull.get(theCoHull.size()-1)]),
			              yDraw(d.y[theCoHull.get(theCoHull.size()-1)]),
		                  xDraw(d.x[theCoHull.get(0)]),yDraw(d.y[theCoHull.get(0)]));
		  } // end paintComponent
	}  // end class Graph
}// end class DT
