MailMaster
==========
Eric and Khaled's Email client.

License Info:
The classes we use from javamail-android (based on javamail 1.4,2) all comply with the classpath exception of 
GPLv2 (see the bottom of LICENSE_JAVAMAIL.txt). This means that we do not need to transfer the license of the
Javamail api over to our own code.

The class JSSEProvider.java uses an apache license, As can be seen in LICENSE-JSSEProvider.txt, apache defines
derivative work as: 
"For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link 
(or bind by name) to the interfaces of, the Work and Derivative Works thereof.". Since we only use the class JSSEProvider
without modifying it, we believe that our app can not be classified as derivative work. This would mean that our app is not
required to follow the apache license either. Our only issue is that we have not been able to find the "NOTICE" file mentioned
in the class's license documentation (which is supposed to contain names of past contributors to the file). Not having this 
information means we are in slight violation of the license (in regards to distribution of JSSE).

As described above, we have assumed that we were free to choose what license we wanted for our app, due to how we used this
project's linked libraries. Thus we have chosen to license our ap under LICENSE (see LICENSE-M@ilMaster).