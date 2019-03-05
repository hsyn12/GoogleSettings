package com.setting.dl.google.googlesettingupdate.mail.gmail;

import android.content.Context;

/**
 * Created by hsyn on 7.06.2017.
 *
 * <p>Gmail is a Acount</p>
 */

public class Account {
    
    private         String  account;
    protected final Context context;
    
    Account(Context context) {
        
        
        this.context = context;
        this.account = this.context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("from", null);
        
    }
    
    String getAccount() {return account;}
}
