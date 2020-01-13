package com.example.dokitdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.bookservice.Book;
import com.example.dokitdemo.R;
import com.example.dokitdemo.base.BaseActivity;
import com.example.dokitdemo.constant.RouteConstant;

/**
 * @author fengxu
 */

@Route(path = RouteConstant.ACTIVITY_THIRD)
public class ThirdActivity extends BaseActivity implements View.OnClickListener {

    /**
     * IoC(Inversion of Control)，中文翻译为 控制反转，具体实现是 由容器来控制业务对象之间的依赖关系，最终的目的是 避免和降低对象间的依赖关系。
     * 本质上是控制权由应用代码转到了外部容器，控制权的转移即是所谓的反转
     *IoC的实现策略有两种：依赖查找,依赖注入
     * 依赖注入
     * 受控对象 -> 使用@Autowired注解的变量。
     * 容器 -> 在编译期动态创建的 InjectActivity $$ ARouter $$ Autowired 类。
     */
    @Autowired
    String test;

    @Autowired(name = "object")
    Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.tv_router_jump).setOnClickListener(this);

        TextView text = findViewById(R.id.tv_show);
        text.setText(getResources().getString(R.string.test_show, test, book.getName()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_router_jump) {
            ARouter.getInstance().build(RouteConstant.ACTIVITY_MAIN).navigation();
        }
    }
}
