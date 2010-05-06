package min3d.vos;

import min3d.interfaces.IDirtyManaged;

/**
 * Same functionality as Color4, but uses proper accessors to r,g,b, and a properties, 
 * rather than VO-style public variables, so that 'dirty flag' can be managed properly.
 */
public class Color4Managed implements IDirtyManaged
{
	private boolean _dirty;

	private short _r;
	private short _g;
	private short _b;
	private short _a;
	
	
	public Color4Managed()
	{
		_r = (short)255;
		_g = (short)255;
		_b = (short)255;
		_a = (short)255;
		
		_dirty = true;
	}
	
	public Color4Managed(short $r, short $g, short $b, short $a)
	{
		_r = $r;
		_g = $g;
		_b = $b;
		_a = $a;

		_dirty = true;
	}

	/**
	 * Convenience method which casts the int arguments to short for you. 
	 */
	public Color4Managed(int $r, int $g, int $b, int $a)
	{
		_r = (short)$r;
		_g = (short)$g;
		_b = (short)$b;
		_a = (short)$a;

		_dirty = true;
	}

	/**
	 *  Convenience method to set all properties in one line.
	 */
	public void setAll(short $r, short $g, short $b, short $a)
	{
		_r = $r;
		_g = $g;
		_b = $b;
		_a = $a;

		_dirty = true;
	}
	
	public Color4 toColor4()
	{
		return new Color4(_r,_g,_b,_a);
	}
	
	/**
	 * Convenience method to set all properties off one 32-bit rgba value 
	 */
	public void setAll(long $argb32)
	{
		_a = (short) (($argb32 >> 24) & 0x000000FF);
		_r = (short) (($argb32 >> 16) & 0x000000FF);
		_g = (short) (($argb32 >> 8) & 0x000000FF);
		_b = (short) (($argb32) & 0x000000FF);		
		
		_dirty = true;
	}	

	public short r()
	{
		return _r;
	}
	public void r(short $r)
	{
		_r = $r;
		_dirty = true;
	}
	
	public short g()
	{
		return _g;
	}
	public void g(short $g)
	{
		_g = $g;
		_dirty = true;
	}
	
	public short b()
	{
		return _b;
	}
	public void b(short $b)
	{
		_b = $b;
		_dirty = true;
	}
	
	public short a()
	{
		return _a;
	}
	public void a(short $a)
	{
		_a = $a;
		_dirty = true;
	}
	
	//
	
	/**
	 * Called indirectly by Renderer
	 */
	public boolean isDirty()
	{
		return _dirty;
	}
	
	/**
	 * Called indirectly by Renderer
	 */
	public void clearDirtyFlag()
	{
		_dirty = false;
	}
	
	// ... unfortunate duplication of code :(
}
