package min3d.sampleProject1;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;

/**
 * @author Lee
 */
public class ExampleVertexColors extends RendererActivity
{
	Object3dContainer _cube;
	
	public void initScene() 
	{
		/**
		 * Remember, the Box class automatically adds vertex colors (a different color for each side).
		 * Set normalsEnabled to false or else vertex colors will not be displayed.
		 */
		_cube = new Box(1,1,1);
		_cube.normalsEnabled(false);
//		_cube.colorsEnabled(true); // (colorsEnabled is true by default)
		
		scene.addChild(_cube);
	}
	
	@Override 
	public void updateScene() 
	{
		_cube.rotation().y +=1;
		_cube.rotation().z += 0.2f;
	}
}
