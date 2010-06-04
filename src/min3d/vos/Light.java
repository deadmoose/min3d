package min3d.vos;

import java.nio.FloatBuffer;

import android.util.Log;

import min3d.Utils;

/**
 * Consists of three main components - ambient color, diffuse color, and position.
 * 
 * Must be added to Scene to take effect.
 * Eg, "scene.lights().add(myLight);"  
 * 
 * WARNING:	Positional light type does not work in v1.5 emulator.
 * 			But is nevertheless the default type.
 */
public class Light
{
	public Color4Managed ambient;
	public Color4Managed diffuse;
	public Number3dManaged position;

	public enum Type {
		POSITIONAL, 
		DIRECTIONAL
	}
	
	private boolean _isVisible; 
	private boolean _isVisibleDirty;
	
	private Type _type;
	
	private FloatBuffer _ambientBuffer; 
	private FloatBuffer _diffuseBuffer;
	private FloatBuffer _positionBuffer;

	
	public Light()
	{
		 ambient = new Color4Managed(128,128,128, 255);
		 diffuse = new Color4Managed(255,255,255, 255);
		 position = new Number3dManaged(0f, 0f, 5f);	
		 type(Type.POSITIONAL);
		 isVisible(true);
	}

	public boolean isVisible()
	{
		return _isVisible;
	}
	public void isVisible(Boolean $b)
	{
		_isVisible = $b;
		_isVisibleDirty = true;
	}
	
	public Type type()
	{
		return _type;
	}
	public void type(Type $type)
	{
		_type = $type;
		
		position.setDirtyFlag(); 
		// .. because position and 'type' go together in OGL data structure
	}

	// Used by Renderer
	public boolean isVisibleDirty()
	{
		return _isVisibleDirty;
	}
	
	// Used by Renderer
	public void clearIsVisibleDirtyFlag()
	{
		_isVisibleDirty = false; 
	}
	// Used by Renderer
	public void setIsVisibleDirtyFlag()
	{
		_isVisibleDirty = true; 
	}
	
	public void setAllDirty()
	{
		position.setDirtyFlag();
		ambient.setDirtyFlag();
		diffuse.setDirtyFlag();
		setIsVisibleDirtyFlag();
	}
	
	// Used by Renderer
	public void commitAmbientBuffer()
	{
		_ambientBuffer = Utils.makeFloatBuffer4(
			(float)ambient.r() / 255f,
			(float)ambient.g() / 255f,
			(float)ambient.b() / 255f,
			(float)ambient.a() / 255f);
	}

	// Used by Renderer
	public FloatBuffer ambientBuffer()
	{
		return _ambientBuffer;
	}
	
	// Used by Renderer
	public void commitDiffuseBuffer()
	{
		_diffuseBuffer = Utils.makeFloatBuffer4(
			(float)diffuse.r() / 255f,
			(float)diffuse.g() / 255f,
			(float)diffuse.b() / 255f,
			(float)diffuse.a() / 255f);
	}

	// Used by Renderer
	public FloatBuffer diffuseBuffer()
	{
		return _diffuseBuffer;
	}
	
	// Used by Renderer
	public void commitPositionBuffer()
	{
		_positionBuffer = Utils.makeFloatBuffer4(
			position.getX(),
			position.getY(),
			position.getZ(),
			_type == Type.POSITIONAL ? 1 : 0
		);
		
		// * Rem, 4th argument determines type!
	}

	// Used by Renderer
	public FloatBuffer positionBuffer()
	{
		return _positionBuffer;
	}
}
