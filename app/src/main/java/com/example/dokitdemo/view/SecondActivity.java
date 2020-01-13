package com.example.dokitdemo.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.bookservice.Book;
import com.example.dokitdemo.R;
import com.example.dokitdemo.base.BaseActivity;
import com.example.dokitdemo.constant.RouteConstant;

/**
 * @author fengxu
 */

@Route(path = RouteConstant.ACTIVITY_SECOND)
public class SecondActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.tv_router_jump).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_router_jump) {
            ARouter.getInstance().build(RouteConstant.ACTIVITY_THIRD)
                    .withString("test", "test text")
                    .withParcelable("object", new Book("ssr"))
//                    .withTransition()
                    .navigation(SecondActivity.this, 12);
        }
    }
}
