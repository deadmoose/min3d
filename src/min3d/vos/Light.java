package min3d.vos;

import java.nio.FloatBuffer;

import min3d.Utils;




/**
 * Consists of three components - ambient color, diffuse color, and position.
 * Used by Scene.  
 */
public class Light
{
	public Color4Managed ambient = new Color4Managed(128,128,128, 255);
	public Color4Managed diffuse = new Color4Managed(255,255,255, 255);
	public Number3dManaged position = new Number3dManaged(0f, 0f, 5f);

	private FloatBuffer _ambientBuffer; 
	private FloatBuffer _diffuseBuffer;
	private FloatBuffer _positionBuffer;

	
	// Called by Renderer
	public void commitAmbientBuffer()
	{
		_ambientBuffer = Utils.makeFloatBuffer4(
			(float)ambient.r() / 255f,
			(float)ambient.g() / 255f,
			(float)ambient.b() / 255f,
			(float)ambient.a() / 255f);
	}

	// Called by Renderer
	public FloatBuffer getAmbientBuffer()
	{
		return _ambientBuffer;
	}
	
	// Called by Renderer
	public void commitDiffuseBuffer()
	{
		_diffuseBuffer = Utils.makeFloatBuffer4(
			(float)diffuse.r() / 255f,
			(float)diffuse.g() / 255f,
			(float)diffuse.b() / 255f,
			(float)diffuse.a() / 255f);
	}

	// Called by Renderer
	public FloatBuffer getDiffuseBuffer()
	{
		return _diffuseBuffer;
	}
	
	// Called by Renderer
	public void commitPositionBuffer()
	{
		_positionBuffer = Utils.makeFloatBuffer4(
				position.getX(),
				position.getY(),
				position.getZ(),
				1f);
	}

	// Called by Renderer
	public FloatBuffer getPositionBuffer()
	{
		return _positionBuffer;
	}
}
