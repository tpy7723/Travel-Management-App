package com.example.pks01.bottom_navi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import java.util.List;

public class Third_Fragment extends Fragment {

    Context context_;
    DisplayMetrics mMetrics;
    ImageAdapter myImageAdapter;
    String category;
    GridView gridview;
    List<DB_Item> db_itemData;

    private GridView.OnItemClickListener gridviewOnItemClickListener
            = new GridView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            Intent intent = new Intent(getContext(), ConfirmActivity.class);
            intent.putExtra("OBJECT", First_Fragment.db_itemData.get(arg2)); // 해당 data를 intent
                                                                                    //에 담아 보냄
            getContext().startActivity(intent);
        }
    };

    public Third_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context_ = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_, container, false);
        gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(gridviewOnItemClickListener);
        WindowManager windowManager = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
        mMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(mMetrics);

        Spinner s = (Spinner) view.findViewById(R.id.spinner1);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                category = parent.getItemAtPosition(position).toString();

                db_itemData = MainActivity.dbHelper.get_ItemDatabyCategory(category); // 해당 카테코리 데이터를 가져옴
                myImageAdapter = new ImageAdapter(db_itemData, getContext());
                gridview.setAdapter(myImageAdapter); // 어댑터 재연결
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = null;
            }
        });

        return view;
    }

    public class ImageAdapter extends BaseAdapter {
        private List<DB_Item> listViewItemList;
        private Context mContext;

        public ImageAdapter(List<DB_Item> items, Context c) {
            this.listViewItemList = items;
            mContext = c;
        }

        public int getCount() {
            return listViewItemList.size();
        }

        public Object getItem(int position) {
            return listViewItemList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {

            int rowWidth = (mMetrics.widthPixels) / 4; // 가로의 4분의 1 길이

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext); // 그리드 사이에 들어갈 이미지뷰 선언
                imageView.setLayoutParams(new GridView.LayoutParams(rowWidth, rowWidth)); // 사이즈 정의
                imageView.setPadding(10, 10, 10, 10); // Padding 정의
            } else {
                imageView = (ImageView) convertView;
            }
            if (position <= First_Fragment.db_itemData.size()) {

                // 비트맵의 축소옵션을 위한 생성
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;    // 이미지의 축소비율

                Bitmap myBitmap = BitmapFactory.decodeFile(listViewItemList.get(position).getImage());
                Bitmap resize = Bitmap.createScaledBitmap(    // 불러온 이미지를 축소한다.
                        myBitmap, myBitmap.getWidth(), myBitmap.getHeight(), true);
                // 회전을 위한 옵션 생성
                Matrix m = new Matrix();
                m.setRotate(90, resize.getWidth(), resize.getHeight());

                // 실제 회전한 값을 적용하여 새로운 이미지를 생성한다.
                Bitmap rotateBitmap = Bitmap.createBitmap(resize, 0, 0, resize.getWidth(),
                        resize.getHeight(), m, false);
                imageView.setImageBitmap(rotateBitmap);
            }

            if (convertView != null) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ConfirmActivity.class);
                        intent.putExtra("OBJECT", listViewItemList.get(position));
                        getContext().startActivity(intent);
                    }
                });
            }
            return imageView;
        }
    }
}