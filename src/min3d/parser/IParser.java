package min3d.parser;

import min3d.core.Object3dContainer;

/**
 * Interface for 3D object parsers
 * 
 * @author dennis.ippel
 *
 */
public interface IParser {
	/**
	 * Start parsing the 3D object
	 */
	public void parse();
	/**
	 * Returns the parsed object
	 * @return
	 */
	public Object3dContainer getParsedObject();
}
