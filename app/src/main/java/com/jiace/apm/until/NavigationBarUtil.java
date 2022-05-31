package com.jiace.apm.until;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
 * 2 * Created by wzh on 2021/7/20.
 * 3 * Description:
 * 4 *
 * 5
 */
public class NavigationBarUtil {

    /**
     * 隐藏虚拟栏 ，显示的时候再隐藏掉
     * @param window
     */
    static public void hideNavigationBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions =
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                window.getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     * @param window
     */
    static public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     * @param window
     */
    static public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * 判断一下虚拟导航栏是否显示
     * @param window
     */
    public static boolean checkNavigationBarShow(@NonNull Context context, @NonNull Window window) {
        try{
            int systemUiVisility = ((Activity)context).getWindow().getDecorView().getSystemUiVisibility();
            if((systemUiVisility & (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION))==0) {

                return true;
            }
            return false;
        }catch (Exception e) {
            return false;
        }
    }
}
