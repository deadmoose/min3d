package min3d.sampleProject1;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.vos.Light;

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
		 * Add a light to the Scene.
		 * The Scene must have light for Object3d's with normals  
		 * enabled (which is the default setting) to be visible.
		 */
		scene.lights().add( new Light() );
		
		/*
		 *  Create an Object3d and add it to the scene.
		 *  In this case, we're creating a cube using the Box class.
		 *  The Box class automatically adds texture coordinates, per-vertex colors, and vertex normals.
		 *  Since we're not assigning any texture, the object is of course textureless.
		 *  Since normals for Object3d's are on by default, per-vertex colors are not displayed.
		 *  The result is a cube whose faces are shaded in relation to the one light in the Scene.
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
