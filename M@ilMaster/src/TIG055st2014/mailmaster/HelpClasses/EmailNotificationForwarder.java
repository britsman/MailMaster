package TIG055st2014.mailmaster.HelpClasses;
import android.app.Activity;
import android.os.Bundle;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
for more details. You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Dummy activity used to reset unread email counter if
 * the notification is pressed/deleted. the call to finish()
 * stops this activity from staying on the history stack 
 * (so back button won't take you to this empty activity).
 */
public class EmailNotificationForwarder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmailNotificationVariables.nrUnreadEmail = 0;
        finish();
    }
}