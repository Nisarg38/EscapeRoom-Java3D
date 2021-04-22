package main;

import java.awt.Font;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

public class RenderText {
	
	/*
	 * Created and renders text onto screen
	 */
	public static TransformGroup letters3D(String textString, double s, Color3f clr) {	
		
		Font3D font3D = new Font3D(new Font("Arial", Font.PLAIN, 1), new FontExtrusion());	//Extrude 2D to make it 3D
		Appearance app = new Appearance();													
		
		//Increasing scalar and decreasing font makes words appear thicker
		Text3D textGeom = new Text3D(font3D, textString, new Point3f(0,0,5), Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT);		
		Shape3D text = new Shape3D(textGeom);		//create object to be rendered
		
		///Set material of text to the colour
		///app.setMaterial(setMaterial());
		Transform3D scaler = new Transform3D();
		scaler.setScale(s);
		
		TransformGroup scene_TG = new TransformGroup(scaler);
		text.setAppearance(app);
		scene_TG.addChild(text);   //render text
		
		return scene_TG;
		
	}
	
}