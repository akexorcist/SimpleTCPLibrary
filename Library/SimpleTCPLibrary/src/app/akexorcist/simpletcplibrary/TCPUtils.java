/*
 * Copyright (c) 2013 Akexorcist
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package app.akexorcist.simpletcplibrary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TCPUtils {
	private static final Pattern PARTIAl_IP_ADDRESS = 
			Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}" 
					+ "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$"); 
	
	public static int AP_STATE_ENABLED = 13;
	
	public static String getIP(Context context) {
    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	String ip = "192.168.43.1";
    	if(!isHotspot(wifiManager)) {
	    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    	int ipAddress = wifiInfo.getIpAddress();
	    	ip = (ipAddress & 0xFF) + "." +
	    			((ipAddress >> 8 ) & 0xFF) + "." +
	    			((ipAddress >> 16 ) & 0xFF) + "." +
	                ((ipAddress >> 24 ) & 0xFF ) ;
    	}
    	return ip;
	}
	
	private static boolean isHotspot(WifiManager wifiManager) {
		try {
			Method method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
			method.setAccessible(true);
			int state = (Integer) method.invoke(wifiManager, (Object[]) null);
			if(state == AP_STATE_ENABLED) {
				return true;
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void forceInputIP(EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {       
		    private String mPreviousText = "";   
		    
		    public void onTextChanged(CharSequence s, int start, int before, int count) {}            
		    public void beforeTextChanged(CharSequence s,int start,int count,int after) {}            
 
		    public void afterTextChanged(Editable s) {          
		        if(PARTIAl_IP_ADDRESS.matcher(s).matches()) {
		            mPreviousText = s.toString();
		        } else {
		            s.replace(0, s.length(), mPreviousText);
		        }
		    }
		});
	}
}

