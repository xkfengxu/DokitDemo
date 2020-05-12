package com.example.dokitdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.example.bookservice.Book;
import com.example.bookservice.BookController;
import com.example.dokitdemo.base.BaseActivity;
import com.example.dokitdemo.constant.RouteConstant;

@Route(path = RouteConstant.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**
     * aidl生成对象
     */
    private BookController bookController;
    /**
     * 服务连接状态
     */
    private boolean connected;
    /**
     * 书籍信息
     */
    private TextView bookInfo;
    /**
     * 请求计数
     */
    private int counter;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //IBinder转换为BookController
            bookController = BookController.Stub.asInterface(service);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //安装bookService,暂时未成功，需要手动安装
        installApp();
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_switch_doraemon).setOnClickListener(this);
        findViewById(R.id.tv_get_book_list).setOnClickListener(this);
        findViewById(R.id.tv_add_book_inOut).setOnClickListener(this);
        findViewById(R.id.tv_test_arouter).setOnClickListener(this);
        bookInfo = findViewById(R.id.tv_book_info);
    }

    private void checkService() {
        //检查服务应用是否存在
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.example.bookservice", "com.example.bookservice.MainActivity");
        intent.setComponent(cn);
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_ALL) != null) {
            //开启服务应用，绑定服务
            startActivity(intent);
            bindService();
        } else {
            TextView title = findViewById(R.id.tv_mid_title);
            title.setText(R.string.test_aidl_no_install);
        }
    }

    @Override
    protected void onResume() {
        checkService();
        super.onResume();
    }

    private boolean isAppInstalled() {
        try {
            getPackageManager().getPackageInfo("com.example.bookservice", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void installApp() {
        if (isAppInstalled()) {
            checkService();
            return;
        }
        AssetManager assetManager = getAssets();
        InputStream inputStream;
        String filename = getBaseContext().getExternalFilesDir(null) + "/" + "bookservice.apk";
        try {
            inputStream = assetManager.open("bookservice.apk");
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i;
            while ((i = inputStream.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(filename));
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filename)), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            unbindService(serviceConnection);
        }
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.bookservice", "com.example.bookservice.BookService"));
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        if (!connected) {
            bindService();
        }
        int id = v.getId();
        switch (id) {
            case R.id.tv_switch_doraemon:
                DoraemonKit.showToolPanel();
                break;
            case R.id.tv_get_book_list:
                if (connected) {
                    try {
                        //界面展示
                        counter++;
                        List<Book> bookList = bookController.getBookList();
                        StringBuilder builder = new StringBuilder();
                        builder.append(counter).append("\n");
                        for (Book book : bookList) {
                            builder.append(book.getName()).append("\n");
                        }
                        bookInfo.setText(builder.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_add_book_inOut:
                if (connected) {
                    Book book = new Book("这是一本新书 " + System.currentTimeMillis());
                    try {
                        //inout方式添加数据
                        bookController.addBookInOut(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_test_arouter:
                ARouter.getInstance().build(RouteConstant.ACTIVITY_SECOND).navigation();
            default:
                break;
        }
    }

}
