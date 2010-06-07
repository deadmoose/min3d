package min3d.vos;

import min3d.interfaces.IDirtyParent;

/**
 * 'Managed' version of Number3d VO 
 */
public class Number3dManaged extends AbstractDirtyManaged 
{
	private float _x;
	private float _y;
	private float _z;
	
	
	public Number3dManaged(IDirtyParent $parent)
	{
		super($parent);
		_x = 0;
		_y = 0;
		_z = 0;
		_dirty = true;
	}
	
	public Number3dManaged(float $x, float $y, float $z, IDirtyParent $parent)
	{
		super($parent);
		_x = $x;
		_y = $y;
		_z = $z;
		_dirty = true;
	}
	
	public float getX() {
		return _x;
	}

	public void setX(float x) {
		_x = x;
		_dirty = true;
	}

	public float getY() {
		return _y;
	}

	public void setY(float y) {
		_y = y;
		_dirty = true;
	}

	public float getZ() {
		return _z;
	}

	public void setZ(float z) {
		_z = z;
		_dirty = true;
	}

	public void setAll(float $x, float $y, float $z)
	{
		_x = $x;
		_y = $y;
		_z = $z;
		_dirty = true;
	}
	
	public void setAllFrom(Number3d $n)
	{
		_x = $n.x;
		_y = $n.y;
		_z = $n.z;
		_dirty = true;
	}

	public void setAllFrom(Number3dManaged $n)
	{
		_x = $n.getX();
		_y = $n.getY();
		_z = $n.getZ();
		_dirty = true;
	}

	public Number3d toNumber3d()
	{
		return new Number3d(_x,_y,_z);
	}
	
	@Override
	public String toString()
	{
		return _x + "," + _y + "," + _z; 
	}
	
}
