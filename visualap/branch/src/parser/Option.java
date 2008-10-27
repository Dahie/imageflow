/*
Version 1.0, 30-12-2007, First release

IMPORTANT NOTICE, please read:

This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE,
please read the enclosed file license.txt or http://www.gnu.org/licenses/licenses.html

Note that this software is freeware and it is not designed, licensed or intended
for use in mission critical, life support and military purposes.

The use of this software is at the risk of the user.
*/
package parser;

public class Option
{	protected String match;
	protected boolean hasValue;
	protected String nameValue;
	protected String description;

	protected boolean present;	// set by Parser.parse()
	protected String value;     // set by Parser.parse()

	protected Option (String match, boolean hasValue, String nameValue, String description) {
		this.match = match;
		this.nameValue = nameValue;
		this.hasValue = hasValue;
		this.description = description;
	}
};
