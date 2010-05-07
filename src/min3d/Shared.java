package min3d;

import min3d.core.Renderer;
import android.content.Context;

/**
 * Holds references to Context and Renderer instances
 * for framework's convenience
 */
public class Shared 
{
	private static Context _context;
	private static Renderer _renderer;

	
	public static Context context()
	{
		return _context;
	}
	public static void context(Context $c)
	{
		_context = $c;
	}

	public static Renderer renderer()
	{
		return _renderer;
	}
	public static void renderer(Renderer $r)
	{
		_renderer = $r;
	}
	
}
