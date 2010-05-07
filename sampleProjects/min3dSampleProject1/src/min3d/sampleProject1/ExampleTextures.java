package min3d.sampleProject1;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;

/**
 * @author Lee
 */
public class ExampleTextures extends RendererActivity
{
	Object3dContainer _cube;
	
	public void initScene() 
	{
		/*
		 * Example of adding a texture to an Object3d by simply passing a (drawable) resource id.
		 * 
		 * initTextureUsingResourceId() creates a Bitmap using the application's embedded resource, 
		 * 'uploads' it to the GPU, and then promptly disposes of it.   
		 */
	
		_cube = new Box(1.5f,1.5f,1.5f);
		_cube.initTextureUsingResourceId(R.drawable.uglysquares);
		scene.addChild(_cube);
		
		/*
		 * A more conventional approach might be to pass a Bitmap using "initTexture(bitmap)"
		 * Be sure to call "bitmap.dispose()" after init'ing the texture, whenever that's appropriate,
		 * to free up system memory.  
		 */
	}
	
	@Override 
	public void updateScene() 
	{
		_cube.rotation().y +=1;
		_cube.rotation().z += 0.2f;
	}
}
