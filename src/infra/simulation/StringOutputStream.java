/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package infra.simulation;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
/**
* String Output Stream class for output redirection
*/
public class StringOutputStream extends OutputStream {
   JTextArea logText;
   public StringOutputStream(JTextArea logText) {
       this.logText = logText;
   }

   @Override
   public void write(int b) throws IOException {
       updateLog(String.valueOf((char) b));
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
       updateLog(new String(b, off, len));
   }

   @Override
   public void write(byte[] b) throws IOException {
       write(b, 0, b.length);
   }

   private synchronized void updateLog(final String text) {
       SwingUtilities.invokeLater(new Runnable() {

           @Override
           public void run() {
               if(text.contains("clearlog"))
                   logText.setText("");
               else
                   logText.append(text);
           }
       });
   }
}
