package org.smartregister.view.activity.mock;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * Created by onadev on 29/06/2017.
 */
public class MenuItemTestVersion implements MenuItem {

    private int itemId;

    @Override
    public MenuItem setTitleCondensed(CharSequence charSequence) {
        return setTitleCondensed(charSequence);
    }

    @Override
    public CharSequence getTitleCondensed() {
        return null;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public MenuItem setTitle(CharSequence charSequence) {
        return null;
    }

    @Override
    public MenuItem setTitle(int i) {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public MenuItem setIcon(Drawable drawable) {
        return null;
    }

    @Override
    public MenuItem setIcon(int i) {
        return null;
    }

    public Drawable getIcon() {
        return null;
    }

    public MenuItem setIntent(Intent var1) {
        return null;
    }

    public Intent getIntent() {
        return null;
    }

    @Override
    public MenuItem setShortcut(char var1, char var2) {
        return null;
    }

    @Override
    public MenuItem setNumericShortcut(char var1) {
        return null;
    }

    @Override
    public char getNumericShortcut() {
        return 'o';
    }

    @Override
    public MenuItem setAlphabeticShortcut(char var1) {
        return null;
    }

    @Override
    public char getAlphabeticShortcut() {
        return 'o';
    }

    @Override
    public MenuItem setCheckable(boolean var1) {
        return null;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public MenuItem setChecked(boolean var1) {
        return null;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public MenuItem setVisible(boolean var1) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public MenuItem setEnabled(boolean var1) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    @Override
    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener var1) {
        return null;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public void setShowAsAction(int var1) {
    }

    @Override
    public MenuItem setShowAsActionFlags(int var1) {
        return null;
    }

    @Override
    public MenuItem setActionView(View var1) {
        return null;
    }

    @Override
    public MenuItem setActionView(int var1) {
        return null;
    }

    @Override
    public View getActionView() {
        return null;
    }

    @Override
    public MenuItem setActionProvider(ActionProvider var1) {
        return null;
    }

    @Override
    public ActionProvider getActionProvider() {
        return null;
    }

    @Override
    public boolean expandActionView() {
        return false;
    }

    @Override
    public boolean collapseActionView() {
        return false;
    }

    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    @Override
    public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
        return null;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
