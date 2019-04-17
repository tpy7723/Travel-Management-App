package com.example.pks01.bottom_navi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    // 아이템 데이터 리스트.
    private List<DB_Item> listViewItemList;
    private Context context;

    ListViewAdapter(List<DB_Item> items, Context context) {
        this.listViewItemList = items;
        this.context = context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            TextView editText_title = (TextView) convertView.findViewById(R.id.title);
            TextView editText_content = (TextView) convertView.findViewById(R.id.snippet);
            TextView editText_num = (TextView) convertView.findViewById(R.id.textView_num);
            TextView editText_category = (TextView) convertView.findViewById(R.id.textView_category);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);

            holder = new Holder();
            holder.text_title = editText_title;
            holder.text_snippet = editText_content;
            holder.text_num = editText_num;
            holder.image_View = imageView;
            holder.text_category = editText_category;

            convertView.setTag(holder);
        } else {
            // convertView가 있으면 홀더를 꺼냅니다.
            holder = (Holder) convertView.getTag();
        }

        // 한명의 데이터를 받아와서 입력합니다.
        final DB_Item db_item = (DB_Item) getItem(position);

        holder.text_title.setText(db_item.getTitle());
        holder.text_snippet.setText(db_item.getSnippet());
        holder.text_num.setText(Integer.toString(db_item.getID()));
        holder.text_category.setText(db_item.getCategory());

        File fileRoot = new File(db_item.getImage());

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90); //-360~360 // 회전 매트릭스

        // 비트맵의 축소옵션을 위한 생성
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;	// 이미지의 축소비율

        Bitmap myBitmap = BitmapFactory.decodeFile(fileRoot.getPath(), options);
        Bitmap resize = Bitmap.createScaledBitmap(	// 불러온 이미지를 축소한다.
                myBitmap, myBitmap.getWidth(), myBitmap.getHeight(), true);
        // 회전을 위한 옵션 생성
        Matrix m = new Matrix();
        m.setRotate(90, resize.getWidth(), resize.getHeight());

        // 실제 회전한 값을 적용하여 새로운 이미지를 생성한다.
        Bitmap rotateBitmap = Bitmap.createBitmap( resize, 0, 0, resize.getWidth(),
                resize.getHeight(), m, false);

        holder.image_View.setImageBitmap(rotateBitmap);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConfirmActivity.class);
                intent.putExtra("OBJECT", db_item);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class Holder {
        TextView text_title;
        TextView text_snippet;
        TextView text_num;
        TextView text_category;
        ImageView image_View;
    }
}

