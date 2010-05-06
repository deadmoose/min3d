package min3d.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import min3d.vos.Color4;
import min3d.vos.Number3d;
import min3d.vos.Uv;



public class VertexInfo
{
	private Number3dBufferList _points;
	private UvBufferList _uvs;
	private Number3dBufferList _normals;
	private Color4BufferList _colors;
	
	
	/**
	 * Primarily just a 'wrapper' to hold the list of vertex points, texture coordinates (UV), normals, and vertex colors. 
	 * Use "addVertex()" to build the vertex data for the Object3d instance associated with this instance. 
	 * 
	 * Direct manipulation of position, UV, normal, or color data can be done directly through the associated 
	 * 'buffer list' instances contained herein.
	 */
	public VertexInfo(int $maxElements)
	{
		_points = new Number3dBufferList($maxElements);
		_uvs = new UvBufferList($maxElements);
		_normals = new Number3dBufferList($maxElements);
		_colors = new Color4BufferList($maxElements);
	}
	
	public int size()
	{
		return _points.size();
	}
	
	public int capacity()
	{
		return _points.capacity();
	}
	
	/**
	 * Use this to populate an Object3d's vertex data.
	 * Return's newly added vertex's index 
	 */
	public short addVertex(
		float $pointX, float $pointY, float $pointZ,  
		float $textureU, float $textureV,  
		float $normalX, float $normalY, float $normalZ,  
		short $colorR, short $colorG, short $colorB, short $colorA)
	{
		_points.add($pointX, $pointY, $pointZ);
		_uvs.add($textureU, $textureV);
		_normals.add($normalX, $normalY, $normalZ);
		_colors.add($colorR, $colorG, $colorB, $colorA);
		
		return (short)(_points.size()-1);
	}
	
	/**
	 * More structured-looking way of adding a vertex (but potentially wasteful).
	 * The VO's taken in as arguments are only used to read the values they hold
	 * (no references are kept to them).  
	 * Return's newly added vertex's index 
	 */
	public short addVertex(Number3d $point, Uv $textureUv, Number3d $normal, Color4 $color)
	{
		_points.add($point);
		_uvs.add($textureUv);
		_normals.add($normal);
		_colors.add($color);
		
		return (short)(_points.size()-1);
	}
	
	
	Number3dBufferList points() /*package-private*/
	{
		return _points;
	}
	
	/**
	 * I.e., texture coordinates
	 */
	UvBufferList uvs() /*package-private*/
	{
		return _uvs;
	}
	
	Number3dBufferList normals() /*package-private*/
	{
		return _normals;
	}
	
	Color4BufferList colors() /*package-private*/
	{
		return _colors;
	}
	
}
