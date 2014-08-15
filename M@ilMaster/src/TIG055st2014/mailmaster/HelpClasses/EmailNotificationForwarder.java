package TIG055st2014.mailmaster.HelpClasses;
import android.app.Activity;
import android.os.Bundle;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License 
Version 2 only; as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

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