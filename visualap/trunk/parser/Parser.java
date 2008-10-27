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

import java.util.ArrayList;


public class Parser
{
	ArrayList<Option> options = new ArrayList<Option>();

// addOption(String match,boolean hasValue, String nameValue, String description)
	public Option addOption(String match,boolean hasValue, String nameValue, String description) {
		Option opt = new Option(match, hasValue, nameValue, description);
		options.add(opt);
		return(opt);
	}

// getUsage(String other_args)
	public String getUsage(String other_args) {
		String blanks = "            ";
		String result = "";
		for (int i = 0; i < options.size(); i++) {
			Option option = options.get(i);
			result += " ["+option.match+(option.hasValue ? " "+option.nameValue : "")+"]";
		}
		if (other_args != null) result += " "+other_args;
		result += "\r\n";
		for (int i = 0; i < options.size(); i++) {
			Option option = options.get(i);
			String left = option.match+(option.hasValue ? " "+option.nameValue : "");
			if (left.length() < blanks.length()) result += "\r\n"+left+blanks.substring(left.length())+option.description;
			else result += "\r\n"+left+"\r\n"+blanks+option.description;
		}
		return result;
	}

// String [] parse(String [] args)
	public String [] parse(String [] args) throws ParserException {
		ArrayList<String> result = new ArrayList<String>();
		for (int j = 0; j < options.size(); j++) {
			Option option = options.get(j);
			option.present = false;
			option.value = null;
		}

		for (int i = 0; i < args.length; i++) {
			boolean found = false;
			for (int j = 0; j < options.size(); j++) {
				if ((options.get(j)).match.equals(args[i]))
				{  Option option = options.get(j);
					option.present = true;
					found = true;
					if (option.hasValue)
					{ 
					  i++;
					  if (i < args.length) option.value = args[i];
					  else throw new ParserException("Value is missing for parameter "+option.match);
					}
				}
			}
			if (!found) result.add(args[i]);
		}
		String [] res = new String[result.size()];
		for (int i = 0; i < res.length; i++) res[i] = result.get(i);
		return (res);
	}

// hasOption(Option opt)
	public boolean hasOption(Option opt) {
		return opt.present;
	}

// getValue(Option opt)
	public String getValue(Option opt) {
		return opt.value;
	}
};
