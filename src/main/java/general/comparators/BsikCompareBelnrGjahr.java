/**
 * 
 */
package general.comparators;

/**
 * @author Azamat
 *
 */
import general.tables.Bsik;

import java.util.Comparator;
 

public class BsikCompareBelnrGjahr implements Comparator<Bsik> 
{
	@Override
	public int compare(Bsik a1, Bsik a2)
	{
		if(a1.getBelnr()>a2.getBelnr())
		{
			return 1;
		}
		else if (a1.getBelnr()<a2.getBelnr())
		{
			return -1;
		}
		else {
			if(a1.getGjahr()>a2.getGjahr())
			{
				return 1;
			}
			else if (a1.getGjahr()<a2.getGjahr())
			{
				return -1;
			}
			else return 0;
		}
	}	

}