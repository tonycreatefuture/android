package com.zhongdasoft.svwtrainnet.widget.imageload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.CameraUtil;
import com.zhongdasoft.svwtrainnet.util.FileUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * http://blog.csdn.net/lmj623565791/article/details/41874561
 *
 * @author zhy
 */
public class ListImgFragment extends Fragment {
    private GridView mGridView;
    private ArrayList<String> imageList;
    private ImageLoader mImageLoader;
    private String uploadFiles;
    private String[] fileExtends;
    private String rootPath;
    private int upload_image_request_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadFiles = getActivity().getIntent().getStringExtra("uploadFiles");
        String fileExtend = getActivity().getIntent().getStringExtra("item");
        if (!StringUtil.isNullOrEmpty(fileExtend)) {
            fileExtends = fileExtend.split(",");
        } else {
            fileExtends = new String[1];
            fileExtends[0] = "";
        }
        imageList = new ArrayList<>();
        rootPath = Environment.getExternalStorageDirectory().getPath();//获取根目录
        if (StringUtil.isNullOrEmpty(rootPath)) {
            setReturn(null, null);
            return;
        } else {
            rootPath += CameraUtil.getInstance().getCameraFilePath();
        }
        getFileDir(rootPath);
        upload_image_request_code = Integer.parseInt(getResources().getString(R.string.UPLOAD_IMAGE_REQUEST_CODE));
        mImageLoader = ImageLoader.getInstance(4, ImageLoader.Type.LIFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_imgs, container,
                false);
        mGridView = (GridView) view.findViewById(R.id.id_gridview);
        setUpAdapter();
        return view;
    }

    private void setUpAdapter() {
        if (getActivity() == null || mGridView == null)
            return;

        if (imageList.size() > 0) {
            for (int i = 0; i < imageList.size(); i++) {
                imageList.set(i, imageList.get(i).split(",")[1]);
            }
            mGridView.setAdapter(new ListImgItemAdaper(getActivity(), 0,
                    imageList.toArray(new String[]{})));
            mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String filePath = imageList.get(position);
                    File file = new File(filePath);
                    long fileSize = file.length();
                    if (!StringUtil.isNullOrEmpty(uploadFiles) && uploadFiles.indexOf(file.getName() + ",") >= 0) {
                        ToastUtil.show(getActivity(), getResources().getString(R.string.errFileExist));
                    } else {
                        String needZip = "0";
                        if (fileSize > Long.parseLong(getResources().getString(R.string.uploadFileSize))) {
                            needZip = "1";
                        }
                        setReturn(filePath, needZip);
                    }
                }
            });
        } else {
            mGridView.setAdapter(null);
        }

    }

    private class ListImgItemAdaper extends ArrayAdapter<String> {

        public ListImgItemAdaper(Context context, int resource, String[] datas) {
            super(getActivity(), 0, datas);
            Log.e("TAG", "ListImgItemAdaper");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.icon_empty);
            mImageLoader.loadImage(getItem(position), imageview, false);
            return convertView;
        }

    }

    private void setReturn(String filePath, String needZip) {
        Intent mIntent = new Intent();
        mIntent.putExtra("item", filePath);
        mIntent.putExtra("zip", needZip);
        // 设置结果，并进行传送
        getActivity().setResult(upload_image_request_code, mIntent);
        getActivity().finish();
    }

    private void getFileDir(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File fileTemp : files) {
            if (fileTemp.isFile() && fileTemp.canRead()) {
                String fileExtendName = getFileExtendName(fileTemp.getName());
                for (String extend : fileExtends) {
                    if (StringUtil.isNullOrEmpty(extend) || fileExtendName.toLowerCase().equals(extend)) {
                        imageList.add(FileUtil.getInstance().getModifiedTime(fileTemp.lastModified()) + "," + fileTemp.getPath());
                        break;
                    }
                }
            }
        }
        Collections.sort(imageList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return rhs.compareTo(lhs);
            }
        });
        return;
    }

    private String getFileExtendName(String name) {
        int pos = name.lastIndexOf(".");
        if (pos < 0) {
            return "";
        }
        return name.substring(pos + 1, name.length());
    }

}
