package com.sinyi.test.uploadimagetest;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class MainActivity extends Activity {
    Button chooseButton, uploadButton;
    ImageView loadImageView, uploadImageView;
    BroadcastReceiver broadcastReceiver;
    Uri imageUrls;
    String imageUrlStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseButton = (Button) findViewById(R.id.chooseButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        loadImageView = (ImageView) findViewById(R.id.loadImageView);
        uploadImageView = (ImageView) findViewById(R.id.uploadImageView);


//        imageLoader();
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (action.equals(UploadFileService.ACTION_ON_UPLOAD_CHANGE)) {
//                    getImgUrl();
//                }
//            }
//        };


        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 建立 "選擇檔案 Action" 的 Intent
                Intent intent = new Intent(Intent.ACTION_PICK);

                // 過濾檔案格式
                intent.setType("image/*");

                // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
                Intent destIntent = Intent.createChooser(intent, "選擇檔案");

                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
                startActivityForResult(destIntent, 0);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, UploadFileService.class);

                System.out.println("SheriTest" + imageUrlStr);
                if (null != imageUrls) {
                    intent.putExtra("imageUrls", imageUrlStr);
                } else {
                    Toast.makeText(MainActivity.this, "choose image", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    UploadFileService.startUpload(MainActivity.this, intent);

                } catch (Exception e) {

                    Log.e("Error in startToUpload", e.getMessage());

                }
            }
        });

    }


//    @Override
//    public void onResume() {
//        super.onResume();
////        IntentFilter intentFilter = new IntentFilter();
////        intentFilter.addAction(UploadFileService.ACTION_ON_UPLOAD_CHANGE);
////        this.registerReceiver(broadcastReceiver, intentFilter);
////        getImgUrl();
//
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        this.unregisterReceiver(broadcastReceiver);
//    }
    public void getImgUrl() {
        Intent intent = new Intent(UploadFileService.ACTION_ON_UPLOAD_CHANGE);
//        if (!intent.getStringExtra("successUpload").equals("")||null!=intent.getStringExtra("successUpload")) {
    	if(!UploadFileService.ImageUrl.equals("")){
            imageUrls = Uri.parse("http://180.150.179.114:8080/uploads/" + UploadFileService.ImageUrl);
//            uploadImageView.setImageURI(imageUrls);
            ImageLoader.getInstance().displayImage(String.valueOf(imageUrls), uploadImageView);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 有選擇檔案
        if (resultCode == RESULT_OK) {
            // 取得檔案的 Uri
            imageUrls = data.getData();
            if (imageUrls != null) {
                // 利用 Uri 顯示 ImageView 圖片
                ImageView iv = (ImageView) this.findViewById(R.id.loadImageView);
                iv.setImageURI(imageUrls);
                setTitle(imageUrls.toString());
                imageUrlStr = getPath(imageUrls);
            } else {
                setTitle("無效的檔案路徑 !!");
            }
        } else {
            setTitle("取消選擇檔案 !!");
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void imageLoader() {
        //创建默认的imageloader配置函数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(500, 500)//缓存文件最大宽高
                .threadPoolSize(3) //线程池的加载数量
                .threadPriority(Thread.NORM_PRIORITY - 2)//优先级定义
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024) //50mb sd卡(本地)缓存最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new BaseImageDownloader(this, 5 * 100, 30 * 1000))
                .denyCacheImageMultipleSizesInMemory()
                .writeDebugLogs()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .build();
        //初始化imageloader
        ImageLoader.getInstance().init(configuration);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
