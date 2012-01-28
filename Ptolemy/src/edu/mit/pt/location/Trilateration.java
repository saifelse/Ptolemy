package edu.mit.pt.location;

import com.google.android.maps.GeoPoint;


// FIXME: k=1 degenerates. 
// Math from: http://paulbourke.net/geometry/2circle/
// Consider: http://en.wikipedia.org/wiki/Conic_section#Intersecting_two_conics
// 


public class Trilateration {
	private Trilateration(){	
	}
	public static void main(String arg[]){
		//Point a = new Point(0.0,0.0);
		//Point b = new Point(10.0,0.0);
		//Point c = new Point(5.0, 5.0*Math.sqrt(2));

		//Triangulation.linearTriangulate(a, b, c, 1, .9, .8);
		
		Point a = new Point(0.0, 0.0); double sa = 3.5;
		Point b = new Point(0.0, 5.0); double sb = 5.0;
		Point c = new Point(5.0, 0.0); double sc = 3.0;
		
		Trilateration.linearTrilaterate(a, b, c, sa, sb, sc);
	}
	public GeoPoint[] trilaterate(GeoPoint a, GeoPoint b, GeoPoint c, double dbmA, double dbmB, double dbmC){
		Point cA = geoPointToCartesianPoint(a);
		Point cB = geoPointToCartesianPoint(b);
		Point cC = geoPointToCartesianPoint(c);
		
		double sA = linearizeDbm(dbmA);
		double sB = linearizeDbm(dbmB);
		double sC = linearizeDbm(dbmC);
		
		Point[] i = linearTrilaterate(cA, cB, cC, sA, sB, sC);
		GeoPoint[] j = new GeoPoint[i.length];
		for(int k=0;k<i.length;k++){
			j[k] = cartesianPointToGeoPoint(i[k]);
		}
		return j;
		
	}
	public static Point[] linearTrilaterate(Point cA, Point cB, Point cC, double sA, double sB, double sC){
		Circle ab = bilaterate(cA, sA, cB, sB);
		Circle bc = bilaterate(cB, sB, cC, sC);
		Circle ca = bilaterate(cC, sC, cA, sA);
		
		System.out.println(ab);
		System.out.println(bc);
		System.out.println(ca);
		
		Point[] i1 = intersect(ab, bc);
		Point[] i2 = intersect(bc, ca);
		Point[] i3 = intersect(ca, ab);
		
		Point[] i = new Point[]{i1[0], i1[1], i2[0], i2[1], i3[0], i3[1]};
		for(Point p : i){
			System.out.println(p);
		}
		return i;
	}
	private static Point[] intersect(Circle a, Circle b){
		double d = distance(a.p, b.p);
		double z = (a.r*a.r-b.r*b.r+d*d)/(2*d);
		double h = Math.sqrt(a.r*a.r-z*z);
		Point c = new Point(a.p.x + z*(b.p.x-a.p.x)/d, a.p.y + z*(b.p.y-a.p.y)/d);
		
		double x = c.x + h * (b.p.y - a.p.y)/d;
		double y = c.y + h * (b.p.x - a.p.x)/d;
		
		Point i = new Point(c.x + h * (b.p.y - a.p.y)/d, c.y - h * (b.p.x - a.p.x)/d);
		Point j = new Point(c.x - h * (b.p.y - a.p.y)/d, c.y + h * (b.p.x - a.p.x)/d);
		
		return new Point[]{i,j};
	}
	
	private static Circle bilaterate(Point a, double sA, Point b, double sB){
		double k = sA/sB;
		double x = (a.x-k*k*b.x)/(1-k*k);
		double y = (a.y-k*k*b.y)/(1-k*k);
		double r = Math.sqrt(x*x + (k*k*b.x*b.x-a.x*a.x)/(1-k*k) + 
				  			y*y + (k*k*b.y*b.y-a.y*a.y)/(1-k*k));
		return new Circle(r, new Point(x,y));
	}
	private static double distance(Point a, Point b){
		double dx = a.x-b.x;
		double dy = a.y-b.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	// TODO implement.
	private static double linearizeDbm(double dbm){
		return 0.0;
	}
	// TODO implement
	private static Point geoPointToCartesianPoint(GeoPoint a){
		return null;
	}
	// TODO implement
	private static GeoPoint cartesianPointToGeoPoint(Point a){
		return null;
	}
	
	private static class Circle {
		public double r;
		public Point p;
		public Circle(double r, Point p){
			this.r = r;
			this.p = p;
		}
		@Override
		public String toString(){
			return "Circle(r="+r+","+p+")";
		}
	}
	
	private static class Point {
		public double x;
		public double y;
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
		@Override
		public String toString(){
			return "Point("+x+","+y+")";
		}
	}
}
