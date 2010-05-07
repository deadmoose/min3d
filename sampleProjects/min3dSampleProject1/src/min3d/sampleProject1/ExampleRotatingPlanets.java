package min3d.sampleProject1;

import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Sphere;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * This is the "demo" example.
 * It shows how to add children to Object3dContainers.  
 * 
 * If you're familiar with Flash, this similar to using DisplayObjects in the Flash API.
 * 
 * If you're familiar with Papervision3D or Away3D for Flash, this is similar to using 
 * DisplayObject3D's or Object3D's within those respective libraries.
 * 
 * @author Lee
 */
public class ExampleRotatingPlanets extends RendererActivity
{
	Object3dContainer _jupiter;
	Object3dContainer _earth;
	Object3dContainer _moon;
	int _count = 0;
	
	public void initScene() 
	{
		Log.i("x", "initScene");
		scene.light().ambient.setAll((short)32, (short)32, (short)32, (short)255);
		scene.light().position.setAll(3, 3, 3);
		
		// Add Jupiter to scene
		_jupiter = new Sphere(0.8f, 15, 10);
		scene.addChild(_jupiter);

		Bitmap b = Utils.makeBitmapFromResourceId(this, R.drawable.jupiter);
		_jupiter.initTexture(b);
		b.recycle();

		// Add Earth as a child of Jupiter
		_earth = new Sphere(0.4f, 12, 9);
		_earth.position().x = 1.6f;
		_earth.rotation().x = 23;
		_jupiter.addChild(_earth);
 
		b = Utils.makeBitmapFromResourceId(this, R.drawable.earth);
		_earth.initTexture(b);
		b.recycle();
			
		// Add the Moon as a child of Earth
		_moon = new Sphere(0.2f, 10, 8);
		_moon.position().x = 0.6f;
		_earth.addChild(_moon);

		b = Utils.makeBitmapFromResourceId(this, R.drawable.moon);
		_moon.initTexture(b);
		b.recycle();
	}
	
	@Override 
	public void updateScene() 
	{
		// Spin spheres
		_jupiter.rotation().y += 1.0f;
		_earth.rotation().y += 3.0f;
		_moon.rotation().y -= 12.0f;
		
		// Wobble Jupiter a little just for fun 
		_count++;
		float mag = (float)(Math.sin(_count*0.2*Utils.DEG)) * 15;
		_jupiter.rotation().z = (float)Math.sin(_count*.33*Utils.DEG) * mag;
		
		// Move camera around
		scene.camera().position.z = 4.5f + (float)Math.sin(_jupiter.rotation().y * Utils.DEG);
		scene.camera().target.x = (float)Math.sin((_jupiter.rotation().y + 90) * Utils.DEG) * 0.8f;
	}
}
