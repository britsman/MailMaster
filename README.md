MailMaster
==========
Eric and Khaled's Email client.

License Info:
The classes we use from javamail-android (found at http://code.google.com/p/javamail-android/ , based on 
javamail 1.4.2) all comply with the classpath exception of GPLv2 (see the bottom of LICENSE_JAVAMAIL.txt). 
However, this exception only applies to compiled versions of app, but not to distributions of its source 
code. Therefore, the source code found in this distribution is licensed under GPLv2 in order to comply 
with the library (see LICENSE-M@ilMaster.txt). We use GPLv2 since javamail doesn't include the optional 
"use later GPL versions" clause in its source file headers. Also, we have assumed this means we must also
"force" General Public License Version 2 only for our own source files.


The class JSSEProvider.java uses an apache license, As can be seen in LICENSE-JSSEProvider.txt. 
Apache defines derivative work as: 
"For the purposes of this License, Derivative Works shall not include works that remain separable 
from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof.". 
Since we only use the class JSSEProvider without modifying it, we believe that our app can not be 
classified as derivative work. This would mean that our app is not required to inherit the apache license. 
Our only issue is that we have not been able to find the "NOTICE" file mentioned in the class's license 
documentation (which is supposed to contain names of past contributors to the file). Not having this 
information means we are in slight violation of the license (in regards to distribution of JSSE).
