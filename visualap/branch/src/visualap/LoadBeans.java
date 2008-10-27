/*
Version 1.0, 30-12-2007, First release
Version 1.1, 03-02-2008, added <version> field in components, added sorting of jars

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

/*
The LoadBeans loads all the available JavaBeans in the directory <beans>

javalc6

*/

package visualap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


class LoadBeans extends ArrayList<BeanDelegate> {
//    private transient HashMap<String, BeanDelegate> delegates = new HashMap<String, BeanDelegate>();

    public LoadBeans load(String beansDir) {
		ArrayList<String> jarNames = getJarNames(beansDir);	
		Collections.sort(jarNames);
		for (int i = 0; i < jarNames.size(); i++) {
			String name = jarNames.get(i);
			try {
					addBeansInJar(name);
					System.out.println(name);
			} catch (Exception ex) {
				ErrorPrinter.printInfo(name + ": jar load failed");
				ErrorPrinter.dump(ex, VisualAp.getUniqueID());
			}
		}
		return this;
	}

	synchronized void addBeansInJar(String jarFile) throws IOException {
		ClassPathHacker.addFile(jarFile); // workaround to avoid the ClassNotFoundException when using XMLEncoder and XMLDecoder
		System.out.println(jarFile);
		URLClassLoader classLoader;
		classLoader = new URLClassLoader(new URL[]{new File(jarFile).toURL()});
		ArrayList<String> beanNames = getBeansName(jarFile);
		if (beanNames.size() != 0)	{	
			for (int i=0; i <beanNames.size(); i++)	{
				String beanName = beanNames.get(i);
				System.out.println(beanName);
				try {
					boolean ignore = false;
					int remove_j = -1;
					BeanDelegate bd = new BeanDelegate(beanName, classLoader);
					System.out.println(bd.name);
					for (int j=0; j <this.size(); j++)
						if (get(j).name.equals(beanName)) {
							System.out.println(beanName+" duplicate detected");
							if (checkVersion(bd.version, get(j).version))
								ignore = true;
							else remove_j = j;
						}
					if (remove_j != -1)
						remove(remove_j);
					if (!ignore)
						System.out.println(bd.name);
						add(bd);
				}
				catch (BeanException ex) {
// this exception can be ignored
					ErrorPrinter.printInfo(beanName+" caused BeanException in " + jarFile + " : "+ex.getMessage());
				}
			}
		}
    }

// checkVersion returns true only if ver2 is greater than ver
    public boolean checkVersion(String ver, String ver2) {
		int min;
		if (ver.length() > ver2.length())
			min = ver2.length();
		else min = ver.length();
		for (int i=0; i<min; i++) {
			if (ver.charAt(i) < ver2.charAt(i))
				return true;
			else if (ver.charAt(i) == ver2.charAt(i))
					continue;
				else break;
		}
		return false;
    }



    private static ArrayList<String> getJarNames(String beansDir) {
		File cwd = new File(System.getProperty("user.dir"));
		File jars = new File(cwd, beansDir);

		if (! jars.isDirectory()) {
			ErrorPrinter.printInfo(jars+" is not a directory!!");
		}

		ArrayList<String> result = new ArrayList<String>();
		String names[];
		names = jars.list(new FilenameFilter() {
			public boolean accept(File f, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});
		if (names != null)
			for (int i=0; i<names.length; i++) {
				result.add(jars.getPath() + File.separatorChar + names[i]);
			}

		return result;
    }


	public ArrayList<String> getBeansName(String filename) throws IOException {
        JarFile jarfile;
        Manifest mf;
		ArrayList<String> result = new ArrayList<String>();

		jarfile = new JarFile(filename);
		mf = jarfile.getManifest();

        Attributes attribs = mf.getMainAttributes();
        if (attribs != null)  {
            // Determine if this is a java bean.
            String isJavaBean = attribs.getValue(new Attributes.Name("Java-Bean"));
            
            if (isJavaBean != null && isJavaBean.equalsIgnoreCase("True"))  {
                String classname = attribs.getValue(new Attributes.Name("Name"));

				if (classname.endsWith(".class"))  {
					classname = classname.substring(0, classname.length() - 6);
				} else if (classname.endsWith(".ser")) {
					// Must deserialize the class.
					classname = classname.substring(0, classname.length() - 4);
				}
				result.add(classname.replace('/', '.'));
            } else {
            	System.out.println(filename + " is not a bean");
            }
        }

        Iterator iterator = mf.getEntries().keySet().iterator();
        while (iterator.hasNext()) {
            String beanName = (String)iterator.next();

            attribs = mf.getAttributes(beanName);

            if (attribs != null)  {
                String isJavaBean = attribs.getValue(new Attributes.Name("Java-Bean"));

				if (isJavaBean != null && isJavaBean.equalsIgnoreCase("True"))  {
					if (beanName.endsWith(".class"))  {
						beanName = beanName.substring(0, beanName.length() - 6);
					} else if (beanName.endsWith(".ser")) {
						// Must deserialize the class.
						beanName = beanName.substring(0, beanName.length() - 4);
					}
					
					result.add(beanName.replace('/', '.'));
				}
			
            }
        }
		return result;
	}
}
