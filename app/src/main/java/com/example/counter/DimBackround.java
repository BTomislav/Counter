package com.example.counter;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

class DimBackground {
    public static void Dim(PopupWindow popupWindow){
            View container = popupWindow.getContentView().getRootView();
            Context context = popupWindow.getContentView().getContext();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.6f;
            wm.updateViewLayout(container, p);
        }
    }
