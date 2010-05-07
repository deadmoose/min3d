package min3d.core;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.Utils;
import min3d.interfaces.IObject3dContainer;
import min3d.sampleProject1.R;
import min3d.vos.Color4;
import min3d.vos.Number3d;
import min3d.vos.RenderType;
import android.graphics.Bitmap;

/**
 * sadfsadfsadfsdflkjsdaf lbleat!
 * @author Lee
 *
 */
public class Object3d
{
	private String _name;
	
	private RenderType _renderType = RenderType.TRIANGLES;
	private int _renderTypeInt = GL10.GL_TRIANGLES;
	
	private boolean _isVisible = true;
	private boolean _useVertColors = true;
	private boolean _doubleSidedEnabled = false;
	private boolean _useTexture = true;
	private boolean _useNormals = true;
	
	private Number3d _position = new Number3d(0,0,0);
	private Number3d _rotation = new Number3d(0,0,0);
	private Number3d _scale = new Number3d(1,1,1);

	private Color4 _defaultColor = new Color4();
	
	private float _pointSize = 3;

	protected ArrayList<Object3d> _children;
	
	protected VertexInfo _verticies; 
	
	protected FacesBufferedList _faces;
	
	private Scene _scene;
	private IObject3dContainer _parent;

	private int _textureId;
	
	/**
	 * Maximum number of verticies and faces must be specified at instantiation (unfortunately).
	 */
	public Object3d(int $maxVerticies, int $maxFaces)
	{
		_verticies = new VertexInfo($maxVerticies);
		_faces = new FacesBufferedList($maxFaces);
	}
	
	/**
	 * Holds references to vertex position list, vertex u/v mappings list, vertex normals list, and vertex colors list
	 */
	public VertexInfo verticies()
	{
		return _verticies;
	}

	/**
	 * List of object's faces (ie, index buffer) 
	 */
	public FacesBufferedList faces()
	{
		return _faces;
	}
	
	/**
	 * Determines if object will be rendered.
	 * Default is true. 
	 */
	public boolean isVisible()
	{
		return _isVisible;
	}
	public void isVisible(Boolean $b)
	{
		_isVisible = $b;
	}
	
	/**
	 * Determines if backfaces will be rendered (ie, doublesided = true).
	 * Default is false.
	 */
	public boolean doubleSidedEnabled()
	{
		return _doubleSidedEnabled;
	}
	public void doubleSidedEnabled(boolean $b)
	{
		_doubleSidedEnabled = $b;
	}

	/**
	 * Determines if per-vertex colors will be using for rendering object.
	 * If false, defaultColor property will dictate object color.
	 * Default is true. 
	 */
	public boolean colorsEnabled()
	{
		return _useVertColors;
	}
	public void colorsEnabled(Boolean $b)
	{
		_useVertColors = $b;
	}

	/**
	 * Determines if texture (if one is initialized) will used for rendering object.
	 * Default is true.  
	 */
	public boolean textureEnabled()
	{
		return _useTexture;
	}
	public void textureEnabled(Boolean $b)
	{
		_useTexture = $b;
	}
	
	/**
	 * Determines if object will be rendered using vertex light normals.
	 * If false, no lighting is used on object for rendering.
	 * Default is true.
	 */
	public boolean normalsEnabled()
	{
		return _useNormals;
	}
	public void normalsEnabled(boolean $b)
	{
		_useNormals = $b;
	}
	
	/**
	 * Default is TRIANGLES
	 */
	public void renderType(RenderType $type)
	{
		_renderType = $type;
		
		switch (_renderType) 
		{
			case TRIANGLES:
				_renderTypeInt = GL10.GL_TRIANGLES;
				break;
			case POINTS:
				_renderTypeInt = GL10.GL_POINTS;
				break;
		}
	}
	public RenderType renderType()
	{
		return _renderType;
	}

	/**
	 * Convenience 'pass-thru' method  
	 */
	public Number3dBufferList points()
	{
		return _verticies.points();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public UvBufferList uvs()
	{
		return _verticies.uvs();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public Number3dBufferList normals()
	{
		return _verticies.normals();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public Color4BufferList colors()
	{
		return _verticies.colors();
	}
	

	/**
	 * Clear object in preparation for garbage collection.
	 */
	public void clear()
	{
		deleteTexture();
		
		this.verticies().points().clear();
		this.verticies().uvs().clear();
		this.verticies().normals().clear();
		this.verticies().colors().clear();
		
		if (this.parent() != null) this.parent().removeChild(this);
	}

	//

	/**
	 * Color used to render object, but only when colorsEnabled is false.
	 */
	public Color4 defaultColor()
	{
		return _defaultColor;
	}

	/**
	 * X/Y/Z position of object. 
	 */
	public Number3d position()
	{
		return _position;
	}
	
	/**
	 * X/Y/Z euler rotation of object, using Euler angles.
	 * Units should be in degrees, to match OpenGL usage. 
	 */
	public Number3d rotation()
	{
		return _rotation;
	}

	/**
	 * X/Y/Z scale of object.
	 */
	public Number3d scale()
	{
		return _scale;
	}
	
	/**
	 * Point size (applicable only when renderType is POINT) 
	 */
	public float pointSize()
	{
		return _pointSize; 
	}
	public void pointSize(float $n)
	{
		_pointSize = $n;
	}
	
	/**
	 * Apply texture to object. 
	 */
	public boolean initTexture(Bitmap $b)
	{
		_textureId = Shared.renderer().uploadTextureAndReturnId($b);
		return true;
	}
	
	public boolean initTextureUsingResourceId(int $resourceId)
	{
		Bitmap b = Utils.makeBitmapFromResourceId($resourceId);
		_textureId = Shared.renderer().uploadTextureAndReturnId(b);
		b.recycle();
		return true;
	}
	
	public void deleteTexture()
	{
		if (_textureId == 0) return;
		
		Shared.renderer().deleteTexture(_textureId);
		_textureId = 0;
	}

	public int textureId()
	{
		return _textureId;
	}
	
	public void textureId(int $id)
	{
		_textureId = $id;
	}
	
	/**
	 * Convenience property for debugging purposes 
	 */
	public String name()
	{
		return _name;
	}
	public void name(String $s)
	{
		_name = $s;
	}
	
	public IObject3dContainer parent()
	{
		return _parent;
	}
	
	void parent(IObject3dContainer $container) /*package-private*/
	{
		_parent = $container;
	}
	
	/**
	 * Called by Scene
	 */
	void scene(Scene $scene) /*package-private*/
	{
		_scene = $scene;
	}
	/**
	 * Called by DisplayObjectContainer
	 */
	Scene scene() /*package-private*/
	{
		return _scene;
	}
	
	/**
	 * Used by Renderer
	 */
	int renderTypeInt() /*package-private*/
	{
		return _renderTypeInt;
	}

	//
	
	/**
	 * Can be overridden to create custom draw routines on a per-object basis, 
	 * rather than using Renderer's built-in draw routine. 
	 * 
	 * If overridden, return true instead of false.
	 */
	public Boolean customRenderer(GL10 gl)
	{
		return false;
	}
}
