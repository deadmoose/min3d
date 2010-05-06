package min3d.vos;

import android.util.Log;

/**
 * Simple struct/VO.
 * Expected range of [0,255] for r, g, b, and a
 * (Unfortunately stored as shorts's, not byte's, since Java bytes only go up to 128 :( )
 *  
 */
public class Color4 
{
	public short r;
	public short g;
	public short b;
	public short a;
	
	public Color4()
	{
		r = (short)255;
		g = (short)255;
		b = (short)255;
		a = (short)255;
	}
	
	public Color4(short $r, short $g, short $b, short $a)
	{
		r = $r;
		g = $g;
		b = $b;
		a = $a;
	}

	/**
	 * Convenience method which casts the int arguments to short for you. 
	 */
	public Color4(int $r, int $g, int $b, int $a)
	{
		r = (short)$r;
		g = (short)$g;
		b = (short)$b;
		a = (short)$a;
	}

	/**
	 *  Convenience method to set all properties in one line.
	 */
	public void setAll(short $r, short $g, short $b, short $a)
	{
		r = $r;
		g = $g;
		b = $b;
		a = $a;
	}
	
	/**
	 * Convenience method to set all properties off one 32-bit argb value 
	 */
	public void setAll(long $argb32)
	{
		a = (short) (($argb32 >> 24) & 0x000000FF);
		r = (short) (($argb32 >> 16) & 0x000000FF);
		g = (short) (($argb32 >> 8) & 0x000000FF);
		b = (short) (($argb32) & 0x000000FF);		
	}
	
	@Override
	public String toString()
	{
		String s = "r:" + r + ", g:" + g + ", b:" + b + ", a:" + a;
		return s;
	}
	
	public Color4Managed toColor4Managed()
	{
		return new Color4Managed(r,g,b,a);
	}
}
