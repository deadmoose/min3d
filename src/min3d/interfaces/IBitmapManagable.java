package min3d.interfaces;

import min3d.core.Object3d;

import android.graphics.Bitmap;

public interface IBitmapManagable 
{
    public int uploadTextureAndReturnId(Bitmap $b);
    public void deleteTexture(int $textureId);
}
