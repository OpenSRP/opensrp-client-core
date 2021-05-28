package org.smartregister.account;

/**
 * Created by ndegwamartin on 08/05/2020.
 */
public class AccountAuthenticatorXml {
    private String accountType;
    private String accountLabel;
    private int icon;

    public String getAccountType() {
        return accountType;
    }

    public String getAccountLabel() {
        return accountLabel;
    }

    public int getIcon() {
        return icon;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAccountLabel(String accountLabel) {
        this.accountLabel = accountLabel;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
