package com.shootr.mobile.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.util.LinkedList;
import java.util.List;

public class MenuItemValueHolder implements MenuItem {

    private MenuItem realMenuItem;
    private List<MenuValueChange> valueChangesQueue = new LinkedList<>();

    public void bindRealMenuItem(MenuItem menuItem) {
        this.realMenuItem = menuItem;
        for (MenuValueChange menuValueChange : valueChangesQueue) {
            menuValueChange.apply(realMenuItem);
        }
        valueChangesQueue.clear();
    }

    private void queueValueChange(MenuValueChange menuValueChange) {
        if (realMenuItem != null) {
            menuValueChange.apply(realMenuItem);
        } else {
            valueChangesQueue.add(menuValueChange);
        }
    }

    @Override
    public int getItemId() {
        if (realMenuItem != null) {
            return realMenuItem.getItemId();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public int getGroupId() {
        if (realMenuItem != null) {
            return realMenuItem.getGroupId();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public int getOrder() {
        if (realMenuItem != null) {
            return realMenuItem.getOrder();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setTitle(final CharSequence title) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setTitle(title);
            }
        });
        return this;
    }

    @Override
    public MenuItem setTitle(final int title) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setTitle(title);
            }
        });
        return this;
    }

    @Override
    public CharSequence getTitle() {
        if (realMenuItem != null) {
            return realMenuItem.getTitle();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setTitleCondensed(final CharSequence title) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setTitleCondensed(title);
            }
        });
        return this;
    }

    @Override
    public CharSequence getTitleCondensed() {
        if (realMenuItem != null) {
            return realMenuItem.getTitleCondensed();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setIcon(final Drawable icon) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setIcon(icon);
            }
        });
        return this;
    }

    @Override
    public MenuItem setIcon(final int iconRes) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setIcon(iconRes);
            }
        });
        return this;
    }

    @Override
    public Drawable getIcon() {
        if (realMenuItem != null) {
            return realMenuItem.getIcon();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setIntent(final Intent intent) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setIntent(intent);
            }
        });
        return this;
    }

    @Override
    public Intent getIntent() {
        if (realMenuItem != null) {
            return realMenuItem.getIntent();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setShortcut(final char numericChar, final char alphaChar) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setShortcut(numericChar, alphaChar);
            }
        });
        return this;
    }

    @Override
    public MenuItem setNumericShortcut(final char numericChar) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setNumericShortcut(numericChar);
            }
        });
        return this;
    }

    @Override
    public char getNumericShortcut() {
        if (realMenuItem != null) {
            return realMenuItem.getNumericShortcut();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setAlphabeticShortcut(final char alphaChar) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setAlphabeticShortcut(alphaChar);
            }
        });
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        if (realMenuItem != null) {
            return realMenuItem.getAlphabeticShortcut();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setCheckable(final boolean checkable) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setCheckable(checkable);
            }
        });
        return this;
    }

    @Override
    public boolean isCheckable() {
        if (realMenuItem != null) {
            return realMenuItem.isCheckable();
        }
        return false;
    }

    @Override
    public MenuItem setChecked(final boolean checked) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setCheckable(checked);
            }
        });
        return this;
    }

    @Override
    public boolean isChecked() {
        if (realMenuItem != null) {
            return realMenuItem.isChecked();
        }
        return false;
    }

    @Override
    public MenuItem setVisible(final boolean visible) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setVisible(visible);
            }
        });
        return this;
    }

    @Override
    public boolean isVisible() {
        if (realMenuItem != null) {
            return realMenuItem.isVisible();
        }
        return false;
    }

    @Override
    public MenuItem setEnabled(final boolean enabled) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setEnabled(enabled);
            }
        });
        return this;
    }

    @Override
    public boolean isEnabled() {
        if (realMenuItem != null) {
            return realMenuItem.isEnabled();
        }
        return false;
    }

    @Override
    public boolean hasSubMenu() {
        if (realMenuItem != null) {
            return realMenuItem.hasSubMenu();
        }
        return false;
    }

    @Override
    public SubMenu getSubMenu() {
        if (realMenuItem != null) {
            return realMenuItem.getSubMenu();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setOnMenuItemClickListener(final OnMenuItemClickListener menuItemClickListener) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setOnMenuItemClickListener(menuItemClickListener);
            }
        });
        return this;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        if (realMenuItem != null) {
            return realMenuItem.getMenuInfo();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public void setShowAsAction(final int actionEnum) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setShowAsAction(actionEnum);
            }
        });
    }

    @Override
    public MenuItem setShowAsActionFlags(final int actionEnum) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setShowAsActionFlags(actionEnum);
            }
        });
        return this;
    }

    @Override
    public MenuItem setActionView(final View view) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setActionView(view);
            }
        });
        return this;
    }

    @Override
    public MenuItem setActionView(final int resId) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setActionView(resId);
            }
        });
        return this;
    }

    @Override
    public View getActionView() {
        if (realMenuItem != null) {
            return realMenuItem.getActionView();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public MenuItem setActionProvider(final ActionProvider actionProvider) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setActionProvider(actionProvider);
            }
        });
        return this;
    }

    @Override
    public ActionProvider getActionProvider() {
        if (realMenuItem != null) {
            return realMenuItem.getActionProvider();
        }
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public boolean expandActionView() {
        if (realMenuItem == null) {
            queueValueChange(new MenuValueChange() {
                @Override
                public void apply(MenuItem realMenuItem) {
                    realMenuItem.expandActionView();
                }
            });
            return false;
        } else {
            return realMenuItem.expandActionView();
        }
    }

    @Override
    public boolean collapseActionView() {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.collapseActionView();
            }
        });
        throw new IllegalStateException("Can't query data");
    }

    @Override
    public boolean isActionViewExpanded() {
        if (realMenuItem != null) {
            return realMenuItem.isActionViewExpanded();
        } else {
            throw new IllegalStateException("Can't query data");
        }
    }

    @Override
    public MenuItem setOnActionExpandListener(final OnActionExpandListener listener) {
        queueValueChange(new MenuValueChange() {
            @Override
            public void apply(MenuItem realMenuItem) {
                realMenuItem.setOnActionExpandListener(listener);
            }
        });
        return this;
    }

    private interface MenuValueChange {

        void apply(MenuItem realMenuItem);

    }
}
