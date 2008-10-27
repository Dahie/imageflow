/*
Version 1.0, 03-02-2008, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/

// VersionException exception
package visualap;

public class VersionException extends Exception {

	static final long serialVersionUID = -2995186027475769430L;

    public VersionException(String message) {
        super(message);
    }
}
