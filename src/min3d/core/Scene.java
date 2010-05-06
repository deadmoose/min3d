package min3d.core;

import java.util.ArrayList;

import min3d.interfaces.IObject3dContainer;
import min3d.interfaces.ISceneController;
import min3d.vos.CameraVo;
import min3d.vos.Color4Managed;
import min3d.vos.Light;


public class Scene implements IObject3dContainer
{
	private ArrayList<Object3d> _children = new ArrayList<Object3d>();

	private Light _light = new Light();
	private CameraVo _camera = new CameraVo();
	
	private Color4Managed _backgroundColor = new Color4Managed(0,0,0,255);
	private boolean _lightingEnabled = true;

	private ISceneController _sceneController;
	private Renderer _renderer;
	

	public Scene(ISceneController $sceneObjectUpdater) 
	{
		_sceneController = $sceneObjectUpdater;
	}

	//
	
	/**
	 * Adds Object3d to Scene. Object3d's must be added to Scene in order to be rendered
	 * Returns always true. 
	 */
	@Override
	public void addChild(Object3d $o)
	{
		_children.add($o);
		
		$o.parent(this);
		$o.scene(this);
		$o.renderer(_renderer);
	}
	
	@Override
	public void addChildAt(Object3d $o, int $index)
	{
		_children.add($index, $o);
	}
	
	/**
	 * Removes Object3d from Scene.
	 * Returns false if unsuccessful
	 */
	@Override
	public boolean removeChild(Object3d $o)
	{
		$o.parent(null);
		$o.scene(null);
		$o.renderer(null);
		return _children.remove($o);
	}
	
	@Override
	public Object3d removeChildAt(int $index)
	{
		Object3d o = _children.remove($index);
		
		if (o != null) {
			o.parent(null);
			o.scene(null);
			o.renderer(null);
		}
		return o;
	}
	
	@Override
	public Object3d getChildAt(int $index)
	{
		return _children.get($index);
	}
	
	/**
	 * TODO: Use better lookup 
	 */
	@Override
	public Object3d getChildByName(String $name)
	{
		for (int i = 0; i < _children.size(); i++)
		{
			if (_children.get(0).name() == $name) return _children.get(0); 
		}
		return null;
	}
	
	@Override
	public int getChildIndexOf(Object3d $o)
	{
		return _children.indexOf($o);
	}
	
	@Override
	public int numChildren()
	{
		return _children.size();
	}

	/**
	 * Scene's camera
	 */
	public CameraVo camera()
	{
		return _camera;
	}
	public void camera(CameraVo $camera)
	{
		_camera = $camera;
	}
	
	/**
	 * Scene instance's background color
	 */
	public Color4Managed backgroundColor()
	{
		return _backgroundColor;
	}

	/**
	 * Scene instance's Light, holding light settings (just the one, for now) 
	 */
	public Light light()
	{
		return _light;
	}

	/**
	 * Determines if lighting is enabled for the scene. 
	 */
	public boolean lightingEnabled()
	{
		return _lightingEnabled;
	}
	
	public void lightingEnabled(Boolean $b)
	{
		_lightingEnabled = $b;
	}
	
	//

	/**
	 * Called by Object3d instances
	 */
	Renderer renderer() /*package-private*/
	{
		return _renderer;
	}
	/**
	 * Called by Renderer instance right after its instantiation
	 */
	void renderer(Renderer $ren) /*package-private*/
	{
		_renderer = $ren;
		_renderer.setSceneController(_sceneController);
	}
	
	/**
	 * Called by Renderer instance
	 */
	void initObjects() /*package-private*/ 
	{
		_sceneController.initScene();
	}
	
	/**
	 * Called by Renderer instance
	 */
	ArrayList<Object3d> children() /*package-private*/ 
	{
		return _children;
	}
}
