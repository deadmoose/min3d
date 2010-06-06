package min3d.vos;

import java.nio.FloatBuffer;

import min3d.Utils;
import min3d.interfaces.IDirtyParent;

/**
 * Consists of three main components - ambient color, diffuse color, and position.
 * 
 * Must be added to Scene to take effect.
 * Eg, "scene.lights().add(myLight);"  
 * 
 * WARNING:	Positional light type does not work in v1.5 emulator.
 * 			But is nevertheless the default type.
 */
public class Light extends AbstractDirtyManaged implements IDirtyParent
{
	private BooleanManaged _isVisible; 
	
	public Color4Managed ambient;
	public Color4Managed diffuse;
	public Color4Managed specular;
	public Color4Managed emissive;
	public Number3dManaged position;
	
	private LightType _type;
	private Number3dManaged _attenuation; // (using the 3 properties of N3D for the 3 attenuation properties)
	private FloatBuffer _positionTypeFloatBuffer;
	
	
	public Light()
	{
		super(null);
		
		 ambient = new Color4Managed(128,128,128, 255, this);
		 diffuse = new Color4Managed(255,255,255, 255, this);
		 specular = new Color4Managed(0,0,0,255, this);
		 emissive = new Color4Managed(0,0,0,255, this);
		 position = new Number3dManaged(0f, 0f, 5f, this);
		 _attenuation = new Number3dManaged(1f,0f,0f, this); // (OpenGL default attenuation values)
		 _isVisible = new BooleanManaged(true, this);
		 type(LightType.POSITIONAL);
		 
		 _positionTypeFloatBuffer = Utils.makeFloatBuffer4(0,0,0,0);
		 
		 setDirtyFlag();
	}

	//
	
	public boolean isVisible()
	{
		return _isVisible.get();
	}
	public void isVisible(Boolean $b)
	{
		_isVisible.set($b);
	}
	
	//
	
	public LightType type()
	{
		return _type;
	}
	public void type(LightType $type)
	{
		_type = $type;
		position.setDirtyFlag(); // .. because position and 'type' go together in OGL data structure
	}
	
	//
	
	public float attenuationConstant()
	{
		return _attenuation.getX();
	}
	public void attenuationConstant(float $normalizedValue)
	{
		_attenuation.setX($normalizedValue);
		setDirtyFlag();
	}
	public float attenuationLinear()
	{
		return _attenuation.getY();
	}
	public void attenuationLinear(float $normalizedValue)
	{
		_attenuation.setY($normalizedValue);
		setDirtyFlag();
	}
	public float attenuationQuadratic()
	{
		return _attenuation.getZ();
	}
	public void attenuationQuadratic(float $normalizedValue)
	{
		_attenuation.setZ($normalizedValue);
		setDirtyFlag();
	}
	public void attenuationSetAll(float $constant, float $linear, float $quadratic)
	{
		_attenuation.setAll($constant, $linear, $quadratic);
		setDirtyFlag();
	}

	//
	
	public void setAllDirty()
	{
		position.setDirtyFlag();
		ambient.setDirtyFlag();
		diffuse.setDirtyFlag();
		specular.setDirtyFlag();
		emissive.setDirtyFlag();
		_attenuation.setDirtyFlag();
		_isVisible.setDirtyFlag();
	}
	
	@Override
	public void onDirty()
	{
		setDirtyFlag();
	}
	
	/**
	 * Used by Renderer
	 */
	public Number3dManaged attenuation()
	{
		return _attenuation;
	}
	
	/**
	 * Used by Renderer
	 */
	public BooleanManaged isVisibleBm()
	{
		return _isVisible;
	}

	/**
	 * Used by Renderer
	 */
	public FloatBuffer positionTypeBuffer()
	{
		return _positionTypeFloatBuffer;
	}
	
	/**
	 * Used by Renderer
	 */
	public void commitPositionTypeBuffer()
	{
		// GL_POSITION takes 4 arguments, the first 3 being x/y/z position, 
		// and the 4th being what we're calling 'type' (positional or directional)
		
		_positionTypeFloatBuffer.position(0);
		_positionTypeFloatBuffer.put(position.getX());
		_positionTypeFloatBuffer.put(position.getY());
		_positionTypeFloatBuffer.put(position.getZ());
		_positionTypeFloatBuffer.put(_type.glValue());
		_positionTypeFloatBuffer.position(0);
	}

}
