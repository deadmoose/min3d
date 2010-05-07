package min3d.sampleProject1;

import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Sphere;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Most minimal example I could think of.
 * 
 * @author Lee
 */
public class ExampleMostMinimal extends RendererActivity
{
	Object3dContainer _cube;
	
	public void initScene() 
	{
		/*
		 *  Create an Object3d and add it to the scene.
		 *  In this case, we're creating a cube using the Box class.
		 *  The Box class automatically adds texture coordinates, per-vertex colors, and vertex normals.
		 *  Since we're not assigning any texture, the object is of course bare.
		 *  Since lighting for Object3d's is on by default, per-vertex colors are not displayed.
		 *  The result is a cube whose faces are shaded in relation to the scene's default light properties.
		 */
		_cube = new Box(1,1,1);
		
		/*
		 * Add cube to the scene.
		 */
		scene.addChild(_cube);
	}

	@Override 
	public void updateScene() 
	{
		/*
		 * Do any manipulation of scene properties or to objects in the scene here.
		 */
		_cube.rotation().y++;
	}
}
