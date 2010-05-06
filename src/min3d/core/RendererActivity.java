package min3d.core;

import min3d.interfaces.ISceneController;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;


public class RendererActivity extends Activity implements ISceneController
{
	protected GLSurfaceView _glSurfaceView;
	protected Renderer _renderer;
	public Scene scene;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		scene = new Scene(this);
		_renderer = new Renderer(this, scene);
		scene.renderer(_renderer); // * Important - don't skip this step
		
		_glSurfaceView = new GLSurfaceView(this);
		
		// Uncomment for logging:
		// _glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);

		_glSurfaceView.setRenderer(_renderer);
		_glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		setContentView(_glSurfaceView);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		_glSurfaceView.onResume();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		_glSurfaceView.onPause();
	}

	/**
	 * Instantiation of Object3D's, setting their properties, and adding Object3D's 
	 * to the scene should be done here (This can be done at any point _after_ this 
	 * method is called as well, of course.)
	 */
	public void initScene()
	{
	}

	/**
	 * All manipulation of scene and Object3D instance properties should go here.
	 * Gets called on every frame, right before drawing.   
	 */
	public void updateScene()
	{
	}
	
}
