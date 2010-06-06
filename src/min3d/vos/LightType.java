package min3d.vos;

public enum LightType 
{
	POSITIONAL (1f), 
	DIRECTIONAL (0f);
	
	
	private final float _glValue;
	
	private LightType(float $glValue)
	{
		_glValue = $glValue;
	}
	
	public float glValue()
	{
		return _glValue;
	}
}
