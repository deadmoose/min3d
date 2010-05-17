package min3d.core;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import min3d.interfaces.IObject3dContainer;
import min3d.vos.Color4;
import min3d.vos.Number3d;
import min3d.vos.RenderType;
import android.util.Log;

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
	private boolean _useTextures = true;
	private boolean _useNormals = true;
	private boolean _ignoreFaces = false;

	private Number3d _position = new Number3d(0,0,0);
	private Number3d _rotation = new Number3d(0,0,0);
	private Number3d _scale = new Number3d(1,1,1);

	private Color4 _defaultColor = new Color4();
	
	private float _pointSize = 3f;
	private boolean _pointSmoothing = true;
	private float _lineWidth = 1f;
	private boolean _lineSmoothing = false;

	protected ArrayList<Object3d> _children;
	
	protected MeshData _vertices; 
	
	protected FacesBufferedList _faces;
	
	private Scene _scene;
	private IObject3dContainer _parent;

	
	private TextureList _textures;
	
	/**
	 * Maximum number of vertices and faces must be specified at instantiation (unfortunately).
	 */
	public Object3d(int $maxVertices, int $maxFaces)
	{
		_vertices = new MeshData($maxVertices);
		_faces = new FacesBufferedList($maxFaces);
		_textures = new TextureList();
	}
	
	/**
	 * Holds references to vertex position list, vertex u/v mappings list, vertex normals list, and vertex colors list
	 */
	public MeshData meshData()
	{
		return _vertices;
	}

	/**
	 * List of object's faces (ie, index buffer) 
	 */
	public FacesBufferedList faces()
	{
		return _faces;
	}
	
	public TextureList textures()
	{
		return _textures;
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
	 * Determines if textures (if any) will used for rendering object.
	 * Default is true.  
	 */
	public boolean texturesEnabled()
	{
		return _useTextures;
	}
	public void texturesEnabled(Boolean $b)
	{
		_useTextures = $b;
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
	 * When true, Renderer draws using vertex points list, rather than faces list.
	 * (ie, using glDrawArrays instead of glDrawElements) 
	 * Default is false.
	 */
	public boolean ignoreFaces()
	{
		return _ignoreFaces;
	}
	public void ignoreFaces(boolean $b)
	{
		_ignoreFaces = $b;
	}	
	
	/**
	 * Options are: TRIANGLES, LINES, and POINTS
	 * Default is TRIANGLES.
	 */
	public RenderType renderType()
	{
		return _renderType;
	}
	public void renderType(RenderType $type)
	{
		_renderType = $type;
		_renderTypeInt = renderTypeToInt(_renderType);
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public Number3dBufferList points()
	{
		return _vertices.points();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public UvBufferList uvs()
	{
		return _vertices.uvs();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public Number3dBufferList normals()
	{
		return _vertices.normals();
	}
	
	/**
	 * Convenience 'pass-thru' method  
	 */
	public Color4BufferList colors()
	{
		return _vertices.colors();
	}


	/**
	 * Clear object for garbage collection.
	 */
	public void clear()
	{
		this.meshData().points().clear();
		this.meshData().uvs().clear();
		this.meshData().normals().clear();
		this.meshData().colors().clear();
		_textures.clear();
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
	 * Point size (applicable when renderType is POINT)
	 * Default is 3. 
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
	 * Point smoothing (anti-aliasing), applicable when renderType is POINT.
	 * When true, points look like circles rather than squares.
	 * Default is true.
	 */
	public boolean pointSmoothing()
	{
		return _pointSmoothing;
	}
	public void pointSmoothing(boolean $b)
	{
		_pointSmoothing = $b;
	}

	/**
	 * Line width (applicable when renderType is LINE)
	 * Default is 1. 
	 * 
	 * Remember that maximum line width is OpenGL-implementation specific, and varies depending 
	 * on whether lineSmoothing is enabled or not. Eg, on Nexus One,  lineWidth can range from
	 * 1 to 8 without smoothing, and can only be 1f with smoothing. 
	 */
	public float lineWidth()
	{
		return _lineWidth;
	}
	public void lineWidth(float $n)
	{
		_lineWidth = $n;
	}
	
	/**
	 * Line smoothing (anti-aliasing), applicable when renderType is LINE
	 * Default is false.
	 */
	public boolean lineSmoothing()
	{
		return _lineSmoothing;
	}
	public void lineSmoothing(boolean $b)
	{
		_lineSmoothing = $b;
	}
	
	/**
	 * Convenience property 
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
	
	//
	
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
	 * Called by Renderer
	 */
	int renderTypeInt() /*package-private*/
	{
		return _renderTypeInt;
	}

	/**
	 * Called by Renderer 
	 */
	static int renderTypeToInt(RenderType $rt) /* package-private */
	{
		int i = 0;
		
		switch ($rt) 
		{
			case TRIANGLES:
				i = GL10.GL_TRIANGLES;
				break;
			case POINTS:
				i = GL10.GL_POINTS;
				break;
			case LINES:
				i = GL10.GL_LINES;
				break;
		}
		
		return i;
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
