package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;





public class Instructions extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;
	
	public static void addLights(BranchGroup sceneBG, Color3f clr) {		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		AmbientLight amLgt = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		amLgt.setInfluencingBounds(bounds);
		sceneBG.addChild(amLgt);
		Point3f pt  = new Point3f(2.0f, 2.0f, 2.0f);
		Point3f atn = new Point3f(1.0f, 0.0f, 0.0f);
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		sceneBG.addChild(ptLight);	
		
		Background bg = new Background();	
		bg.setImage(new TextureLoader("images/backgroundLight.jpg",null).getImage());		
    	bg.setImageScaleMode(Background.SCALE_FIT_ALL);							
    	bg.setApplicationBounds(bounds);
    	bg.setColor(clr);	

    	
    	sceneBG.addChild(bg);
	}
	
	public static Appearance createAppearance() {
		Appearance Appear = new Appearance();
		
 		TexCoordGeneration tcg = new TexCoordGeneration();
		tcg.setEnable(false);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		PolygonAttributes polyAttrib = new PolygonAttributes();
		polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
		Appear.setPolygonAttributes(polyAttrib);
		
		return Appear;
	}

	
	private static TransformGroup txt1(String txt, float scl, Color3f clr, Point3f p) {
    	Font my2DFont = new Font("New Tegomin", Font.PLAIN, 1);
    	FontExtrusion myExtrude = new FontExtrusion();
    	Font3D font3D = new Font3D(my2DFont, myExtrude);
    	Text3D text3D = new Text3D(font3D,txt,p);
  
        Transform3D scalar = new Transform3D();
        scalar.setScale(scl);
        Appearance look = new Appearance();
        look.setMaterial(new Material(clr, clr, clr, clr, 1));
        TransformGroup text = new TransformGroup(scalar);
        Shape3D s2 = new Shape3D(text3D, look);
        text.addChild(s2);
        
        return text;
    }
	
	private static TransformGroup panel(Point3f[] verts, Color3f clr) {
    	// Base Quadarray for the front side 
    	QuadArray sqr = new QuadArray(4, GeometryArray.NORMALS| GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    	sqr.setCoordinates(0, verts);
    	// 1/sqrt(3) as there are 3 normals 
    	float normal = (float)(1/Math.sqrt(3));
    	float [] f1 = {normal,normal,normal};
 
    	for(int i =0; i<4;i++) {
    		sqr.setColor(i, clr);
    		sqr.setNormal(i, f1);
    	}
    	
    	Shape3D FrontF = new Shape3D(sqr);

    	TransformGroup tg = new TransformGroup();
    	tg.addChild(FrontF);
    	Transform3D scence = new Transform3D();
    	TransformGroup stg = null;
    	scence.setTranslation(new Vector3f(0,0,0.4f));
    	stg = new TransformGroup(scence);
    	
    	tg.addChild(stg);
    	return tg;
    	
    }
	
	
	/*
	//
	Use this TransformGroup to add back panel and text 
	//
	*/
	public static TransformGroup PaintingPuzzleInstruction() {
    	
		Transform3D t3d = new Transform3D();
		t3d.rotY(Math.PI); 
    	TransformGroup stg = new TransformGroup(t3d);
    	//Writing Instruction 
    	// With txt() we are inputing the text string, scale of the text, color of the text,
    	// Point3f of the text and "no-effect" is for mouse click action, to identify the shape
    	Point3f [] verts = {  new Point3f(-3f,2f,2.5f), new Point3f(-3f,-1f,2.5f), new Point3f(3f,-1f,2.5f),new Point3f(3f,2f,2.5f)};
		stg.addChild(panel(verts,Commons.None));
    	Point3f b1 = new Point3f((float) -9.5,(float) 5.5,8.5f);
    	stg.addChild(txt1("Instruction to solve Painting Puzzle",0.3f,Commons.White,b1));
    	Point3f b2 = new Point3f((float) -14.5,(float) 6,12.5f);
    	stg.addChild(txt1("- You will have to make a single painting using the pieces",0.2f,Commons.White,b2));
    	Point3f b3 = new Point3f((float) -14.5,(float) 4.5,12.5f);
    	stg.addChild(txt1("- When you click on the single piece it will rotate in a specifc ",0.2f,Commons.White,b3));
    	Point3f b4 = new Point3f((float) -14.5,(float) 3,12.5f);
    	stg.addChild(txt1("  direction ",0.2f,Commons.White,b4));
    	Point3f b5 = new Point3f((float) -14.5,(float) 1.5,12.5f);
    	stg.addChild(txt1("- Keep on clicking the piece until you convert it into a painting",0.2f,Commons.White,b5));
    	
    	
    	
    	return stg;
    }

	public static TransformGroup WheelPuzzleInstruction() {
    	
    	TransformGroup stg = new TransformGroup();
    	//Writing Instruction 
    	// With txt() we are inputing the text string, scale of the text, color of the text,
    	// Point3f of the text and "no-effect" is for mouse click action, to identify the shape
    	Point3f [] verts = {  new Point3f(-3f,2f,0f), new Point3f(-3f,-1f,0f), new Point3f(3f,-1f,0f),new Point3f(3f,2f,0f)};
		stg.addChild(panel(verts,Commons.None));
    	Point3f b1 = new Point3f((float) -9.5,(float) 5.5,0);
    	stg.addChild(txt1("Instruction to solve Wheel Puzzle",0.3f,Commons.White,b1));
    	Point3f b2 = new Point3f((float) -14.5,(float) 6,0);
    	stg.addChild(txt1("- The dial in the wheel puzzle is constantly in rotation",0.2f,Commons.White,b2));
    	Point3f b3 = new Point3f((float) -14.5,(float) 4.5,0);
    	stg.addChild(txt1("- You will have to interact with the dial with the 'z' key on the ",0.2f,Commons.White,b3));
    	Point3f b4 = new Point3f((float) -14.5,(float) 3,0);
    	stg.addChild(txt1("  keyboard",0.2f,Commons.White,b4));
    	Point3f b5 = new Point3f((float) -14.5,(float) 1.5,0);
    	stg.addChild(txt1("- When you press the 'z' Key the dial will stop its motion",0.2f,Commons.White,b5));
    	Point3f b6 = new Point3f((float) -14.5,(float) 0,0);
    	stg.addChild(txt1("- The goal is to stop the dial's motion in the given collision zone",0.2f,Commons.White,b6));
    	
    	return stg;
    }
	
	public static TransformGroup CubePuzzleInstruction() {
    	
    	TransformGroup stg = new TransformGroup();
    	//Writing Instruction 
    	// With txt() we are inputing the text string, scale of the text, color of the text,
    	// Point3f of the text and "no-effect" is for mouse click action, to identify the shape
    	Point3f [] verts = {  new Point3f(-3f,2f,-2.5f), new Point3f(-3f,-1f,-2.5f), new Point3f(3f,-1f,-2.5f),new Point3f(3f,2f,-2.5f)};
		stg.addChild(panel(verts,Commons.None));
    	Point3f b1 = new Point3f((float) -9.5,(float) 5.5,-8.5f);
    	stg.addChild(txt1("Instruction to solve Cube Puzzle",0.3f,Commons.White,b1));
    	Point3f b2 = new Point3f((float) -14.5,(float) 6,-12.5f);
    	stg.addChild(txt1("- You are able to cycle through cube's color faces using key 'Q' ",0.2f,Commons.White,b2));
    	Point3f b3 = new Point3f((float) -14.5,(float) 4.5,-12.5f);
    	stg.addChild(txt1("  and 'E'",0.2f,Commons.White,b3));
    	Point3f b4 = new Point3f((float) -14.5,(float) 3,-12.5f);
    	stg.addChild(txt1("- With the goal of matching the coloured faces using combination ",0.2f,Commons.White,b4));
    	Point3f b5 = new Point3f((float) -14.5,(float) 1.5,-12.5f);
    	stg.addChild(txt1("  shown and pressing 'Enter' to lock in the combination",0.2f,Commons.White,b5));
    	Point3f b6 = new Point3f((float) -14.5,(float) 0,-12.5f);
    	stg.addChild(txt1("- Use 'R' key to reset after after a successful or failed combination ",0.2f,Commons.White,b6));
    	Point3f b7 = new Point3f((float) -14.5,(float) -1.5,-12.5f);
    	stg.addChild(txt1("  attempt",0.2f,Commons.White,b7));
    	
    	return stg;
    }
	
	
	public static BranchGroup createScene() {
		BranchGroup scene = new BranchGroup(); 
		               
		// Calling the function to check its functionality
		scene.addChild(PaintingPuzzleInstruction());
		//scene.addChild(WheelPuzzleInstruction());
		//scene.addChild(CubePuzzleInstruction());

		addLights(scene,Commons.White);
		scene.compile(); // optimize scene BG

		return scene;
	}
	
	public Instructions(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);                        

		SimpleUniverse su = new SimpleUniverse(canvas);       // create a SimpleUniverse
		Commons.setEye(new Point3d(0, 0, 10.0));
		Commons.defineViewer(su);                           // set the viewer's location
	
		
		sceneBG.compile();
		su.addBranchGraph(sceneBG);                           // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas);		
		frame.setSize(1000, 700);                              // set the size of the JFrame
		frame.setVisible(true);
	}

	/* the main entrance of the application */
	public static void main(String[] args) {
		frame = new JFrame("XY's Assignment 4");
		frame.getContentPane().add(new Instructions(createScene())); 
	}
	

}