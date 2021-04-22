package main;

import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PointLight;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;


public class AppearanceExtra {
	
	//Function to add a point and ambient light 
	public static void addLights (TransformGroup b) {
		
		//Ambient light of color 0.2f, 0.2f, 0.2f
		AmbientLight light = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		light.setInfluencingBounds(bounds);
		b.addChild(light);
		
		//Point light(white) at position (2, 2, 2)
		Point3f pt = new Point3f(2, 2, 2);
		Point3f atn = new Point3f(1, 0, 0);
		Color3f clr = Commons.White;
		
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		b.addChild(ptLight);	
	
	}
	
	public static void addptLights (TransformGroup b, Color3f clr) {
		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		Point3f pt = new Point3f(-1, 2, 5);
		Point3f atn = new Point3f(1, 0, 0);
		
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		b.addChild(ptLight);
		
	}
	
	public static Appearance setApp (Color3f clr) {
		
		Appearance app = new Appearance();
		app.setMaterial(setMaterial(clr));

		ColoringAttributes colorAtt = new ColoringAttributes();
		colorAtt.setColor(clr);
		
		app.setColoringAttributes(colorAtt);
		
		return app;
		
	}

	public static Material setMaterial (Color3f clr) {
		
		Material mat= new Material();
		
		int SH = 10;
		
		mat.setAmbientColor(new Color3f(0.6f, 0.6f, 0.6f));
		mat.setEmissiveColor(new Color3f(0.0f, 0.0f, 0.0f));
		mat.setDiffuseColor(new Color3f(clr));
		mat.setSpecularColor(1.0f, 1.0f, 1.0f);
		mat.setShininess(SH);
		mat.setLightingEnable(true);
		
		return mat;
		
	}
	
	public static Texture texturedApp (String name) {
		
		String filename = "images/" +name+ ".jpg";
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage();
		
		if(image == null)
			System.out.println("File not found");
		
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		
		return texture;

	}
	
	public static Background createBackground (String name) {
		
		Background bg = new Background();
		String filename = "images/" + name;
		
		bg.setImage(new TextureLoader(filename, null).getImage());
		bg.setImageScaleMode(Background.SCALE_FIT_MAX);
		bg.setApplicationBounds(new BoundingSphere());
		bg.setColor(Commons.White);
		
		return bg;	
	}

}