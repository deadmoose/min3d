package min3d.interfaces;

import min3d.core.Object3d;

import android.graphics.Bitmap;

public interface IBitmapManagable 
{
    public int initTexture(Object3d $o, Bitmap $b);
    public void deleteTexture(int $textureId);
}
