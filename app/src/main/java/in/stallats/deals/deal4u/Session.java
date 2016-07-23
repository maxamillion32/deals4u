package in.stallats.deals.deal4u;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ramyapriya on 18-07-2016.
 */
public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("deals4u", Context.MODE_PRIVATE);
        editor = prefs.edit();
        //editor.putString("fav", "");
        //editor.commit();
    }

    public void setLoggedIn(boolean logedin){
        editor.putBoolean("loggedinmode", logedin);
        editor.putString("fav", "");
        editor.commit();
    }

    public void clearEditorData(){
        editor.clear();
        editor.commit();
    }

    public void set_favourites(String data){
        String current_fav = prefs.getString("fav", null);
        if(current_fav.isEmpty()){
            editor.putString("fav", data);
        }else{
            editor.putString("fav", current_fav + "," + data);
        }
        editor.commit();
    }

    public ArrayList<String> get_favourites(){
        String current_fav = prefs.getString("fav", null);
        //System.out.println(current_fav);
        if(current_fav.isEmpty()){
            return null;
        }else{
            ArrayList<String> items = new ArrayList(Arrays.asList(current_fav.split("\\s*,\\s*")));
            return items;
        }
    }

    public boolean check_favourite(String data){
        ArrayList<String> temp =  get_favourites();
        //System.out.println(Arrays.toString(temp));
        if(temp == null) {
            return false;
        }
        else{
            for(String str : temp){
                if(str.equals(data)){
                    return true;
                }
            }
            return false;
        }
    }

    public void del_favourites(String data){
        ArrayList<String> temp = get_favourites();
        String tempVal = null;
        for(String str : temp){
            if(str.equals(data)){

            }else{
                if(tempVal == null){
                    tempVal = str;
                }else{
                    tempVal = tempVal + "," + str;
                }
            }
        }
        editor.putString("fav", tempVal);
        editor.commit();
    }

    public void set_sessionData(String id, String name, String email, String mobile, String gender, String referal){
        editor.putString("name", name); // Storing string
        editor.putString("email", email); // Storing string
        editor.putString("mobile", mobile); // Storing string
        editor.putString("gender", gender); // Storing string
        editor.putString("referal", referal); // Storing string
        editor.putString("id", id); // Storing integer
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put("name", prefs.getString("name", null));
        user.put("email", prefs.getString("email", null));
        user.put("mobile", prefs.getString("mobile", null));
        user.put("gender", prefs.getString("gender", null));
        user.put("referal", prefs.getString("referal", null));
        user.put("id", prefs.getString("id", null));

        // return user
        return user;
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedinmode", false);
    }
}
